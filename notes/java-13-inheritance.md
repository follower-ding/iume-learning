# Java 第13课：InheritanceDemo（继承与方法重写）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson13_inheritance/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
继承 = 「是一种」关系：`Dog extends Animal`，狗先当动物，再补自己的本事。  
`Animal` 管共用的：`private name`、吃饭 `eat()`、默认叫声 `speak()`。  
`Dog` 构造里 `super(name)` 把名字交给父类装好；再 `@Override speak()` 改成「汪汪」——吃饭照用父类的，叫声自己说。  
`InheritanceDemo`：`new Dog("旺财")` → 打印 `旺财在吃饭` + `汪汪`。正文就这两行。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson13_inheritance.InheritanceDemo'
```

正文两行与讲义一致：

```text
旺财在吃饭
汪汪
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:43:30+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/13-inheritance/` —— `Animal` / `Dog`（讲义对齐）+ `YapDog`（练习2改叫声→「汪」）+ `Cat`（练习3 `extends Animal`，`speak`→「喵喵」）；`InheritanceDemo`（讲义两行 + 三道练习输出）。本地 `javac`/`java` 讲义两行对齐，练习也过。

## 原理（小白话）
**`extends` = 子类是父类的一种**：Dog 是 Animal，能直接用 `eat()`，不用再抄一份。像「模板再开一档定制款」。  
**`super(...)` 必须首行**：子类构造先喊父类构造把父类那摊事办完（这里是塞 `name`），再写自己的活；放第二行编译直接挂。跟第10课 `this(...)` 必须首行一个味。  
**`@Override` = 请编译器盯梢**：方法名+参数必须跟父类对得上；你要是写成 `speak(String text)` 还硬贴 `@Override`，编译器会喊——那是**重载**不是**重写**。  
**重写 vs 重载（别混）**：

| | 重载 overload | 重写 override |
| --- | --- | --- |
| 位置 | 同类或继承链 | 子类盖父类 |
| 方法名 | 相同 | 相同 |
| 参数 | **必须不同** | **必须相同** |
| 谁拍板 | 编译期看参数 | 运行时看真实对象 |

**父类 `private` 字段子类摸不着**：`Dog` 里写 `this.name`？编译挂。但父类方法 `eat()` 自己能读 `name`，所以子类调 `eat()` 照样打出「旺财在吃饭」——封装没破，能力照继承。  
**单继承**：一个类只能 `extends` 一个直接父类（多行为往后接口课再说）。  
**对比 Python（零基础白话）**：Python 也是子类写父类名、`super().__init__(...)`；Java 强制 `super(...)` 首行、用 `@Override` 防手滑，比 Python「靠自觉」更硬。Python 多继承常见，Java 类这边只认一条亲爹线。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐。
- 提醒：`super(name)` 别挪到第二行；别把 `speak(String)` 当重写（那是重载，`@Override` 会打脸）；别在子类里直接读父类 `private name`。
- 练习确认：改叫声→第二行变「汪」（`YapDog`）；`Cat` → `咪咪在吃饭` / `喵喵`；`dog.eat()` 仍是继承来的「旺财在吃饭」。
- 每个 `public` 类单独文件、同 package；作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第14课、未代交验收。
