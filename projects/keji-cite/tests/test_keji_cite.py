"""课记引的标准库测试。在项目目录运行：

    python3 -X utf8 -m unittest tests/test_keji_cite.py -v
"""

import json
import math
import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))

from keji_cite.bm25 import B, K1, score_bm25  # noqa: E402
from keji_cite.calibrate import (  # noqa: E402
    choice_payload,
    choose_threshold,
    load_questions,
    render_record,
    score_questions,
    would_cite,
)
from keji_cite.chunk import chunk_text  # noqa: E402
from keji_cite.cite import REFUSAL, decide, format_decision, verify_hit  # noqa: E402
from keji_cite.embed import cosine, embed_text, fit_idf, smooth_idf  # noqa: E402
from keji_cite.errors import CorruptIndexError, MissingIndexError  # noqa: E402
from keji_cite.formula import SPARSE_WEIGHT, SQUASH_K, VECTOR_WEIGHT, fuse, squash  # noqa: E402
from keji_cite.paths import default_questions_path, default_record_path, default_threshold_path, repo_root  # noqa: E402
from keji_cite.store import build_index, load_index, save_index  # noqa: E402
from keji_cite.tokens import bigrams  # noqa: E402

ACCEPTANCE_ASKS = (
    ("python3 -X utf8 -m keji_cite ask 两路原始分不能直接相加", "两路原始分不能直接相加"),
    ("python3 -X utf8 -m keji_cite ask 明天门票多少钱", "明天门票多少钱"),
    ('python3 -X utf8 -m keji_cite ask ""', ""),
    ("python3 -X utf8 -m keji_cite ask 纳斯达克今天收盘点位", "纳斯达克今天收盘点位"),
    ("python3 -X utf8 -m keji_cite ask 游泳池几点开放", "游泳池几点开放"),
)


class ChunkTests(unittest.TestCase):
    def test_overlap_must_be_smaller_than_size(self) -> None:
        with self.assertRaises(ValueError):
            chunk_text("甲乙丙丁", size=80, overlap=80)
        with self.assertRaises(ValueError):
            chunk_text("甲乙丙丁", size=10, overlap=12)

    def test_size_must_be_positive(self) -> None:
        with self.assertRaises(ValueError):
            chunk_text("甲乙丙丁", size=0, overlap=0)

    def test_short_paragraph_stays_whole_and_blank_lines_split(self) -> None:
        text = "第一段保持整段。\n\n第二段也短。"
        chunks = chunk_text(text, size=80, overlap=20)
        self.assertEqual([chunk.text for chunk in chunks], ["第一段保持整段。", "第二段也短。"])
        self.assertEqual(text[chunks[0].start : chunks[0].end], chunks[0].text)

    def test_single_newline_does_not_split(self) -> None:
        text = "甲乙丙。\n丁戊己。"
        chunks = chunk_text(text, size=80, overlap=20)
        self.assertEqual(len(chunks), 1)
        self.assertEqual(chunks[0].text, text)

    def test_long_window_prefers_punctuation_in_second_half(self) -> None:
        text = ("甲" * 40) + "。" + ("乙" * 100)
        chunks = chunk_text(text, size=50, overlap=10)
        self.assertEqual(chunks[0].text, ("甲" * 40) + "。")
        self.assertEqual(text[chunks[0].start : chunks[0].end], chunks[0].text)
        self.assertGreater(chunks[1].start, chunks[0].start)
        self.assertEqual(chunks[-1].end, len(text))

    def test_window_advances_when_overlap_is_almost_size(self) -> None:
        text = "甲" * 300
        chunks = chunk_text(text, size=80, overlap=79)
        self.assertEqual(chunks[0].start, 0)
        self.assertEqual(chunks[-1].end, 300)
        for previous, nxt in zip(chunks, chunks[1:]):
            self.assertGreater(nxt.start, previous.start)


