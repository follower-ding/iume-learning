# 非流式一次读取完整回复，流式按 data 行拼接。默认只用本地剧本，不读取密钥。
import json

import httpx


def build_chat_payload(model, user_text, stream):
    return {
        "model": model,
        "messages": [
            {"role": "user", "content": user_text}
        ],
        "stream": stream,
    }


def read_message_content(payload):
    return payload["choices"][0]["message"]["content"]


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


def require_api_key(api_key):
    if api_key.strip() == "":
        return "缺少密钥"
    return "已配置"


def load_env_file(text):
    values = {}
    for line in text.splitlines():
        stripped = line.strip()
        if stripped == "" or stripped.startswith("#"):
            continue
        if "=" not in stripped:
            continue
        name, value = stripped.split("=", 1)
        name = name.strip()
        if name == "":
            continue
        values[name] = value.strip()
    return values


def env_value(values, name):
    if name in values:
        return values[name]
    return ""


def post_chat(client, url, api_key, payload):
    headers = {"Authorization": "Bearer " + api_key}
    return client.post(url, json=payload, headers=headers)


def chat_with_config(env_text, client, user_text):
    # 密钥、地址或模型名为空时直接返回说明，不调用 client。
    values = load_env_file(env_text)
    api_key = env_value(values, "LLM_API_KEY")
    if api_key == "":
        return "缺少密钥"
    url = env_value(values, "LLM_BASE_URL")
    model = env_value(values, "LLM_MODEL")
    if url == "" or model == "":
        return "缺少地址或模型名"
    payload = build_chat_payload(model, user_text, False)
    response = post_chat(client, url, api_key, payload)
    return read_message_content(response.json())


def run_demo():
    lines = []
    lines.append("密钥来源：本地模拟，不读取密钥")

    def handler(request):
        if request.url.path.endswith("/stream"):
            body = (
                'data: {"choices":[{"delta":{"content":"你好"}}]}\n'
                "\n"
                'data: {"choices":[{"delta":{"content":"，学员"}}]}\n'
                "\n"
                "data: [DONE]\n"
            )
            return httpx.Response(200, text=body)
        payload = {
            "choices": [
                {"message": {"role": "assistant", "content": "你好，学员"}}
            ]
        }
        return httpx.Response(200, json=payload)

    client = httpx.Client(transport=httpx.MockTransport(handler))
    try:
        payload = build_chat_payload("local-stub", "请打个招呼", False)
        response = client.post("http://example.test/v1/chat/completions", json=payload)
        lines.append("非流式：" + read_message_content(response.json()))
        stream_payload = build_chat_payload("local-stub", "请打个招呼", True)
        with client.stream("POST", "http://example.test/v1/chat/stream", json=stream_payload) as stream_response:
            streamed = read_stream_text(stream_response.iter_lines())
        lines.append("流式：" + streamed)
    finally:
        client.close()
    return lines


if __name__ == "__main__":
    for line in run_demo():
        print(line)
