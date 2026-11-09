# Java 第14课：AbstractDemo（抽象类）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson14_abstract/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
抽象类 = 「半成品模板」：规定每种形状都得会算面积，但通用公式写不出来。  
`Shape` 标了 `abstract`，还塞了抽象方法 `area()`——只签合同不给公式；具体 `Rectangle` 继承后必须实现面积，并校验边长不为负。  
`describe()` 是普通方法，内部喊 `area()`，所以父类也能用子类的公式。  
`AbstractDemo`：`Shape shape = new Rectangle(3, 4)` → 打印 `矩形` + `面积=12`。正文就这两行。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson14_abstract.AbstractDemo'
```

正文两行与讲义一致：

```text
矩形
面积=12
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:45:04+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/14-abstract/` —— `Shape` / `Rectangle`（讲义对齐）+ `Square`（练习3 `extends Shape`，面积边长×边长）；`AbstractDemo`（讲义两行 + 三道练习）。本地 `javac`/`java`：讲义两行对齐；练习1 `2×5`→`area=10`；练习2 `Rectangle(-1,5)`→`IllegalArgumentException: 边长不能为负数`；练习3 `Square(6)`→`正方形` / `面积=36`。

## 原理（小白话）
**`abstract class` 不能 `new`**：`new Shape()` 编译直接挂——抽象类是半成品，只能当父类被人继承。像「只有图纸，没有整车」。  
**抽象方法只写签名**：`public abstract long area();` 结尾分号，没有 `{}`。合同写清「必须会算面积」，公式留给具体图形。  
**具体子类必须实现**：`Rectangle` 漏写 `area()`？编译挂。有几个抽象方法就得还几个。  
**抽象类也能有字段 / 构造 / 具体方法**：`private name`、`protected Shape(String name)`、`describe()` / `name()` 都是实货；抽象不等于「空壳」。  
**`describe()` 调 `area()`**：父类具体方法里喊抽象方法——运行时看真实对象是谁，Rectangle 就按宽×高算。模板方法味儿。  
**`long` 防溢出**：`(long) width * height` 先把一个操作数抬成 `long` 再乘；俩 `int` 直接乘可能先溢出再塞进 `long`，结果就歪了。  
**`Shape shape = new Rectangle(...)`**：父类引用装着子类对象——下一课多态会系统讲；这课先认「能这么写」。  
**对比 Python ABC（零基础白话）**：Python 用 `abc.ABC` + `@abstractmethod` 也能逼子类实现；不实现也能「勉强实例化」有时靠自觉。Java 这边更硬：抽象类 / 未实现抽象方法，编译期就拦死，装不了懂。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐。
- 提醒：别 `new Shape()`；别漏写子类 `area()`；边长负数走 `IllegalArgumentException`；面积乘法先转 `long`。
- 练习确认：`Rectangle(2,5).area()`→10；`Rectangle(-1,5)` 抛「边长不能为负数」；`Square(6)`→`正方形` / `面积=36`，`Square(-1)` 同样校验挂。
- 每个 `public` 类单独文件、同 package；作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第15课、未代交验收。
