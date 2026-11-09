# Java 第18课：CommonApisDemo（字符串与常用API）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson18_common_apis/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
`String` 不可变：`replace` 返回新串，原变量仍指向旧串——所以 `originalText()` 里丢了返回值，仍是 `abc`；接住返回值才是 `axc`。  
`equals` 比内容（`"abc".equals(new String("abc"))` → true），`==` 比是不是同一引用。`split(",")` 按逗号拆段。  
`Integer` 包装 `int`：自动装箱塞 7，拆箱取回；`null` 直接拆箱会炸，所以 `requireNumber` 先拒 null。  
`LocalDate.parse` + `plusDays(1)` 得次日，原日期不变。金额用 `new BigDecimal("0.1")` 十进制字符串，别用 `new BigDecimal(0.1)` 吃浮点近似；`setScale(2, HALF_UP)` 定两位舍入。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson18_common_apis.CommonApisDemo'
```

正文八行与讲义一致：

```text
原字符串=abc
新字符串=axc
内容相同=true
分段数=3
拆箱=7
次日=2026-10-01
金额和=0.3
四舍五入=1.01
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:52:07+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/18-common-apis/` —— 自敲 `CommonApisDemo`（讲义八行 + 三道练习）。本地 `javac`/`java`：讲义八行对齐；练习1 `"红,蓝"`→分段数=2；练习2 `2026-12-31` 次日→`2027-01-01`；练习3 `0.7+0.8` 两位 HALF_UP→`1.50`。

## 原理（小白话）
**String 不可变**：`"abc".replace("b","x")` 造出新串 `"axc"`；若没把返回值赋回变量，原引用仍指 `"abc"`。  
**equals vs ==**：`equals` 比字符内容；`==` 比两个引用是不是同一个对象（`new String("abc")` 内容同、引用不同）。  
**split**：`text.split(",")` 按逗号切开，`"甲,乙,丙"` → 3 段。  
**装箱/拆箱**：`Integer wrapped = 7` 自动装箱；拆箱取 `int` 时若是 `null` → NPE，先判空再拆。  
**LocalDate 不可变**：`parse` 读日期，`plusDays(1)` 返回新日期对象，旧的不动。  
**BigDecimal**：`new BigDecimal("0.1")` 按十进制字面精确；`new BigDecimal(0.1)` 先经 double 近似再塞进去会脏。`setScale(2, RoundingMode.HALF_UP)` 明确保留两位与四舍五入规则（`1.005` → `1.01`）。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 丢弃 `replace` 返回值 | 原串不变 | 接住返回值或链式用 |
| `==` 比字符串内容 | 可能 false | 用 `equals` |
| `new BigDecimal(0.1)` | 带入浮点脏值 | `new BigDecimal("0.1")` |
| 拆箱 null Integer | NPE | 先判 null |

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文八行对齐；本地练习副本 `javac`/`java` 亦通。
- 练习确认：1) `"红,蓝"`→2段；2) `2026-12-31` 次日→`2027-01-01`；3) `0.7+0.8` 两位→`1.50`。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第19课、未代交验收。
