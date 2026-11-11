"""探针：shell 可被解析，但不在 hotel_tools 里 → 拒绝执行。"""

from ailab.agent import ToolCall, parse_action
from ailab.tools import hotel_tools


def main() -> int:
    tools = hotel_tools()
    raw = '{"type":"tool_call","name":"shell","arguments":{"cmd":"ls"}}'
    action = parse_action(raw)
    assert isinstance(action, ToolCall)
    in_list = action.name in tools
    print(f"解析成功：name={action.name} arguments={action.arguments}")
    print(f"允许列表：{sorted(tools)}")
    print(f"是否执行：{'执行' if in_list else '拒绝'}")
    if in_list:
        print("shell 不该进允许列表")
        return 1
    print("ok: 先解析、再对照允许列表，不执行")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
