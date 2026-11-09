# AI Lab 第05课：引用或拒绝（rag-cite-or-refuse）

## 学了啥
检索之后要有门槛。`DEFAULT_MIN_SCORE=0.2`：top-k 里分不够的丢掉；一条都不剩 → 固定拒绝语，`reason=low_score`，不调用模型。过线的段落带 `[faq-...]` 进系统提示；草稿引用必须落在本次过线 id 上，缺引用 / 未知引用都整段作废。入住 `ok`/`faq-checkin`≈0.396；游泳池最高分 0 → `low_score`；MisleadingClient 写 `[faq-pool]` → `unknown_citation`，无「全天开放」。延迟退房只复述「先问前台」，不补 30/90。

## 为什么能跑
`build_lab_rag()` → `RagPipeline.answer`：嵌入 → 检索 → 过滤 →（有强命中才）`complete` → `apply_citation_policy`。Mock 只复述第一条过线资料；`min_score=0.0` 探针证明关掉门槛后 0 分也会进生成。一律 `.venv` 的 python，未改三仓讲义。

## 卡过啥
- 低分别调模型；失败别拼草稿。
- 只允许过线 id；0.2 不是通用常数。
- Mock 稳 ≠ 会推理；单价留给第06课工具。
