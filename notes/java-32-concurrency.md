# Java 第32课：ConcurrencyDemo（并发与线程）

仓库讲义：`follower-ding/java-learning-lab` → `02-java-advanced/.../learn/advanced/concurrency/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
并发就是：**同时安排多个任务，读结果前先等它们真正做完**。启动靠 `start()`，等待靠 `join()` / `Future.get()`；共享计数要用原子或锁，普通 `count++` 会丢更新。

| API / 概念 | 干什么 |
| --- | --- |
| `Runnable` + `new Thread(...).start()` | 建线程并真正开新线程；直接 `run()` 只是当前线程里的普通调用 |
| `thread.join()` | 等这个线程结束，不靠 `sleep` 猜 |
| `count++` 竞态 | 读→加一→写回三步；多线程交错可能丢更新，碰巧正确≠安全 |
| `AtomicInteger.incrementAndGet()` | 原子自增，本课安全计数用它 |
| `synchronized` 方法 | 练习2：锁住共享 `int`，效果也能到 4000 |
| `ExecutorService` + `submit` | 交任务，拿回 `Future` |
| `Future.get()` | 等完成；被中断会清标记 → 要 `interrupt()` 恢复再往上抛 |
| `shutdown()` / `shutdownNow()` | 用完必关；`shutdownNow` 还会中断工作线程 |
| 虚拟线程 | 适合大量阻塞等待（IO/闩锁）；**不能**让 CPU 计算自动变快 |
| `CountDownLatch` | 真实阻塞等待信号，不靠睡眠 |

本课两个类：
- **CounterExperiment**：`countSafely`（AtomicInteger）/ `countUnsafely`（故意共享普通 int）；每轮检查中断，`finally` 里 `shutdownNow`。
- **ConcurrencyDemo**：`start`+`join` → 安全/不安全对比 → 虚拟线程等闩锁后打印「阻塞型任务完成」。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 02-java-advanced compile exec:java '-Dexec.mainClass=learn.advanced.concurrency.ConcurrencyDemo'
```

正文模式对齐（不安全计数是动态观测值）：

```text
线程已结束：true
安全计数：4000
不安全计数（结果不固定）：2651
阻塞型任务完成
```

安全计数恒为 **4000**；本次不安全计数观测值 **2651**（可能碰巧也是 4000，不能当精确断言）。  
Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:18:55+08:00）。  
可选测试：`./mvnw -pl 02-java-advanced '-Dtest=CounterExperimentTest' test` → Tests run: 4, Failures: 0（Finished at 2026-11-10T03:19:05+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/32-concurrency/` —— `CounterExperiment` / `ConcurrencyDemo` / `ConcurrencyExercises`。本地 `javac`/`java`：正文对齐；练习见下。

## 原理（小白话）
**start ≠ run**：`start()` 才开新线程；`run()` 就是自己跑一遍方法，看起来像「有线程」其实还是单线程。  
**等结果别猜**：`join` / `Future.get` 是「做完再说」；`sleep` 猜时间，机器一忙就翻车。  
**竞态像多人抢改同一张纸**：每人读到旧数字再写回，后写的覆盖先写的，总数就少了；`AtomicInteger` 或 `synchronized` 相当于「一次只让一个人改」。  
**中断要恢复标记**：`Future.get` 抛 `InterruptedException` 时会清掉中断位，本课先 `Thread.currentThread().interrupt()` 再往上抛，上层才知道「有人要取消」。  
**虚拟线程像大量服务员**：适合等人回消息（阻塞）；厨房炉灶（CPU）不会因为服务员变多就变快。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 调 `run()` 当启动 | 仍在当前线程 | 用 `start()` |
| `sleep` 猜完成 | 负载一变就错 | `join` / `Future.get` |
| 忘记等 `Future` | 提前读结果 / 看不到异常 | 先 `get()` 再读 |
| 空 `catch InterruptedException` | 上层不知取消 | 恢复标记再抛或退出 |
| 把虚拟线程当 CPU 加速 | 期望落空 | 只用于大量阻塞等待 |
| 用完不 `shutdown` | 线程池泄漏 | `finally` 里关 |

## 三级练习结果（先预测再跑，对照参考答案）
预测 → 实跑（`ConcurrencyExercises`）：

1. **模仿**：`countSafely(2, 10)` → 预测 20 → 实跑 **20**。
2. **修改**：`SynchronizedCounter`（`synchronized add/get`），4 任务 × 1000 次 → 预测 4000 → 实跑 **4000**。
3. **独立**：线程池提交 `() -> 2` 与 `() -> 3`，`a.get() + b.get()`，`finally pool.shutdown()` → 预测 5 → 实跑 **5**。

## 卡过啥
- 本课入口在 **02-java-advanced**（进阶篇），主类 `learn.advanced.concurrency.ConcurrencyDemo`。
- `./mvnw` 正文对齐；`CounterExperimentTest` 4 过；练习副本 `javac`/`java` 亦通。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 不安全计数每次可能不同（本次 mvnw 观测 2651）；只断言安全计数 4000。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第33课、未代交验收。
