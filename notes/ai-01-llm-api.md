# AI Lab 第01课：一次调用（llm-api）

## 学了啥
岸港酒店虚构前台开场：把带 `system`/`user` 角色的消息列表交给客户端，拿回一段文本。练习用 `MockChatClient.complete`，模型名 `mock-hotel-front-desk`；按关键词抄 `hotel_faq.json`，没有就说「只准备了……」。打印的 token 是估算，不是账单。

## 为什么能跑
`Message` 校验 role；`MockChatClient` 调 `respond` → `plain_reply` 匹配关键词后返回 FAQ 原文。调用方只依赖 `complete(messages)`，不绑厂商 SDK。练习不读 `.env`、不联网。讲义建议把问题改成「游泳池几点开放？」——应回 fallback，不编开放时间。

## 卡过啥
- Mock 不是模型，没资料的题不该现编。
- 估算 token 不能对账，真实看 `usage`。
- 角色要分开；一律 `.venv` 里的 python（本课 editable 安装 `_refs/ai-learning-lab`，未改讲义仓）。
