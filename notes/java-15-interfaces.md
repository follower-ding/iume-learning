# Java 第15课：InterfacesDemo（接口）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson15_interfaces/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
接口 = 「能力合同」：不管你咋发消息，先保证有个 `send(String)` 能被调用。  
`MessageSender` 只签合同不写实现；`ConsoleSender` 拼「发送:…」，`RecordingSender` 把最后一条存起来再回「已记录:…」。  
`InterfacesDemo`：用接口类型装两个不同实现，各自 `send("你好")` → 打印两行正文。  
同一调用姿势，不同干活方式——这就是接口的爽点。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson15_interfaces.InterfacesDemo'
```

正文两行与讲义一致：

```text
发送:你好
已记录:你好
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:46:58+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/15-interfaces/` —— `MessageSender` / `ConsoleSender` / `RecordingSender` / `InterfacesDemo`（讲义对齐）+ `PrefixSender`（练习3）；`InterfacesDemo` 含讲义两行 + 三道练习。本地 `javac`/`java`：讲义两行对齐；练习1 `早上好`→`发送:早上好`；练习2 `lastMessage()`→`你好`；练习3 `PrefixSender`→`通知:开会啦`。

## 原理（小白话）
**`interface` 是契约**：只写「你得有啥能力」，不写「你咋干」。`MessageSender` 只有 `send` 签名，谁实现谁填公式。  
**`implements` 表态**：`ConsoleSender implements MessageSender` = 「我接这单」——必须把接口里声明的方法全写上，漏一个编译挂。  
**可多实现接口、只能单继承类**：`implements A, B` 没问题；`extends` 只能挂一个爹。接口用来拼能力，类用来搭血缘。  
**不能 `new` 接口**：`new MessageSender()` 编译直接挂——合同不是实物，得找具体实现类。可以写 `MessageSender s = new ConsoleSender()`，左边合同、右边干活的人。  
**接口 vs 抽象类**：接口偏「你会啥」；抽象类还能塞字段、构造、共享具体方法（上节 `Shape`）。纯能力约定优先接口；要共享状态/模板流程再上抽象类。  
**`@Override` 验签名**：写错参数名/返回类型，注解帮你当场抓现行。  
**对比 Python Protocol / ABC（零基础白话）**：Python `typing.Protocol` 偏「长得像就能用」（结构子类型），`abc.ABC` + `@abstractmethod` 更像强制合同。Java 接口是显式 `implements`，编译期硬拦——装不了懂，漏实现就编不过。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐。
- 提醒：别 `new MessageSender()`；`implements` 后必须实现全部方法；`RecordingSender.lastMessage()` 是实现类自己的额外方法，接口引用上直接调不了，得用 `RecordingSender` 类型（或先转型）。
- 练习确认：`ConsoleSender().send("早上好")`→`发送:早上好`；`RecordingSender` 发完 `lastMessage()`→`你好`；`PrefixSender`→`通知:…`。
- 每个 `public` 类/接口单独文件、同 package；作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第16课、未代交验收。
