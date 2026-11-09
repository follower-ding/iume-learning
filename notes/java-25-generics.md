# Java 第25课：GenericsDemo（泛型与可复用仓储）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson25_generics/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
用类型参数把「装什么 / 存什么」推迟到使用时再定：`Box<T>` 是泛型类，`first` 是泛型方法，`sum(List<? extends Number>)` 用上界通配符只读 Number；`Repository<T, ID>` 同时参数化实体与键，同一套 `InMemoryRepository` 可服务商品或订单。对比裸类型（raw type）：写了 `<T>` 后编译器在调用处拦住类型混用，不必等到运行时 `ClassCastException`。Java 泛型不变（`List<Integer>` 不是 `List<Number>`）；读用 `? extends`、写用 `? super`（PECS）。运行时多半擦除，不能 `new T()` / `instanceof List<String>`。

| 写法 | 作用 | 本课用法 |
| --- | --- | --- |
| `Box<T>` | 泛型类：字段/返回值都是 T | `Box<String>` 装 "Java" |
| `<T> T first(...)` | 泛型方法：方法自己引入 T | `first(List.of(1,2))` → Integer |
| `? extends Number` | 上界：只安全读出 Number | `sum` 累加 doubleValue |
| `Repository<T,ID>` | 接口双参数 | `Repository<String,Integer>` 存订单 |

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson25_generics.GenericsDemo'
```

正文与讲义一致：

```text
盒子=Java
方法=1
上界读取=3.0
仓储=订单
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:05:11+08:00）。  
可选测试：`./mvnw -pl 01-java-basics '-Dtest=InMemoryRepositoryTest' test` → Tests run: 6, Failures: 0（Finished at 2026-11-10T03:05:16+08:00，BUILD SUCCESS）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/25-generics/` —— `Repository` / `InMemoryRepository` / `GenericsDemo` / `GenericsExercises`。本地 `javac`/`java`：讲义正文对齐；练习1 `copy` Integer→Number 得 `[1, 2]`；练习2 `copyNumbers` 上界 Number 得 `[3, 4]`（字符串来源编译失败）；练习3 各步 `[A]`→`[A,B]`→`[C,B]`→`[C]`，旧快照仍 `[A, B]`。

## 原理（小白话）
**为什么写 `<T>`**：把「装什么类型」留给调用方；`Box<String>` 的 `get` 直接是 String，不用强制转换。裸 `List` / 裸 `Repository` 能塞进任意 Object，错要到读的时候才炸。  
**上界能读什么**：`List<? extends Number>` 保证元素至少是 Number，所以可 `doubleValue()`；不能安全往里 `add` 任意 Number（可能实际是 `List<Integer>`）。  
**仓储怎么复用**：`Repository<T,ID>` + `InMemoryRepository` 内部 `LinkedHashMap<ID,T>`；商品用 `Repository<Product,String>`，订单演示用 `Repository<String,Integer>`，存取规则同一份。`findAll` 返回 `List.copyOf` 快照，之后 save/delete 不影响旧列表。  
**不变 + PECS**：子类型关系不会抬到 List 上；需要只读来源用 `? extends T`，需要写入目标用 `? super T`（练习1 的 `copy`）。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 用 raw type | 编译器放弃 T/ID 检查 | 始终写 `Repository<T,ID>` |
| 当 `List<Integer>` 是 `List<Number>` | 编译失败 / 破坏类型约束 | 只读用 `? extends Number` |
| 改 `findAll()` 返回的列表 | UnsupportedOperationException | 用 save/delete，再取新快照 |
| 指望运行时还有 T | 擦除后没有具体 T | 需要时显式传 `Class<T>` 等令牌 |
| save/find 传 null id | NPE | 本实现 `Objects.requireNonNull` |

## 卡过啥
- 本课 `./mvnw` 正文对齐；`InMemoryRepositoryTest` 6/0；练习副本 `javac`/`java` 亦通。
- 练习确认：1) PECS copy；2) `<T extends Number>` 拦住非 Number；3) 同键替换保插入位、旧快照独立。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第26课、未代交验收。
