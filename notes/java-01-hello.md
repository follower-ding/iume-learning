# Java 第01课：HelloDemo（第一个 Java 程序）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson01_hello/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
从空文件写出能跑的 `HelloDemo`：`package` 对齐目录、公开类名与文件名一致、`main` 是入口、`System.out.println` 打印并换行。两条 `println` 按源码顺序输出：
`你好，Java！` / `这是我的第一个 Java 程序。`
注释 `//` 只给人看；括号、花括号、分号必须是英文半角。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson01_hello.HelloDemo'
```

正文输出与讲义一致；Maven `BUILD SUCCESS`。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/01-hello/` —— `HelloDemo` / `ProfileDemo` / `print` vs `println` 均用 `javac`+`java` 跑通；`print` 不换行会粘在同一行。

## 原理（小白话）
Java 源文件 `.java` → **先编译成字节码**（`.class`）→ 再交给 **JVM** 执行。Maven/`javac` 负责编译这一步；跑起来的是 JVM，不是直接解释源码。  
对比 Python：`.py` 通常由解释器当场读并执行（也有 `.pyc`，但日常体感是「改完就能跑」）。Java 多了明确的「编译成字节码」关口——漏分号、类名大小写错，会在编译期就拦住，而不是跑到那一行才爆。

## 卡过啥
- 本机原先没有 `java`：装 Temurin 25 并设 `JAVA_HOME` 后才能 `./mvnw`。
- 只盯 Maven INFO 日志时会漏正文两行——在输出尾部找「你好，Java！」。
- 文件名/`HelloDemo`/`hellodemo` 混用会过不了编译（大小写敏感）。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义。
