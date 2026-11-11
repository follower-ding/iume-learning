"""用写定的校准集选择阈值，并生成可核对的记录。

规则在看分数之前就定好：
- 只读 calibrate_positive / calibrate_negative
- 最低正例严格高于最高负例时，阈值取中点
- 没有这条缝时，阈值取刚好大于最高负例的下一个浮点，校准负例全部留下
- 保留集和观察项只写进记录，不回头改阈值，也不改 k1 / b
"""

import json
import math
from dataclasses import dataclass
from pathlib import Path

from keji_cite.errors import ThresholdError
from keji_cite.formula import fuse, squash
from keji_cite.hybrid import search
from keji_cite.store import Index, display_path

CALIBRATE_ROLES = ("calibrate_positive", "calibrate_negative")
KNOWN_ROLES = CALIBRATE_ROLES + ("holdout_positive", "holdout_negative", "observe")

RULE_NAME = "gap_midpoint_else_just_above_max_negative"


@dataclass(frozen=True)
class Question:
    id: str
    role: str
    text: str
    expect_file: str | None


@dataclass(frozen=True)
class ScoredQuestion:
    question: Question
    fusion: float
    vector_score: float
    sparse_raw: float
    both_zero: bool
    top_id: str
    top_path: str
    top_lines: str
    preview: str

    @property
    def sparse_squash(self) -> float:
        return squash(self.sparse_raw)


@dataclass(frozen=True)
class ThresholdChoice:
    rule: str
    gap: bool
    threshold: float
    min_positive: float
    max_negative: float
    min_positive_id: str
    max_negative_id: str
    used_ids: tuple[str, ...]


def load_questions(path: Path) -> list[Question]:
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, UnicodeError, json.JSONDecodeError) as exc:
        raise ThresholdError("问句文件损坏：不是合法的 JSON") from exc
    if not isinstance(data, dict) or not isinstance(data.get("questions"), list):
        raise ThresholdError("问句文件损坏：缺少 questions")
    questions: list[Question] = []
    seen: set[str] = set()
    for item in data["questions"]:
        if not isinstance(item, dict):
            raise ThresholdError("问句文件损坏：有一条不是对象")
        qid = item.get("id")
        role = item.get("role")
        text = item.get("text")
        expect = item.get("expect_file")
        if not isinstance(qid, str) or not qid or qid in seen:
            raise ThresholdError("问句文件损坏：编号缺失或重复")
        if role not in KNOWN_ROLES:
            raise ThresholdError(f"问句文件损坏：不认识的角色 {role}")
        if not isinstance(text, str) or text.strip() == "":
            raise ThresholdError(f"问句文件损坏：{qid} 没有问句")
        if expect is not None and not isinstance(expect, str):
            raise ThresholdError(f"问句文件损坏：{qid} 的 expect_file 不是字符串")
        seen.add(qid)
        questions.append(Question(qid, role, text, expect))
    roles = {question.role for question in questions}
    if "calibrate_positive" not in roles or "calibrate_negative" not in roles:
        raise ThresholdError("校准集需要同时有正问句和负问句")
    return questions


def score_questions(index: Index, notes_dir: Path, questions: list[Question]) -> list[ScoredQuestion]:
    scored: list[ScoredQuestion] = []
    for question in questions:
        hits = search(index, question.text)
        if not hits:
            raise ThresholdError("索引里没有切块，无法校准")
        top = hits[0]
        if abs(top.fusion - fuse(top.vector_score, top.sparse_raw)) > 1e-12:
            raise ThresholdError("融合分和公式对不上，停止校准")
        preview = ""
        if not top.both_zero:
            preview = " ".join(top.text.split())
            if len(preview) > 42:
                preview = preview[:42] + "…"
        scored.append(
            ScoredQuestion(
                question=question,
                fusion=top.fusion,
                vector_score=top.vector_score,
                sparse_raw=top.sparse_raw,
                both_zero=top.both_zero,
                top_id=top.doc_id,
                top_path=display_path(notes_dir, top.path),
                top_lines=f"{top.start_line}-{top.end_line}",
                preview=preview,
            )
        )
    return scored


