# AI Lab 第06课：工具调用（tool-calling）

## 学了啥
模型只输出一个 JSON：`tool_call` 或 `final`（不用厂商 `tool_calls` 字段）。允许列表两只纯函数：`lookup_policy`（topic 七选一）、`quote_late_checkout`（hours 1..12 的 int；bool 单独拒）。2 小时 → 工具算合计 60 元；无小时数 → final 反问，不默认 1、输出无 30。`shell` 可解析但不在 `hotel_tools()` → 拒绝执行。坏参数抛 `ToolArgumentError`。本课无循环，`run_agent` 留第07课。

## 为什么能跑
`build_tool_system` + `MockChatClient` + `parse_action`：有「N 小时」就提出 `quote_late_checkout`；没有就 final 反问。金额只信 `quote_late_checkout` 返回值（`HOURLY_RATE=30`），不信模型 arguments 里的合计。一律 `.venv` 的 python，未改三仓讲义。

## 卡过啥
- 金额必须工具算，别让模型填合计。
- 缺小时反问，不默认 1；bool≠1。
- 先解析再对照允许列表；shell 不执行。
