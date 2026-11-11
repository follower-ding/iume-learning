"""探针：分数全相同时按 id 字母序；全 0 不表示早餐更相关。"""

from ailab.indexing import build_faq_index
from ailab.vectorstore import MemoryVectorStore


def main() -> int:
    embedder, store = build_faq_index()
    hits = store.search(embedder.embed(["游泳池几点开放？"])[0], k=3)
    print("游泳池问句 top-3（全 0 分，按 id 字母序）：")
    for hit in hits:
        print(f"  {hit.score:.3f}  {hit.id}")
    ids = [hit.id for hit in hits]
    print(f"顺序固定：{ids}（不等于谁更相关）")

    demo = MemoryVectorStore()
    demo.add("b", "乙", [1.0, 0.0])
    demo.add("a", "甲", [1.0, 0.0])
    tied = demo.search([1.0, 0.0], k=2)
    print("手工平分 tie-break：", [hit.id for hit in tied])
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