def choose_threshold(scored: list[ScoredQuestion]) -> ThresholdChoice:
    """只看校准集。调用方即使把保留集传进来，这里也会丢掉。"""
    positives = [row for row in scored if row.question.role == "calibrate_positive"]
    negatives = [row for row in scored if row.question.role == "calibrate_negative"]
    if not positives or not negatives:
        raise ThresholdError("校准集需要同时有正问句和负问句")
    # 分数相同就取编号字母序靠前的一条，记录每次都一样。
    min_row = sorted(positives, key=lambda row: (row.fusion, row.question.id))[0]
    max_row = sorted(negatives, key=lambda row: (-row.fusion, row.question.id))[0]
    used = tuple(sorted(row.question.id for row in positives + negatives))
    if min_row.fusion > max_row.fusion:
        threshold = _json_float((min_row.fusion + max_row.fusion) / 2.0)
        if not (min_row.fusion >= threshold > max_row.fusion):
            threshold = _json_float(min_row.fusion)
        gap = True
    else:
        threshold = _json_float(math.nextafter(max_row.fusion, math.inf))
        if not threshold > max_row.fusion:
            raise ThresholdError("无法把阈值放到全部校准负例之上")
        gap = False
    return ThresholdChoice(
        rule=RULE_NAME,
        gap=gap,
        threshold=threshold,
        min_positive=min_row.fusion,
        max_negative=max_row.fusion,
        min_positive_id=min_row.question.id,
        max_negative_id=max_row.question.id,
        used_ids=used,
    )


def would_cite(row: ScoredQuestion, threshold: float) -> bool:
    return (not row.both_zero) and row.fusion >= threshold


def choice_payload(choice: ThresholdChoice) -> dict[str, object]:
    return {
        "course_threshold_0_30_used": False,
        "gap": choice.gap,
        "max_negative": choice.max_negative,
        "max_negative_id": choice.max_negative_id,
        "min_positive": choice.min_positive,
        "min_positive_id": choice.min_positive_id,
        "rule": choice.rule,
        "threshold": choice.threshold,
        "used_ids": list(choice.used_ids),
    }


