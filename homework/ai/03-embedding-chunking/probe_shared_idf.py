"""探针：查询必须共用文档 fit 出的 idf；单独 fit 会换一套空间。"""

from ailab.corpus import load_faq
from ailab.embeddings import TfidfEmbedder
from ailab.indexing import document_text


def main() -> int:
    print("第 03 课探针 · 查询共用同一套 idf")
    items = load_faq()
    docs = [document_text(item) for item in items]
    shared = TfidfEmbedder().fit(docs)
    alone = TfidfEmbedder().fit(["今晚有乐队演出吗"])
    print(f"文档 fit 维度：{shared.dimension}")
    print(f"查询单独 fit 维度：{alone.dimension}")
    print("单独 fit 换了一套 vocab/idf，不能再和文档向量比余弦。")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
