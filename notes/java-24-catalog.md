# Java 第24课：CollectionsDemo / ProductCatalog（集合综合·商品目录）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson24_catalog/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
把第21–23课的 `List` / `Set` / `Map` 选型接到一个商品目录：`LinkedHashMap<String,Product>` 按 id 查找并保插入序；`sortedByPrice` 复制 values 到 `ArrayList` 再按 `BigDecimal` 升序排序，返回 `List.copyOf` 不可变快照；`groupByWholePrice` 用 `price.setScale(0, RoundingMode.DOWN)` 向下取整到整元，桶放进 `TreeMap`（键升序），组内保目录插入序，再包成不可修改 Map。同 id 再 `add` 会覆盖值但保留该键原位置。跨课复用第19课不可变 `Product` record。本课 main 用循环拼接输出；`ProductCatalog` 里的 `::` / `->` 先当「能跑的写法」看，Lambda 留第27课。

| 需要 | 选 | 本课用法 |
| --- | --- | --- |
| 有序可重复 | List | 排序结果 / 分组成员 |
| 唯一 | Set | 练习3 名称去重 |
| 键→值 | Map | id→商品；整元→组 |

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson24_catalog.CollectionsDemo'
```

正文与讲义一致：

```text
lookup book: Book
sorted prices: pen=1.50, card=12.10, book=12.90
whole price 1: pen
whole price 12: book, card
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:03:13+08:00）。  
可选测试：`./mvnw -pl 01-java-basics '-Dtest=ProductCatalogTest' test` → Tests run: 6, Failures: 0（Finished at 2026-11-10T03:03:14+08:00，BUILD SUCCESS）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/24-catalog/` —— 复制第19课 `Product` + 自敲 `ProductCatalog` / `CollectionsDemo` / `CatalogExercises`。本地 `javac`/`java`：讲义正文对齐；练习1 前缀 `B`→book,bag,box，空前缀 size=4，小写 `b` 空、快照不可变；练习2 `0.99/1.00/1.99` → `{0=1, 1=2}`；练习3 LinkedHashSet 保首次插入序可作业务契约。

## 原理（小白话）
**Map 查 id**：`findById` → `Optional.ofNullable(get)`；缺键空 Optional，同键 `put` 覆盖。  
**List 排序**：先 `new ArrayList<>(values)` 再 `sort`，不打乱目录内部 LinkedHashMap 顺序；同价保留相对序。  
**整元分组**：`RoundingMode.DOWN` 把 `12.99`/`12.00` 都打到键 `12`，`0.99`→`0`；TreeMap 保证桶键升序。  
**不可变快照**：`List.copyOf` / `Collections.unmodifiableMap`；之后 `add` 不影响已取出结果；对快照 `clear`/`remove` → `UnsupportedOperationException`。  
**选型综合**：查 id 用 Map；要稳定演示序用 LinkedHashMap；要排序结果用 List 副本；要按键排序的分组用 TreeMap；去重看是否承诺顺序再选 HashSet / LinkedHashSet。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 增强 for 里直接 list.remove | CME / 跳元素 | 迭代器 remove，或先收集再删 |
| 改快照当可变目录 | UnsupportedOperationException | 通过 catalog.add 更新，再取新快照 |
| 把 DOWN 改成 UP | 分组键变了、测试挂 | 本课契约是向下取整 |
| 只用 List 按 id 查 | 常要线性扫 | Map 按键查 |
| 猜 HashSet 固定顺序 | 下次遍历变 | 顺序是契约就用 LinkedHashSet 或排序后展示 |

## 卡过啥
- 本课 `./mvnw` 正文对齐；`ProductCatalogTest` 6/0；练习副本 `javac`/`java` 亦通。
- 练习确认：1) 前缀 List + 不可变；2) DOWN 统计 `{0=1, 1=2}`；3) 业务顺序选 LinkedHashSet。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第25课、未代交验收。