def render_record(index: Index, scored: list[ScoredQuestion], choice: ThresholdChoice) -> str:
    lines: list[str] = [
        "# 阈值校准记录",
        "",
        "本文件由 `python3 -m keji_cite calibrate` 根据仓库 `notes/` 和当前源码生成。",
        "问句写在 `calibration/questions.json`。角色在查看融合分之前就分好。",
        "定阈值时只读校准集。保留集和观察项的分数写在下面，但不进入选择规则。",
        "",
        "## 固定公式",
        "",
        "- 切块：空行分段，短段整留，长段滑窗。size=80，overlap=20。这是第 03 课的窗口，写代码前定下。",
        "- 二元组：丢掉空白和标点后，取相邻两个字。中文不按空格分词。",
        "- 向量：次数 × 平滑 idf，再做 L2，余弦是归一化向量的点积。文档和查询共用一次 fit。",
        "- BM25：k1=1.5，b=0.75。idf = ln(1 + (N-df+0.5)/(df+0.5))。",
        "- squash = raw / (raw + 2.5)。融合 = 0.5 × 向量 + 0.5 × squash。",
        "- 不把原始分直接相加，不除以本问最高分，不用 max 代替加权。",
        "- 两路都是 0 时融合是 0，必须拒绝，哪怕阈值写成 0。",
        "- 不采用课程阈值 0.30。那条缝来自酒店 FAQ，不是这批笔记。",
        "",
        "## 规则",
        "",
        "校准正例取最低融合分，校准负例取最高融合分。",
        "若最低正例严格更高，阈值取两者中点。中点写成 JSON 后若不再落在这条缝里，就改用最低正例。",
        "若没有这条缝，阈值取刚好大于最高负例的下一个浮点，保证校准负例都过不了线。",
        "落在阈值下面的正例记为漏检。不为它们改 k1、b、窗口或问句名单。",
        "",
        "## 这次的索引",
        "",
        f"- 切块数：{index.doc_count}",
        f"- 词表维度：{index.dimension}",
        f"- 平均文档长度（二元组个数）：{index.avgdl:.6f}",
        "",
        "## 这次的阈值",
        "",
        f"- 有缝：{'是' if choice.gap else '否'}",
        f"- 最低校准正例：{choice.min_positive_id}  {_fmt(choice.min_positive)}",
        f"- 最高校准负例：{choice.max_negative_id}  {_fmt(choice.max_negative)}",
        f"- 阈值：{_fmt(choice.threshold)}（精确值 {choice.threshold!r}）",
        "- 课程阈值 0.30 是否使用：否",
        f"- 参与定阈值的编号：{', '.join(choice.used_ids)}",
        "",
        "## 校准集",
        "",
        _table(scored, choice, CALIBRATE_ROLES),
        "",
        _mismatch_notes(scored, choice, "calibrate_positive"),
        "",
        "## 保留集",
        "",
        "这些问句没有用来定阈值。过线或拒绝都按已经定下的阈值记录。",
        "",
        _table(scored, choice, ("holdout_positive", "holdout_negative")),
        "",
        _holdout_notes(scored, choice),
        "",
        "## 观察项",
        "",
        "观察项不参与定阈值，也不是验收里的「应当拒绝」。",
        "笔记若原文写过这句问法，字面检索会命中那一段。这只说明笔记里有这些字，不说明问题被回答了。",
        "",
        _table(scored, choice, ("observe",)),
        "",
        "## 不能泛化到哪里",
        "",
        *limitation_lines(scored, choice),
        "",
    ]
    return "\n".join(lines)


def _table(scored: list[ScoredQuestion], choice: ThresholdChoice, roles: tuple[str, ...]) -> str:
    header = [
        "| 编号 | 角色 | 问句 | 融合 | 向量 | 关键词原始 | 压缩 | 两路全零 | 按阈值 | 第一出处 | 行 | 预期文件 | 第一出处是否预期 | 预览 |",
        "| --- | --- | --- | ---: | ---: | ---: | ---: | --- | --- | --- | --- | --- | --- | --- |",
    ]
    body = []
    for row in scored:
        if row.question.role not in roles:
            continue
        expect = row.question.expect_file or ""
        matched = "是" if expect and row.top_path == expect and not row.both_zero else "否"
        if not expect:
            matched = "—"
        decision = "摘录" if would_cite(row, choice.threshold) else "拒绝"
        body.append(
            "| "
            + " | ".join(
                [
                    _cell(row.question.id),
                    _cell(row.question.role),
                    _cell(row.question.text),
                    _fmt(row.fusion),
                    _fmt(row.vector_score),
                    _fmt(row.sparse_raw),
                    _fmt(row.sparse_squash),
                    "是" if row.both_zero else "否",
                    decision,
                    _cell(row.top_path),
                    _cell(row.top_lines),
                    _cell(expect),
                    matched,
                    _cell(row.preview if not row.both_zero else "（两路都是 0，不摘录）"),
                ]
            )
            + " |"
        )
    if not body:
        return "（这一组没有问句）"
    return "\n".join(header + body)


def _mismatch_notes(scored: list[ScoredQuestion], choice: ThresholdChoice, role: str) -> str:
    notes: list[str] = []
    for row in scored:
        if row.question.role != role:
            continue
        if would_cite(row, choice.threshold) and row.question.expect_file and row.top_path != row.question.expect_file:
            notes.append(
                f"- {row.question.id} 过线，但第一出处是 {row.top_path}，不是预期的 {row.question.expect_file}。"
                "这只说明那里的二元组更像问句，不把这条改成正确答案。"
            )
        if not would_cite(row, choice.threshold):
            notes.append(
                f"- {row.question.id} 没有过线（融合 {_fmt(row.fusion)}）。"
                "它留在校准集里，不删题，也不为它放宽阈值。"
            )
    if not notes:
        return "校准正例按本规则都会摘录，且第一出处落在预期文件上。"
    return "\n".join(notes)


