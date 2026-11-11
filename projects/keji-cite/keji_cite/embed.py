"""字面 TF-IDF 和余弦。不是神经网络嵌入。

文档先 fit 出一套 idf。查询只能使用这套 idf，不能自己再 fit 一次。
权重 = 次数 × idf，再除以向量长度。两边都归一化之后，余弦就是点积。
全 0 向量的余弦定义为 0，不当成相关。
"""

import math

from keji_cite.tokens import count_bigrams


def smooth_idf(df: int, n_docs: int) -> float:
    """idf = ln((1+N)/(1+df)) + 1。常见词不会变成 0，生僻词更大。"""
    return math.log((1.0 + n_docs) / (1.0 + df)) + 1.0


def fit_idf(texts: list[str]) -> dict[str, float]:
    """对一批文档 fit 一次。df 是「有这个二元组的文档数」，不是出现次数。"""
    if not texts:
        return {}
    df: dict[str, int] = {}
    for text in texts:
        for term in count_bigrams(text):
            df[term] = df.get(term, 0) + 1
    n_docs = len(texts)
    return {term: smooth_idf(freq, n_docs) for term, freq in df.items()}


def l2_normalize(weights: dict[str, float]) -> dict[str, float]:
    square = 0.0
    for value in weights.values():
        square += value * value
    if square <= 0.0:
        return {}
    norm = math.sqrt(square)
    return {term: value / norm for term, value in weights.items()}


def vector_from_tf(tf: dict[str, int], idf: dict[str, float]) -> dict[str, float]:
    """只用 fit 时见过的二元组。查询里的新二元组直接丢掉。"""
    weights: dict[str, float] = {}
    for term, freq in tf.items():
        if not isinstance(freq, int) or isinstance(freq, bool) or freq <= 0:
            continue
        weight = idf.get(term)
        if weight is None:
            continue
        weights[term] = freq * weight
    return l2_normalize(weights)


def embed_text(text: str, idf: dict[str, float]) -> dict[str, float]:
    return vector_from_tf(count_bigrams(text), idf)


def cosine(left: dict[str, float], right: dict[str, float]) -> float:
    """非负权重下余弦在 0 和 1 之间。空向量返回 0。"""
    if not left or not right:
        return 0.0
    if len(left) > len(right):
        left, right = right, left
    dot = 0.0
    for term, value in left.items():
        other = right.get(term)
        if other:
            dot += value * other
    if dot <= 0.0:
        return 0.0
    if dot >= 1.0:
        return 1.0
    return dot
