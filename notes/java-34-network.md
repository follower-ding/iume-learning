# Java 第34课：NetworkDemo（网络与 HTTP 客户端）

仓库讲义：`follower-ding/java-learning-lab` → `02-java-advanced/.../learn/advanced/network/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`  
本课**不访问公网**，只用 `localhost` 回环。

## 复述（学了啥）
IP 定位主机，端口定位服务；HTTP 在 TCP 上规定请求/响应/状态码/正文。`localhost` 只指本机。`HttpClient` 要设**连接超时**和**请求超时**；404 仍是有效响应，必须看状态码，不能只读正文。端口 `0` 让系统选空闲口；`try-with-resources` 同时关服务器与客户端（含 executor）。

| API / 概念 | 干什么 |
| --- | --- |
| `InetAddress.getLoopbackAddress()` + 端口 `0` | 本机回环监听；系统选空闲端口，避免冲突 |
| `HttpServer.createContext("/hello", …)` | 注册路径，回写 UTF-8 正文 |
| `HttpClient.newBuilder().connectTimeout(...)` | 建立连接的等待上限 |
| `HttpRequest.newBuilder(uri).timeout(...).GET()` | 整次请求超时 + GET |
| `HttpClient.send` + `BodyHandlers.ofString()` | 发请求、读字符串正文；可能抛 IO/中断 |
| `LocalGreetingServer` / `close()` | 启停本机服务 + `shutdownNow` executor |
| `NetworkDemo.fetch(URI)` | 带双超时的 GET；404 是状态码不是异常 |

本课两个类：**LocalGreetingServer**（本机服务）+ **NetworkDemo**（客户端演示）。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 02-java-advanced compile exec:java '-Dexec.mainClass=learn.advanced.network.NetworkDemo'
```

正文（mvnw exec 实测）：

```text
状态：200
正文：你好，HTTP！
不存在的路径：404
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:23:13+08:00）。  
可选测试：`./mvnw -pl 02-java-advanced -am test -Dtest=learn.advanced.network.NetworkDemoTest` → Tests run: 1, Failures: 0（Finished at 2026-11-10T03:23:22+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/34-network/` —— `NetworkDemo` / `LocalGreetingServer` / `NetworkExercises`。本地 `javac`/`java`：正文对齐；练习见下。

## 原理（小白话）
**地址像门牌，端口像房间号**：HTTP 是房间里说话的规矩（请求行、状态码、正文）。  
**回环 = 自言自语**：`localhost` 只在本机转一圈，不会变成公网服务。  
**404 ≠ 程序炸了**：路径没有时服务器仍回一条有效 HTTP 响应；连接失败才是 IO 异常。  
**超时是保险丝**：连不上别干等，请求太久也别干等。  
**端口 0**：让系统挑空房，避免写死 8080 撞车。  
**关干净**：服务停了还要把 executor `shutdownNow`，否则非守护线程可能拖住进程。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 默认打公网示例站 | 离线/本课环境失败 | 只用本机回环 |
| 固定端口 | 与其他程序冲突 | 端口 `0` |
| 忘记关 executor | 进程退不干净 | `stop` + `shutdownNow` |
| 不设超时 | 故障时长挂 | connect + request 双超时 |
| 把 404 当 Java 异常 | 误判错误类型 | 先看 `statusCode()` |

## 三级练习结果（先预测再跑，对照参考答案）
预测 → 实跑（`NetworkExercises`，本机回环）：

1. **模仿**：`/hello` 正文改为「本地服务」→ 预测状态 `200`、正文 `本地服务` → 实跑 **状态=200 正文=本地服务**。
2. **修改**：新增 `/ping` → 预测 `200` / `pong` → 实跑 **/ping 状态=200 正文=pong**。
3. **独立**：`status(URI)` → 预测 `/hello=200`、`/missing=404` → 实跑 **/hello=200 /missing=404**。

## 卡过啥
- 本课入口在 **02-java-advanced**（进阶篇），主类 `learn.advanced.network.NetworkDemo`。
- `./mvnw` 正文对齐；`NetworkDemoTest` 1 过；练习副本 `javac`/`java` 亦通。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第35课、未访问公网、未代交验收。
