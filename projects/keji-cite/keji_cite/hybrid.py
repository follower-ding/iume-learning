"""两路融合。先压缩 BM25，再和余弦等权相加。

squash = raw / (raw + 2.5)
融合 = 0.5 × 向量 + 0.5 × squash

不把两路原始分直接相加，不除以这一问的最高分，也不改用 max。
两路都是 0 时，压缩还是 0，融合也是 0。
"""

from dataclasses import dataclass

from keji_cite.bm25 import score_bm25
from keji_cite.embed import cosine
from keji_cite.formula import fuse
from keji_cite.store import Index
from keji_cite.tokens import bigrams, count_bigrams


@dataclass(frozen=True)
class Hit:
    doc_id: str
    path: str
    start: int
    end: int
    start_line: int
    end_line: int
    text: str
    vector_score: float
    sparse_raw: float
    fusion: float

    @property
    def both_zero(self) -> bool:
        return self.vector_score == 0.0 and self.sparse_raw == 0.0


def search(index: Index, question: str) -> list[Hit]:
    """给每个切块打融合分，按分数从高到低，同分按编号字母序。"""
    query_tokens = bigrams(question)
    query_vector = index.embed_query(count_bigrams(question))
    hits: list[Hit] = []
    for doc in index.docs:
        vector_score = cosine(query_vector, doc.vector)
        sparse_raw = score_bm25(query_tokens, doc.tf, doc.dl, index.avgdl, index.bm25_idf)
        fusion = fuse(vector_score, sparse_raw)
        hits.append(
            Hit(
                doc_id=doc.id,
                path=doc.path,
                start=doc.start,
                end=doc.end,
                start_line=doc.start_line,
                end_line=doc.end_line,
                text=doc.text,
                vector_score=vector_score,
                sparse_raw=sparse_raw,
                fusion=fusion,
            )
        )
    hits.sort(key=lambda hit: (-hit.fusion, hit.doc_id))
    return hits
