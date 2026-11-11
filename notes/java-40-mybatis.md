# Java 第40课：MyBatisDemo（MyBatis / SQL 映射）

仓库讲义：`follower-ding/java-learning-lab` → `04-frameworks/mybatis/.../learn/mybatis/`（只读引用，未改讲义）  
本地跑通：2026-11-11（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`  
本课在**独立 H2 内存库**里跑真实 SQL，**不使用 Spring / Boot**。程序退出后数据消失。下一课才把 MyBatis 交给 Spring。

## 复述（学了啥）
`Product` 是一条商品数据；`ProductMapper` 是 Java 接口，自己不用写实现；真正的 SQL 写在 `ProductMapper.xml`。XML 的 `namespace` 必须等于接口全名 `learn.mybatis.ProductMapper`，每条语句的 `id` 必须等于方法名。`SqlSessionFactory` 按数据源和映射配置只建一次；每次干活另开一个短暂的 `SqlSession`。从 session 拿到的 Mapper 是 MyBatis 生成的代理。

`#{id}` / `#{name}` / `#{price}` 把 Java 值绑成 SQL 参数，类似 JDBC 的 `?`，数据库按值处理；`${...}` 会把文本直接拼进 SQL，用户输入这么写有注入风险，本课全部用 `#{}`。`resultMap` 说明查询列怎么写进 `Product` 的属性；单个简单参数用 `@Param("id")`，让 XML 里的名字可预测。无参构造器和 getter/setter 是结果映射能填上字段的前提。

默认 `openSession()` **不自动提交**。写操作后要 `commit()` 才持久化；失败就 `rollback()`。try-with-resources 保证 session 关闭、连接被释放。H2 URL 的 `DB_CLOSE_DELAY=-1` 让同一 JVM 里最后一条连接关掉后，内存库暂时还留着，这样才能演示跨 session 的提交和回滚；进程退出数据仍会消失。

| API / 概念 | 干什么 |
| --- | --- |
| Mapper 接口 + XML | `namespace`=接口全名，语句 `id`=方法名；代理执行 SQL |
| `#{}` vs `${}` | `#{}` 绑值（类似 `?`）；`${}` 拼接文本，有注入风险 |
| `resultMap` | 查询列 → `Product` 属性 |
| `@Param` | 给简单参数一个 XML 里能对上的名字 |
| `SqlSessionFactory` | 只建一次，可复用 |
| `SqlSession` | 一次数据库工作的会话；用完关闭 |
| `commit()` / `rollback()` | 默认不自动提交；提交才持久化，回滚撤销本 session 的写 |
| `DB_CLOSE_DELAY=-1` | 同一 JVM 内跨 session 仍能看到这片内存库 |

