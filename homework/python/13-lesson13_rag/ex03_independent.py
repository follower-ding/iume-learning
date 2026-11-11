# 在两段笔记里选字符计数更接近的一段。空列表没有可选片段。
def embed_stub(text, dimensions):
    vector = []
    index = 0
    while index < dimensions:
        vector.append(0)
        index = index + 1
    for character in text:
        bucket = ord(character) % dimensions
        vector[bucket] = vector[bucket] + 1
    return vector


def dot(left, right):
    total = 0
    index = 0
    while index < len(left):
        total = total + left[index] * right[index]
        index = index + 1
    return total


def cosine(left, right):
    left_size = dot(left, left) ** 0.5
    right_size = dot(right, right) ** 0.5
    if left_size == 0 or right_size == 0:
        return 0.0
    return dot(left, right) / (left_size * right_size)


def best_chunk(question, chunks, dimensions):
    if len(chunks) == 0:
        return None
    question_vector = embed_stub(question, dimensions)
    best_index = 0
    best_score = cosine(question_vector, embed_stub(chunks[0], dimensions))
    index = 1
    while index < len(chunks):
        score = cosine(question_vector, embed_stub(chunks[index], dimensions))
        if score > best_score:
            best_score = score
            best_index = index
        index = index + 1
    return chunks[best_index]


chunks = ["虚拟环境把依赖装在项目里。", "超时后可以按次数重试。"]
print(best_chunk("超时重试", chunks, 16))
print(best_chunk("超时重试", [], 16))