def _holdout_notes(scored: list[ScoredQuestion], choice: ThresholdChoice) -> str:
    leaks = [
        row
        for row in scored
        if row.question.role == "holdout_negative" and would_cite(row, choice.threshold)
    ]
    misses = [
        row
        for row in scored
        if row.question.role == "holdout_positive" and not would_cite(row, choice.threshold)
    ]
    lines: list[str] = []
    if leaks:
        lines.append("保留负例里，下面这些按本次阈值会过线。阈值没有为了挡住它们而改动：")
        lines.extend(f"- {row.question.id}：{row.question.text}（融合 {_fmt(row.fusion)}）" for row in leaks)
    else:
        lines.append("这次的保留负例按该阈值都不会过线。这只覆盖这几句，换一个无关问法仍可能过线。")
    if misses:
        lines.append("保留正例里，下面这些没有过线。问法若不是原文，二元组对不上就会这样。阈值没有为了留下它们而放宽：")
        lines.extend(f"- {row.question.id}：{row.question.text}（融合 {_fmt(row.fusion)}）" for row in misses)
    else:
        lines.append("这次的保留正例按该阈值都会摘录。这不表示换一种说法也会留住。")
    return "\n".join(lines)


def limitation_lines(scored: list[ScoredQuestion], choice: ThresholdChoice) -> list[str]:
    """把这次分数里已经能看见的局限写出来。数字来自当次运行。"""
    lines = [
        "- 换一批笔记、改切块或改公式，缝会移动。要整份重跑 calibrate，不能沿用这次的阈值，也不能退回 0.30。",
        "- 这不是语义检索。同义说法对不上二元组就会掉分或拒绝。第 09 课的「狗 / 宠物」在这里同样对不上。",
        "- 过线不是回答正确。命令只摘抄这次核对过的原文和文件行号，不生成一句「答案是……」。",
        "- 校准负例低于阈值，是因为规则就把阈值放在它们上面。不能拿校准负例证明所有无关问句都会被拒绝。",
        "- 问句很少。中点只是预先写好的规则，不是对所有问法最优的分界。",
        "- 短段落只要碰上少数二元组，余弦可能偏高。这是字面余弦的特性，不靠改 k1 / b 去压某一句。",
        "- 全 0 时编号字母序靠前的切块会排到第一，那不是相关度。回答里不摘录它。",
    ]
    raws = [row.sparse_raw for row in scored if row.question.role in CALIBRATE_ROLES]
    if raws and max(raws) > 10:
        lines.append(
            f"- 这次校准集里关键词原始分最高到 {max(raws):.1f}。"
            "课内 squash 的加数是 2.5，原始分远大于 2.5 时压缩值会挤在 1 附近，两路差别主要剩在余弦上，缝会变窄。"
            "没有因此把 2.5 改大，也没有改 k1 / b。"
        )
    if choice.gap:
        width = choice.min_positive - choice.max_negative
        lines.append(
            f"- 这次的缝宽 {width:.6f}（{choice.min_positive_id} {choice.min_positive:.6f}，"
            f"{choice.max_negative_id} {choice.max_negative:.6f}）。缝窄时，多一个字面碰擦就可能翻到另一侧。"
        )
    return lines


def _fmt(value: float) -> str:
    return f"{value:.6f}"


def _cell(value: str) -> str:
    return value.replace("|", "\\|").replace("\n", " ")


def _json_float(value: float) -> float:
    """走一次 JSON，保证写进文件的值和内存里用来比较的值相同。"""
    return float(json.dumps(value))
