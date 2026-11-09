# Java 第29课：AnnotationBasicsDemo（注解元数据）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson29_annotations/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
注解是**贴在代码上的标签（元数据）**，不是方法调用。`@Override` 是内置的，帮编译器检查你是不是真的重写了父类方法。自定义注解用 `@interface` 声明；`@Target` 限制能贴在哪（方法 / 类型 / 字段…）；`@Retention` 决定留到哪一阶段：

| 保留策略 | 意思 |
| --- | --- |
| `SOURCE` | 只在源码里，编译后没了 |
| `CLASS` | 进 class 文件，但反射通常读不到 |
| `RUNTIME` | 运行时还在，才能 `getAnnotation` |

本课 `@DemoCase` 标方法、RUNTIME；`@MiniComponent` 标类型、RUNTIME。**标上注解不会自己跑方法**——读标签后次数仍是 0，显式调用 `annotatedExample()` 才变成 1。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson29_annotations.AnnotationBasicsDemo'
```

正文与讲义一致：

```text
标签=annotation metadata
读取后次数=0
调用后次数=1
```

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:12:51+08:00）。  
可选测试：`./mvnw -pl 01-java-basics '-Dtest=DemoCaseTest' test` → Tests run: 4, Failures: 0（Finished at 2026-11-10T03:12:56+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/29-annotations/` —— `AnnotationBasicsDemo` / `AnnotationExercises`。本地 `javac`/`java`：正文对齐；练习见下。

## 原理（小白话）
**元数据 vs 行为**：注解像快递面单上的备注——写了「易碎」并不会自动垫泡沫，要有人读备注再动手。`getAnnotation` 只是读面单；`demo.annotatedExample()` 才是真执行。  
**为什么要 RUNTIME**：反射是运行时的事。若只留 SOURCE/CLASS，`getAnnotation` 拿不到，测试会挂。  
**@Target 是护栏**：`METHOD` 的注解贴到类上会编译失败；`TYPE` 的贴到方法上也一样。别把这种错误写进正式源码，记在笔记里就行。  
**value() 没默认值 → 必填**；`description() default ""` 才是可选。`@MiniComponent` 没有元素，直接 `@MiniComponent` 即可。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 以为贴了注解方法就会跑 | 计数一直 0 | 显式调用 / 下一课反射运行器 |
| 漏写 `RUNTIME` | 反射读到 null | 运行时要用就写 RUNTIME |
| `@Target` 太宽或用错位置 | 乱用或编译失败 | 按用途选 METHOD / TYPE / FIELD |
| 以为 `@RequiredField` 会拦空值 | 空值照样进去 | 自己写反射读标记再校验 |

## 三级练习结果（先预测再跑，对照参考答案）
预测 → 实跑（`AnnotationExercises`）：

1. `description() default ""`：无描述 → `[]`（空串）；`description="说明"` → `[说明]`；`value()` 仍必填。
2. `@Marker`（TYPE+RUNTIME）标在类上：`getAnnotation` 非 null；若标在方法上，编译器报 *annotation type not applicable to this kind of declaration*（只写在注释里演示）。
3. `@RequiredField` + 反射：非空 `"C001"` 通过；`null` / 空白 `"  "` 都被显式拒绝；**仅贴注解不会自动校验**。

## 卡过啥
- 本课 `./mvnw` 正文对齐；`DemoCaseTest` 4 过；练习副本 `javac`/`java` 亦通。
- 主入口是 **AnnotationBasicsDemo**（`AnnotationDemo` 没有 main）。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第30课、未代交验收。
