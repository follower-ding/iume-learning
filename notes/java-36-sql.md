# Java 第36课：SqlDemo（SQL 入门）

仓库讲义：`follower-ding/java-learning-lab` → `03-web-data/.../learn/data/sql/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`  
本课用 **H2 内存库**（`jdbc:h2:mem:sql_demo`），程序退出后数据消失；不装外部数据库。

## 复述（学了啥）
关系表有列（字段）和行（记录）。`id` 是主键（PK），唯一定位行；`orders.customer_id` 是外键（FK），指向 `customers.id`，防止订单指向不存在顾客。金额用整数分（`amount_cents`）。NULL 表示未知/缺失，判断用 `IS NULL`，不能 `= NULL`。

DDL 定义结构（`CREATE TABLE`），DML 改变或读取数据（`INSERT`/`SELECT`/`UPDATE`/`DELETE`）。`WHERE` 过滤、`ORDER BY` 排序、`GROUP BY` 分组、`JOIN` 按关联列合并。SELECT **逻辑顺序**：FROM/JOIN → WHERE → GROUP BY/聚合 → HAVING → SELECT → ORDER BY；写出语法仍是 `SELECT ... FROM ... WHERE ...`。`PreparedStatement` 的 `?` 只传值，不把输入拼进 SQL。

| API / 概念 | 干什么 |
| --- | --- |
| 表 / 行 / 列 | 关系数据的基本形状 |
| PK / FK | 主键唯一定位；外键引用他表，拒绝脏引用 |
| NULL / `IS NULL` | 缺失值；不能用 `=` 判断 |
| DDL vs DML | 建表结构 vs 增删改查数据 |
| `WHERE` / `ORDER BY` / `GROUP BY` / `JOIN` | 过滤、排序、分组、关联 |
| `PreparedStatement` + `?` | 参数化查询，防拼接注入 |
| H2 `jdbc:h2:mem:...` | 进程内练习库；退出即消失 |
| `JdbcDataSource` + `Connection` | 拿会话；本课只当「执行 SQL 的通道」 |
| `ResultSet.next()` | 逐行读结果，不是一次得到 List |

本课一个类：**SqlDemo**（建表播种 → 过滤 → JOIN 汇总 → UPDATE/DELETE → 计数）。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
./mvnw -pl 03-web-data compile exec:java '-Dexec.mainClass=learn.data.sql.SqlDemo'
```

正文（mvnw exec 实测）：

```text
至少3000分：102:6000，101:3000
订单汇总：
小林:2:9000
阿青:1:2500
删除后订单数：2
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:27:15+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/36-sql/` —— `SqlDemo` / `SqlExercises`。本地 `javac`/`java`（H2 2.4.240 classpath）：Demo 正文对齐；练习见下。

## 原理（小白话）
**表像 Excel**：列是字段，行是一条记录；PK 像工号，FK 像「必须指向已有工号」。  
**NULL ≠ "NULL"**：缺失要用 `IS NULL`；`= NULL` 永远配不上。  
**先 FROM 再 WHERE**：脑子里按逻辑顺序想（先凑表、再过滤、再聚合），手写仍按语法顺序。  
**`?` 只塞值**：`PreparedStatement` 不能改 SQL 结构，但能挡住把用户输入拼进语句。  
**内存库像草稿纸**：H2 mem 适合本课演示；退出进程，纸就扔了。  
**汇总在前、删除在后**：打印 JOIN 汇总时还有 3 笔订单；DELETE id=103 后 COUNT 才变成 2。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| UPDATE/DELETE 忘 WHERE | 整表被改/删 | 先同条件 SELECT 确认范围 |
| `= NULL` | 永远匹配不到 | `IS NULL` / `IS NOT NULL` |
| JOIN 忘 ON | 笛卡尔积，行数/总额错 | 写清关联条件 |
| 以为结果天然有序 | 顺序不稳定 | 明确 `ORDER BY` |
| 插入不存在顾客的订单 | 外键拒绝（保护） | 先有顾客再下单；失败行未保存 |
| 字符串拼 SQL | 注入风险 | `PreparedStatement` + `?` |

## 三级练习结果（先预测再跑，对照参考答案）
均在 **UPDATE/DELETE 之前** 的初始数据上（`SqlExercises`，H2 mem）：

1. **模仿**：`ordersAtLeast(connection, 5000)` → 预测 **102:6000** → 实跑 **至少5000分：102:6000**。
2. **修改**：插入 `INSERT INTO customers(id,name,note) VALUES(3,'小雨',NULL)`，再 `LEFT JOIN` + `COUNT(o.id)` → 预测 **小林:2 / 阿青:1 / 小雨:0** → 实跑一致。LEFT JOIN 保留无订单顾客；`COUNT(o.id)` 不计 NULL。若 INNER JOIN，小雨不会出现。
3. **独立**：`WHERE note IS NULL` → 预测原始数据仅 **1,小林** → 实跑 **1,小林**；`INSERT ... customer_id=99` → 预测 **SQLException（外键）** → 实跑 **JdbcSQLIntegrityConstraintViolationException（外键拒绝 customer_id=99）**。失败后不要把该行视为已保存。

## 卡过啥
- 本课入口在 **03-web-data**（Web 与数据篇），主类 `learn.data.sql.SqlDemo`。
- `./mvnw` 正文对齐；练习副本需 H2 classpath（`com.h2database:h2:2.4.240`）。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第37课、未装外部数据库、未代交验收。
