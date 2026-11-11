"""探针：解析失败消耗一步并写回；修好后可 final。换小时数靠 max_steps 停。"""

import json

from ailab.agent import run_agent
from ailab.client import MockChatClient
from ailab.tools import hotel_tools


def main() -> int:
    repaired = run_agent(
        MockChatClient(
            script=[
                "我去查一下",
                json.dumps({"type": "final", "answer": "请说明主题。"}, ensure_ascii=False),
            ]
        ),
        hotel_tools(),
        "你好",
        max_steps=4,
    )
    print(f"repaired kinds={[s.kind for s in repaired.steps]} reason={repaired.stopped_reason}")
    assert [s.kind for s in repaired.steps] == ["invalid", "final"]
    assert repaired.stopped_reason == "final"

    script = [
        json.dumps(
            {"type": "tool_call", "name": "quote_late_checkout", "arguments": {"hours": hour}},
            ensure_ascii=False,
        )
        for hour in (1, 2, 3, 4)
    ]
    capped = run_agent(MockChatClient(script=script), hotel_tools(), "一直换小时数", max_steps=3)
    print(f"capped reason={capped.stopped_reason} steps={len(capped.steps)} answer={capped.answer}")
    assert capped.stopped_reason == "max_steps"
    assert len(capped.steps) == 3
    assert capped.answer.startswith("达到步数上限")
    assert "30" not in capped.answer
    print("ok: invalid 消耗步数；换参数靠 max_steps 停且不把价格当答案")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
