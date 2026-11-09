# Java 第17课：CompositionDemo（组合与依赖）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson17_composition/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
组合 = 「有一个」（has-a），不是「是一种」（is-a）。  
`NotificationService` **有一个** `MessageSender`：构造时塞进来，之后发消息全交给它。  
主路径：`RecordingSender` → `new NotificationService(sender)` → `notifyUser("订单已发货")` → 打印「已记录:订单已发货」，再用 `sender.lastMessage()` 核对「最后消息=订单已发货」。  
换 ConsoleSender 就换发送方式——服务本身不用改。别用 `extends RecordingSender` 把发送器焊死。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson17_composition.CompositionDemo'
```

正文两行与讲义一致：

```text
已记录:订单已发货
最后消息=订单已发货
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:50:18+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/17-composition/` —— 复制第15课 `MessageSender` / `ConsoleSender` / `RecordingSender`（包名不变）+ 自敲 `NotificationService`（含练习3 `welcome`）/ `CompositionDemo`（讲义两行 + 三道练习）。本地 `javac`/`java`：讲义两行对齐；练习1 ConsoleSender→`发送:你好`；练习2 RecordingSender `lastMessage`→`练习二消息`；练习3 `welcome("iume")`→`已记录:欢迎，iume`。

## 原理（小白话）
**has-a vs is-a**：Dog **是** Animal（`extends`）；NotificationService **有** 发送器（字段 + 构造传入）。别把「能发消息」写成「服务就是一种发送器」——那焊死了，换不了实现。

| 关系 | 语法 | 例子 |
| --- | --- | --- |
| is-a | `extends` / `implements` | Dog 是 Animal；RecordingSender 是 MessageSender |
| has-a | 字段 + 构造注入 | NotificationService 有 MessageSender |

**构造注入**：`new NotificationService(sender)` 把协作对象交进去；服务只认 `MessageSender` 契约，不关心 Console 还是 Recording。  
**`private final MessageSender sender`**：`final` = 字段装上后不许再换指针；`private` = 外人摸不着，只能走 `notifyUser`。  
**拒 null**：构造里 `if (sender == null) throw ...`——空协作者后面一调就炸，早点拦。  
**可替换实现**：同一服务，塞 ConsoleSender →「发送:…」；塞 RecordingSender →「已记录:…」还能读 `lastMessage()`。调用方决定用谁。  
**别 extends 硬绑**：`NotificationService extends RecordingSender` 表达不了「可换发送器」；方法里每次 `new RecordingSender()` 也锁死了，外面换不了。  
**对比 Python 组合（零基础白话）**：Python 也常 `self.sender = sender` 塞协作对象，鸭子打字靠运行时有没有 `send`。Java 这边构造期就要 `MessageSender` 类型（编译器硬核检查），再加 `final` / 拒 null 把依赖钉牢——合同更严，换实现仍靠接口多态。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐。
- 提醒：`import learn.lesson15_interfaces.*` 是跨课复用类型，不是复制粘贴讲义仓；练习副本本地自带第15课三个文件方便 `javac`。
- 练习确认：ConsoleSender→`发送:你好`；Recording 读 lastMessage；`welcome` 委托 `notifyUser("欢迎，" + name)`。
- 每个 `public` 类单独文件、同 package；作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第18课、未代交验收。
