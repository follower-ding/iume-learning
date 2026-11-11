"""探针：金额必须由 quote_late_checkout 算，不能信模型 arguments 里的「合计」。"""

from ailab.agent import ToolCall, parse_action
from ailab.tools import hotel_tools


def main() -> int:
    # 假模型：arguments 里自己填了合计，hours 却是 2
    raw = (
        '{"type":"tool_call","name":"quote_late_checkout",'
        '"arguments":{"hours":2,"合计":"999 元"}}'
    )
    action = parse_action(raw)
    assert isinstance(action, ToolCall)
    tools = hotel_tools()
    result = tools[action.name].run({"hours": action.arguments["hours"]})
    print(f"模型 arguments：{action.arguments}")
    print(f"工具结果：{result}")
    if "999" in result:
        print("工具吃了模型编的合计")
        return 1
    if "合计 60 元" not in result:
        print("期望工具算出 60")
        return 1
    print("ok: 单价/合计只信工具返回值")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
