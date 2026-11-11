"""BM25 关键词分。k1 和 b 是预先定死的常用值，不按问句改。

k1 控制词频加到后面还升多少，b 控制长文档要不要压一点。
idf 用 ln(1 + (N-df+0.5)/(df+0.5))，避免常见二元组出现负分。
"""

import math

# 写校准之前就固定。缝若不好看，回去看问句和哪一路变了，不改这两个数。
K1 = 1.5
B = 0.75


def bm25_idf(df: int, n_docs: int) -> float:
    return math.log(1.0 + (n_docs - df + 0.5) / (df + 0.5))


def fit_bm25_idf(df: dict[str, int], n_docs: int) -> dict[str, float]:
    return {term: bm25_idf(freq, n_docs) for term, freq in df.items()}


def score_bm25(
    query_tokens: list[str],
    tf: dict[str, int],
    dl: int,
    avgdl: float,
    idf: dict[str, float],
) -> float:
    """问句里每个二元组加一次。问句里重复出现，就加多次。对不上的词加 0。"""
    if not query_tokens or avgdl <= 0.0 or dl <= 0:
        return 0.0
    length_norm = K1 * (1.0 - B + B * dl / avgdl)
    total = 0.0
    for token in query_tokens:
        weight = idf.get(token)
        if weight is None:
            continue
        freq = tf.get(token, 0)
        if freq <= 0:
            continue
        total += weight * (freq * (K1 + 1.0)) / (freq + length_norm)
    return total
