# 本地向量只按字符计数。它用来走通切分、比较和拼提示，不表示理解词义。
import httpx


def chunk_text(text, size):
    if size < 1:
        return []
    chunks = []
    start = 0
    while start < len(text):
        chunks.append(text[start:start + size])
        start = start + size
    return chunks


def split_sentences(text):
    parts = []
    current = ""
    for character in text:
        current = current + character
        if character == "。":
            parts.append(current)
            current = ""
    if current != "":
        parts.append(current)
    return parts


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


def build_prompt(question, chunk):
    if chunk is None:
        return "没有可用笔记。问题：" + question
    return "根据笔记回答。笔记：" + chunk + "问题：" + question


def parse_embedding_payload(payload):
    vectors = []
    for item in payload["data"]:
        vectors.append(item["embedding"])
    return vectors


def request_embeddings(client, url, api_key, model, texts):
    # 没有密钥就不要发请求。有密钥时也只解析 data[].embedding 这一种形状。
    if api_key.strip() == "":
        return "缺少密钥"
    payload = {"model": model, "input": texts}
    headers = {"Authorization": "Bearer " + api_key}
    response = client.post(url, json=payload, headers=headers)
    return parse_embedding_payload(response.json())


if __name__ == "__main__":
    note = "虚拟环境把依赖装在项目里。超时后可以按次数重试。"
    question = "依赖装在哪里"
    dimensions = 16
    chunks = split_sentences(note)
    hit = best_chunk(question, chunks, dimensions)
    question_vector = embed_stub(question, dimensions)
    first_score = cosine(question_vector, embed_stub(chunks[0], dimensions))
    second_score = cosine(question_vector, embed_stub(chunks[1], dimensions))
    print("片段数：" + str(len(chunks)))
    print("命中：" + hit)
    print("第一段相似度：" + f"{first_score:.2f}")
    print("第二段相似度：" + f"{second_score:.2f}")
    print("提示：" + build_prompt(question, hit))
