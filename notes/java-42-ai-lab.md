# Java 第42课：AiLabDemo（OpenAI 兼容接口与最小 RAG）

仓库讲义：`follower-ding/java-learning-lab` → `07-ai-lab/.../learn/ai/lesson42/`（只读引用，未改讲义）  
本地跑通：2026-11-11（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`  
本课用 **LangChain4j 1.23.0** 调 OpenAI 兼容聊天接口：先用内存资料拼一条检索增强提示，再让模型请求本地订单查询工具。默认入口只访问本机回环上的替身服务，不需要 API 密钥。本课只做本地交付，未开综合服务实验，未加 `--live`，未访问公网。

## 复述（学了啥）
默认 `main` 不带 `--live`，走的是 `rehearse()`：在 `127.0.0.1` 的空闲端口起一个本机替身，占位令牌是 `local-demo`（不是服务商密钥）。客户端把 `baseUrl` 写成以 `/v1` 结尾，LangChain4j 再请求 `chat/completions`。就算终端里已经有 `AI_API_KEY`，这条默认命令也不会用它。

检索增强（RAG）先查资料，再提问。本课资料只有退款、发货两段，用二字片段重叠打分，不调用嵌入接口。问「几天内可以退款？」时命中「退款」，把标题和正文拼进三行提示（只根据资料回答、资料、问题），替身看见「签收后7天内可申请退款」就回固定句子。分数是 0 就不返回，避免把无关段落塞进提示。

订单状态不能靠模型背。工具调用的分工是：替身先提出要执行 `lookupOrder`，Java 查自己的 Map（示例只有 `1001=已发货`），再把结果送回去，替身看见「已发货」才组织最终一句。状态仍在 `OrderLookup` 里。`@Tool` 标在 Java 方法上，`@P("orderId")` 写明参数名，`AiServices` 负责这一圈来回。

`AiConfig` 只认 `AI_BASE_URL`、`AI_API_KEY`、`AI_MODEL`；环境变量覆盖 `.env`。`toString` 只写「已设置」或「未设置」，不写密钥原文。没有密钥时 `CompatibleChatModels.create` 直接抛 `IllegalStateException`，避免空着 Authorization 去打默认公网。`--live` 才读密钥；本课作业不加这个参数。

| API / 概念 | 干什么 |
| --- | --- |
| 本机替身 `LocalChatCompletionServer` | 只听 `127.0.0.1` 空闲端口，接受 `POST /v1/chat/completions`，按正文回写死的句子或 `tool_calls` |
| `KeywordCorpus.best` | 二字片段重叠打分；0 分返回空；并列时保留更早的那段 |
| `RagPrompts.userPrompt` | 把命中资料和问题拼成三行提示 |
| `CompatibleChatModels` | 用 LangChain4j `OpenAiChatModel` 调兼容接口；没密钥就拒绝 |
| `@Tool` + `@P` + `AiServices` | 模型请求 `lookupOrder`，Java 查 Map，再把结果送回模型 |
| `OrderLookup` | 订单状态在内存表里，不在模型里 |
| `--live` | 才读密钥访问真实兼容服务；默认入口和 `mvn test` 都不走这条路 |

本课主要类型：**AiLabDemo**（默认本机排练 / `--live`）、**KeywordCorpus** 与 **Passage**、**RagPrompts**、**LocalChatCompletionServer**、**CompatibleChatModels**、**AiConfig** 与 **DotEnv**、**OrderLookup**、**OrderAssistant** / **OrderAssistants**。

## 跑通
仓根（`_refs/java-learning-lab`）。不要加 `--live`，不要配密钥：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8'
./mvnw -q -pl 07-ai-lab test
./mvnw -q -pl 07-ai-lab compile exec:java '-Dexec.mainClass=learn.ai.lesson42.AiLabDemo'
```

正文（mvnw exec 实测，JDK/Maven 诊断行不计；端口不固定，不出现在对齐正文里）：

```text
检索命中：退款
资料：签收后7天内可申请退款。质量问题由店铺承担运费。
本地兼容接口回答：根据资料，签收后7天内可以退款。
本地工具调用回答：订单 1001 的状态是已发货。
真实接口未调用。复制 07-ai-lab/.env.example 为 07-ai-lab/.env 并填写 AI_API_KEY 后，加上 --live 才会访问兼容服务。
```

Maven `BUILD SUCCESS`（test Finished at 2026-11-11T12:20:14+08:00，Asia/Shanghai；Tests run: 16, Failures: 0，全部走本机替身或脚本 `ChatModel`，不访问公网）。  
exec 完成于 2026-11-11T12:20:12+08:00（Asia/Shanghai）。  
练习副本（自敲，不进讲义仓）：`/workspace/iume-learning/java/42-ai-lab/` —— `AiLabDemo` 及同包源码 + `PracticeOne` / `PracticeTwo` / `PracticeThree`。本地 `javac`/`java`，classpath 带 LangChain4j 1.23.0、`langchain4j-open-ai`、`slf4j-nop` 2.0.18 及传递依赖。`javac` 须加 `-parameters`，与讲义 pom 的 `maven.compiler.parameters=true` 对齐。默认入口和练习都不加 `--live`。副本 Demo 正文对齐；练习见下。

