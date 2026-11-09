# Java 学习仓摸底（阶段 D）

仓库：https://github.com/follower-ding/java-learning-lab（私有，默认分支 `master`）  
摸底时间：2026-11-10（Asia/Shanghai）  
方式：只读（cursor-github），未改 java-learning-lab。

## 仓库目标

从零基础到 Spring / Spring MVC / MyBatis / Spring Boot 与服务部署的中文 Java 学习项目。讲解（`.md`）与可运行代码同目录；每课有 main 入口、预期输出、常见错误、三级练习与独立答案；测试在 `src/test/java`。

## 模块结构

| 路径 | 课次 | 用途 |
| --- | --- | --- |
| `01-java-basics/` | 01–30 + 订单 capstone | 语法→OOP→集合/泛型/异常/Lambda/Stream/注解/反射 |
| `02-java-advanced/` | 31–34 | 文件 IO、并发、JVM 诊断、本地 HTTP 客户端 |
| `03-web-data/` | 35–37 | HTTP/JSON、SQL、JDBC 与事务（H2/Jackson） |
| `04-frameworks/` | 38–41 | spring-core / spring-mvc / mybatis / spring-boot 四独立实验 |
| `05-service-lab/` | 综合服务 | 双 Boot 服务、可选 Redis、Nacos 客户端、部署课 |
| `06-capstone-project/` | 综合案例 | 商品/库存/订单：单体→双服务→缓存/发现→部署 |
| `07-ai-lab/` | 42 | LangChain4j + OpenAI 兼容；默认 Mock，不需密钥 |
| `docs/roadmap/` | — | 学习进度、路线、框架版本、课程模板 |
| `scripts/` | — | Windows JDK 25 准备/切换脚本 |
| `pom.xml` + `mvnw` | — | Maven 多模块聚合；`maven.compiler.release=25` |

## 第 1 课入口

- 讲解：`01-java-basics/src/main/java/learn/lesson01_hello/第一个Java程序.md`
- 代码：`01-java-basics/src/main/java/learn/lesson01_hello/HelloDemo.java`
- 索引：`01-java-basics/README.md`
- 预期输出：`你好，Java！` / `这是我的第一个 Java 程序。`

## 本地怎么跑

- **JDK 25**（根 POM `maven.compiler.release=25`）；Maven Wrapper **3.9.16**（`./mvnw` / `mvnw.cmd`）。
- Windows：`.\scripts\setup-jdk25.ps1` → `. .\scripts\use-jdk25.ps1`
- Unix：自行安装 JDK 25 并设 `JAVA_HOME`。
- 根目录常用命令：

```sh
java -version          # 应为 25
./mvnw -version
./mvnw clean verify    # 多模块全量测试
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson01_hello.HelloDemo'
```

- 前 18 课无需数据库 / Docker / 外网；默认测试不访问公网。
- 中文乱码：`chcp 65001` + `JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8`。

## 学习进度（仓内文档摘要）

- 01–37、框架 38–41、综合服务与 capstone、第 42 课 AI lab：内容与离线测试已齐。
- 真实 Redis / Nacos / Docker / Kubernetes 联调仍受环境阻塞，勿宣称已跑通。
- 2026-11-09：`07-ai-lab` 16 项测试默认不访问公网。

## 与 python / ai 仓差异

| | java-learning-lab | python / ai（iume 阶段） |
| --- | --- | --- |
| 语言/构建 | Java 25 + Maven 多模块 | Python + 单课脚本/venv |
| 讲解位置 | 与 `.java` 同目录的中文 `.md` | 讲义常在独立 notes / 课目录 |
| 跑通方式 | `./mvnw -pl <module> exec:java` / `test` | `python xxx.py` |
| 外部依赖 | 前段零外部；后段可选 Redis/Nacos/Docker | AI 课多为本地 Mock |
| 默认分支 | `master` | 多为 `main` |
| 验收节奏 | 仓内已有大量 Demo+Test；作业笔记仍写 iume-learning | 按课复述/跑通/笔记 push |

## 注意事项

1. **从第 1 课开刀**，勿跳 ai 第 9 课；阶段 D 入口是 Java 01。
2. 只读摸底阶段勿改 `java-learning-lab`；作业笔记写 `iume-learning/notes/`。
3. 跑课用 **JDK 25**，勿用系统自带旧 JDK。
4. `exec:java` 要 `-pl` 到具体模块（框架篇要到子模块，不能打在聚合父 POM 上）。
5. 综合服务 / 部署的「真实基础设施」未验证前，验收只认离线测试与静态清单。
6. 第 42 课可插队，但默认不依赖密钥；与阶段 C 的 Python AI 课概念可对照，实现栈不同。

## 建议开课路径

**建议从第 01 课 `HelloDemo` 开刀** → 按 `01-java-basics/README.md` 顺序推进。  
命令：仓库根目录 `./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson01_hello.HelloDemo'`。
