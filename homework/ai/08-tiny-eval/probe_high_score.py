"""讲义建议：把门槛抬到 0.99 看检索用例失败。不改三仓源码，用 build_lab_rag(min_score=0.99)。"""

from ailab.evals import load_cases, _grade_rag
from ailab.rag import build_lab_rag


def main() -> int:
    high = build_lab_rag(min_score=0.99)
    normal = build_lab_rag()
    cite_fail = 0
    cite_total = 0
    print("min_score=0.99 时，原本该 cite 的用例：")
    for case in load_cases():
        if case.kind != "rag" or case.expect != "cite":
            continue
        cite_total += 1
        result = high.answer(case.question)
        passed, detail = _grade_rag(case, result)
        best = f"{result.hits[0].score:.3f}" if result.hits else "-"
        mark = "通过" if passed else "失败"
        print(f"  {case.id}  {mark}  {detail} best={best}")
        if not passed and result.reason == "low_score":
            cite_fail += 1

    # 对照：默认门槛仍应全部 cite 通过
    ok_default = True
    for case in load_cases():
        if case.kind != "rag" or case.expect != "cite":
            continue
        passed, _ = _grade_rag(case, normal.answer(case.question))
        ok_default = ok_default and passed

    print(f"高门槛 cite 失败（期望全是 low_score）：{cite_fail}/{cite_total}")
    print(f"默认门槛 cite 仍通过：{ok_default}")
    ok = cite_fail == cite_total and cite_total > 0 and ok_default
    print("ok" if ok else "FAIL")
    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
