# AI Lab 第04课：向量库（vector-store）

## 学了啥
向量本身不是答案。`MemoryVectorStore` 把稳定 id、原文、向量、metadata 放同一条记录，按余弦取 top-k。短问答标题只进向量、入库只留正文。7 条 FAQ，维度 209。「入住时间是什么时候」top1=`faq-checkin`≈0.396，第二名掉到 0.15 以下；「游泳池几点开放」三条全 0.000，按 id 字母序固定，不表示谁更相关。

## 为什么能跑
`build_faq_index`：FAQ 文本一次 `fit` → `embed` → `add`。查询用同一套 embedder。维度不一致抛 `VectorDimensionError`（不截断/不补零）；重复 id 拒写。排序键 `(-score, id)`。低分仍留在 top-k，删不删留给第05课阈值。

## 卡过啥
- 别只存向量；引用需要 id+原文。
- 别把全 0 的字母序当相关度；别把 k=1 当过滤。
- 查询必须同空间同维度；换模型整库重算。一律 `.venv` 的 python。
