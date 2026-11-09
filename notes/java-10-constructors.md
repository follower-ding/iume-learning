# Java 第10课：ConstructorsDemo（构造方法与 this）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson10_constructors/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
构造方法 = `new` 造对象时**自动跑**的那段「装初始值」代码；跟类同名，**没有返回类型**（连 `void` 都不写）。  
`Book` 有：无参构造（内部 `this("未命名")` 甩锅给带参版）、带参构造（`this.title = title`）、以及读标题的 `title()`。  
`ConstructorsDemo` 造两本：空白本走无参 → 标题「未命名」；命名本走带参 → 「Java入门」。正文两行就这俩。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson10_constructors.ConstructorsDemo'
```

正文两行与讲义一致：

```text
未命名
Java入门
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:38:30+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/10-constructors/` —— `Book`（无参默认「待录入」+ 带参 + 带版次构造）；`ConstructorsDemo`（算法 + Java 第2版）。本地 `javac`/`java` 输出：`待录入` / `Java入门` / `算法` / `Java 第2版`。

## 原理（小白话）
**构造方法何时跑**：`new Book(...)` 的那一刻；对象刚生出来、字段还没装好，就靠它塞初始值。  
**为什么写了带参构造后无参要自己写**：你一写任何自定义构造，编译器就不再白送无参版；想 `new Book()` 还能用，就得亲手补上。  
**`this(...)` 必须首行**：在本构造里调用另一个构造，只能当第一条语句；先打印再 `this(...)` 直接编译挂。  
**`this.title` vs 参数 `title`**：参数把字段名「盖住」了；`this.title` 指当前对象的字段，光写 `title` 是形参。不写 `this.` 就变成「参数=参数」，字段还是空的——经典坑。  
**不能写 `void Book()`**：一加 `void` 就变成普通方法，不再是构造；`new` 时不会调它。  
对比 Python（零基础白话）：Python `__init__(self, title)` ≈ Java 构造方法；两边都能重载/默认值思路，但 Java 的 `this(...)` 链式调用必须首行，且写了带参就要自己补无参。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐。
- 提醒：`this(...)` 必须首行；`void Book()` 不是构造；写了 `Book(String)` 后编译器不再送 `Book()`。
- 练习确认：`new Book("算法")` →「算法」；无参默认改「待录入」→「待录入」；`new Book("Java", 2)` →「Java 第2版」（`this(title + " 第" + edition + "版")`）。
- 每个 `public` 类单独文件、同 package；作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第11课、未代交验收。
