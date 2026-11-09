# Java 第02课：VariablesDemo（变量与数据类型）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson02_variables/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
在 `main` 里声明六个变量并打印：`String name`、`int age`、`long population`、`char level`、`boolean learning`，以及用 `(int)` 把 `double height` 截成整数。  
`age = age + 1` 是重新赋值（18→19）；`1_400_000_000L` 里的 `_` 只方便阅读；`char` 单引号、`String` 双引号。  
六行正文：`姓名=小明` / `年龄=19` / `人口=1400000000` / `等级=A` / `正在学习=true` / `强制转换后的身高=1`。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson02_variables.VariablesDemo'
```

正文六行与讲义一致；Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:25:04+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/02-variables/` —— `VariablesDemo` 与讲义一致；`TruncateProbe` 改年龄起点 20→21，并确认 `(int)1.75=1`、`(int)1.99=1`、`(int)-1.75=-1`（向零截断，不是四舍五入）。

## 原理（小白话）
**变量** = 有名字的内存位置：`int age = 18;` 里 `int` 是类型、`age` 是名字、`18` 是初值，`=` 把右边存进左边。  
**八种基本类型**：整数 `byte/short/int/long`（日常先用 `int`）、小数 `float/double`（日常先用 `double`）、单字符 `char`、真假 `boolean`。`String` 是**引用类型**，用来存文字，不算在八种里。  
**赋值 vs 重新赋值**：第一次是声明并初始化；之后 `age = age + 1` 先算右边，再写回左边。  
**强制转换截断**：`(int) 1.75` 得到 `1`，丢掉小数部分，**不是四舍五入**；小范围可自动进大范围（`int`→`long`），反过来必须写 `(int)`。  
**局部变量使用前必须赋值**，否则编译不过。  
对比 Python：Python 变量不用先写死类型，改成别的类型也能跑（动态）；Java 声明时定好类型，类型对不上会在编译期拦住——零基础理解就是「Java 更早告诉你写错了」。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，六行正文对齐，无编译/路径/类型错误。
- 仍要提醒：Maven INFO 可能盖住正文——在输出尾部找「姓名=小明」起的六行。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第03课。
