"""探针：正常计价但 max_steps=1，停在工具之后，来不及 final。"""

from ailab.agent import run_agent
from ailab.client import MockChatClient
from ailab.tools import hotel_tools


def main() -> int:
    result = run_agent(MockChatClient(), hotel_tools(), "延迟退房 2 小时要多少钱？", max_steps=1)
    print(f"stopped_reason={result.stopped_reason}")
    print(f"steps={len(result.steps)} kinds={[s.kind for s in result.steps]}")
    print(f"answer={result.answer}")
    assert result.stopped_reason == "max_steps"
    assert len(result.steps) == 1
    assert result.steps[0].kind == "tool_call"
    assert result.steps[0].tool_result is not None
    assert "60" in (result.steps[0].tool_result or "")
    assert result.answer.startswith("达到步数上限")
    assert "60" not in result.answer
    print("ok: max_steps=1 不把工具价升成正式回答")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
