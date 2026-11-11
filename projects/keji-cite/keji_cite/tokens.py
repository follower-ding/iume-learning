"""字符二元组。中文不按空格切词。

第 03 课笔记写过：单独 fit「今晚有乐队演出吗」得到 7 维。
这 8 个字的相邻二元组正好 7 个，所以这里用重叠的二字片段。
标点和空白丢掉，是写代码前定下的规则：笔记是 Markdown，标点二元组不是课里说的字面证据。
看到分数之后不再按某一问增删字符。
"""

TOKENIZER_NAME = "alnum_char_bigram_v1"


def compact(text: str) -> str:
    """只留下字母、数字和汉字。Python 里汉字也算 isalnum。"""
    return "".join(ch for ch in text if ch.isalnum())


def bigrams(text: str) -> list[str]:
    """相邻两个字组成一个二元组。不足两个字就没有词。"""
    raw = compact(text)
    if len(raw) < 2:
        return []
    return [raw[i : i + 2] for i in range(len(raw) - 1)]


def count_bigrams(text: str) -> dict[str, int]:
    counts: dict[str, int] = {}
    for token in bigrams(text):
        counts[token] = counts.get(token, 0) + 1
    return counts
