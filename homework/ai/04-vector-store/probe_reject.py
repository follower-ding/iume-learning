"""探针：重复 id 拒写；维度不一致抛 VectorDimensionError。"""

from ailab.errors import VectorDimensionError
from ailab.vectorstore import MemoryVectorStore


def main() -> int:
    store = MemoryVectorStore()
    store.add("faq-checkin", "下午入住", [1.0, 0.0], {"source_id": "faq-checkin"})
    try:
        store.add("faq-checkin", "另一条", [0.0, 1.0])
        print("dup: unexpected ok")
    except ValueError as exc:
        print(f"dup: ValueError → {exc}")
    try:
        store.add("faq-wifi", "网络", [1.0, 0.0, 0.0])
        print("dim-add: unexpected ok")
    except VectorDimensionError as exc:
        print(f"dim-add: VectorDimensionError → {exc}")
    try:
        store.search([1.0], k=1)
        print("dim-search: unexpected ok")
    except VectorDimensionError as exc:
        print(f"dim-search: VectorDimensionError → {exc}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
