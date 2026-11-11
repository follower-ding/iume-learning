# Java 第41课：LearningApplication（Spring Boot / 整合实战）

仓库讲义：`follower-ding/java-learning-lab` → `04-frameworks/spring-boot/.../learn/boot/`（只读引用，未改讲义）  
本地跑通：2026-11-11（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`  
本课把 **Spring MVC、MyBatis 与 H2** 组合成商品 API。Boot 负责启动和自动配置，MVC 处理请求，MyBatis 跑 XML SQL，Spring 事务管两笔写入一起成功或一起回滚。程序退出后内存库消失。本课只做本地交付，未开第42课或综合服务实验。

## 复述（学了啥）
`@SpringBootApplication` 打开自动配置：classpath 里有 Web、校验、数据源、事务和 MyBatis starter，Boot 就按约定把 Tomcat、`DataSource`、事务管理器和 Jackson 接上。`@MapperScan("learn.boot")` 为 `ProductMapper` 生成代理，再和 `ProductMapper.xml` 对上。`application.yml` 指定本机 `127.0.0.1:8085`、H2 地址、`spring.sql.init.mode: always` 和 XML 位置；`--demo` 会把端口改成 0，插入演示商品后关掉上下文。

分层是固定的：Controller 接 HTTP 和校验，Service 放业务和事务边界，Mapper 只执行 SQL，H2 保存带名称唯一约束的表。请求按「Tomcat → DispatcherServlet → Controller → Service → Mapper → H2」前进，返回的 Java 对象由 Jackson 写成 JSON。

`@Valid` 激活请求 DTO 上的 `@NotBlank`、`@Size(max=100)`、`@Positive`。校验失败由 `@RestControllerAdvice` 变成 400；找不到商品抛 `ProductNotFoundException`，变成 404；数据库唯一约束冲突变成 409。409 依据的是库里的真实约束，不是只在内存里判断。更新和删除还要看受影响行数，并发删掉后更新 0 行仍返回 404。

`@Transactional` 只在**从外部调用服务公开方法**时由 Spring 代理开启事务。`createTwo` 先插入第一件，再插入与已有商品重名的第二件；第二次违反唯一约束，运行时异常穿过代理，第一笔随之回滚。不能只看 HTTP 409，必须再查数据库确认「事务甲」不存在。不要在同一类里写 `this.createTwo(...)` 自调用：调用不经过代理，注解不能以为一定生效。

| API / 概念 | 干什么 |
| --- | --- |
| `@SpringBootApplication` | 自动配置 Web、DataSource、事务等 |
| `@MapperScan` | 扫描 Mapper 接口，生成与 XML 绑定的代理 |
| 分层 | Controller 接请求与 `@Valid`；Service 放业务和事务；Mapper 跑 SQL |
| `@Valid` + Jakarta 约束 | 入口拒绝空白名、超长名、非正价格 |
| 错误码 | 校验失败 400、找不到 404、唯一约束冲突 409 |
| `@Transactional` | 外部调用公开方法时开事务；第二笔失败回滚第一笔 |
| 自调用 `this.method()` | 不经过代理，事务注解可能不生效 |
| `--demo` | 端口 0 启动，插入演示商品后关闭，不占 8085 |

本课主要类型：**LearningApplication**（启动与 `--demo`）、**Product**（行对象）、**ProductMapper** + XML、**ProductService**（事务边界）、**ProductController**（`/products` 与 `/products/batch`）、**ApiExceptionHandler**（400/404/409）、**ProductNotFoundException**。

## 跑通
仓根（`_refs/java-learning-lab`）。Tomcat 11 在 JDK 25 上关闭时需要 `--add-opens`（按第39课补，不改讲义）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED'
./mvnw -q -pl 04-frameworks/spring-boot -am verify
./mvnw -q -pl 04-frameworks/spring-boot compile exec:java '-Dexec.mainClass=learn.boot.LearningApplication' '-Dexec.args=--demo'
```

正文（mvnw exec 实测，忽略 Boot/Tomcat/Hikari 启停日志）：

```text
Boot 创建: 1 演示纸笔 12
```

Maven `BUILD SUCCESS`（verify Finished at 2026-11-11T12:16:38+08:00，Asia/Shanghai；Tests run: 3, Failures: 0，含 `@SpringBootTest(RANDOM_PORT)` 真实 HTTP + 真实 H2）。  
exec `--demo` 完成于 2026-11-11T12:16:47+08:00（Asia/Shanghai）。H2 是内存库，新进程从空表开始，因此演示 id 是 1。  
练习副本（自敲，不进讲义仓）：`/workspace/iume-learning/java/41-spring-boot/` —— `LearningApplication` / `Product` / `ProductService` / `ProductController` / `ProductMapper` + `src/main/resources`（`application.yml`、`schema.sql`、`learn/boot/ProductMapper.xml`）+ `PracticeOne` / `PracticeTwo` / `PracticeThree`。本地 `javac`/`java`，classpath 带 Spring Boot 4.1.1、MyBatis Spring Boot starter 4.1.0、MyBatis 3.5.19、H2 2.4.240、嵌入式 Tomcat 11.0.24、Jackson 3.1.5 及传递依赖。默认入口带 `--demo` 或练习自测后退出，不占 8085。副本 Demo 正文对齐；练习见下。

