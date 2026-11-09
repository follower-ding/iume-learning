# Java 第28课：StreamsDemo（Stream 流水线）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson28_streams/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
集合是**仓库**，Stream 是**一次性流水线**：不存数据，只把「筛 → 排 → 映 → 去重 → 收」串起来。`filter` / `sorted` / `map` / `distinct` 是**中间操作**，通常惰性（光写不跑）；碰到 `toList` / `reduce` 这种**终结操作**才真正干活。流水线**不改源集合**——本课源数量始终是 3。`reduce(0, Integer::sum)` 从 0 起累加，空数据也得 0，不会 NPE。本课先用 `for` 循环筛已支付，再对照 Stream 版，让每一步可见。

| 操作 | 本课作用 |
| --- | --- |
| `filter(OrderView::paid)` | 只留已支付 |
| `sorted(comparingInt(amount))` | 金额从小到大 |
| `map(OrderView::id)` | 取出订单 ID |
| `distinct()` | 去掉重复 ID |
| `toList()` / `reduce(0,Integer::sum)` | 触发流水线并收结果 |

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson28_streams.StreamsDemo'
```

正文与讲义一致：

```text
循环筛选=[A, C]
流水线排序=[C, A]
已支付总额=140
源数量=3
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:11:18+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/28-streams/` —— `StreamsDemo` / `StreamsExercises`。本地 `javac`/`java`：正文对齐；练习见下。

## 原理（小白话）
**集合 vs Stream**：集合负责「存」；Stream 负责「算一次就扔」。流用过再终结会炸，要从集合重新 `.stream()`。  
**惰性 / 终结**：中间操作像菜谱上的步骤，写了不等于做了；终结操作才是「开火」。所以只写 `filter` 不写 `toList`，啥也不发生。  
**源不变**：流水线读源、产新结果；`orders.size()` 前后一样，这点很适合做对照测试。  
**reduce 空数据得 0**：初始值就是「没有元素时的答案」；没有初始值的 `reduce` 会变成 `Optional`，本课先用带初始值的稳妥写法。  
**别在 map/filter 里搞副作用**：别改外部变量、别偷偷付款；纯转换好测好读。初学不做并行 Stream。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 流消费后再用同一流终结 | 运行时异常 | 从集合再 `.stream()` |
| 把 HashSet 顺序当契约 | 偶发「对」 | 要稳定顺序就 `sorted` |
| 在 map 里改外部计数器 | 难测、难读 | 用 `reduce` / 终结收集 |
| 忘了终结操作 | 「为啥没输出？」 | 记得 `toList` / `reduce` / `forEach` |

## 三级练习结果（先预测再跑，对照参考答案）
预测 → 实跑：

1. 加 D(10,true)：预测排序 `[D, C, A]`、总额 `150` → 实跑一致。
2. C 改未支付：预测循环/Stream 都是 `[A]`、总额 `90`、源数量 `3` → 实跑一致。
3. 类别 `Apple,Banana,Apricot,Apple,Avocado,Berry`：循环版与 Stream 版都得 `[Apple, Apricot, Avocado]`（A 开头、去重、排序）。

## 卡过啥
- 本课 `./mvnw` 正文对齐；练习副本 `javac`/`java` 亦通。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第29课、未代交验收。