class TokenAndVectorTests(unittest.TestCase):
    def test_lesson_probe_has_seven_bigrams(self) -> None:
        text = "今晚有乐队演出吗"
        self.assertEqual(bigrams(text), ["今晚", "晚有", "有乐", "乐队", "队演", "演出", "出吗"])
        self.assertEqual(len(fit_idf([text])), 7)

    def test_chinese_is_not_split_on_spaces_and_punctuation_is_dropped(self) -> None:
        self.assertEqual(bigrams("入住 时间"), bigrams("入住时间"))
        self.assertEqual(bigrams("不截断/不补零"), ["不截", "截断", "断不", "不补", "补零"])

    def test_query_uses_the_fitted_idf(self) -> None:
        docs = ["入住时间是下午两点", "早餐供应到十点"]
        shared = fit_idf(docs)
        alone = fit_idf(["入住时间"])
        self.assertNotEqual(set(shared), set(alone))
        vector = embed_text("入住时间新词", shared)
        self.assertIn("入住", vector)
        self.assertNotIn("新词", vector)
        self.assertEqual(smooth_idf(1, 2), math.log((1 + 2) / (1 + 1)) + 1)

    def test_cosine_of_identical_unrelated_and_empty(self) -> None:
        idf = fit_idf(["入住时间", "乐队演出"])
        left = embed_text("入住时间", idf)
        right = embed_text("乐队演出", idf)
        self.assertAlmostEqual(cosine(left, left), 1.0, places=6)
        self.assertEqual(cosine(left, right), 0.0)
        self.assertEqual(cosine({}, left), 0.0)


class FusionTests(unittest.TestCase):
    def test_constants_and_squash_formula(self) -> None:
        self.assertEqual(K1, 1.5)
        self.assertEqual(B, 0.75)
        self.assertEqual(SQUASH_K, 2.5)
        self.assertEqual(VECTOR_WEIGHT, 0.5)
        self.assertEqual(SPARSE_WEIGHT, 0.5)
        self.assertEqual(squash(0.0), 0.0)
        self.assertEqual(squash(-3.0), 0.0)
        self.assertEqual(squash(2.5), 0.5)
        self.assertEqual(fuse(0.2, 2.5), 0.5 * 0.2 + 0.5 * 0.5)
        self.assertNotEqual(fuse(0.2, 2.5), max(0.2, squash(2.5)))

    def test_bm25_matches_the_written_formula_and_zero_overlap_is_zero(self) -> None:
        notes = Path(tempfile.mkdtemp())
        (notes / "a.md").write_text("甲乙丙丁。\n", encoding="utf-8")
        (notes / "b.md").write_text("甲乙戊己。\n", encoding="utf-8")
        index = build_index(notes)
        self.assertEqual(score_bm25(["狗狗"], index.docs[0].tf, index.docs[0].dl, index.avgdl, index.bm25_idf), 0.0)
        query = ["甲乙"]
        doc = next(item for item in index.docs if "甲乙" in item.tf)
        n_docs = index.doc_count
        df = sum(1 for item in index.docs if "甲乙" in item.tf)
        idf = math.log(1.0 + (n_docs - df + 0.5) / (df + 0.5))
        freq = doc.tf["甲乙"]
        norm = K1 * (1.0 - B + B * doc.dl / index.avgdl)
        expected = idf * (freq * (K1 + 1.0)) / (freq + norm)
        # 问句里「甲乙」出现一次，所以就加这一次。
        self.assertAlmostEqual(
            score_bm25(query, doc.tf, doc.dl, index.avgdl, index.bm25_idf),
            expected,
            places=9,
        )

    def test_search_does_not_divide_by_the_query_max(self) -> None:
        notes = self._notes({"strong.md": "甲乙丙丁戊己庚辛。", "weak.md": "甲乙其他文字。"})
        index = build_index(notes)
        from keji_cite.hybrid import search

        hits = search(index, "甲乙丙丁戊己庚辛")
        self.assertGreater(hits[0].fusion, hits[1].fusion)
        self.assertAlmostEqual(hits[0].fusion, fuse(hits[0].vector_score, hits[0].sparse_raw), places=9)
        self.assertAlmostEqual(hits[1].fusion, fuse(hits[1].vector_score, hits[1].sparse_raw), places=9)
        self.assertLess(hits[0].fusion, 1.0)

    def test_tie_breaks_by_id(self) -> None:
        notes = self._notes({"b.md": "甲乙丙丁。", "a.md": "甲乙丙丁。"})
        index = build_index(notes)
        from keji_cite.hybrid import search

        hits = search(index, "甲乙丙丁")
        self.assertEqual(hits[0].fusion, hits[1].fusion)
        self.assertLess(hits[0].doc_id, hits[1].doc_id)

    def _notes(self, files: dict[str, str]) -> Path:
        root = Path(tempfile.mkdtemp())
        for name, text in files.items():
            path = root / name
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(text, encoding="utf-8")
        return root


