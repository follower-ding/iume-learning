# AI Lab 第07课：有上限的循环（agent-loop）

## 学了啥
`run_agent` 把「complete → 解析 → 执行或结束 → 写回」重复，默认 `max_steps=4`。`final` 正常停；允许列表工具执行后写 `role="tool"` 再下一轮（坏参数写回错误文案继续）；JSON 解析失败消耗一步并提示只输出 JSON。未知工具 → `unknown_tool` 不执行；同名+同参签名第二次 → `repeated_tool` 第二次不执行；步数用尽 → `max_steps`，正式回答是「达到步数上限…」，不把工具已算出的价格升成答案。换 hours 不算重复，靠上限停。

## 为什么能跑
启发式 Mock：有小时数先 `quote_late_checkout` 再 `final`（正常两步，合计 60）。`script=` 按序喂回复可演示换小时数触顶、重复签名、未知工具、invalid 修复。真客户端把 tool 改写成 user 文本以兼容接口。一律 `.venv` 的 python，未改三仓讲义。

## 卡过啥
- 两个刹车：上限挡换参循环，签名挡同一调用死循环。
- 上限时答案是停止句，价格只留在 steps 里排错。
- 解析失败也算一步；`max_steps=1` 来不及 final。
