"""探针：删掉「2 小时」后应 final 反问，不调用报价、输出里没有 30。"""

from ailab.agent import ToolCall, build_tool_system, parse_action
from ailab.client import MockChatClient
from ailab.messages import Message
from ailab.tools import hotel_tools


def main() -> int:
    tools = hotel_tools()
    system = Message("system", build_tool_system(tools))
    # 讲义建议：把「2 小时」删掉，应走反问而不是计价
    question = "延迟退房要多少钱？"
    raw = MockChatClient().complete([system, Message("user", question)]).text
    action = parse_action(raw)
    print(f"问题：{question}")
    print(f"模型输出：{raw}")
    if isinstance(action, ToolCall):
        print(f"意外 tool_call：{action.name} {action.arguments}")
        return 1
    print(f"直接回答：{action.answer}")
    if "30" in action.answer or "合计" in action.answer:
        print("反问里出现了单价，失败")
        return 1
    print("ok: final 反问，无单价")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
