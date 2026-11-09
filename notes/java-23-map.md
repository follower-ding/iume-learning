# Java 第23课：MapDemo（键值映射）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson23_map/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
`Map<K,V>` 用键找值，不是 `Collection` 子接口。`put` 新增或覆盖，`get` 查找，`getOrDefault` 给缺失键默认值，`containsKey` 判键是否存在，`remove` 删除，`entrySet` 遍历键值对，`computeIfAbsent` 在缺失时算并放入。同一键再 `put` 只替换值、不增条目。  
`HashMap` 不保证迭代顺序；`LinkedHashMap` 保插入顺序（本课 main 用它，条目顺序稳定）；`TreeMap` 按键排序。`HashMap` 允许 `null` 值：`get` 返回 `null` 可能是缺键，也可能是映射到 `null`，用 `containsKey` 区分。选型接第19课 `equals`/`hashCode`：HashMap 键靠二者；TreeMap 靠比较规则。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson23_map.MapDemo'
```

正文与讲义一致（main 用 `LinkedHashMap`，条目顺序稳定：pen→book→eraser）：

```text
读取=5
默认值=0
存在=true
条目=pen:8
条目=book:2
条目=eraser:1
删除后={pen=8, eraser=1}
按键排序={eraser=1, pen=8}
映射空值存在=true
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:01:36+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/23-map/` —— 自敲 `MapDemo`（讲义正文）+ `MapExercises`（三道练习）。本地 `javac`/`java`：讲义正文对齐；练习1 再 `put("pen",9)` → size=1、get=9；练习2 `computeIfAbsent` 两次 → 计算次数=1；练习3 TreeMap 汇总 A/B/A → `{A=2, B=1}`。

## 原理（小白话）
**键唯一**：`put` 同键覆盖；`size` 不因覆盖变大。  
**get vs containsKey**：`get` 得 `null` 分不清「缺键」与「值就是 null」→ 用 `containsKey`。  
**getOrDefault**：缺键时给默认，不写入 Map。  
**computeIfAbsent**：键不在才调 `Function.apply` 并放入；已存在（含已映射非 null）不再算。本课用具名 `InitialStock`，第27课再写 Lambda。  
**LinkedHashMap vs TreeMap**：前者保插入序（演示条目稳定），后者按键排序（删除后 `{eraser=1, pen=8}`）。  
**equals/hashCode（第19课）**：自定义类型当 HashMap 键须契约正确；放入后别改哈希字段。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 猜 HashMap 固定顺序 | 下次运行顺序变 | 用 LinkedHashMap / TreeMap，或打印前排序 |
| 只看 get==null | 混淆缺键与 null 值 | `containsKey` 区分 |
| 当 Collection 用 Map | 编译/设计不对 | Map 不是 Collection；遍历用 entrySet/keySet/values |
| 增强 for 里直接改同一 Map | ConcurrentModificationException | 迭代器或先收集再删 |
| 自定义键 equals≠hashCode | 查不到 / 重复键 | 相等对象同哈希；放入后别改关键字段 |

## 卡过啥
- 本课 `./mvnw` `BUILD SUCCESS`，正文对齐讲义；本地练习副本 `javac`/`java` 亦通。
- 练习确认：1) 覆盖 size=1、get=9；2) computeIfAbsent 两次只算 1 次；3) A/B/A → `{A=2, B=1}`。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第24课、未代交验收。
