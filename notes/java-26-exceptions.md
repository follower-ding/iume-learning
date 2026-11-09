# Java 第26课：ExceptionsDemo（异常）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson26_exceptions/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
`try` 包住可能失败的语句，匹配的 `catch` 处理，`finally` 无论正常/异常都会跑（进程被杀等除外）。`throw` 主动抛异常对象；`throws` 在方法签名声明可能外传的**受检**异常。`IOException` 是受检（须 catch 或继续声明）；`IllegalArgumentException` / 本课两个业务异常是**非受检**（`RuntimeException` 子类）。try-with-resources 自动关 Reader；包装底层故障时把原异常传给 cause，别写空 catch。订单场景：缺商品 → `ProductNotFoundException`；状态非法 → `InvalidOrderStateException`；仓储掉线 → `IllegalStateException(..., cause)`。

| 写法 | 作用 | 本课用法 |
| --- | --- | --- |
| `try / catch / finally` | 捕获与收尾 | 读到 A → 抛数量无效 → 捕获消息 → finally 收尾 |
| `throws IOException` | 受检异常声明 | `readOne()` 向调用方声明 |
| try-with-resources | 自动关资源 | `StringReader` 读完即关 |
| `ProductNotFoundException` | 业务：缺商品 | 与存储故障区分 |
| `InvalidOrderStateException` | 业务：状态不允许 | 二次 confirm / 非法 cancel |

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson26_exceptions.ExceptionsDemo'
```

正文与讲义一致：

```text
读取=A
捕获=数量无效
收尾=执行
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:07:25+08:00）。  
可选测试：`./mvnw -pl 01-java-basics '-Dtest=OrderExceptionTest' test` → Tests run: 9, Failures: 0（Finished at 2026-11-10T03:07:32+08:00，BUILD SUCCESS）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/26-exceptions/` —— `ExceptionsDemo` / `ProductNotFoundException` / `InvalidOrderStateException` / `ExceptionsExercises`。本地 `javac`/`java`：讲义正文对齐；练习1 qty0/负数均 `quantity must be at least one`，qty1 小计 `100.00`；练习2 首次 cancel→`CANCELLED`，再 cancel→`cannot cancel order o-1 from state CANCELLED`；练习3 wrap 消息含 `p-1` 且 `cause` 同一引用，业务 `ProductNotFoundException` 原样传播。

## 原理（小白话）
**受检 vs 非受检**：受检强迫调用方处理或声明（如 IO）；业务规则失败与编程错误多用非受检，避免签名到处 `throws`。  
**为什么自定义业务异常**：`null`/布尔分不清「缺货」还是「库挂了」；专用类型让边界层决定提示用户还是记日志。  
**保留 cause**：`new IllegalStateException("failed to load product: " + id, cause)` 消息给上下文，`getCause()` 给排查根因；别只抄消息丢堆栈。  
**finally 只收尾**：关资源、打日志可以；不要在 finally `return`，会盖掉 try 的返回值或正在传播的异常。  
**异常不代替普通分支**：购物车为空用 `if`；数量 0 是调用错误用 `IllegalArgumentException`；缺商品才抛业务异常。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 空 `catch (Exception ignored) {}` | 失败被吞，后续在脏状态继续 | 捕获后处理或再抛，至少记日志 |
| 新异常只抄消息丢 cause | 排查丢根因堆栈 | 用带 cause 的构造 |
| 业务异常再包一层 | 调用方认不出缺货 | `ProductNotFoundException` 原样传播 |
| finally 里 return | 覆盖原返回值/异常 | finally 只做收尾 |
| 用异常控制正常流程 | 难读、难测、贵 | 可预期结果用 Optional/`if` |

## 卡过啥
- 本课 `./mvnw` 正文对齐；`OrderExceptionTest` 9/0；练习副本 `javac`/`java` 亦通。
- 练习确认：1) 数量边界属调用错误；2) 只允许 `CREATED→CANCELLED`；3) 故障包装同 cause，业务异常不包。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第27课、未代交验收。
