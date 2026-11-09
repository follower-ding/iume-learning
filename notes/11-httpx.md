# 第11课：超时与重试（httpx）

## 学了啥
岗位短轨第三课：用 `httpx` 当调用方请求接口；`Timeout` 设等待上限；超时与 ≥500 可有限次重试，404 等非 200 停止，次数用完记失败。示例用 `MockTransport` 按剧本返回，不访问互联网。三级练习：一次成功；503 不重试（只超时重试）；404 不重试 + 超时次数用尽失败。

## 为什么能跑
`Client(transport=MockTransport(handler))` 时请求不进真实网络，handler 按剧本 `pop`：超时抛 `ReadTimeout`（属 `TimeoutException`），否则返回 `Response`。循环里 `attempt` 递增；超时或 5xx 且未超上限则 `continue`；`finally` 里 `client.close()`。剧本地址只是文字标签。

## 卡过啥
- 忘加 `attempt` → 死循环；剧本太短 → `IndexError`。
- 404 误重试；超时未 `except` 会打停程序。
- `close` 须放 `finally`；一律用 `.venv` 里的 python/httpx。
