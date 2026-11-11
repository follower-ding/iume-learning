# Java 第39课：MvcDemo（Spring MVC / 请求处理）

仓库讲义：`follower-ding/java-learning-lab` → `04-frameworks/spring-mvc/.../learn/spring/mvc/`（只读引用，未改讲义）  
本地跑通：2026-11-11（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`  
本课用 **普通 Spring MVC + 嵌入式 Tomcat 11**，**不使用 Boot**。默认演示自行发请求并退出，不用 `--serve`。

## 复述（学了啥）
Tomcat 接收本机 HTTP；`DispatcherServlet` 是前端控制器，按路径和方法找到 `GreetingController` 里对应的处理方法。控制器读参数、返回 `Message` 对象；Jackson 3 消息转换器再把这个 Java 对象写成 JSON 响应体。

`@RestController` 表示方法返回值就是响应体（不是页面名）。`@GetMapping` / `@PostMapping` 定义路由；`@RequestParam("name")` 取查询参数；`@PathVariable("id")` 取路径段；`@RequestBody` 从 JSON 反序列化成对象。参数名要写在注解里，不要依赖编译器是否保留参数名元数据。

`@EnableWebMvc` 打开 MVC 配置。`@RestControllerAdvice` 配 `@ExceptionHandler`，把空白姓名抛出的 `IllegalArgumentException` 变成 JSON 400（`{"error":"name 不能为空"}`）。缺少查询参数、损坏的 JSON 也是 400；没有映射的路径是 404。MockMvc 在内存里检查控制器链路；真实端口测试还检查 Tomcat 和 Servlet 接线。

本机监听要两处都设：`tomcat.setHostname("127.0.0.1")` 只是虚拟主机名，还须给 connector 设 `address=127.0.0.1`。端口 `0` 让系统分配空闲端口。传给 `DispatcherServlet` 的 `AnnotationConfigWebApplicationContext` **先不要 refresh**，等 Servlet 初始化时再启动。必须把 `/` 映射到 dispatcher，否则 Tomcat 起来了控制器也进不去。用 try-with-resources 在正常及异常退出时停止 Tomcat、关闭上下文、删除临时目录；关机 hook 与正常关闭可能同时发生，关闭过程要幂等。

| API / 概念 | 干什么 |
| --- | --- |
| `DispatcherServlet` | 前端控制器：按映射找到控制器方法 |
| `@RestController` | 返回值直接写成响应体 |
| `@GetMapping` / `@PostMapping` | 声明 HTTP 方法与路径 |
| `@RequestParam` / `@PathVariable` / `@RequestBody` | 查询参数、路径段、JSON 体 → 方法参数 |
| Jackson 消息转换器 | Java 对象 ↔ JSON |
| `@RestControllerAdvice` + `@ExceptionHandler` | 业务异常变成 JSON 状态码 |
| `@EnableWebMvc` + `WebConfig` | 纯 MVC 配置，注册控制器与异常处理器 |
| 本机监听 | hostname + connector `address` 都设 `127.0.0.1`；端口 0 系统分配 |
| MockMvc vs 真实端口 | 内存查控制器链路；真实端口再查 Tomcat / Servlet 接线 |

本课四个类：**GreetingController**（GET 查询、GET 路径、POST JSON）、**ApiExceptionHandler**（非法参数 → 400）、**WebConfig**（`@EnableWebMvc` + 两个 `@Bean`）、**MvcDemo**（嵌入式 Tomcat + DispatcherServlet，默认自测后退出）。

## 跑通
仓根（`_refs/java-learning-lab`）。Tomcat 11 在 JDK 25 上关闭时需要 `--add-opens`，否则会有线程本地 / RMI 检测告警：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED'
./mvnw -q -pl 04-frameworks/spring-mvc -am verify
./mvnw -q -pl 04-frameworks/spring-mvc compile exec:java '-Dexec.mainClass=learn.spring.mvc.MvcDemo'
```

正文（mvnw exec 实测，端口由系统分配为 **41693**）：

```text
MVC 端口: 41693
GET /greetings: 200 {"message":"你好，小明"}
```

Maven `BUILD SUCCESS`（exec Finished at 2026-11-11T12:08:58+08:00）。  
可选：`./mvnw -pl 04-frameworks/spring-mvc -am verify` → Tests run: 4, Failures: 0（Finished at 2026-11-11T12:08:47+08:00；含 MockMvc + 真实端口）。  
练习副本（自敲，不进讲义仓）：`/workspace/iume-learning/java/39-spring-mvc/` —— `MvcDemo` / `GreetingController` / `WebConfig` / `ApiExceptionHandler` + `PracticeOne` / `HealthController`+`PracticeTwo` / `UppercaseController`+`PracticeThree`。本地 `javac`/`java`，classpath 带 Spring Framework 7.0.9 `spring-webmvc`、嵌入式 Tomcat 11.0.24、Jackson 3.1.5、`spring-test` 及传递依赖。副本 Demo 正文对齐（另分配端口 33801）；练习见下。

