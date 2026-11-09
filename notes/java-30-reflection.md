# Java 第30课：ReflectionDemo（反射）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson30_reflection/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
反射 = **运行时读类型结构并受控操作**：拿 `Class`，再看构造器 / 字段 / 方法，必要时 `invoke` / `newInstance`。

| API | 干什么 |
| --- | --- |
| `SomeType.class` | 已知类型拿 Class（本课主入口用这个） |
| `getDeclaredConstructors/Fields/Methods` | 读**本类声明**的成员（含非公开） |
| `Method.invoke(target)` | 执行方法；目标抛出的异常包在 `InvocationTargetException`，用 `getCause()` 找原因 |
| `trySetAccessible()` | 尝试打开访问；**不保证成功**（模块边界可能拦） |
| `Class.forName(名字)` | 按名字加载；**别把用户输入直接塞进去** |

本课两个小工具：
- **DemoRunner**：只跑带 `@DemoCase` 的**无参**方法，按方法名排序；先全检签名再执行。
- **MiniContainer**：只注册带 `@MiniComponent` 的类，无参构造，**重复注册复用同一实例**。

依赖上一课注解包：`AnnotationDemo` / `@DemoCase` / `@MiniComponent`（RUNTIME）。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson30_reflection.ReflectionDemo'
```

正文与讲义一致：

```text
class: learn.lesson29_annotations.AnnotationDemo
constructor: learn.lesson29_annotations.AnnotationDemo
field: invocationCount
method: annotatedExample
method: invocationCount
labels: [annotation metadata]
invocations: 1
same component: true
same on duplicate registration: true
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:14:57+08:00）。  
可选测试：`./mvnw -pl 01-java-basics '-Dtest=DemoRunnerTest,MiniContainerTest' test` → Tests run: 9, Failures: 0（Finished at 2026-11-10T03:15:03+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/30-reflection/` —— `ReflectionDemo` / `DemoRunner` / `MiniContainer` / `ReflectionExercises`（含 lesson29 依赖副本）。本地 `javac`/`java`：正文对齐；练习见下。

## 原理（小白话）
**Class 像说明书**：编译时你写死 `new Foo()`；反射是「打开说明书再按规则动手」。  
**声明 ≠ 公开**：`getDeclared*` 能看到 private；真正调用还要过 `trySetAccessible`，失败就别硬闯。  
**DemoRunner 像考官**：先点名（筛 `@DemoCase`）→ 按姓名排序 → 检查都是无参 → 再一个个叫上台。半路有人带道具（有参数），整场不开考。  
**MiniContainer 像简易单例柜**：只收贴了 `@MiniComponent` 的类；第二次登记同一把钥匙，还给同一件东西（构造器不再跑）。  
**泛型擦除**：字段声明 `List<String>` 用 `getGenericType()` 还能看到 String；`new ArrayList<String>()` 和 `new ArrayList<Integer>()` 的运行时 class 都是 `ArrayList`。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 以为注解会自己跑方法 | 计数一直 0 | DemoRunner / 显式 invoke |
| 不检查签名就 invoke | 运行时炸，前面 demo 可能已跑 | 先全检再执行 |
| `getDeclaredMethods` 次序当业务序 | 偶发乱序 | 显式 `sorted(按方法名)` |
| 用户输入直接 `Class.forName` | 任意类加载风险 | 白名单 / 类字面量 |
| 把 `setAccessible` 当万能钥匙 | 模块边界仍可能拒绝 | `trySetAccessible` + 失败要处理 |
| 以为实例能还原 `List<String>` | 只有擦除后的 ArrayList | 声明信息用 `getGenericType` |

## 三级练习结果（先预测再跑，对照参考答案）
预测 → 实跑（`ReflectionExercises`）：

1. **StrictContainer**：重复 `register` → `IllegalStateException`（消息含 `already registered`）；构造计数仍 **1**。对比本课默认「复用实例」：拒绝策略更容易暴露误用。
2. **StrictDemoRunner**：非法签名消息拼 `declaringClass.getName() + "." + method.getName()`；`alpha` 未执行（`invocations=0`）——先全检再执行。
3. `Holder.names` 的 `getGenericType()` = `java.util.List<java.lang.String>`；`new ArrayList<String>().getClass()` 与 `new ArrayList<Integer>().getClass()` **相同**，都是擦除后的 `java.util.ArrayList`。

## 卡过啥
- 本课 `./mvnw` 正文对齐；`DemoRunnerTest`+`MiniContainerTest` 共 9 过；练习副本 `javac`/`java` 亦通。
- 主入口是 **ReflectionDemo**（依赖 lesson29 的 `AnnotationDemo` 等）。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第31课、未代交验收。
