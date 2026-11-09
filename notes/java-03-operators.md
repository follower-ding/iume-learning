# Java 第03课：OperatorsDemo（运算符）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson03_operators/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
在 `main` 里用运算符算新结果：`7 / 3` 得每人 2 个、`7 % 3` 剩 1；`(apples >= people)` 比较得 `true`。  
会员优惠：`member && amount >= 100` 要求两边都真。`count += 2` 再 `count++`，从 0→2→3。  
圆括号里的表达式先算完，再和左边文字用 `+` 拼成字符串。  
五行正文：`每人得到=2` / `剩余=1` / `够分吗=true` / `可以优惠=true` / `数量=3`。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson03_operators.OperatorsDemo'
```

正文五行与讲义一致；Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:26:22+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/03-operators/` —— `OperatorsDemo` 与讲义一致；`MinutesProbe`（17÷5→3 余2）；`DiscountProbe`（`member=false` 或 `amount=99` 都得 false）；`CanEnterProbe`（年龄 18~60 且持票才可进）。

## 原理（小白话）
**整数 `/` 与 `%`**：两个 `int` 相除仍是整数，`7 / 3 = 2`（整除，丢掉小数）；`%` 是余数，`7 % 3 = 1`。想要小数要写成 `7.0 / 3`。  
**比较运算符**：`== != > >= < <=` 结果是 `boolean`（真/假）。`=` 是赋值，`==` 才是比相等。  
**`&&`**：左右两边都真才是真；一边假就整体假（还会短路：左边已假时右边不跑）。  
**`+=` 与 `++`**：`count += 2` 等于 `count = count + 2`；`count++` 等于 `count = count + 1`。  
**圆括号先算再拼接**：`"够分吗=" + (apples >= people)` 先算出 `true`，再和文字拼；不加括号可能被算错优先级。  
对比 Python（零基础白话）：Python3 里 `7 / 3` 是**真除**得 `2.333...`，要整除得写 `7 // 3`；Java 两个整数用 `/` 就是整除。两边都有 `%` 取余，意思差不多。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，五行正文对齐，无编译/路径错误。
- 仍要提醒：Maven INFO 可能盖住正文——在输出尾部找「每人得到=2」起的五行。
- 练习里确认：`&&` 改 `member=false` 或金额改 99，优惠都变 false；年龄 17 或无票，`canEnter` 也是 false。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第04课。
