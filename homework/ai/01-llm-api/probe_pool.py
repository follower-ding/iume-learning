"""讲义建议改一行：问游泳池。Mock 应回到「只准备了……」，不编开放时间。"""

from ailab.client import MockChatClient
from ailab.messages import Message


def main() -> int:
    client = MockChatClient()
    messages = [
        Message("system", "你是岸港酒店实验前台。只回答课程资料里有的规章，不要编造价格。"),
        Message("user", "游泳池几点开放？"),
    ]
    completion = client.complete(messages)
    print("第 01 课 · 探针：游泳池")
    print(f"模型：{completion.model}")
    print(f"回答：{completion.text}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
