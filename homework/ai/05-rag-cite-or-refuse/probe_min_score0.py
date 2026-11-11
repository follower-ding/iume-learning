"""讲义建议改一行：min_score=0.0 时，游泳池 0 分也会进入生成。看完改回去。"""

from ailab.rag import build_lab_rag


def main() -> int:
    # 默认门槛：应拒绝
    refused = build_lab_rag().answer("游泳池几点开放？")
    print(f"default reason={refused.reason} refused={refused.refused}")

    # 门槛关掉：0 分 hit 也会交给 Mock，它只会复述第一条资料
    opened = build_lab_rag(min_score=0.0).answer("游泳池几点开放？")
    best = opened.hits[0]
    print(f"min0 reason={opened.reason} refused={opened.refused}")
    print(f"min0 best={best.score:.3f} {best.id}")
    print(f"min0 citations={opened.citations}")
    print(f"min0 answer={opened.answer}")
    ok = refused.reason == "low_score" and opened.reason == "ok" and best.score == 0.0
    print("ok" if ok else "FAIL")
    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
