# Java 第12课：StaticDemo（static 与成员归属）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson12_static/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
`static` = 「挂在类上」的成员，不是某个对象私藏的。  
`Visitor` 里：`name` 是实例字段（甲乙各有各的名）；`createdCount` 是静态字段（所有访客共用一个柜台）；`SITE_NAME` 是 `static final` 常量（牌子钉死了，别想改）。每 `new` 一次，构造里 `createdCount++`。查总数用 `Visitor.createdCount()`——喊类名，别拿某个访客当喇叭。  
`StaticDemo`：先甲后乙 → 打印 `甲/乙`、`创建数=2`、`学习站`。正文就这三行。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson12_static.StaticDemo'
```

正文三行与讲义一致：

```text
甲/乙
创建数=2
学习站
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:41:37+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/12-static/` —— `Visitor`（含 `label()`：`SITE_NAME + ":" + name`）；`StaticDemo`（讲义三行 + 练习：第三个 Visitor 计数3、交换甲乙创建顺序姓名各自走/计数继续累加、`label()` 输出）。本地 `javac`/`java` 讲义三行对齐，练习也过。

## 原理（小白话）
**实例字段 vs 静态字段**：`name` 跟着对象走——甲叫甲、乙叫乙，互不串台；`createdCount` 挂在类上——全站就一个计数器，谁 `new` 都往同一个数上加。  
**静态方法没有 `this`**：`createdCount()` 是 `static`，里面写 `this.name` 直接编译挂——因为它压根没「当前对象」。只能摸静态成员。反过来，实例方法可以读静态的（比如 `label()` 用 `SITE_NAME` + `name`）。  
**调用姿势**：写 `Visitor.createdCount()`，别写 `first.createdCount()`——后者能编译但像拿某个访客问「全站创建了几个」，读者会懵。常量也用 `Visitor.SITE_NAME`。  
**`final` 不能再赋值**：`SITE_NAME` 初始化后不能 `SITE_NAME = "别的站"`；注意 `final` 只管「变量本身不能换指向」，不保证对象内部可变性（String 本身不可变是后话，第18课）。  
**对比 Python（零基础白话）**：Python 类变量大家共用、实例属性挂 `self`；Java 用 `static` 显式标「类成员」，编译器帮你拦「静态方法里乱用 this」——比 Python「约定俗成」更硬。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文三行对齐。
- 提醒：静态方法里别写 `this.name`（编译挂）；别用实例去调静态方法装酷（能跑但误导）；`final` 只锁赋值，别和「对象不可变」混为一谈。
- 练习确认：第三个 Visitor → `createdCount()==3`；交换甲乙创建顺序 → 姓名各自独立、静态计数仍按创建次数累加；`label()` → `学习站:甲` 这类「站点:姓名」。
- 每个 `public` 类单独文件、同 package；作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第13课、未代交验收。
