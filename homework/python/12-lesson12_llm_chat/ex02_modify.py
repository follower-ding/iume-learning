# 把多行 data 事件拼成一条回复，遇到 [DONE] 停止。
import json


def read_stream_text(lines):
    pieces = []
    for line in lines:
        text = line.strip()
        if text == "" or not text.startswith("data:"):
            continue
        data = text[5:].strip()
        if data == "[DONE]":
            break
        payload = json.loads(data)
        delta = payload["choices"][0]["delta"]
        if "content" in delta:
            pieces.append(delta["content"])
    result = ""
    for piece in pieces:
        result = result + piece
    return result


lines = [
    'data: {"choices":[{"delta":{"content":"你好"}}]}',
    "",
    'data: {"choices":[{"delta":{"content":"，学员"}}]}',
    "data: [DONE]",
]
print(read_stream_text(lines))
