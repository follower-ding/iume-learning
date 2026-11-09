# 第12课：对话接口与流式输出（llm_chat）

## 学了啥
岗位短轨第四课：用本地剧本模拟对话接口。非流式一次读 `choices[0].message.content`；流式按 `data:` 行拼接 `delta.content`，遇 `data: [DONE]` 停止。密钥检查只报告「缺少密钥/已配置」，不打印密钥本身；`demo` 默认不读 `.env`、不连厂商。

## 为什么能跑
`MockTransport` 按路径返回 JSON 或 SSE 文本；`client.stream` + `iter_lines` 把多行交给同一个解析函数。`json.loads` 把 data 后的文本变字典；空行和非 `data:` 行跳过；`delta` 无 `content` 也跳过。可选 `chat_with_config` 缺配置直接返回说明，不调用 client。

## 卡过啥
- `[DONE]` 要用 `break` 不是 `continue`。
- 流式先判断 `"content" in delta`，避免 KeyError。
- 不要 print 密钥或 Authorization；一律 `.venv` 里的 python/httpx。
