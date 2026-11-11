"""过线之后还要核对出处。分数够高只说明字面融合分够高，不是回答正确。

一条合格出处必须同时满足：
- 来自这一次检索，并且融合分达到阈值
- 两路不是全 0
- 编号对应的原文，和笔记文件里的字符区间完全一致

做不到就整段不输出，改成第 05 课那句固定拒绝。这里不调用模型，也不编一句自然语言答案。
阈值来自 calibration/threshold.json，不使用课程里的 0.30。
"""

import json
from dataclasses import dataclass
from pathlib import Path

from keji_cite.errors import ThresholdError
from keji_cite.formula import squash
from keji_cite.hybrid import Hit, search
from keji_cite.store import Index, display_path

# 第 05 课的固定拒绝语。失败时只留这一句，不把摘录拼在后面。
REFUSAL = "知识库里没有足够依据，不能回答。"

# 展示上限不是门槛。过线更多时仍然只摘这几条，避免把整库打出来。
MAX_SHOW = 3


@dataclass(frozen=True)
class Decision:
    kind: str
    reason: str
    threshold: float | None
    shown: tuple[Hit, ...]
    passed_count: int
    top: Hit | None

    @property
    def cited(self) -> bool:
        return self.kind == "cite"


def load_threshold(path: Path) -> float:
    if not path.is_file():
        raise ThresholdError("还没有阈值文件。请先运行：python3 -m keji_cite calibrate")
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, UnicodeError, json.JSONDecodeError) as exc:
        raise ThresholdError("阈值文件损坏：不是合法的 JSON") from exc
    if not isinstance(data, dict) or "threshold" not in data:
        raise ThresholdError("阈值文件损坏：缺少 threshold")
    value = data["threshold"]
    if isinstance(value, bool) or not isinstance(value, (int, float)):
        raise ThresholdError("阈值文件损坏：threshold 不是数字")
    threshold = float(value)
    if threshold < 0.0 or threshold > 1.0:
        raise ThresholdError("阈值文件损坏：threshold 不在 0 到 1 之间")
    return threshold


def decide(question: str, index: Index, notes_dir: Path, threshold: float) -> Decision:
    if question.strip() == "":
        return Decision("refuse", "empty_question", None, (), 0, None)

    hits = search(index, question)
    top = hits[0] if hits else None
    if not hits or all(hit.both_zero for hit in hits):
        return Decision("refuse", "both_zero", threshold, (), 0, top)

    passed = [hit for hit in hits if hit.fusion >= threshold and not hit.both_zero]
    cache: dict[Path, str] = {}
    verified = [hit for hit in passed if verify_hit(hit, notes_dir, cache)]
    if not verified:
        reason = "bad_citation" if passed else "low_score"
        return Decision("refuse", reason, threshold, (), 0, top)
    return Decision("cite", "cited_excerpt", threshold, tuple(verified[:MAX_SHOW]), len(verified), top)


def verify_hit(hit: Hit, notes_dir: Path, cache: dict[Path, str] | None = None) -> bool:
    """重新打开笔记，确认摘录就是该文件里的那一段。"""
    relative = Path(hit.path)
    if relative.is_absolute() or ".." in relative.parts:
        return False
    full = (notes_dir / relative).resolve()
    root = notes_dir.resolve()
    if not full.is_relative_to(root) or not full.is_file():
        return False
    try:
        if cache is not None and full in cache:
            text = cache[full]
        else:
            text = full.read_text(encoding="utf-8")
            if cache is not None:
                cache[full] = text
    except (OSError, UnicodeError):
        return False
    if hit.start < 0 or hit.end > len(text) or hit.start >= hit.end:
        return False
    if text[hit.start : hit.end] != hit.text:
        return False
    start_line = text.count("\n", 0, hit.start) + 1
    end_line = text.count("\n", 0, hit.end - 1) + 1
    return start_line == hit.start_line and end_line == hit.end_line


def format_decision(decision: Decision, notes_dir: Path) -> str:
    if decision.cited:
        return _format_cite(decision, notes_dir)
    return _format_refuse(decision)


def _format_cite(decision: Decision, notes_dir: Path) -> str:
    lines = [
        "状态：命中",
        "原因：cited_excerpt",
        f"阈值：{decision.threshold:.6f}",
        f"核对通过：{decision.passed_count}",
        f"展示数量：{len(decision.shown)}",
        "说明：分数过线只说明字面混合分够高。下面是本次检索里核对过的原文摘录，不是生成的答案。",
    ]
    for hit in decision.shown:
        lines.append("---")
        lines.append(f"融合：{hit.fusion:.6f}")
        lines.append(f"向量：{hit.vector_score:.6f}")
        lines.append(f"关键词原始：{hit.sparse_raw:.6f}")
        lines.append(f"关键词压缩：{squash(hit.sparse_raw):.6f}")
        lines.append(f"出处：{display_path(notes_dir, hit.path)}:{hit.start_line}-{hit.end_line}")
        lines.append(f"编号：{hit.doc_id}")
        lines.append("摘录：")
        lines.extend(f"> {line}" for line in hit.text.split("\n"))
    lines.append("")
    return "\n".join(lines)


def _format_refuse(decision: Decision) -> str:
    lines = [
        "状态：拒绝",
        f"原因：{decision.reason}",
    ]
    if decision.threshold is not None:
        lines.append(f"阈值：{decision.threshold:.6f}")
    if decision.top is not None and decision.reason != "empty_question":
        lines.append(f"最高融合：{decision.top.fusion:.6f}")
        lines.append(f"最高向量：{decision.top.vector_score:.6f}")
        lines.append(f"最高关键词原始：{decision.top.sparse_raw:.6f}")
    if decision.reason == "bad_citation":
        lines.append("说明：分数过线的条目对不上笔记原文，不能当成出处。")
    elif decision.reason == "both_zero":
        lines.append("说明：向量和关键词两路都是 0，融合也是 0，不能因为多算一路就过线。")
    elif decision.reason == "empty_question":
        lines.append("说明：问句是空的，没有可检索的字。")
    lines.append(f"固定拒绝：{REFUSAL}")
    lines.append("")
    return "\n".join(lines)
