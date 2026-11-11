# Java 第38课：CoreDemo（Spring Core / 依赖注入）

仓库讲义：`follower-ding/java-learning-lab` → `04-frameworks/spring-core/.../learn/spring/core/`（只读引用，未改讲义）  
本地跑通：2026-11-11（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`  
本课只用 **Spring 容器** 装配对象，不启动 HTTP，不连数据库。

## 复述（学了啥）
`GreetingService` 负责“用问候语”，`MessageRepository` 负责“提供问候语”。服务只依赖接口，换实现不用改服务。

**依赖注入（DI）**是从外面把仓储传进服务的构造器；**控制反转（IoC）**是对象的创建、连接和生命周期交给容器。二者相关但不是同一个词：手工 `new GreetingService(repository)` 已经是 DI；Spring 容器接管装配才体现 IoC。

`@Configuration` 标记配置类；`@Bean` 标记方法返回值交给容器保存。容器看到 `greetingService(MessageRepository repository)` 的参数，按类型找到仓储 Bean 再注入。默认 Bean 是单例，同一容器里两次 `getBean` 拿到的是同一个实例。用 try-with-resources 关掉 `AnnotationConfigApplicationContext`，让容器管理的资源正常结束。本课用明确的 `@Bean` 画出每条连接；另一种写法是 `@Component` + `@ComponentScan`。不要两种方式同时注册同名/同类型 Bean，否则注入会歧义。

| API / 概念 | 干什么 |
| --- | --- |
| IoC vs DI | IoC=容器管创建与生命周期；DI=从外部传入依赖。手工 new 也是 DI |
| `@Configuration` / `@Bean` | 配置类 + 方法返回的对象交给容器 |
| 构造器注入 | 服务构造器收接口，字段用 `final`；不要在服务里 `new` 实现 |
| 默认单例 | 同一容器重复 `getBean` 得到同一实例（`==` 为 true） |
| `AnnotationConfigApplicationContext` | 读配置类、创建并连接 Bean |
| try-with-resources 关容器 | 用完关闭，资源正常结束；关闭后 `isActive()` 为 false |

本课五个类：**MessageRepository**（接口）、**InMemoryMessageRepository**（返回 `你好，`+姓名）、**GreetingService**（构造器注入后委托）、**AppConfig**（两个 `@Bean`）、**CoreDemo**（先手工再容器，比较输出和单例）。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8"
./mvnw -q -pl 04-frameworks/spring-core -am verify
./mvnw -q -pl 04-frameworks/spring-core compile exec:java '-Dexec.mainClass=learn.spring.core.CoreDemo'
```

正文（mvnw exec 实测）：

```text
手工构造: 你好，小明
Spring 容器: 你好，小明
默认单例: true
```

Maven `BUILD SUCCESS`（exec Finished at 2026-11-11T12:05:17+08:00）。  
可选：`./mvnw -pl 04-frameworks/spring-core -am verify` → Tests run: 2, Failures: 0（Finished at 2026-11-11T12:05:37+08:00）。  
练习副本（自敲，不进讲义仓）：`/workspace/iume-learning/java/38-spring-core/` —— `CoreDemo` / `AppConfig` / `GreetingService` / 仓储 + `PracticeOne`/`PracticeTwo`/`PracticeThree`。本地 `javac`/`java`（Spring Framework 7.0.9 `spring-context` 及传递依赖 classpath）：Demo 正文对齐；练习见下。

前两行相同，是因为用了同一仓储实现（`你好，`+姓名），但装配方不同：一行是调用者自己 `new`，一行是容器按 `AppConfig` 连接。

## 原理（小白话）
**DI 像把零件递进去**：仓储不在服务里自己 `new`，构造器从外面收下，所以换零件不用改服务。  
**IoC 像把装配线交给工厂**：谁创建、谁连接、活多久，改由 Spring 容器管。  
**`@Bean` 像登记册**：配置方法返回的对象放进容器；服务方法的参数按类型被填上。  
**默认单例像共用一把钥匙**：同一容器两次取 `GreetingService`，拿到的是同一个对象。  
**try-with-resources 像用完关门**：容器实现了 `AutoCloseable`，出了 try 就 close；本例虽只用内存，仍要练对生命周期。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 忘记 `@Bean` 或传错配置类 | `NoSuchBeanDefinitionException` | 在配置类声明 Bean，打开容器时传入正确配置类 |
| 同时注册多个同类型仓储 | `NoUniqueBeanDefinitionException` | `@Qualifier` 或练习里用独立配置类只留一个 |
| 在 `GreetingService` 内部 `new` 仓储 | 构造器不再暴露依赖，换实现只能改服务 | 构造器注入接口 |
| 忘记关闭容器 | 有连接/线程的 Bean 可能继续占资源 | try-with-resources 或 finally `close()` |
| `@Bean` 与 `@ComponentScan` 重复注册 | 同类型多个 Bean，注入歧义 | 本课只用明确 `@Bean`，不要两套一起上 |
| 练习 classpath 没带 Spring | `javac`/`java` 找不到 `AnnotationConfigApplicationContext` | 带上 `spring-context` 7.0.9 及其依赖 |

## 三级练习结果（先预测再跑，对照参考答案）
均在练习副本独立入口中（不改课程 `AppConfig` / `CoreDemo`）：

1. **模仿**：手工传入返回 `欢迎，` 的仓储 → 预测 **欢迎，小明**（有构造器注入，还没有容器）→ 实跑一致。
2. **修改**：`FormalConfig` 只注册 `FormalMessageRepository` → 预测容器打印 **您好，小明** → 实跑一致。不跟原 `AppConfig` 混用，避免两个仓储抢类型。
3. **独立**：两个配置类各开一个真实容器 → 预测依次 **你好，小明**、**欢迎，小明**、**true**（同一容器两次取服务相同）、**true**（都 close 后 `isActive()` 为 false）→ 实跑一致。分别证明：注入的依赖决定行为、同一容器默认单例、关闭后容器不再活跃。两个容器之间不共享单例。

## 卡过啥
- 本课入口在 **04-frameworks/spring-core**，包 `learn.spring.core`，主类 `CoreDemo`。
- IoC 和 DI 不要混成一个词：手工 `new` 已经是 DI；容器接管才是 IoC。
- 前两行输出相同，不代表装配方式相同——只是仓储实现一样。
- `./mvnw` 正文对齐；练习副本需 Spring classpath（`org.springframework:spring-context:7.0.9` 及传递依赖）。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 学习仓默认分支是 `main`（不是 `master`）。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第39课、未代交验收。
