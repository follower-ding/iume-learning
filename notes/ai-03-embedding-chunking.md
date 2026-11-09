# AI Lab 第03课：切块与向量（embedding-chunking）

## 学了啥
长规章先切再向量化。`chunk_text`：空行分段落→短段整留→长段滑窗，窗口后半优先在 `。！？；` 切开，下一块回退 `overlap`；`overlap` 必须 `< size` 否则直接报错。本课向量是字符二元组 TF-IDF（不是神经网络嵌入）：先对 FAQ `fit`，再 `embed` 文档与查询，用余弦比方向。三句探针相对入住规章约 0.396 / 0.227 / 0.000（字面最高、换说法掉分、无关为 0）。规章切成 18 块（size=80, overlap=20），维度 209。

## 为什么能跑
练习走本地 `ailab.chunking` / `TfidfEmbedder`，不联网、不读密钥。查询共用文档那次 `fit` 的 vocab/idf；单独 fit 会换空间（探针里查询单独 fit 只有 7 维）。overlap=size 探针抛 `ValueError`。

## 卡过啥
- 别把 TF-IDF 当语义嵌入；中文别按空格分词。
- 文档/查询必须同一套 idf；换模型别留旧向量。
- `overlap >= size` 必须拒，防转圈；一律 `.venv` 的 python。
