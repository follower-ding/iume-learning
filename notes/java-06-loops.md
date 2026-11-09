# Java 第06课：LoopsDemo（循环）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson06_loops/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
循环让同一段代码重复执行：次数清楚用 `for`，更看重结束条件用 `while`。  
`main` 先 `for` 打印「for 第 1/2/3 次」；再用 `while` 从 3 倒数到 1；最后调用 `sumTo(5)` 得到 15。  
`sumTo` 先检查 `end` 在 0~10000，再用 for 把 1 到 end 累加进 `sum` 后返回。  
正文七行：三行 for → 三行倒计时 → `1 到 5 的和=15`。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson06_loops.LoopsDemo'
# 可选：
# ./mvnw -pl 01-java-basics test '-Dtest=learn.lesson06_loops.LoopsDemoTest'
```

正文七行与讲义一致；Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:31:39+08:00）。可选测试 2 例通过。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/06-loops/` —— `LoopsDemo`；`EvenForProbe`（2~10 偶数）；`SumToWhileProbe`（while 版 sumTo，0→0 / 5→15）；`TimesTableProbe`（九九表，外层行号、内层该行算式）。

## 原理（小白话）
**for 三部分**：`for (初始化; 条件; 步进)` —— 首次进入做初始化（如 `int number = 1`）；每轮前看条件（`number <= 3`）；本轮结束后做步进（`number++`）。条件变假就停。  
**while 看结束条件**：先准备变量（如 `countdown = 3`），只要条件真就进循环体；**体内必须更新变量**（`countdown--`），否则永远为真 → **无限循环**。  
**累加器**：`sum` 先置 0，每轮 `sum += number`，把结果一点点攒起来；别在循环里又把 `sum` 设回 0。  
**sumTo 范围检查**：`end < 0 || end > 10_000` 就 `throw`，避免无意义超大循环，也让 `int` 结果仍安全。  
**边界 / off-by-one**：`<` 与 `<=` 差一轮——例如 `number < 3` 只打到 2，少一轮。  
对比 Python（零基础白话）：Python `for x in range(1, 4)` 本质也是「起止+步进」；`while` 同样要记得改条件变量。Java 的 `for` 把头三件写在一行更醒目；两边忘记更新都会死循环。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文七行对齐；可选 `LoopsDemoTest` 2 例通过。
- 提醒：while 倒计时若漏写 `countdown--` 会卡死；练习里刻意对照 for/while 两种 sumTo。
- 练习确认：偶数 2/4/6/8/10；while `sumTo(0)=0`、`sumTo(5)=15`；九九表 9 行，首行 `1×1=1`，末行含 `9×9=81`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第07课。