`GET /greetings?name=小明` 能出 `你好，小明`，是因为查询参数绑定到 `greet`，返回的 `Message` 再被写成 JSON；状态 200 是该 GET 方法的默认成功码。POST 同类请求应是 201（`@ResponseStatus(CREATED)`）。

## 原理（小白话）
**DispatcherServlet 像前台接待**：HTTP 先到它手里，再按路径和方法把活分给控制器。  
**控制器像具体柜员**：用注解声明接什么参数、走哪条路由，返回 Java 对象而不是自己拼 JSON 字符串。  
**参数绑定像填表**：查询串、路径段、JSON 体分别对上 `@RequestParam`、`@PathVariable`、`@RequestBody`。  
**Jackson 像翻译官**：Java 记录类型进出都变成 JSON。没有这个依赖，返回值变不成响应体。  
**异常处理像统一窗口**：控制器只管抛 `IllegalArgumentException`，`@RestControllerAdvice` 统一改成 JSON 400。  
**本机监听像只开内线**：hostname 和 connector address 都钉在 127.0.0.1，外面进不来；端口 0 则是“空闲窗口号，用时再发”。  
**try-with-resources 像用完关门**：停 Tomcat、关上下文、删临时目录；hook 与正常退出抢着关时，幂等门只清理一次。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 忘记把 `/` 映射到 DispatcherServlet | Tomcat 启动了，控制器仍 404 | `addServletMappingDecoded("/", "dispatcher")` |
| 只设 `tomcat.setHostname` | 可能监听到非本机地址 | 同时设 connector `address=127.0.0.1` |
| 漏掉 Jackson 依赖 | 无法把返回值写成 JSON | classpath 带 `jackson-databind` 3.1.5 |
| 中文查询参数不 URL 编码 | 客户端可能构造无效 URL | `小明`→`%E5%B0%8F%E6%98%8E`，`阿青`→`%E9%98%BF%E9%9D%92` |
| 打开容器时先 `refresh` 再交给 Servlet | 与本课接线方式不一致，初始化时机错乱 | 未刷新的上下文交给 `DispatcherServlet`，由 Servlet 初始化启动 |
| JDK 25 关 Tomcat 11 缺少 `--add-opens` | 关闭时线程本地 / RMI 检测告警 | 运行前设置两条 `--add-opens` |
| MockMvc 练习忘挂 `ApiExceptionHandler` | 空白姓名不会变成 400 JSON | `setControllerAdvice(new ApiExceptionHandler())` |
| 练习 classpath 没带 Spring MVC / Tomcat | `javac`/`java` 找不到 Servlet 与注解 | 带上 `spring-webmvc`、`tomcat-embed-core`、Jackson、`spring-test` |
| 默认演示用 `--serve` | 进程占着不放 | 默认入口自测后退出；手工访问才用 `--serve` |

## 三级练习结果（先预测再跑，对照参考答案）
均在练习副本独立入口中（不改课程 `GreetingController` / `WebConfig`）：

1. **模仿**：请求 `/greetings?name=阿青`（UTF-8 编码 `%E9%98%BF%E9%9D%92`）→ 预测 **200 `{"message":"你好，阿青"}`** → 实跑一致。未用 `--serve`，端口 0 启动后自测退出。
2. **修改**：新增 `HealthController` 的 GET `/health` 返回 `{"message":"运行中"}`，用 MockMvc `standaloneSetup` 断言 → 预测 **200，字段为「运行中」** → 实跑一致。先过 MockMvc，不必把该控制器注册进课程 `WebConfig`。
3. **独立**：新增 POST `/uppercase`，请求 `{"name":"abc"}` 预测 **201 `{"message":"ABC"}`**，空白姓名预测 **400** → 实跑一致。`@RequestBody` 负责反序列化；空白姓名抛的异常要靠 `ApiExceptionHandler` 才变成 400。

## 卡过啥
- 本课入口在 **04-frameworks/spring-mvc**，包 `learn.spring.mvc`，主类 `MvcDemo`。本课不是 Boot。
- `DispatcherServlet` 找不到映射就是 404；缺参数或坏 JSON 是 400，和「空白姓名被异常处理器转 400」不是同一条路径，结果碰巧都是 400。
- 只设 hostname 不等于只监听本机，还要设 connector 的 `address`。
- 默认演示不要用 `--serve`。Tomcat 11 + JDK 25 关闭需要 `--add-opens`。
- `./mvnw` 正文对齐；练习副本需 Spring MVC / 嵌入式 Tomcat / Jackson / spring-test classpath。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 学习仓默认分支是 `main`（不是 `master`）。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第40课、未代交验收。
