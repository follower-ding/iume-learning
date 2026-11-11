"""讲义建议：把 VECTOR_WEIGHT 改成 1.0、SPARSE_WEIGHT 改成 0.0 再跑。

不改讲义仓 hybrid.py，也不改 hybrid_queries.json 的期望。
用 build_hybrid_retriever(vector_weight=1.0, sparse_weight=0.0) 只在练习副本里观察。
"""

from ailab.hybrid import build_hybrid_retriever, format_hybrid_report, run_hybrid_eval

EXPECTED_FAIL = ("paraphrase-room", "afternoon-checkin", "luggage")
REFUSE_PREFIX = "refuse-"


def main() -> int:
    retriever = build_hybrid_retriever(vector_weight=1.0, sparse_weight=0.0)
    print(
        f"权重实验：融合 = {retriever.vector_weight:.1f}×向量 + {retriever.sparse_weight:.1f}×压缩后的关键词分。"
        f"阈值 {retriever.min_score:.2f}。"
    )
    print("融合分应退回余弦（fused 与 vector 相同）：")
    probes = (
        "入住时间是什么时候？",
        "我什么时候能进房间",
        "下午可以住吗",
        "行李可以先放前台吗",
        "今晚有乐队演出吗",
        "可以带狗吗",
        "时间",
    )
    for question in probes:
        hit = retriever.search(question, k=1)[0]
        same = abs(hit.score - hit.vector_score) < 1e-12
        print(
            f"  向量 {hit.vector_score:.3f}  关键词 {hit.sparse_score:.3f}"
            f"  融合 {hit.score:.3f}  {'=余弦' if same else '≠余弦'}  {hit.id}"
        )
        print(f"    {question}")

    report = run_hybrid_eval(retriever=retriever)
    print(format_hybrid_report(report))

    failed = {check.case_id for check in report.checks if not check.passed}
    refuse_ok = all(
        check.passed
        for check in report.checks
        if check.case_id.startswith(REFUSE_PREFIX)
    )
    expected_fail = set(EXPECTED_FAIL)
    print(f"失败用例：{', '.join(sorted(failed)) or '（无）'}")
    print(f"期望失败（靠关键词才过 0.30）：{', '.join(EXPECTED_FAIL)}")
    print(f"refuse-* 仍通过：{refuse_ok}")
    ok = failed == expected_fail and refuse_ok
    print("ok" if ok else "FAIL")
    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
