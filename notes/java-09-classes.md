# Java 第09课：ClassesDemo（类与对象）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson09_classes/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
类是造对象的**模板**；对象是按模板造出的**实例**，各自有自己的字段值。  
`Student` 有字段 `name`、构造方法 `Student(String)`、实例方法 `introduce()`。  
`ClassesDemo` 用 `new Student("小明")` / `new Student("小红")` 各造一个对象，变量保存引用，再分别调用 `introduce()`。  
正文两行：`我叫小明` → `我叫小红`。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson09_classes.ClassesDemo'
```

正文两行与讲义一致；Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:36:35+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/09-classes/` —— `Student`（含 `greet`）；`ClassesDemo`（小红改小兰 + 第三学生小华 + greet）；`IndependenceProbe`（改 a.name，b 不受影响）。

## 原理（小白话）
**类=模板，对象=按模板造出的实例**：`Student` 描述「学生有名字、能自我介绍」；`new Student("小明")` 真的造出一个对象，并返回指向它的引用。  
**new**：创建对象 + 调用构造方法 + 返回引用。写成 `Student("小明")` 缺 `new` 会编译失败；只写 `Student first;` 没有对象。  
**字段存状态**：`name` 属于每个对象自己；两个引用指向不同对象，改一个不影响另一个。  
**构造方法**：`public Student(String initialName)` 在造对象时先把名字装好，后面 `introduce()` 才能拼出「我叫…」。  
**通过引用调方法**：`first.introduce()` = 让 first 指向的那个对象执行 introduce。  
对比 Python（零基础白话）：Python `class Student:` + `__init__(self, name)` 对应 Java 的类 + 构造方法；Python `s = Student("小明")` 不写 `new`，Java 必须 `new`。两边都是「变量存引用/指向对象」；改 `a.name` 不影响另一个独立对象。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐。
- 提醒：不能写成 `Student("小明")` 缺 `new`；仅声明 `Student first;` 不会创建对象。
- 练习确认：第三学生「我叫小华」；小红改小兰 → 第二行「我叫小兰」；`first.greet("小红")` →「你好，小红，我叫小明」；改 a.name 后 b 仍「我叫小红」。
- 每个 `public` 类单独文件、同 package；作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第10课。