class CiteTests(unittest.TestCase):
    def test_hit_excerpt_matches_file_lines(self) -> None:
        notes = Path(tempfile.mkdtemp()) / "notes"
        notes.mkdir()
        (notes / "demo.md").write_text("标题\n\n甲乙丙丁戊己庚辛。\n", encoding="utf-8")
        index = build_index(notes)
        decision = decide("甲乙丙丁戊己庚辛", index, notes, 0.0)
        text = format_decision(decision, notes)
        self.assertTrue(decision.cited)
        self.assertIn("notes/demo.md:3-3", text)
        self.assertIn("> 甲乙丙丁戊己庚辛。", text)
        self.assertIn("不是生成的答案", text)
        hit = decision.shown[0]
        self.assertTrue(verify_hit(hit, notes))
        file_text = (notes / "demo.md").read_text(encoding="utf-8")
        self.assertEqual(file_text[hit.start : hit.end], hit.text)

    def test_below_threshold_refuses_without_excerpt(self) -> None:
        notes = Path(tempfile.mkdtemp())
        (notes / "demo.md").write_text("甲乙丙丁戊己庚辛。\n", encoding="utf-8")
        index = build_index(notes)
        decision = decide("甲乙丙丁戊己庚辛", index, notes, 1.0)
        text = format_decision(decision, notes)
        self.assertEqual(decision.reason, "low_score")
        self.assertIn(REFUSAL, text)
        self.assertNotIn("甲乙丙丁戊己庚辛", text)
        self.assertNotIn("摘录：", text)

    def test_both_zero_refuses_even_if_threshold_is_zero(self) -> None:
        notes = Path(tempfile.mkdtemp())
        (notes / "demo.md").write_text("甲乙丙丁。\n", encoding="utf-8")
        index = build_index(notes)
        decision = decide("狗狗宠物", index, notes, 0.0)
        text = format_decision(decision, notes)
        self.assertEqual(decision.reason, "both_zero")
        self.assertEqual(decision.top.fusion, 0.0)
        self.assertIn(REFUSAL, text)
        self.assertNotIn("甲乙丙丁", text)
        self.assertNotIn("摘录：", text)

    def test_empty_question_refuses_without_searching(self) -> None:
        notes = Path(tempfile.mkdtemp())
        (notes / "demo.md").write_text("甲乙丙丁。\n", encoding="utf-8")
        index = build_index(notes)
        for question in ("", "   ", "\n"):
            decision = decide(question, index, notes, 0.0)
            text = format_decision(decision, notes)
            self.assertEqual(decision.reason, "empty_question")
            self.assertIn(REFUSAL, text)
            self.assertNotIn("甲乙丙丁", text)

    def test_only_above_threshold_hits_are_shown(self) -> None:
        notes = Path(tempfile.mkdtemp())
        (notes / "strong.md").write_text("甲乙丙丁戊己庚辛。\n", encoding="utf-8")
        (notes / "weak.md").write_text("甲乙其他文字完全不同。\n", encoding="utf-8")
        index = build_index(notes)
        from keji_cite.hybrid import search

        hits = search(index, "甲乙丙丁戊己庚辛")
        threshold = (hits[0].fusion + hits[1].fusion) / 2.0
        decision = decide("甲乙丙丁戊己庚辛", index, notes, threshold)
        text = format_decision(decision, notes)
        self.assertEqual(decision.passed_count, 1)
        self.assertIn("甲乙丙丁戊己庚辛", text)
        self.assertNotIn("完全不同", text)

    def test_stale_file_is_not_quoted(self) -> None:
        notes = Path(tempfile.mkdtemp())
        path = notes / "demo.md"
        path.write_text("甲乙丙丁戊己庚辛。\n", encoding="utf-8")
        index = build_index(notes)
        path.write_text("这是改过的正文，不能拿旧索引当出处。\n", encoding="utf-8")
        decision = decide("甲乙丙丁戊己庚辛", index, notes, 0.0)
        text = format_decision(decision, notes)
        self.assertEqual(decision.reason, "bad_citation")
        self.assertIn(REFUSAL, text)
        self.assertNotIn("甲乙丙丁戊己庚辛", text)
        self.assertNotIn("改过的正文", text)

    def test_same_question_is_deterministic(self) -> None:
        notes = Path(tempfile.mkdtemp())
        (notes / "demo.md").write_text("甲乙丙丁戊己庚辛。\n", encoding="utf-8")
        index = build_index(notes)
        first = format_decision(decide("甲乙丙丁", index, notes, 0.0), notes)
        second = format_decision(decide("甲乙丙丁", index, notes, 0.0), notes)
        self.assertEqual(first, second)


