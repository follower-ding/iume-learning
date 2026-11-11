# 按固定字符数切分。最后一段可以比 size 短。
def chunk_text(text, size):
    if size < 1:
        return []
    chunks = []
    start = 0
    while start < len(text):
        chunks.append(text[start:start + size])
        start = start + size
    return chunks


for part in chunk_text("学习笔记", 2):
    print(part)