默认命令能打出这五行，是因为二字片段命中「退款」，本机替身收到带资料的提示后回固定句子；同一替身先要求调用 `lookupOrder`，Java 查到 1001 是已发货，替身再回最终一句；进程结束，真实接口未调用。

## 原理（小白话）
**兼容接口像统一窗口**：许多服务商都接受 `POST /v1/chat/completions`，换家时只改基础地址和模型名，请求形状不变。  
**本机替身像排练用的假柜台**：只听回环地址，看见退款资料、工具声明或工具结果，就按写死的规则回话，不必真的请大模型。  
**RAG 像先翻档案再回答**：先用二字片段找出相关段落，再把段落和问题一起放进提示，而不是让模型凭记忆讲店铺规则。  
**工具调用像柜台去后仓查单**：模型只能提出「请执行 lookupOrder」，Java 查自己的 Map，再把事实送回去；订单状态不在模型里。  
**没密钥就停在门口**：`CompatibleChatModels` 拒绝空密钥，避免空着 Authorization 打到默认公网。  
**默认命令不走出门**：即使环境里已有 `AI_API_KEY`，不加 `--live` 也不会用它。  
**`toString` 和日志要藏密钥**：配置打印只写「已设置」，`logRequests` 保持关闭。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 把密钥写进 Java 字符串或提交 `.env` | 密钥进仓库或被贴出去 | 只跟踪 `.env.example`；`.env` 已被 Git 忽略；本课作业不建 `.env`、不加 `--live` |
| `baseUrl` 少了 `/v1` 或多写了 `/chat/completions` | 客户端再拼一次路径，结果 404 | 地址以 `/v1` 结尾 |
| 打开 `logRequests(true)` 后把日志贴出去 | 请求头里有密钥 | 保持关闭 |
| 检索分数为 0 仍把第一段塞进提示 | 模型对着无关段落回答 | `best` 在 0 分时返回空 |
| 让模型「记住」订单状态 | 那句话不是查出来的 | 表在 `OrderLookup`，要看工具是否被调用 |
| 默认 `mvn test` 或作业入口加 `--live` | 可能带着密钥出网 | 测试用脚本模型或本机替身；作业不加 `--live` |
| 练习 `javac` 漏掉 `-parameters` | 工具参数绑不上，替身看不到「已发货」，会一直要 `tool_calls` | 与讲义 pom 对齐，加上 `-parameters` |
| 练习 classpath 没带 LangChain4j | `javac`/`java` 找不到 `OpenAiChatModel` / `AiServices` | 带上 LangChain4j 1.23.0、`langchain4j-open-ai`、`slf4j-nop` 等依赖 |
| 把替身端口写进对齐正文 | 端口每次不同，对不上讲义 | 端口不出现在输出里 |

## 三级练习结果（先预测再跑，对照参考答案）
均在练习副本独立入口、本机替身（不加 `--live`，不访问公网）：

1. **模仿**：问题改成「什么时候发货」→ 预测命中标题「发货」，资料「成都仓在工作日16:00前付款的订单当天发货，默认使用顺丰。」；提示里没有退款原文，替身第三行回「不知道。」；工具那一行仍是订单 1001 已发货 → 实跑一致。二字片段「发货」只在发货段里。
2. **修改**：资料末尾追加「发票：电子发票在发货后24小时内开具。」再问「发票多久开好」→ 预测命中「发票」，资料是那句开票说明，替身仍回「不知道。」→ 实跑一致。「发票」二字只出现在新段落；发货段有「发货」没有「发票」，不会因为都提到发货时间而命中发货。检索对了，替身也只按写死的规则回答，说明检索和模型是两步。练习用独立 `KeywordCorpus`，不改默认 `shopPolicies()`，所以 Demo 五行仍然对齐。
3. **独立**：`OrderLookup` 同时放入 `1001=已发货` 和 `1002=已签收`，只调用 Java 方法 → 预测 `lookupOrder("1002")` 为「订单 1002 的状态是已签收。」，`lookupOrder("1003")` 为「没有订单 1003。」 → 实跑一致（`calls=2`）。未知订单走的是 Map 里没有这个键，不是模型在猜测。

## 卡过啥
- 本课入口在 **07-ai-lab**，包 `learn.ai.lesson42`，主类 `AiLabDemo`。默认命令只走本机替身，不要加 `--live`，不要填写或提交密钥。
- 端口不固定，不要写进对齐正文。就算环境里已有 `AI_API_KEY`，默认命令也不会用它。
- 练习副本 `javac` 必须带 `-parameters`；漏了以后，工具参数绑不上，会在 `AiServices` 里超过 100 轮 tool calling。
- `./mvnw` 正文对齐；练习副本需 LangChain4j 1.23.0 classpath。PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 学习仓默认分支是 `main`（不是 `master`）。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开综合服务实验、未用 `--live` 访问公网、未代交验收。
