"""讲义建议改一行：脚本第一句就给合法 JSON，次数应当变成 1。"""

import json

from ailab.client import MockChatClient
from ailab.json_prompt import classify_intent


def main() -> int:
    print("第 02 课 · 探针：第一句已是合法 JSON")
    scripted = MockChatClient(
        script=[
            json.dumps(
                {"intent": "check_in", "question": "入住时间是什么时候？"},
                ensure_ascii=False,
            ),
        ]
    )
    result = classify_intent(scripted, "入住时间是什么时候？")
    print(f"结果：{result.intent}，用了 {result.attempts} 次调用")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
