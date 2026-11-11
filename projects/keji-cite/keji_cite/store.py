"""索引：每条记录同时留下编号、原文、来源和词频。

读入内存时用保存的 idf 把词频变成向量，查询和文档共用这一套 idf。
磁盘上的索引不提交。参数和当前代码不一致就当损坏，要求重建。
"""

import json
import os
from dataclasses import dataclass
from pathlib import Path

from keji_cite.bm25 import B, K1, fit_bm25_idf
from keji_cite.chunk import DEFAULT_OVERLAP, DEFAULT_SIZE, chunk_text
from keji_cite.embed import smooth_idf, vector_from_tf
from keji_cite.errors import CorruptIndexError, MissingIndexError, NotesError
from keji_cite.formula import SPARSE_WEIGHT, SQUASH_K, VECTOR_WEIGHT
from keji_cite.tokens import TOKENIZER_NAME, count_bigrams

INDEX_VERSION = 1


@dataclass
class Doc:
    id: str
    path: str
    start: int
    end: int
    start_line: int
    end_line: int
    text: str
    tf: dict[str, int]
    dl: int
    vector: dict[str, float]


@dataclass
class Index:
    docs: list[Doc]
    idf: dict[str, float]
    bm25_idf: dict[str, float]
    avgdl: float

    @property
    def doc_count(self) -> int:
        return len(self.docs)

    @property
    def dimension(self) -> int:
        return len(self.idf)

    def embed_query(self, tf: dict[str, int]) -> dict[str, float]:
        return vector_from_tf(tf, self.idf)


def build_index(notes_dir: Path, size: int = DEFAULT_SIZE, overlap: int = DEFAULT_OVERLAP) -> Index:
    notes_dir = notes_dir.resolve()
    if not notes_dir.is_dir():
        raise NotesError(f"笔记目录不存在：{notes_dir}")
    files = list_markdown(notes_dir)
    if not files:
        raise NotesError(f"笔记目录里没有 .md 文件：{notes_dir}")

    pending: list[Doc] = []
    for path in files:
        relative = path.relative_to(notes_dir).as_posix()
        file_text = _read_note(path)
        for number, chunk in enumerate(chunk_text(file_text, size=size, overlap=overlap)):
            if file_text[chunk.start : chunk.end] != chunk.text:
                raise NotesError(f"切块和原文对不上：{relative}")
            tf = count_bigrams(chunk.text)
            pending.append(
                Doc(
                    id=f"{relative}#c{number:04d}",
                    path=relative,
                    start=chunk.start,
                    end=chunk.end,
                    start_line=_line_at(file_text, chunk.start),
                    end_line=_line_at(file_text, chunk.end - 1),
                    text=chunk.text,
                    tf=tf,
                    dl=sum(tf.values()),
                    vector={},
                )
            )
    if not pending:
        raise NotesError("笔记切完之后没有正文")

    df: dict[str, int] = {}
    for doc in pending:
        for term in doc.tf:
            df[term] = df.get(term, 0) + 1
    n_docs = len(pending)
    idf = {term: smooth_idf(freq, n_docs) for term, freq in df.items()}
    bm25_idf = fit_bm25_idf(df, n_docs)
    total_dl = 0
    for doc in pending:
        doc.vector = vector_from_tf(doc.tf, idf)
        total_dl += doc.dl
    avgdl = total_dl / n_docs
    return Index(docs=pending, idf=idf, bm25_idf=bm25_idf, avgdl=avgdl)


def list_markdown(notes_dir: Path) -> list[Path]:
    """不跟随符号链接，避免索引走到 notes/ 外面。"""
    found: list[Path] = []
    for dirpath, dirnames, filenames in os.walk(notes_dir, followlinks=False):
        dirnames.sort()
        for name in sorted(filenames):
            if not name.endswith(".md"):
                continue
            path = Path(dirpath) / name
            if path.is_symlink():
                continue
            found.append(path)
    found.sort(key=lambda path: path.relative_to(notes_dir).as_posix())
    return found


def save_index(index: Index, path: Path) -> None:
    payload = {
        "version": INDEX_VERSION,
        "tokenizer": TOKENIZER_NAME,
        "chunk_size": DEFAULT_SIZE,
        "chunk_overlap": DEFAULT_OVERLAP,
        "k1": K1,
        "b": B,
        "squash_k": SQUASH_K,
        "vector_weight": VECTOR_WEIGHT,
        "sparse_weight": SPARSE_WEIGHT,
        "doc_count": index.doc_count,
        "dimension": index.dimension,
        "avgdl": index.avgdl,
        "idf": index.idf,
        "bm25_idf": index.bm25_idf,
        "docs": [_doc_to_json(doc) for doc in index.docs],
    }
    _atomic_write(path, json.dumps(payload, ensure_ascii=False, indent=2, sort_keys=True) + "\n")