class IndexTests(unittest.TestCase):
    def test_roundtrip_bytes_are_stable_and_skip_symlinks(self) -> None:
        root = Path(tempfile.mkdtemp())
        notes = root / "notes"
        notes.mkdir()
        outside = root / "outside.md"
        outside.write_text("外部机密甲乙丙丁。\n", encoding="utf-8")
        (notes / "real.md").write_text("甲乙丙丁在内部。\n", encoding="utf-8")
        (notes / "secret.md").symlink_to(outside)
        index = build_index(notes)
        blob = "\n".join(doc.text for doc in index.docs)
        self.assertIn("甲乙丙丁在内部", blob)
        self.assertNotIn("外部机密", blob)
        first = root / "a.json"
        second = root / "b.json"
        save_index(index, first)
        save_index(build_index(notes), second)
        self.assertEqual(first.read_bytes(), second.read_bytes())
        loaded = load_index(first)
        self.assertEqual(loaded.doc_count, index.doc_count)
        self.assertEqual(loaded.docs[0].text, index.docs[0].text)

    def test_missing_corrupt_and_escaped_index(self) -> None:
        root = Path(tempfile.mkdtemp())
        missing = root / "missing.json"
        with self.assertRaises(MissingIndexError):
            load_index(missing)
        broken = root / "broken.json"
        broken.write_text("{", encoding="utf-8")
        with self.assertRaises(CorruptIndexError):
            load_index(broken)
        notes = root / "notes"
        notes.mkdir()
        (notes / "demo.md").write_text("甲乙丙丁。\n", encoding="utf-8")
        good = root / "good.json"
        save_index(build_index(notes), good)
        payload = json.loads(good.read_text(encoding="utf-8"))
        payload["docs"][0]["path"] = "../outside.md"
        escaped = root / "escaped.json"
        escaped.write_text(json.dumps(payload), encoding="utf-8")
        with self.assertRaises(CorruptIndexError):
            load_index(escaped)
        payload = json.loads(good.read_text(encoding="utf-8"))
        payload["docs"][0]["tf"] = {"甲乙": True}
        flagged = root / "bool.json"
        flagged.write_text(json.dumps(payload), encoding="utf-8")
        with self.assertRaises(CorruptIndexError):
            load_index(flagged)
        payload = json.loads(good.read_text(encoding="utf-8"))
        payload["version"] = 2
        mismatch = root / "mismatch.json"
        mismatch.write_text(json.dumps(payload), encoding="utf-8")
        with self.assertRaises(CorruptIndexError) as caught:
            load_index(mismatch)
        self.assertIn("version", str(caught.exception))


