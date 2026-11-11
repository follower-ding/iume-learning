"""探针：名字不在允许列表 → unknown_tool，不执行。"""

import json

from ailab.agent import run_agent
from ailab.client import MockChatClient
from ailab.tools import Tool


def main() -> int:
    calls = {"n": 0}

    def run(arguments):
        del arguments
        calls["n"] += 1
        return "不应该执行"

    tools = {"lookup_policy": Tool("lookup_policy", "读取规章", run)}
    raw = json.dumps(
        {"type": "tool_call", "name": "shell", "arguments": {"cmd": "ls"}},
        ensure_ascii=False,
    )
    result = run_agent(MockChatClient(script=[raw]), tools, "列目录", max_steps=3)
    print(f"stopped_reason={result.stopped_reason}")
    print(f"answer={result.answer}")
    print(f"calls={calls['n']}")
    assert result.stopped_reason == "unknown_tool"
    assert "shell" in result.answer
    assert calls["n"] == 0
    print("ok: unknown_tool 不执行")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
