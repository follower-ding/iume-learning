"""融合用的常数。索引文件会把它们记下来，和代码对不上就要求重建。

这些数在看笔记分数之前就写死：
squash 的 2.5 和第 09 课一样；两路各 0.5。
不从问句、也不从课程阈值 0.30 反推它们。
"""

SQUASH_K = 2.5
VECTOR_WEIGHT = 0.5
SPARSE_WEIGHT = 0.5


def squash(raw: float) -> float:
    """raw / (raw + 2.5)。0 还是 0，再大也到不了 1。"""
    if raw <= 0.0:
        return 0.0
    return raw / (raw + SQUASH_K)


def fuse(vector_score: float, sparse_raw: float) -> float:
    """0.5 × 余弦 + 0.5 × 压缩后的关键词分。不取 max，也不除以本问最高分。"""
    return VECTOR_WEIGHT * vector_score + SPARSE_WEIGHT * squash(sparse_raw)
