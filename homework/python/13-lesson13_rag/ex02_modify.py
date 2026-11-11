# 同一段文字得到同一条向量。不同文字在这个例子里落到不同格子。
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


print(embed_stub("ab", 4) == embed_stub("ab", 4))
print(embed_stub("ab", 4) == embed_stub("cd", 4))
