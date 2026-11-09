# AI Lab 第08课：小评测（tiny-eval）

## 学了啥
用 9 条固定用例评**应用行为**（cite / refuse / tool / no_tool），不是给模型打榜。`kind` 分检索与工具路径；同句「延迟退房怎么收费？」检索要 cite `faq-checkout` 且 forbid 单价，工具路径缺小时反问且 `no_tool`；给了 3 小时才要求 `quote_late_checkout` + `90`。失败看 `reason`，不放宽期望迁就输出。

## 为什么能跑
`run_eval` 读 `eval_cases.json`，RAG 走 `build_lab_rag`，Agent 走 Mock + `hotel_tools`。forbid/require 再扫回答片段。高门槛演示用 `build_lab_rag(min_score=0.99)` 让 cite 变 `low_score`，不改三仓常量。一律 `.venv` 的 python，未改三仓讲义。

## 卡过啥
- 9/9 测的是门槛/引用/工具循环，不是模型分。
- 同句必须分路径，才知价格来自工具而非规章。
- 失败改 expect / 删 forbid = 评测失守；抬门槛看 `low_score` 再改回。
