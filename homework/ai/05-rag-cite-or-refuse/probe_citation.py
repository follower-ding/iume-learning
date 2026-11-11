"""探针：缺引用 / 未知引用 → 整段草稿作废，只留固定拒绝语。"""

from ailab.messages import Completion
from ailab.rag import REFUSAL, build_lab_rag


class StubClient:
    def __init__(self, text: str) -> None:
        self.text = text

    def complete(self, messages, *, temperature: float = 0.0) -> Completion:
        del messages, temperature
        return Completion(text=self.text, model="stub", prompt_tokens=1, completion_tokens=1)


def main() -> int:
    missing = build_lab_rag(StubClient("下午 14:00 可以入住，不用看来源。")).answer(
        "入住时间是什么时候？"
    )
    print(f"missing reason={missing.reason}")
    print(f"missing answer={missing.answer}")
    print(f"missing keeps_draft={'14:00' in missing.answer}")

    unknown = build_lab_rag(StubClient("游泳池全天开放。来源：[faq-pool]")).answer(
        "入住时间是什么时候？"
    )
    print(f"unknown reason={unknown.reason}")
    print(f"unknown answer={unknown.answer}")
    print(f"unknown keeps_draft={'全天开放' in unknown.answer}")
    print(f"unknown citations={unknown.citations}")

    ok = (
        missing.reason == "missing_citation"
        and missing.answer == REFUSAL
        and "14:00" not in missing.answer
        and unknown.reason == "unknown_citation"
        and unknown.answer == REFUSAL
        and "全天开放" not in unknown.answer
        and unknown.citations == []
    )
    print("ok" if ok else "FAIL")
    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
