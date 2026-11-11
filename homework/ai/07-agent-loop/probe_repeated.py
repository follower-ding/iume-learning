"""探针：相同工具名+相同参数第二次出现 → repeated_tool，第二次不执行。"""

import json

from ailab.agent import run_agent
from ailab.client import MockChatClient
from ailab.tools import hotel_tools


def main() -> int:
    raw = json.dumps(
        {"type": "tool_call", "name": "quote_late_checkout", "arguments": {"hours": 2}},
        ensure_ascii=False,
    )
    result = run_agent(MockChatClient(script=[raw, raw]), hotel_tools(), "再算一次", max_steps=4)
    print(f"stopped_reason={result.stopped_reason}")
    print(f"answer={result.answer}")
    print(f"step0_result={result.steps[0].tool_result}")
    print(f"step1_result={result.steps[1].tool_result}")
    assert result.stopped_reason == "repeated_tool"
    assert result.answer == "重复调用同一工具，已停止。"
    assert result.steps[0].tool_result is not None
    assert result.steps[1].tool_result is None
    print("ok: 签名重复第二次不执行")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
