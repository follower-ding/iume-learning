# Java 第16课：PolymorphismDemo（多态）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson16_polymorphism/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
多态 = 同一句 `speak()`，谁真在干活就听谁的。  
左边写 `Animal`（编译器只认「动物会叫」），右边塞 `new Dog()` / `new Cat()`（运行时才知道汪还是喵）。  
`PolymorphismDemo`：`Animal first = new Dog(); Animal second = new Cat();` → 打印 `汪汪` + `喵喵`。正文就这两行。  
展示板不用 if-else 分狗猫——统一喊 `speak()`，各子类自己回。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson16_polymorphism.PolymorphismDemo'
```

正文两行与讲义一致：

```text
汪汪
喵喵
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:48:41+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/16-polymorphism/` —— `Animal` / `Dog` / `Cat` / `PolymorphismDemo`（讲义对齐）+ `Bird`（练习3 `speak`→「啾啾」）；`PolymorphismDemo` 含讲义两行 + 三道练习。本地 `javac`/`java`：讲义两行对齐；练习1 第三个 Dog→`汪汪`；练习2 交换顺序→先`喵喵`后`汪汪`；练习3 Bird→`啾啾`。

## 原理（小白话）
**父类型引用 → 子类对象**：`Animal a = new Dog()` 合法。左边是「名片上写啥」，右边是「真人是谁」。  
**编译看左边、运行看右边**：编译器盯 `Animal`，确认有 `speak()` 才放行；真正跑起来按堆里对象（Dog/Cat）挑 `@Override` 版本——这叫**运行时分派**（runtime dispatch）。  

| 引用 | 编译时可见方法 | 运行时执行 |
| --- | --- | --- |
| `Animal first = new Dog()` | Animal.speak | Dog.speak → 汪汪 |
| `Animal second = new Cat()` | Animal.speak | Cat.speak → 喵喵 |

**统一调用、不用分支**：循环/列表里全是 `Animal`，挨个 `speak()` 就行，不用 `if (是狗) … else if (是猫)`——新加 Bird 也不必改调用方。  
**别随意强转**：`Animal` 引用上调不了只属于 Dog 的专属方法；硬 `(Dog) someCat` 运行期会炸（ClassCastException）。需要专属能力就用更具体的类型，或先 `instanceof` 再转——能不转就不转。  
**对比 Python duck typing（零基础白话）**：Python 常靠「长得像鸭子就会叫」——没显式父类型也能 `obj.speak()`。Java 这边要先有继承/接口这条线，编译期硬核检查左边类型；运行时再按真实对象分派。鸭子打字灵活，Java 多态更「合同 + 运行时选实现」。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐。
- 提醒：变量类型是 Animal 时，只能调 Animal 上有的方法；换右边对象会换叫声，但左边类型不变。
- 练习确认：第三个 Dog→`汪汪`；交换赋值→先喵后汪；`Bird`→`啾啾`。
- 每个 `public` 类单独文件、同 package；作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第17课、未代交验收。
