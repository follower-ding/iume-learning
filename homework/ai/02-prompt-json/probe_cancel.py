"""关键词顺序：取消排在入住前，「入住日前可以取消吗」应归 cancel。"""

from ailab.client import MockChatClient
from ailab.json_prompt import classify_intent


def main() -> int:
    print("第 02 课 · 探针：取消优先于入住")
    intent = classify_intent(MockChatClient(), "入住日前可以取消吗？")
    print(f"结果：{intent.intent}（第 {intent.attempts} 次），question={intent.question}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
