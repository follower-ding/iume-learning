# Java 第20课：PricingDemo（面向对象与价格策略）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson20_pricing_strategy/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
`PricingPolicy` 是接口：约定「给小计 → 返回应付」。  
`FullPricePolicy` 原样返回非空小计；`PercentageDiscountPolicy` 算 `subtotal × (1-rate)`，`HALF_UP` 两位。  
`rate` 只能在 0~1；小计不能 null——构造/apply 里直接拦。  
订单（或 Demo）只认 `PricingPolicy` 变量：塞原价实现或九折实现都行，调用始终 `policy.apply(subtotal)`，不用为每种折扣写 `if`/`switch`。  
衔接第15课接口、第16课多态、第17课组合（未来订单 **有一个** `PricingPolicy` 字段）。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson20_pricing_strategy.PricingDemo'
```

正文两行与讲义一致：

```text
原价=100.00
九折=90.00
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:56:03+08:00）。  
可选测试：`./mvnw -pl 01-java-basics '-Dtest=PricingPolicyTest' test` → Tests run: 11, Failures: 0（Finished at 2026-11-10T02:56:08+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/20-pricing-strategy/` —— 自敲 `PricingPolicy` / `FullPricePolicy` / `PercentageDiscountPolicy` / `PricingDemo`（讲义两行）+ `FixedDiscountPolicy` / `PricingExercises`（三道练习）。本地 `javac`/`java`：讲义两行对齐；练习1 固定减 5 → `95.00`，`3.00` 减 5 → `0.00`；练习2 同小计换策略无需 if → `100.00`/`90.00`（原价沿用输入 scale，折扣实现 `setScale(2)`）；练习3 `new PricingPolicy()` 编译失败（`is abstract; cannot be instantiated`），改为 `new FullPricePolicy()` 经接口引用调用 → `100.00`。

## 原理（小白话）
**接口变量装不同实现**：`PricingPolicy normal = new FullPricePolicy()`——左边是契约类型，右边是真正干活的对象。  
**apply 多态**：同一句 `policy.apply(subtotal)`，实际跑哪个实现的 `apply`，看当前引用指向谁。  
**组合策略，别硬 if**：订单保存一个 `PricingPolicy` 字段（has-a），把计价委托出去；新增固定减额等实现时，调用方不用改分支。别 `instanceof` / 按策略名 `switch`——那就失去可替换性。  
**scale 两位来自实现**：`FullPricePolicy` 原样返回，保留输入 scale；`PercentageDiscountPolicy`（及练习里的固定减额）在 `apply` 里 `setScale(2, HALF_UP)`。调用方要懂契约，不要假定两种策略 scale 行为完全一样。  
**不能 new 接口**：接口只声明行为，没有可构造的完整对象；必须 `new` 具体实现，再用接口类型引用它。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 把 10% 写成 `10` | 构造器拒绝（rate 须 0~1） | `new BigDecimal("0.10")` |
| 用 `double` 造金额/费率 | 二进制浮点脏值 | 十进制字符串 → `BigDecimal` |
| 调用方 `instanceof` 分支 | 失去可替换性 | 只调 `apply` |
| `new PricingPolicy()` | 编译失败 | `new` 具体实现，接口变量引用 |
| 减额超小计不定义行为 | 可能负应付 | 本练习落到 `0`（或另立业务规则） |

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文两行对齐；`PricingPolicyTest` 11 测全过；本地练习副本 `javac`/`java` 亦通。
- 练习确认：1) 固定减额 100→95、3→0；2) 同小计换策略无需 if，scale 分别来自输入/实现；3) `new PricingPolicy()` 编译失败，接口引用 + 实现对象才对。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第21课、未代交验收。
