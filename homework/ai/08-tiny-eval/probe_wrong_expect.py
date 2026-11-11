"""失败时看 reason，不要改期望迁就输出：把游泳池改成 expect=cite，应失败。"""

from ailab.evals import EvalCase, format_report, run_eval


def main() -> int:
    good = run_eval()
    print(format_report(good))
    broken = run_eval(
        [EvalCase("bad-pool-as-cite", "rag", "游泳池几点开放？", "cite", source_id="faq-checkin")]
    )
    print("---")
    print(format_report(broken))
    ok = good.passed == good.total == 9 and broken.passed == 0
    print("ok" if ok else "FAIL")
    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
