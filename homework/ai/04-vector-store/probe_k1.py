"""探针：把 k 改成 1，只看第一名，看不见第二名差多远。"""

from ailab.indexing import build_faq_index


def main() -> int:
    embedder, store = build_faq_index()
    q = "入住时间是什么时候？"
    hits = store.search(embedder.embed([q])[0], k=1)
    print(f"k=1 时只剩 {len(hits)} 条：")
    for hit in hits:
        print(f"  {hit.score:.3f}  {hit.id}  {hit.metadata.get('title', '')}")
    print("低分邻居不见了；第二名差多远也看不见。")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
