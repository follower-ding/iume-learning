"""按第 03 课的方式切笔记：空行分段，短段整留，长段滑窗。

size 和 overlap 用课内那组 80 / 20。写实现之前就定下，不按问句再改。
overlap 必须小于 size，否则窗口会原地打转。
"""

from dataclasses import dataclass

# 第 03 课练习用的窗口。换窗口会换分数，所以和阈值一起固定。
DEFAULT_SIZE = 80
DEFAULT_OVERLAP = 20

_BREAKS = "。！？；"


@dataclass(frozen=True)
class Chunk:
    """原文里的一片，text 必须等于 source[start:end]。"""

    text: str
    start: int
    end: int


def chunk_text(text: str, size: int = DEFAULT_SIZE, overlap: int = DEFAULT_OVERLAP) -> list[Chunk]:
    if size < 1:
        raise ValueError("size 必须 >= 1")
    if overlap < 0 or overlap >= size:
        raise ValueError("overlap 必须小于 size")

    chunks: list[Chunk] = []
    for para_start, para_end in _paragraph_spans(text):
        start, end = _strip_span(text, para_start, para_end)
        if start >= end:
            continue
        chunks.extend(_windows(text, start, end, size, overlap))
    return chunks


def _paragraph_spans(text: str) -> list[tuple[int, int]]:
    """按空行切开。空行是只含空白的一行。返回原文中的半开区间。"""
    spans: list[tuple[int, int]] = []
    start: int | None = None
    line_start = 0
    length = len(text)
    while line_start <= length:
        newline = text.find("\n", line_start)
        if newline == -1:
            line_end = length
        else:
            line_end = newline
        blank = text[line_start:line_end].strip() == ""
        if blank:
            if start is not None:
                spans.append((start, line_start))
                start = None
        elif start is None:
            start = line_start
        if newline == -1:
            break
        line_start = newline + 1
    if start is not None:
        spans.append((start, length))
    return spans


def _strip_span(text: str, start: int, end: int) -> tuple[int, int]:
    while start < end and text[start].isspace():
        start += 1
    while end > start and text[end - 1].isspace():
        end -= 1
    return start, end


def _windows(text: str, start: int, end: int, size: int, overlap: int) -> list[Chunk]:
    if end - start <= size:
        return [Chunk(text[start:end], start, end)]

    chunks: list[Chunk] = []
    cursor = start
    # 最长也只是每次前进 1 个字符，用这个上限挡住死循环。
    guard = 0
    limit = (end - start) + 2
    while cursor < end:
        guard += 1
        if guard > limit:
            raise RuntimeError("切块没有前进")
        window_end = min(cursor + size, end)
        if window_end < end:
            half = cursor + (window_end - cursor) // 2
            cut = _cut_in_second_half(text, half, window_end)
            if cut is not None and cut > cursor:
                window_end = cut
        chunks.append(Chunk(text[cursor:window_end], cursor, window_end))
        if window_end >= end:
            break
        next_cursor = window_end - overlap
        if next_cursor <= cursor:
            next_cursor = cursor + 1
        cursor = next_cursor
    return chunks


def _cut_in_second_half(text: str, half: int, window_end: int) -> int | None:
    """窗口后半里，靠右的句读优先，让这一块尽量停在句号上。"""
    index = window_end - 1
    while index >= half:
        if text[index] in _BREAKS:
            return index + 1
        index -= 1
    return None
