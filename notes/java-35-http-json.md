# Java 第35课：HttpJsonDemo（HTTP 与 JSON）

仓库讲义：`follower-ding/java-learning-lab` → `03-web-data/.../learn/web/http/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`  
本课**不访问公网**，只用 `127.0.0.1` 回环。Web 与数据篇开篇。

## 复述（学了啥）
HTTP 请求 = 方法 + 路径 + 可选查询 + 头 + 正文；响应 = 状态码 + 头 + 正文。本课把 `HttpServer` 与 `HttpClient` 放同一程序：POST 创建商品、GET 查询、未知 id 得 404。JSON 是**文本**，不等于 Java 对象；引号/反斜杠须正确转义，不能手拼用户输入。先看 `statusCode` 再解释 body。`ProductHttpServer` 只监听本机、限制正文 64 KiB、方法不对回 405+`Allow`。

| API / 概念 | 干什么 |
| --- | --- |
| 方法 / 路径 / 查询 / 头 / 正文 | 组成一条 HTTP 请求报文 |
| 201 / 200 / 400 / 404 / 405 / 413 | 创建成功、查询成功、JSON 无效、不存在、方法不允许、正文过大 |
| `HttpServer` + `127.0.0.1` + 端口 `0` | 本机回环小服务；系统选空闲口 |
| `HttpClient` + 双超时 | 发真实请求；连接与请求都要设上限 |
| Jackson `ObjectMapper` | `readTree` 解析、`writeValueAsBytes` 序列化（负责转义） |
| `record Product` + `ConcurrentHashMap` + `AtomicLong` | 商品载体、并发安全存取、发号 |
| `MAX_BODY_BYTES=64KiB`（多读 1 字节） | 发现超限 → 413 |
| `Allow` 头 | 405 时指明可用方法（如 POST） |
| `AutoCloseable` / try-with-resources | 退出时停服务并 `shutdownNow` executor |

本课两个类：**ProductHttpServer**（本机商品 API）+ **HttpJsonDemo**（客户端演示）。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
./mvnw -pl 03-web-data compile exec:java '-Dexec.mainClass=learn.web.http.HttpJsonDemo'
```

正文（mvnw exec 实测）：

```text
创建状态：201
创建正文：{"id":1,"name":"中文\"书","priceInCents":2500}
查询状态：200
查询正文：{"id":1,"name":"中文\"书","priceInCents":2500}
不存在状态：404
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:25:03+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/35-http-json/` —— `HttpJsonDemo` / `ProductHttpServer` / `HttpJsonExercises`。本地 `javac`/`java`（Jackson 3.1.5 classpath）：Demo 正文对齐；练习见下。

## 原理（小白话）
**请求像填表**：方法说「要干啥」，路径说「找谁」，头说「格式」，正文说「带什么数据」。  
**状态码先说话**：201 创建成了、200 查到了、400 数据坏、404 没有、405 方法不对、413 正文太大——先看码再看正文。  
**JSON ≠ 对象**：它是带规矩的文本；`中文\"书` 里的引号必须转义，交给 Jackson，别手拼。  
**回环自测**：服务和客户端都在本机 `127.0.0.1`，不碰公网。  
**完整路径匹配**：只认 `/products` 与 `/products/{正整数}`，前缀匹配会误收 `/productsXYZ`。  
**关干净**：`stop(0)` + `shutdownNow`，虚拟线程池也要收。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 只读 body 不看状态码 | 把 404/400 当成功 | 先 `statusCode()` |
| 手拼 JSON | 引号/中文转义炸 | Jackson 序列化 |
| 路径前缀匹配 | 误收脏路径 | 完整路径 / 正则整段 |
| 不限正文大小 | 内存被灌满 | 64 KiB + 多读 1 字节判 413 |
| 不设超时 / 不关 executor | 挂死或退不干净 | 双超时 + try-with-resources |
| 打公网 | 离线失败、偏离本课 | 只用 localhost |

## 三级练习结果（先预测再跑，对照参考答案）
预测 → 实跑（`HttpJsonExercises`，本机回环）：

1. **模仿**：POST `{"name":"钢笔","priceInCents":1200}` → 预测 **201** + `{"id":1,"name":"钢笔","priceInCents":1200}` → 实跑 **状态=201 正文={"id":1,"name":"钢笔","priceInCents":1200}**。
2. **修改**：POST `{"name":"钢笔"}`（缺 priceInCents）→ 预测 create **400**（不发号），再 GET `/products/1` → **404** → 实跑 **create状态=400 /products/1状态=404**。
3. **独立**：PUT `/products` → 预测 **405** + `Allow=POST`；GET `/products/99` → **404**；超大 POST（65537 字节）→ **413** → 实跑 **PUT状态=405 Allow=POST /products/99状态=404 超大POST状态=413**（代码断言，无交互 `--serve`）。

## 卡过啥
- 本课入口在 **03-web-data**（Web 与数据篇），主类 `learn.web.http.HttpJsonDemo`。
- `./mvnw` 正文对齐；练习副本需 Jackson classpath（`jackson-databind` + `jackson-core` + `jackson-annotations`）。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第36课、未访问公网、未代交验收。
