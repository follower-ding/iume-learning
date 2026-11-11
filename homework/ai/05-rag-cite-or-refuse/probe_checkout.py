"""探针：延迟退房走检索只复述规章，不补单价（30/90 留给第06课工具）。"""

from ailab.rag import build_lab_rag


def main() -> int:
    result = build_lab_rag().answer("延迟退房怎么收费？")
    print(f"reason={result.reason}")
    print(f"citations={result.citations}")
    print(f"answer={result.answer}")
    print(f"has_30={'30' in result.answer}")
    print(f"has_90={'90' in result.answer}")
    print(f"hits={[ (h.id, round(h.score, 3)) for h in result.hits ]}")
    ok = (
        result.reason == "ok"
        and "faq-checkout" in result.citations
        and "30" not in result.answer
        and "90" not in result.answer
    )
    print("ok" if ok else "FAIL")
    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
