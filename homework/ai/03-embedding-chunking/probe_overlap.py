"""探针：overlap >= size 必须直接报错，不能转圈。"""

from ailab.chunking import chunk_text


def main() -> int:
    print("第 03 课探针 · overlap=size 应报错")
    try:
        chunk_text("岸港酒店规章。" * 20, size=80, overlap=80)
    except ValueError as exc:
        print(f"OK ValueError: {exc}")
        return 0
    print("FAIL: 没有抛 ValueError")
    return 1


if __name__ == "__main__":
    raise SystemExit(main())
