# Java 第04课：InputDemo（键盘输入）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson04_input/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
以前数据都写死在源码里；本课用 `Scanner` 从键盘读姓名和年龄。  
流程：`print` 提示姓名 → `nextLine()` 读一整行 → 提示年龄 → `hasNextInt()` 先检查下一段能不能当整数；不行就打印「年龄必须是整数，程序结束。」并 `return`；行才 `nextInt()`，再检查 0~150 范围，最后打印明年年龄。  
合法「小明」+「18」核心最后一行：`你好，小明，明年你 19 岁。`  
非法「小明」+「十八」：`年龄必须是整数，程序结束。`（不输出问候）

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
printf '小明\n18\n' | ./mvnw -q -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson04_input.InputDemo'
printf '小明\n十八\n' | ./mvnw -q -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson04_input.InputDemo'
```

合法正文：`请输入姓名：请输入年龄：你好，小明，明年你 19 岁。`  
非法正文：`请输入姓名：请输入年龄：年龄必须是整数，程序结束。`  
Maven `-q` 已压噪音；两次核心输出与讲义一致（Finished at 2026-11-10T02:27+08:00 附近）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/04-input/` —— `InputDemo` 与讲义一致；`SumTwoProbe`（2+3→和=5）；`AdultCheckProbe`（18→已成年，17→未成年）；`ProductTotalProbe`（笔/3/2→笔总价=6；非法单价/数量分别提示）。

## 原理（小白话）
**Scanner 读 System.in**：`new Scanner(System.in)` 就是「听键盘」的听筒；用户按 Enter 送来的字从标准输入进来。  
**import**：`import java.util.Scanner;` 告诉编译器去哪找 `Scanner` 这个类（放在 `package` 后、`class` 前）。  
**try-with-resources**：`try (Scanner scanner = new Scanner(System.in)) { ... }` 用完自动关掉输入源，不用自己记关闭；资源和异常细节以后课再深讲。  
**print vs println**：`print` 提示后**不换行**，光标停在提示后面方便输入；`println` 打完会换行。管道喂 stdin 时两个提示会挤在同一行，属正常。  
**nextLine vs nextInt**：`nextLine()` 读一整行（含中文姓名）；`nextInt()` 只读下一个整数。本课先读行再读整数，暂时避开「nextInt 留下换行、下一个 nextLine 读到空行」的坑。  
**hasNextInt 先检查再读**：中文「十八」不是整数；若直接 `nextInt()` 会崩。先 `hasNextInt()`，假就温柔报错并 `return`，真才 `nextInt()`。范围不对（&lt;0 或 &gt;150）同样 `return`。  
对比 Python（零基础白话）：Python 用 `input("请输入年龄：")` 得到字符串，再 `int(...)` 转整数；转失败会抛异常，常用 `try/except`。Java 这边用 `hasNextInt()` 先问「能不能当整数」，再决定读还是报错，思路类似「先检查再转换」。

## 卡过啥
- 本课合法/非法各一次均对齐讲义，无编译/路径错误。
- 管道喂入时 `print` 不换行，提示会连在一行（`请输入姓名：请输入年龄：...`），真实控制台里输入会出现在提示后。
- 练习确认：`hasNextInt` 挡住非法单价/数量；成年判断 18/17 分支正确。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第05课。
