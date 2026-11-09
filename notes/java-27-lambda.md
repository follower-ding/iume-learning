# Java 第27课：LambdaDemo（Lambda 与函数式接口）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson27_lambda/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
只有一个抽象方法的接口叫**函数式接口**，可用 `@FunctionalInterface` 让编译器检查。匿名类写 `new Calculator() { ... }`；Lambda 写 `(left, right) -> left + right`。单表达式直接当返回值；多语句要 `{ ...; return x; }`。捕获外层局部变量须 **final / 有效 final**。四个标准接口：`Predicate.test` 判布尔、`Function.apply` 转换、`Consumer.accept` 消费、`Supplier.get` 生产。本课 `expensiveProductNames` 用 Predicate 判贵、Function/`Product::name` 取名；普通循环让每步可见，原列表不变。Lambda 适合短行为；复杂规则用命名方法。为第28课 Stream 铺路。

| 写法 | 作用 | 本课用法 |
| --- | --- | --- |
| 匿名类 | 显式实现唯一抽象方法 | `oldWay.add(2,3)→5` |
| Lambda | 同一目标类型的简写 | `lambda.add(2,3)→5` |
| `Predicate` | 布尔判断 | `12 > 10` → `true` |
| `Function` / 方法引用 | 转换 / `String::length` | `"Java"` → `4` |
| `Consumer` + `Supplier` | 消费 + 生产 | 打印 `消费=订单` |

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson27_lambda.LambdaDemo'
```

正文与讲义一致：

```text
匿名类=5
Lambda=5
谓词=true
转换=4
消费=订单
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:09:29+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/27-lambda/` —— `LambdaDemo` / `LambdaExercises`（依赖只读复制的 `Product`）。本地 `javac`/`java`：讲义正文对齐；练习1 求积两种写法均为 `6`；练习2 `threshold=15` → `谓词=false`；练习3 `Formatter` 匿名类与 Lambda 均得 `[A]`。

## 原理（小白话）
**为什么需要函数式接口**：Lambda 必须落到「唯一抽象方法」上，编译器才知道把箭头体塞进哪个方法；多抽象方法就没法推断。  
**匿名类 vs Lambda**：语义都是「给接口一个实现」；Lambda 更短，且不生成具名内部类实例的啰嗦写法，但捕获规则更严（有效 final）。  
**方法引用**：`Product::name` / `String::length` 是「已有方法签名刚好对上」时的简写，不是新语法能力。  
**PECS 预习感**：本课 Predicate/Function 已是「行为当参数」；下一课 Stream 会把筛/映/收连成流水线。  
**别把业务塞进超长箭头**：可读性掉、难测、难复用；复杂逻辑抽成命名方法再引用。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 捕获后再改局部变量 | 编译失败 | 保持有效 final，或改用字段/参数 |
| 空输入未测 | 边界行为不清楚 | 空列表应得空结果，原列表不变 |
| 把 HashSet 顺序当契约 | 偶发「对」 | 要稳定顺序用有序结构或排序后再打 |
| 长 Lambda 藏业务规则 | 难读难测 | 抽命名方法 / 类 |

## 卡过啥
- 本课 `./mvnw` 正文对齐；练习副本 `javac`/`java` 亦通（求积/阈值/Formatter）。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第28课、未代交验收。
