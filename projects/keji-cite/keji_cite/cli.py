"""命令行入口。

在项目目录运行：

    python3 -X utf8 -m keji_cite index
    python3 -X utf8 -m keji_cite calibrate
    python3 -X utf8 -m keji_cite ask "问句"

解释器读到 `-m keji_cite`，会执行本包的 `__main__.py`，再进到这里。
"""

import argparse
import sys
from pathlib import Path

from keji_cite.calibrate import (
    choice_payload,
    choose_threshold,
    load_questions,
    render_record,
    score_questions,
)
from keji_cite.cite import Decision, decide, format_decision, load_threshold
from keji_cite.errors import KejiError
from keji_cite.paths import (
    default_index_path,
    default_notes_dir,
    default_questions_path,
    default_record_path,
    default_threshold_path,
)
from keji_cite.store import build_index, list_markdown, load_index, save_index


def main(argv: list[str] | None = None) -> int:
    _utf8_stdio()
    parser = build_parser()
    args = parser.parse_args(argv)
    try:
        return args.func(args)
    except KejiError as exc:
        print(f"错误：{exc}", file=sys.stderr)
        return 2


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="keji_cite",
        description="课记引：在本仓库 notes/ 上做字面混合检索。只输出核对过的摘录，或固定拒绝。不调用模型。",
    )
    sub = parser.add_subparsers(dest="command", required=True)

    index_cmd = sub.add_parser("index", help="读取 notes/，写出本地索引")
    _add_notes(index_cmd)
    _add_index(index_cmd)
    index_cmd.set_defaults(func=cmd_index)

    ask_cmd = sub.add_parser("ask", help="提问。过线就摘录，否则固定拒绝")
    _add_notes(ask_cmd)
    _add_index(ask_cmd)
    ask_cmd.add_argument(
        "--threshold",
        type=Path,
        default=None,
        help="阈值 JSON，默认 calibration/threshold.json",
    )
    ask_cmd.add_argument("question", nargs="?", default=None, help="问句。空字符串会固定拒绝")
    ask_cmd.set_defaults(func=cmd_ask)

    cal_cmd = sub.add_parser("calibrate", help="只根据校准集重写阈值和记录")
    _add_notes(cal_cmd)
    _add_index(cal_cmd)
    cal_cmd.add_argument("--questions", type=Path, default=None)
    cal_cmd.add_argument("--threshold-out", type=Path, default=None)
    cal_cmd.add_argument("--record-out", type=Path, default=None)
    cal_cmd.set_defaults(func=cmd_calibrate)
    return parser


def cmd_index(args: argparse.Namespace) -> int:
    notes_dir = _notes(args)
    index = build_index(notes_dir)
    destination = _index_path(args)
    save_index(index, destination)
    print(f"笔记目录：{_relative_or_abs(notes_dir)}")
    print(f"笔记文件：{len(_markdown_count(notes_dir))}")
    print(f"切块：{index.doc_count}")
    print(f"词表维度：{index.dimension}")
    print(f"索引：{_relative_or_abs(destination)}")
    print("索引只在本机使用，不调用模型，也不要提交。")
    return 0


def cmd_ask(args: argparse.Namespace) -> int:
    if args.question is None:
        print("错误：缺少问句。空字符串请写成：python3 -m keji_cite ask \"\"", file=sys.stderr)
        return 2
    # 空问句没有字可以检索，也不去读索引。
    if str(args.question).strip() == "":
        decision = Decision("refuse", "empty_question", None, (), 0, None)
        sys.stdout.write(format_decision(decision, Path("notes")))
        return 0
    notes_dir = _notes(args)
    threshold_path = args.threshold if args.threshold is not None else default_threshold_path()
    index = load_index(_index_path(args))
    threshold = load_threshold(threshold_path)
    decision = decide(str(args.question), index, notes_dir, threshold)
    sys.stdout.write(format_decision(decision, notes_dir))
    return 0


def cmd_calibrate(args: argparse.Namespace) -> int:
    notes_dir = _notes(args)
    index = load_index(_index_path(args))
    questions_path = args.questions if args.questions is not None else default_questions_path()
    questions = load_questions(questions_path)
    scored = score_questions(index, notes_dir, questions)
    choice = choose_threshold(scored)
    record = render_record(index, scored, choice)
    threshold_path = args.threshold_out if args.threshold_out is not None else default_threshold_path()
    record_path = args.record_out if args.record_out is not None else default_record_path()
    _write(threshold_path, _dump_json(choice_payload(choice)))
    _write(record_path, record)
    sys.stdout.write(record)
    return 0


def _markdown_count(notes_dir: Path) -> list[Path]:
    return list_markdown(notes_dir)


def _notes(args: argparse.Namespace) -> Path:
    return args.notes if args.notes is not None else default_notes_dir()


def _index_path(args: argparse.Namespace) -> Path:
    return args.index if args.index is not None else default_index_path()


def _add_notes(parser: argparse.ArgumentParser) -> None:
    parser.add_argument("--notes", type=Path, default=None, help="笔记目录，默认仓库 notes/")


def _add_index(parser: argparse.ArgumentParser) -> None:
    parser.add_argument("--index", type=Path, default=None, help="索引文件，默认 .index/index.json")


def _relative_or_abs(path: Path) -> str:
    resolved = path.resolve()
    try:
        from keji_cite.paths import repo_root

        return resolved.relative_to(repo_root()).as_posix()
    except (OSError, ValueError):
        return str(path)


def _dump_json(payload: dict[str, object]) -> str:
    import json

    return json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True) + "\n"


def _write(path: Path, text: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text, encoding="utf-8")


def _utf8_stdio() -> None:
    for stream in (sys.stdout, sys.stderr):
        reconfigure = getattr(stream, "reconfigure", None)
        if reconfigure is None:
            continue
        try:
            reconfigure(encoding="utf-8")
        except (OSError, ValueError):
            continue


if __name__ == "__main__":
    raise SystemExit(main())
