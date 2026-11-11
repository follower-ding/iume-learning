# 从一次返回的 JSON 里取出完整回复。
def read_message_content(payload):
    return payload["choices"][0]["message"]["content"]


payload = {
    "choices": [
        {"message": {"role": "assistant", "content": "你好，学员"}}
    ]
}
print(read_message_content(payload))
