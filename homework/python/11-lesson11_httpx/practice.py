# 练习完整版：MockTransport 剧本 + 超时/5xx 重试，次数用完失败。
import httpx


def make_handler(plan):
    remaining = []
    for item in plan:
        remaining.append(item)

    def handler(request):
        current = remaining.pop(0)
        if current == "timeout":
            raise httpx.ReadTimeout("模拟读取超时", request=request)
        if current == "503":
            return httpx.Response(503, text="busy")
        if current == "404":
            return httpx.Response(404, text="missing")
        return httpx.Response(200, json={"status": "ok"})

    return handler


def run_plan(plan, max_attempts):
    lines = []
    transport = httpx.MockTransport(make_handler(plan))
    timeout = httpx.Timeout(0.2)
    client = httpx.Client(transport=transport, timeout=timeout)
    try:
        attempt = 1
        while attempt <= max_attempts:
            try:
                response = client.get("http://example.test/status")
            except httpx.TimeoutException:
                lines.append("第" + str(attempt) + "次：超时")
                attempt = attempt + 1
                continue
            lines.append("第" + str(attempt) + "次：" + str(response.status_code))
            if response.status_code >= 500 and attempt < max_attempts:
                attempt = attempt + 1
                continue
            if response.status_code >= 500:
                lines.append("结果：失败")
                return lines
            if response.status_code != 200:
                lines.append("结果：不重试")
                return lines
            lines.append("结果：" + response.json()["status"])
            return lines
        lines.append("结果：失败")
        return lines
    finally:
        client.close()


for line in run_plan(["timeout", "503", "200"], 3):
    print(line)
