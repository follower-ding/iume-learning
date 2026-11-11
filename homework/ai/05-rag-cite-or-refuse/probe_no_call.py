"""探针：最高分低于阈值时不调用模型（reason=low_score）。"""

from ailab.messages import Completion
from ailab.rag import REFUSAL, build_lab_rag


class CountingClient:
    def __init__(self) -> None:
        self.calls = 0

    def complete(self, messages, *, temperature: float = 0.0) -> Completion:
        del messages, temperature
        self.calls += 1
        return Completion(text="不应该被调用", model="counter", prompt_tokens=1, completion_tokens=1)


def main() -> int:
    client = CountingClient()
    result = build_lab_rag(client).answer("游泳池几点开放？")
    print(f"reason={result.reason}")
    print(f"refused={result.refused}")
    print(f"calls={client.calls}")
    print(f"answer={result.answer}")
    print(f"best={result.hits[0].score:.3f} {result.hits[0].id}")
    ok = (
        result.reason == "low_score"
        and result.refused
        and client.calls == 0
        and result.answer == REFUSAL
    )
    print("ok" if ok else "FAIL")
    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