`--demo` 能打出这一行，是因为自动配置把 Web、数据源和事务接好，`ProductService.create` 在空表里插入「演示纸笔」，生成 id 为 1，随后上下文关闭。

## 原理（小白话）
**自动配置像按零件清单装配**：classpath 里有对应 starter，Boot 就帮你把 Tomcat、H2、事务管理器和 Jackson 接上，不用再手写一整套配置类。  
**分层像柜台、后厨和仓库**：Controller 只接待和验单，Service 做生意并划事务边界，Mapper 按 XML 进出仓库。  
**`@Valid` 像门口安检**：DTO 上的 `@NotBlank` / `@Size` / `@Positive` 必须被 `@Valid` 激活，否则进不了入口检查。  
**错误码像统一窗口**：校验失败 400、找不到 404、唯一约束 409，都由 `@RestControllerAdvice` 转成 JSON，业务方法不用自己拼状态码。  
**`@Transactional` 像一笔生意的窗口**：代理从外面调用公开方法时才开窗；第二笔撞上唯一约束，第一笔也要撤回。  
**不要 `this.method()` 自调用**：`this` 走的是自己，不是代理，窗口可能根本没开，第一笔可能已经落库。  
**409 不够当证据**：HTTP 只说明第二笔失败了；要用 Mapper 再查真实 H2，确认「事务甲」不在库里。  
**`--demo` 像试完就关门**：端口 0 临时占用，插入演示商品后关闭上下文，不要去掉该参数去占默认 8085。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 漏掉 `@MapperScan` | 创建 Controller 时找不到 `ProductMapper` Bean | 在启动类扫描 `learn.boot` |
| XML `namespace`/`id` 与接口不一致 | 调用时找不到 SQL | `namespace`=接口全名，`id`=方法名 |
| 把 `@Transactional` 放在 `this.method()` 自调用目标上 | 代理未介入，第一笔可能已提交 | 由 Controller 或另一个 Bean 调用事务方法 |
| 只在 Java 里检查重名，不加库唯一约束 | 并发请求可能都通过检查 | 表上保留 `UNIQUE`，冲突映射为 409 |
| 不检查 `UPDATE`/`DELETE` 受影响行数 | 记录刚被删掉时会虚报成功 | 0 行仍抛 `ProductNotFoundException` |
| 忘记 `@Valid` | DTO 约束在入口不生效 | 控制器参数加 `@Valid` |
| 去掉 `--demo` 跑默认端口 | 进程占着 8085 不放 | 演示和练习都用端口 0 / `--demo`，用完退出 |
| XML / `schema.sql` 没进 classpath | 找不到映射或建不起表 | 从 `src/main/resources` 拷进运行 classpath |
| JDK 25 关 Tomcat 11 缺少 `--add-opens` | 关闭时线程本地 / RMI 检测告警 | 按第39课在 `JAVA_TOOL_OPTIONS` 补两条 opens，不改讲义 |
| 练习 classpath 没带 Spring Boot / MyBatis / H2 | `javac`/`java` 无法启动自动配置 | 带上 Boot 4.1.1、MyBatis starter、H2 等依赖 |

## 三级练习结果（先预测再跑，对照参考答案）
均在练习副本独立入口、端口 0 与各自独立 H2 URL（不占 8085，不改课程演示库）：

1. **模仿**：`POST /products` 创建「橡皮」5 元 → 预测 **201**，`Location: /products/1`，JSON `{"id":1,"name":"橡皮","price":5}` → 实跑一致。`Location` 指向可再次 GET 的资源；新进程空表所以 id=1。
2. **修改**：价格 0、名称 101 字、未知 id → 预测依次 **400**（入口 `@Valid` + `@Positive`）、**400**（入口 `@Valid` + `@Size(max=100)`）、**404**（Service 查不到后抛异常，Advice 转状态）→ 实跑一致。前两笔在控制器入口被拒，第三笔到了服务层。
3. **独立**：先创建「纸笔」得 201，再 `POST /products/batch`（firstName「事务甲」、secondName「纸笔」、price 10）→ 预测 **409**，Mapper 再查「事务甲」为 **null**，「纸笔」仍在 → 实跑一致。只看 409 不能证明第一笔回滚；必须查真实 H2。`createTwo` 由 Controller 经代理调用，`@Transactional` 生效。若改成类内 `this.createTwo(...)` 自调用，代理不拦截，第一笔可能已经提交。

## 卡过啥
- 本课入口在 **04-frameworks/spring-boot**，包 `learn.boot`，主类 `LearningApplication`。必须带 `--demo`，不要去掉该参数去占 8085。
- `@Transactional` 只对外部调用的公开方法生效；自调用不会走代理。回滚是否生效，要再查数据库，不能只看 409。
- 409 来自数据库唯一约束；校验失败 400 和找不到 404 是另外两条路径。
- `./mvnw` 正文对齐；练习副本需 Spring Boot / MyBatis / H2 classpath，且 `schema.sql` 与 Mapper XML 必须从 resources 进 classpath。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。JDK 25 关 Tomcat 11 需 `--add-opens`。
- 学习仓默认分支是 `main`（不是 `master`）。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第42课或综合服务实验、未代交验收。
