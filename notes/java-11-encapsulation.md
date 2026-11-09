# Java 第11课：EncapsulationDemo（封装与访问权限）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson11_encapsulation/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
封装 = 对象自己守「有效状态」——外人别直接伸手改内部字段。  
`BankAccount` 把 `balance` 藏成 `private`，只开门三条路：`deposit`（存）、`withdraw`（取）、`balance()`（只读查一眼）。存/取前先校验（金额必须 >0、取不能超余额、存不能把 int 撑爆），校验不过就甩 `IllegalArgumentException`，**余额原封不动**。  
`EncapsulationDemo`：存100再取40 → 余额60；再想取100 → 被拒，余额还是60。正文就这两行。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson11_encapsulation.EncapsulationDemo'
```

正文两行与讲义一致：

```text
余额=60
超额取款被拒绝，余额=60
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:39:59+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/11-encapsulation/` —— `BankAccount`（含 `transferTo`：先校验目标/金额/余额/溢出，再 withdraw+deposit）；`EncapsulationDemo`（讲义两行 + 练习：deposit(20)→20、withdraw(0)被拒余额不变、transfer 成功与三种拒绝）。本地 `javac`/`java` 输出与讲义两行对齐，练习也过。

## 原理（小白话）
**封装 = 对象自己守有效状态**：余额不能是负数、不能被外人偷偷改成 -1；规则写在类里面，外部只能走公开方法。  
**`private` 字段 vs `public` 方法**：`private int balance` 只有本类摸得着；外面想动钱，只能喊 `deposit`/`withdraw`；想看一眼，喊 `balance()`（注意：没有 `setBalance`——随便 set 就绕过业务规则了）。  
**先校验再改**：`if` 不合格就 `throw`，合格才 `balance += amount`。顺序反了（先减再判断）会留下错误状态。  
**拒绝时余额不变**：异常抛出去，改余额那行根本没执行到；所以超额取款后还是原来的数。  
**`IllegalArgumentException` 本课只作演示**：表示「你传的参数不合法」；`try/catch` 用来打印「被拒绝」；正经异常课在第26课。  
**访问权限表一句**：`public` 谁都能碰；`protected` 同包+子类；不写（包内）仅同包；`private` 只有本类——余额这种「不能乱摸」的字段就用 `private`。  
对比 Python（零基础白话）：Python 一个下划线 `_balance` 是「请别碰」的君子约定，双下划线 `__balance` 会名字改写但仍可绕；Java 的 `private` 是编译器硬拦——外面写 `account.balance = -1` 直接编译挂，比约定更硬。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐。
- 提醒：余额别做成 `public`（否则 `account.balance = -1` 能绕过所有规则）；先改再校验会留脏状态；溢出检查别忘（`amount > Integer.MAX_VALUE - balance`）。
- 练习确认：`deposit(20)` → 余额20；`withdraw(0)` 被拒、余额不变；`transferTo` 成功后两边合计不变，空目标/金额0/超额均拒绝且两边余额不动。
- 每个 `public` 类单独文件、同 package；作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第12课、未代交验收。