class CommandTests(unittest.TestCase):
    def test_sources_do_not_import_network_or_eval(self) -> None:
        banned = ("socket", "urllib", "http.client", "requests", "openai", "httpx")
        for path in (ROOT / "keji_cite").glob("*.py"):
            text = path.read_text(encoding="utf-8")
            for name in banned:
                self.assertNotIn(f"import {name}", text)
                self.assertNotIn(f"from {name}", text)
            self.assertNotIn("eval(", text)

    def test_cli_empty_missing_and_corrupt(self) -> None:
        empty = run_cli(["ask", ""])
        self.assertEqual(empty.returncode, 0)
        self.assertIn("原因：empty_question", empty.stdout)
        self.assertIn(REFUSAL, empty.stdout)
        self.assertNotIn("摘录：", empty.stdout)

        missing = run_cli(["ask", "--index", str(ROOT / "不存在的索引.json"), "甲乙丙丁"])
        self.assertEqual(missing.returncode, 2)
        self.assertIn("还没有索引", missing.stderr)
        self.assertEqual(missing.stdout, "")

        broken = Path(tempfile.mkdtemp()) / "bad.json"
        broken.write_text("{", encoding="utf-8")
        corrupt = run_cli(["ask", "--index", str(broken), "甲乙丙丁"])
        self.assertEqual(corrupt.returncode, 2)
        self.assertIn("索引损坏", corrupt.stderr)
        self.assertNotIn(REFUSAL, corrupt.stdout)

        omitted = run_cli(["ask"])
        self.assertEqual(omitted.returncode, 2)


class RepoNotesTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls) -> None:
        cls.tmp = tempfile.TemporaryDirectory()
        cls.notes = repo_root() / "notes"
        cls.index_path = Path(cls.tmp.name) / "index.json"
        cls.index = build_index(cls.notes)
        save_index(cls.index, cls.index_path)
        cls.questions = load_questions(default_questions_path())
        cls.scored = score_questions(cls.index, cls.notes, cls.questions)
        cls.choice = choose_threshold(cls.scored)

    @classmethod
    def tearDownClass(cls) -> None:
        cls.tmp.cleanup()

    def test_threshold_file_matches_the_calibration_rule(self) -> None:
        saved = json.loads(default_threshold_path().read_text(encoding="utf-8"))
        self.assertEqual(saved, choice_payload(self.choice))
        self.assertFalse(saved["course_threshold_0_30_used"])
        holdout_ids = {
            question.id
            for question in self.questions
            if question.role not in ("calibrate_positive", "calibrate_negative")
        }
        self.assertTrue(holdout_ids)
        self.assertTrue(holdout_ids.isdisjoint(self.choice.used_ids))
        cited = [
            row
            for row in self.scored
            if row.question.role == "calibrate_positive" and would_cite(row, self.choice.threshold)
        ]
        self.assertTrue(cited, "校准正例里至少要有一条字面摘录，否则这条缝没有留下来")

    def test_record_matches_a_fresh_render(self) -> None:
        fresh = render_record(self.index, self.scored, self.choice)
        self.assertEqual(default_record_path().read_text(encoding="utf-8"), fresh)
        self.assertIn("不采用课程阈值 0.30", fresh)
        self.assertNotIn("obs-pool", saved_used_text(self.choice))

    def test_acceptance_transcript_and_citation(self) -> None:
        transcript = build_acceptance(self.index_path, self.notes)
        saved = (ROOT / "acceptance" / "cli-output.txt").read_text(encoding="utf-8")
        self.assertEqual(saved, transcript)
        sections = dict(parse_acceptance(transcript))
        hit = sections["python3 -X utf8 -m keji_cite ask 两路原始分不能直接相加"]
        self.assertTrue(hit.startswith("状态：命中\n"), hit)
        first_path, hits = parse_hits(hit)
        self.assertEqual(first_path, "notes/ai-09-hybrid-retrieval.md")
        self.assertIn("不是生成的答案", hit)
        for path, start, end, excerpt in hits:
            self.assert_excerpt_in_file(path, start, end, excerpt)
        refused = sections["python3 -X utf8 -m keji_cite ask 明天门票多少钱"]
        self.assertTrue(refused.startswith("状态：拒绝\n"), refused)
        self.assertIn("原因：low_score", refused)
        self.assertIn(REFUSAL, refused)
        self.assertNotIn("摘录：", refused)
        self.assertNotIn("出处：", refused)
        empty = sections['python3 -X utf8 -m keji_cite ask ""']
        self.assertIn("原因：empty_question", empty)
        self.assertIn(REFUSAL, empty)
        self.assertNotIn("摘录：", empty)
        both_zero = sections["python3 -X utf8 -m keji_cite ask 纳斯达克今天收盘点位"]
        self.assertIn("原因：both_zero", both_zero)
        self.assertIn("最高融合：0.000000", both_zero)
        self.assertIn(REFUSAL, both_zero)
        self.assertNotIn("摘录：", both_zero)
        observed = sections["python3 -X utf8 -m keji_cite ask 游泳池几点开放"]
        self.assertTrue(observed.startswith("状态：命中\n"), observed)
        self.assertIn("不是生成的答案", observed)
        _, observed_hits = parse_hits(observed)
        self.assertTrue(observed_hits)
        for path, start, end, excerpt in observed_hits:
            self.assert_excerpt_in_file(path, start, end, excerpt)

    def assert_excerpt_in_file(self, path: str, start: int, end: int, excerpt: str) -> None:
        file_text = (repo_root() / path).read_text(encoding="utf-8")
        window = "\n".join(file_text.splitlines()[start - 1 : end])
        self.assertIn(excerpt, window)
        self.assertIn(excerpt, file_text)

    def test_paths_stay_inside_notes(self) -> None:
        for doc in self.index.docs:
            self.assertNotIn("..", Path(doc.path).parts)
            self.assertNotIn("homework", doc.path)
            self.assertTrue((self.notes / doc.path).is_file())


