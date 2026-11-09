# Java 第22课：SetDemo（集合去重与排序）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson22_set/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
`Set` 不保留重复元素。`HashSet` 用 `hashCode` 找桶、再用 `equals` 判重（接第19课契约：相等对象必须同哈希，放入后别改哈希字段）；迭代顺序不保证，要稳定展示就打印前排序。  
`LinkedHashSet` 去重且保插入顺序。`TreeSet` 按比较规则排：`Integer` 自带自然序；自定义类型传入 `Comparator`。`TreeSet` 把比较结果 `0` 当重复——只比年龄时，同龄不同名会被当成一项；年龄后再比姓名可保留二者，业务还要唯一则最后加 ID。  
选型：唯一+排序 → `TreeSet`；只去重不关心顺序 → `HashSet`；保留最早出现顺序 → `LinkedHashSet`。`add` 返回是否新增；遍历用增强 `for`。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson22_set.SetDemo'
```

正文五行与讲义一致：

```text
去重且排序=[ann, bob]
插入顺序=[bob, ann]
自然顺序=[1, 2, 3]
年龄比较数量=1
补充姓名后=[ann, bob]
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:00:10+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/22-set/` —— 自敲 `SetDemo`（讲义五行）+ `SetExercises`（三道练习）。本地 `javac`/`java`：讲义五行对齐；练习1 `add(A/B/A)` → `true,true,false`，顺序 `[A, B]`；练习2 加 `cara(19)` → `cara(19), ann(20), bob(20)`；练习3 `Member` 按 name+id 数量=3，只按 name 数量=2（同名不同 id 第二个丢失）。

## 原理（小白话）
**HashSet 去重**：先 `hashCode` 定位，再 `equals` 确认；不相等却同哈希只是碰撞，仍可共存。无顺序保证 → 演示里拷到 List 再 `sort`。  
**LinkedHashSet**：哈希去重 + 链表记插入次序；重复 `add` 返回 `false`，不改已有位置。  
**TreeSet 自然序**：`Integer`/`String` 等实现 `Comparable`，无需额外 Comparator。  
**TreeSet 自定义比较**：比较结果 `0` ≡ 重复（与 `equals` 可不一致，但业务上应尽量对齐「相等」定义）。只比年龄 → ann/bob 同龄只留一个；年龄+姓名 → 同龄不同名都留。  
**equals/hashCode 契约（第19课）**：HashSet/HashMap 键依赖二者；TreeSet 依赖 Comparator/`compareTo`——别混用两套「相等」定义。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 猜 HashSet 固定顺序 | 下次运行顺序变 | 打印前排序 / 用 LinkedHashSet/TreeSet |
| TreeSet 只比年龄 | 同龄不同名被当成一项 | 比较键补姓名或唯一 ID |
| 自定义对象 equals≠hashCode | HashSet 去重失灵 | 相等对象同哈希 |
| 放入 Set 后改哈希字段 | 找不到 / 幽灵元素 | 放入后别改关键字段 |
| 增强 for 里直接改集合 | ConcurrentModificationException | 迭代器或先收集再删 |

## 卡过啥
- 本课 `./mvnw` `BUILD SUCCESS`，正文五行对齐；本地练习副本 `javac`/`java` 亦通。
- 练习确认：1) A/B/A → true,true,false 顺序 A,B；2) 19 岁排在两位 20 岁之前；3) name+id 留 3，只 name 丢同名。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第23课、未代交验收。
