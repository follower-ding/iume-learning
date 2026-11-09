# Java 第19课：ObjectDemo（对象、引用与相等性）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson19_object_identity/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
`alias = first` 只复制引用，不 `new` 对象——所以 `first == alias` 为 true。再 `new` 一次哪怕字段全同，也是另一份对象，`==` 为 false。  
普通类未重写 `equals` 时，默认仍按身份；本课用 `one.name().equals(another.name())` 比字段内容才得 true。  
`Product` 是 record：编译器按全部组件生成 `equals`/`hashCode`，所以独立对象但组件相同 → `equals` true、`==` 仍 false。  
`BadProductKey` 把可变 id 塞进 `hashCode`：入 `HashSet` 后再 `setId`，对象还在集合里，但哈希桶错位 → `contains` 变 false。  
`BigDecimal("399.0")` 与 `"399.00"` 的 `equals` 还比 scale，数值相等不等于组件相等。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson19_object_identity.ObjectDemo'
```

正文八行与讲义一致：

```text
普通类同一引用=true
普通类独立对象=false
普通类相同字段=true
alias.sameReference=true
separate.sameReference=false
separate.equalValues=true
mutableKey.containsBefore=true
mutableKey.containsAfter=false
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:54:02+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/19-object-identity/` —— 自敲 `Product` / `BadProductKey` / `ObjectDemo`（讲义八行）+ `ObjectExercises`（三道练习 + BigDecimal scale）。本地 `javac`/`java`：讲义八行对齐；练习1 改价/改名 → `equals` false、`alias==` 仍 true；练习2 等值 Product 进 HashSet → size=1、`contains` true，改价新对象 → false；练习3 不可变 `record Key` → 旧键仍可查、新键需再 add；`399.0` vs `399.00` → `equals` false、`compareTo==0` true。

## 原理（小白话）
**引用赋值**：`Product alias = first;` 只把「指向同一对象的指针」抄一份，不造新商品。  
**== vs equals**：`==` 问「是不是同一个对象」（身份）；`equals` 问「业务值相不相等」。record 自动按组件写好 `equals`/`hashCode`。  
**普通类默认身份**：没重写 `equals` 时，`equals` 也走 `Object` 的身份比较；要比字段得自己比（本课 `name().equals(...)`）或自己重写。  
**可变哈希键**：`HashSet` 按加入时的 `hashCode` 放桶；事后改参与哈希的字段，查找走新哈希路径 → 可能找不到（本课 `containsAfter=false`）。别改集合里键的相等性字段；优先不可变键。  
**BigDecimal.equals 比 scale**：`399.0` 与 `399.00` 金额大小一样（`compareTo==0`），但 `equals` 为 false——塞进 record 组件时，两个 `Product` 也不会 `equals`。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 用 `==` 当业务相等 | 独立等值对象被判不同 | record/`equals` 比组件 |
| 可变字段进 `hashCode` 后改 | `contains` 假阴性 | 不可变键；要改就删旧插新 |
| 只重写 `equals` 不重写 `hashCode` | 破坏集合约定 | 两者一起改 |
| `BigDecimal` 只看数值忽略 scale | record/集合判不等 | 统一 scale，或用 `compareTo` 比大小 |

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文八行对齐；本地练习副本 `javac`/`java` 亦通。
- 练习确认：1) 改价/改名 → equals false；2) HashSet 等值去重 size=1；3) 不可变 Key 旧键可查、新键需 add；BigDecimal scale → equals false。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第20课、未代交验收。
