# Java 第37课：JdbcDemo（JDBC 与事务）

仓库讲义：`follower-ding/java-learning-lab` → `03-web-data/.../learn/data/jdbc/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`  
本课用 **H2 内存库**（`jdbc:h2:mem:transfer_demo`），程序退出后数据消失；不装外部数据库。

## 复述（学了啥）
JDBC 把 SQL 接到 Java：`DataSource` 拿连接，`Connection` 是会话，`PreparedStatement` 带 `?` 参数，`ResultSet` 是查询游标。`?` 只绑**值**，不要拼用户输入进 SQL。

转账要原子：同一 `Connection` 上 `setAutoCommit(false)`，扣款 + 入账都成功才 `commit()`；任一步失败就 `rollback()`。成功那笔已经 commit，失败那笔的回滚不会把成功结果抹掉——所以 Demo 里成功后小林=400，失败回滚后仍是 400，不是初始 500。

金额用 int 分；扣款 `balance_cents>=?`，入账 `balance_cents=balance_cents+?` 且带上界防溢出。库内原子加减，避免「先 SELECT 再覆盖 UPDATE」的丢更新。H2 mem 最后连接关掉表就没了，所以 Demo 留一个 keeper Connection。

| API / 概念 | 干什么 |
| --- | --- |
| `DataSource` / `Connection` | 取会话；事务边界在同一连接上 |
| `setAutoCommit(false)` + `commit`/`rollback` | 多语句要么全成要么全撤 |
| `PreparedStatement` + `?` | 参数化；姓名里的引号/DROP 只当字符串 |
| 扣款 `WHERE balance_cents>=?` | 余额不足或不存在 → 0 行 → `IllegalStateException` |
| 入账原子 `balance_cents=balance_cents+?` | 并发入账不丢钱；上界防 int 溢出 |
| keeper `Connection` | H2 mem 保活，短连接才能看到同一库 |

本课三个类：**AccountRepository**（单条读写，各自开关连接）、**TransferService**（事务所有者）、**JdbcDemo**（建表播种 → 成功转 → 失败回滚）。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
./mvnw -pl 03-web-data compile exec:java '-Dexec.mainClass=learn.data.jdbc.JdbcDemo'
```

正文（mvnw exec 实测）：

```text
成功后：小林=400，阿青=300
失败原因：收款账户不存在
回滚后：小林=400，阿青=300
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:29:13+08:00）。  
可选：`./mvnw -pl 03-web-data -Dtest=learn.data.jdbc.TransferServiceTest test` → Tests run: 7, Failures: 0（Finished at 2026-11-10T03:29:20+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/37-jdbc/` —— `JdbcDemo` / `AccountRepository` / `TransferService` / `JdbcExercises`。本地 `javac`/`java`（H2 2.4.240 classpath）：Demo 正文对齐；练习见下。

## 原理（小白话）
**连接像窗口**：同一窗口里关自动提交，两笔 UPDATE 才算一笔生意。  
**commit 像盖章**：成功转账已盖章；失败那笔撕掉草稿，盖章过的不动。  
**`?` 只塞值**：`张'; DROP TABLE...` 仍是姓名字符串，表不会被删。  
**原子加减**：数据库自己 `余额=余额+金额`，别先读再写覆盖。  
**keeper**：H2 内存库像草稿纸，最后一个握笔的人走了纸就扔；keeper 一直握着。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 两次 UPDATE 不同 Connection | 组不成一个事务 | 同一 Connection + 关 autoCommit |
| 失败路径不 rollback | 依赖关连接的隐式行为，不可靠 | catch 里显式 rollback；rollback 失败 addSuppressed |
| 先 SELECT 再覆盖 UPDATE | 并发丢更新 | `balance_cents=balance_cents±?` |
| 姓名拼进 SQL | 注入，表可能被拆 | `PreparedStatement` + `?` |
| `target + amount` 不查上界 | int 溢出变负 | `balance_cents<=Integer.MAX_VALUE-amount` |
| H2 mem 无 keeper | 短连接看不到表/数据 | 主流程 try-with-resources 留 keeper |

## 三级练习结果（先预测再跑，对照参考答案）
均在独立 H2 mem 上（`JdbcExercises`）：

1. **模仿**：转 50 → 预测 **450/250**；再失败转 id=99 → 回滚后仍 **450/250** → 实跑一致。
2. **修改**：转 501 → 预测 **IllegalStateException**，仍 **500/200**；转 id=99 → 预测 **IllegalArgumentException** + rollback，仍 **500/200** → 实跑一致。
3. **独立**：插入 `张'; DROP TABLE accounts; --` → 预测 **原样读回**，小林余额仍 **500**（表还在）→ 实跑一致。并发：付款 1、3 各 500，收款 2 初始 200，两线程各转 100 → 预测 **400/400/400** → 实跑一致（库内原子加款，不会写成基于旧余额的 300）。

## 卡过啥
- 本课入口在 **03-web-data**，包 `learn.data.jdbc`，主类 `JdbcDemo`。
- 成功转账已 commit，失败回滚**不会**回到初始 500——回滚只撤失败那笔。
- `./mvnw` 正文对齐；练习副本需 H2 classpath（`com.h2database:h2:2.4.240`）。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第38课、未装外部数据库、未代交验收。