本课三个类型 + 一份 XML：**Product**（实体）、**ProductMapper**（接口）、**ProductMapper.xml**（SQL 与结果映射）、**MyBatisDemo**（建工厂，开三个 session：插入并提交 → 改名后回滚 → 再读确认仍是「纸笔」）。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8'
./mvnw -q -pl 04-frameworks/mybatis -am verify
./mvnw -q -pl 04-frameworks/mybatis compile exec:java '-Dexec.mainClass=learn.mybatis.MyBatisDemo'
```

正文（mvnw exec 实测）：

```text
查询: 纸笔
回滚后: 纸笔
```

Maven `BUILD SUCCESS`（exec Finished at 2026-11-11T12:13:07+08:00）。  
可选：`./mvnw -pl 04-frameworks/mybatis -am verify` → Tests run: 1, Failures: 0（Finished at 2026-11-11T12:13:05+08:00；真实 H2 上的完整 CRUD 与回滚，不用 mock）。  
练习副本（自敲，不进讲义仓）：`/workspace/iume-learning/java/40-mybatis/` —— `MyBatisDemo` / `Product` / `ProductMapper` + `src/main/resources/learn/mybatis/ProductMapper.xml` + `PracticeOne` / `PracticeTwo` / `PracticeThree`。本地 `javac`/`java`，classpath 带 MyBatis 3.5.19 和 H2 2.4.240。XML 从 `src/main/resources` 拷进运行 classpath，不把 XML 只放在 `src/main/java`。副本 Demo 正文对齐；练习见下。

第二个 session 已把商品改为「钢笔」，却没有提交。第三个 session 重新从数据库读到「纸笔」，证明回滚生效。

## 原理（小白话）
**Mapper 像对照表**：接口方法名对上 XML 的 `id`，`namespace` 对上接口全名，MyBatis 生成代理去跑 SQL，不用自己写实现类。  
**`#{}` 像 JDBC 的 `?`**：只把值塞进参数，不会改 SQL 结构；`${}` 是直接拼字符串，别拿来接用户输入。  
**`resultMap` 像填表**：列名对属性名，一行查询结果变成一个 `Product`。  
**`SqlSession` 像窗口**：工厂只建一次，每次干活开一扇短暂的窗；try-with-resources 用完就关。  
**commit 像盖章，rollback 像撕草稿**：默认不自动提交；没盖章的改名，下一扇窗里还是原来的「纸笔」。  
**`DB_CLOSE_DELAY=-1` 像先别收桌子**：同一 JVM 里换窗口还能看到这张内存桌；进程退出桌子还是会收走。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| XML `namespace` 或语句 `id` 与接口不一致 | 调用时找不到 mapped statement | `namespace`=接口全名，`id`=方法名 |
| XML 只放在 `src/main/java` | 资源不会按预期进 classpath，`getResourceAsStream` 读到 null | 放在 `src/main/resources`，随 resources 进 classpath |
| 写了 `insert` / `update` 却忘记 `commit()` | 下一 session 看不到这次写入 | 写操作后显式 `commit()` |
| 不关闭 session | 连接资源无法及时释放 | 始终用 try-with-resources |
| 用 `${name}` 拼接用户输入 | 可能改变 SQL 结构，有注入风险 | 一律用 `#{name}` |
| 只检查同一 session 里的 Java 对象 | 看到的是内存状态，不能证明库是否回滚 | 再开新 session 做 SELECT |
| 练习 classpath 没带 MyBatis / H2 | `javac`/`java` 找不到会话工厂或驱动 | 带上 `mybatis` 3.5.19 和 `h2` 2.4.240 |

## 三级练习结果（先预测再跑，对照参考答案）
均在练习副本独立入口、各自独立 H2 URL（不改课程 `MyBatisDemo` 的演示库）：

1. **模仿**：插入 id=2 的「橡皮」5 元并提交，新 session 再查 → 预测打印 **橡皮** → 实跑一致。新 session 能看到，是因为第一段显式提交。
2. **修改**：插入 id=3 的「尺子」8 元，改为「长尺」10 元并提交；再读出、删除、提交 → 预测依次 **长尺 10** 与 **true**（`findById` 返回 null）→ 实跑一致。
3. **独立**：先提交 id=4 的「书签」3 元；新 session 改成「金属书签」9 元后故意回滚；再开 session 验证 → 预测 **书签 3** → 实跑一致。不能只检查同一个 session 里手里的 Java 对象：那只是内存状态；**重新打开 session 并执行 SELECT**，才能检验数据库是否回滚。

## 卡过啥
- 本课入口在 **04-frameworks/mybatis**，包 `learn.mybatis`，主类 `MyBatisDemo`。本课不是 Boot，不要提前把 MyBatis 交给 Spring。
- 默认 `openSession()` 不自动提交；回滚是否生效，要换一个新 session 再读才能证明。
- `./mvnw` 正文对齐；练习副本需 MyBatis + H2 classpath，且 XML 必须从 resources 进 classpath。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 学习仓默认分支是 `main`（不是 `master`）。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第41课、未代交验收。