def saved_used_text(choice) -> str:
    return " ".join(choice.used_ids)


def run_cli(args: list[str]) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        [sys.executable, "-X", "utf8", "-m", "keji_cite", *args],
        cwd=ROOT,
        capture_output=True,
        text=True,
        check=False,
    )


def build_acceptance(index_path: Path, notes: Path) -> str:
    parts = [
        "# 课记引 CLI 验收输出\n",
        "# 下面每段都是当次命令的标准输出，不是手写的期望。\n",
        "\n",
    ]
    for display, question in ACCEPTANCE_ASKS:
        result = run_cli(
            [
                "ask",
                "--notes",
                str(notes),
                "--index",
                str(index_path),
                "--threshold",
                str(default_threshold_path()),
                question,
            ]
        )
        parts.append(f"$ {display}\n")
        parts.append(result.stdout)
        if result.stderr:
            parts.append(result.stderr)
        if not result.stdout.endswith("\n"):
            parts.append("\n")
        parts.append("\n")
    return "".join(parts)


def parse_acceptance(transcript: str) -> list[tuple[str, str]]:
    sections: list[tuple[str, str]] = []
    current: str | None = None
    buffer: list[str] = []
    for line in transcript.splitlines(keepends=True):
        if line.startswith("$ "):
            if current is not None:
                sections.append((current, "".join(buffer).strip("\n") + "\n"))
            current = line[2:].strip()
            buffer = []
        elif current is not None:
            buffer.append(line)
    if current is not None:
        sections.append((current, "".join(buffer).strip("\n") + "\n"))
    return sections


def parse_hits(stdout: str) -> tuple[str, list[tuple[str, int, int, str]]]:
    """返回第一条出处路径，以及每条摘录（路径、起行、止行、正文）。"""
    hits: list[tuple[str, int, int, str]] = []
    source: str | None = None
    excerpt: list[str] = []
    seen = False
    for line in stdout.splitlines():
        if line.startswith("出处："):
            if seen and source is not None:
                hits.append((*_split_source(source), "\n".join(excerpt)))
            source = line.split("：", 1)[1]
            excerpt = []
            seen = False
        elif line == "摘录：":
            seen = True
        elif seen and line.startswith("> "):
            excerpt.append(line[2:])
        elif seen and source is not None:
            hits.append((*_split_source(source), "\n".join(excerpt)))
            source = None
            excerpt = []
            seen = False
    if seen and source is not None:
        hits.append((*_split_source(source), "\n".join(excerpt)))
    if not hits:
        raise AssertionError(stdout)
    return hits[0][0], hits


def _split_source(source: str) -> tuple[str, int, int]:
    path, rows = source.rsplit(":", 1)
    start_s, end_s = rows.split("-")
    return path, int(start_s), int(end_s)


if __name__ == "__main__":
    unittest.main()
