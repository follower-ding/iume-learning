# Java 第05课：ConditionsDemo（条件判断）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson05_conditions/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
程序要根据数据走不同路径：`grade(int score)` 先拦越界（0~100 以外 `throw`），再从高分到低分判断。  
`score=85`：不满足 `>=90`，满足 `>=60`，返回「及格」。  
`main` 打印两行：成绩与等级。`grade` 有返回值，调用回来后才打下一行。  
正文：`成绩=85` / `等级=及格`。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson05_conditions.ConditionsDemo'
# 可选：
# ./mvnw -pl 01-java-basics test '-Dtest=learn.lesson05_conditions.ConditionsDemoTest'
```

正文两行与讲义一致；Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:29:48+08:00）。可选测试亦通过。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/05-conditions/` —— `ConditionsDemo` 与讲义三级规则一致；`WeatherProbe`（9冷 / 10舒适 / 25热）；`GradeWithGoodProbe`（加「良好」80~89，仅练习）；`ShippingFeeProbe`（98→10，99→0，负数拒绝）。

## 原理（小白话）
**if / else if / else 从上往下**：先看第一个条件；成立就执行该分支，**后面不再查**。所以高分分支必须写在低分前面——若先写 `>=60`，95 也会被当成「及格」。`else` 不写条件，表示前面全不成立。  
**条件必须是 boolean**：括号里要是真/假（如 `score >= 90`），不能写「差不多」的模糊话。  
**有返回值的方法**：`grade` 返回 `String`；`main` 里 `grade(score)` 先算出等级，再拼进 `println`。命中分支用 `return` 立刻把结果交出去。  
**越界 throw**：`score` 不在 0~100 时 `throw new IllegalArgumentException(...)` 会打断正常流程（本课只需知道「会停下来报错」；异常细节第26课再学）。  
对比 Python（零基础白话）：Python 的 `if / elif / else` 顺序规则一样——从上往下命中即停；高分判断也要写在前面。Java 用花括号和分号更醒目；两边条件都要是真假值。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐，无编译/路径错误。
- 提醒：分支顺序搞反会「吃掉」高分——练习里刻意把「良好」插在「优秀」和「及格」之间验证。
- 练习确认：天气三段、良好边界（90/89/80/79）、运费 98/99 与负数拒绝均符合预期。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第06课。