def load_index(path: Path) -> Index:
    if not path.is_file():
        raise MissingIndexError("还没有索引。请先运行：python3 -m keji_cite index")
    try:
        data = json.loads(path.read_text(encoding="utf-8"))
    except (OSError, UnicodeError, json.JSONDecodeError) as exc:
        raise CorruptIndexError("索引损坏：文件不是合法的 UTF-8 JSON") from exc
    if not isinstance(data, dict):
        raise CorruptIndexError("索引损坏：顶层不是对象")
    _require_params(data)
    docs_raw = data.get("docs")
    idf = _number_map(data.get("idf"), "idf")
    bm25_idf = _number_map(data.get("bm25_idf"), "bm25_idf")
    if set(idf) != set(bm25_idf):
        raise CorruptIndexError("索引损坏：两套 idf 的词表不一致")
    if not isinstance(docs_raw, list) or not docs_raw:
        raise CorruptIndexError("索引损坏：没有切块")
    if data.get("doc_count") != len(docs_raw) or data.get("dimension") != len(idf):
        raise CorruptIndexError("索引损坏：文档数或维度和内容对不上")
    docs: list[Doc] = []
    seen: set[str] = set()
    total_dl = 0
    for item in docs_raw:
        doc = _doc_from_json(item, idf)
        if doc.id in seen:
            raise CorruptIndexError(f"索引损坏：重复编号 {doc.id}")
        seen.add(doc.id)
        docs.append(doc)
        total_dl += doc.dl
    avgdl = data.get("avgdl")
    if not _is_number(avgdl):
        raise CorruptIndexError("索引损坏：avgdl 不是数字")
    if abs(float(avgdl) - (total_dl / len(docs))) > 1e-9:
        raise CorruptIndexError("索引损坏：平均文档长度和词频对不上")
    return Index(docs=docs, idf=idf, bm25_idf=bm25_idf, avgdl=float(avgdl))


def display_path(notes_dir: Path, relative: str) -> str:
    return f"{notes_dir.name}/{Path(relative).as_posix()}"


def _doc_to_json(doc: Doc) -> dict[str, object]:
    return {
        "id": doc.id,
        "path": doc.path,
        "start": doc.start,
        "end": doc.end,
        "start_line": doc.start_line,
        "end_line": doc.end_line,
        "text": doc.text,
        "tf": doc.tf,
        "dl": doc.dl,
    }


def _doc_from_json(item: object, idf: dict[str, float]) -> Doc:
    if not isinstance(item, dict):
        raise CorruptIndexError("索引损坏：切块不是对象")
    path = item.get("path")
    doc_id = item.get("id")
    text = item.get("text")
    if not isinstance(path, str) or not _safe_relative(path):
        raise CorruptIndexError("索引损坏：切块路径不在笔记目录内")
    if not isinstance(doc_id, str) or not doc_id:
        raise CorruptIndexError("索引损坏：切块缺少编号")
    if not isinstance(text, str) or text == "":
        raise CorruptIndexError("索引损坏：切块缺少原文")
    start = item.get("start")
    end = item.get("end")
    start_line = item.get("start_line")
    end_line = item.get("end_line")
    dl = item.get("dl")
    if not all(_is_int(value) for value in (start, end, start_line, end_line, dl)):
        raise CorruptIndexError("索引损坏：切块的位置或长度不是整数")
    if start < 0 or end <= start or start_line < 1 or end_line < start_line:
        raise CorruptIndexError("索引损坏：切块的区间不合法")
    if end - start != len(text):
        raise CorruptIndexError("索引损坏：原文长度和区间对不上")
    tf = _count_map(item.get("tf"))
    if sum(tf.values()) != dl:
        raise CorruptIndexError("索引损坏：词频和文档长度对不上")
    if any(term not in idf for term in tf):
        raise CorruptIndexError("索引损坏：词频里有词表以外的二元组")
    return Doc(
        id=doc_id,
        path=path,
        start=int(start),
        end=int(end),
        start_line=int(start_line),
        end_line=int(end_line),
        text=text,
        tf=tf,
        dl=int(dl),
        vector=vector_from_tf(tf, idf),
    )


def _require_params(data: dict[str, object]) -> None:
    expected = {
        "version": INDEX_VERSION,
        "tokenizer": TOKENIZER_NAME,
        "chunk_size": DEFAULT_SIZE,
        "chunk_overlap": DEFAULT_OVERLAP,
        "k1": K1,
        "b": B,
        "squash_k": SQUASH_K,
        "vector_weight": VECTOR_WEIGHT,
        "sparse_weight": SPARSE_WEIGHT,
    }
    for key, value in expected.items():
        if data.get(key) != value:
            raise CorruptIndexError(f"索引损坏：字段 {key} 和当前代码不一致，请重新运行 index")


def _number_map(value: object, name: str) -> dict[str, float]:
    if not isinstance(value, dict):
        raise CorruptIndexError(f"索引损坏：{name} 不是词表")
    result: dict[str, float] = {}
    for term, weight in value.items():
        if not isinstance(term, str) or not _is_number(weight):
            raise CorruptIndexError(f"索引损坏：{name} 里有非法项")
        result[term] = float(weight)
    return result


def _count_map(value: object) -> dict[str, int]:
    if not isinstance(value, dict):
        raise CorruptIndexError("索引损坏：词频不是对象")
    result: dict[str, int] = {}
    for term, freq in value.items():
        if not isinstance(term, str) or not _is_int(freq) or freq <= 0:
            raise CorruptIndexError("索引损坏：词频必须是正整数")
        result[term] = int(freq)
    return result


def _safe_relative(relative: str) -> bool:
    path = Path(relative)
    return not path.is_absolute() and ".." not in path.parts and not relative.startswith(("/", "\\"))


def _read_note(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8")
    except (OSError, UnicodeError) as exc:
        raise NotesError(f"读不了笔记：{path.name}") from exc


def _line_at(text: str, offset: int) -> int:
    if offset < 0:
        offset = 0
    if offset > len(text):
        offset = len(text)
    return text.count("\n", 0, offset) + 1


def _atomic_write(path: Path, data: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    temporary = path.with_name(path.name + ".tmp")
    temporary.write_text(data, encoding="utf-8")
    os.replace(temporary, path)


def _is_int(value: object) -> bool:
    return isinstance(value, int) and not isinstance(value, bool)


def _is_number(value: object) -> bool:
    return isinstance(value, (int, float)) and not isinstance(value, bool)
