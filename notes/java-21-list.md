# Java 第21课：ListDemo + QueueDemo（列表与队列）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson21_list/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
`List` 按位置排、允许重复：日常起点是 `ArrayList`（下标读写、尾部追加方便）；`LinkedList` 不要当默认——只有明确需要在已知节点附近频繁插删才考虑。  
`Queue`/`Deque`：用 `ArrayDeque` 做队列（`offer`/`poll` FIFO）或栈（`push`/`pop` LIFO），别默认用 `LinkedList`。空队列 `poll` → `null`；`ArrayDeque` 拒 `null` 元素。  
增删改查：`add` / `remove(index)` / `set` / `get`；`size()` 看数量。遍历用增强 `for`；边走边删改用 `Iterator.remove()`。整数列表里 `remove(1)` 删下标 1，`remove(Integer.valueOf(1))` 删值 1——靠参数类型选重载。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson21_list.ListDemo'
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson21_list.QueueDemo'
```

先跑 ListDemo 再跑 QueueDemo，合并正文十行与讲义一致：

```text
读取=笔
数量=2
遍历=笔
遍历=本子
删除后=[本子]
数字=[1]
链表=[封面, 本子]
出队=[订单A, 订单B]
空队列=null
出栈=后放
```

Maven `BUILD SUCCESS`（ListDemo Finished at 2026-11-10T02:58:28+08:00；QueueDemo Finished at 2026-11-10T02:58:30+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/21-list/` —— 自敲 `ListDemo` / `QueueDemo`（讲义十行）+ `ListQueueExercises`（三道练习）。本地 `javac`/`java`：讲义十行对齐；练习1 追加「橡皮」→数量=3，遍历笔/本子/橡皮；练习2 `[1,2,3]`→`remove(0)` 得 `[2,3]`，再 `remove(Integer.valueOf(1))` 不变（值 1 已不在）；练习3 三票 offer A/B/C，三次 poll 得 A/B/C，第四次 `null`。

## 原理（小白话）
**ArrayList 按下标**：`get(0)`/`set(1,x)` 直接定位；尾部 `add` 常见。`LinkedList` 按下标要走节点，默认优先 ArrayList。  
**Iterator 边走边删**：增强 `for` 里直接 `list.remove` 会炸；用 `iterator.remove()` 才安全。  
**remove 重载陷阱**：`List<Integer>` 上 `remove(1)` 走 `remove(int index)` 删位置；要删值 1 须 `remove(Integer.valueOf(1))`。  
**ArrayDeque 队列**：`offer` 入队尾、`poll` 出队头 → FIFO；空时 `poll` 返回 `null`（不抛）。  
**ArrayDeque 栈**：`push`/`pop` 同端操作 → LIFO；本课「先放/后放」→ `pop` 出「后放」。  
**拒 null**：`ArrayDeque` 不允许 null 元素；入队前可用 `Objects.requireNonNull`。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 增强 for 里直接 `list.remove` | ConcurrentModificationException | `Iterator.remove()` |
| `numbers.remove(1)` 想删值 1 | 实际删下标 1 | `remove(Integer.valueOf(1))` |
| 默认用 LinkedList 当队列 | 非必要复杂 | `ArrayDeque` + offer/poll |
| `ArrayDeque` 塞 null | NPE | 拒 null / requireNonNull |
| 空队列还当有元素 | 逻辑错 | 认 `poll()==null` / `isEmpty` |

## 卡过啥
- 本课两次 `./mvnw` 均 `BUILD SUCCESS`，合并正文十行对齐；本地练习副本 `javac`/`java` 亦通。
- 练习确认：1) 追加橡皮→size=3、遍历笔/本子/橡皮；2) remove(0)→[2,3]，再 remove 值1 不变；3) A/B/C 出队后第四次 poll=null。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第22课、未代交验收。
