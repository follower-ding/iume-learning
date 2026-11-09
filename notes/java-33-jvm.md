# Java 第33课：JvmDemo（JVM 与诊断）

仓库讲义：`follower-ding/java-learning-lab` → `02-java-advanced/.../learn/advanced/jvm/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
JVM 给每个线程一条**调用栈**：方法一进就建栈帧，返回就退栈。对象一般在**堆**上；局部变量里的是**引用**，不能说「所有基本类型都在栈上」。`maxMemory()` 是堆**上限**，不是已用内存；别强制 OOM，也别把 `System.gc()` 当成回收保证。`StackOverflowError`（栈炸）≠ 堆 OOM。

| API / 概念 | 干什么 |
| --- | --- |
| 调用栈 / 栈帧 | 每线程一条栈；调用建帧、返回退帧 |
| `depth(n)` 小递归 | 观察栈帧层数；输入保持很小，防栈溢出 |
| `new Object()` + 局部引用 | 对象在堆，变量里是引用；可访问 ≠ 「在栈上」 |
| `Thread.currentThread().getName()` | 本课普通 `main` 常为 `main`；启动器可能改名，以实测为准 |
| `Runtime.getRuntime().maxMemory()` | 当前 JVM 堆上限（字节），随机器/启动参数变 |
| `--wait` + `ProcessHandle.current().pid()` | 进程保活，方便另一终端 `jcmd` |
| `jcmd -l` / `VM.version` / `Thread.print` / `GC.heap_info` | 列进程、查版本、看线程栈、看堆概况；只查自己起的进程 |

本课一个类：**JvmDemo** — 打印递归深度、对象可访问、线程名、堆上限；可选 `--wait` 读 Enter。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 02-java-advanced compile exec:java '-Dexec.mainClass=learn.advanced.jvm.JvmDemo'
```

正文（mvnw exec 实测）：

```text
调用深度：3
对象可访问：true
线程：learn.advanced.jvm.JvmDemo.main()
最大堆字节数（环境相关）：4196401152
```

固定两行：`调用深度：3`、`对象可访问：true`。  
线程名：Maven `exec:java` 下为 **`learn.advanced.jvm.JvmDemo.main()`**；直接 `java -cp ... JvmDemo` 则为 **`main`**（测试断言的是后者）。  
堆上限本次 **4196401152**（环境相关正整数）。  
Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:21:07+08:00）。  
可选测试：`./mvnw -pl 02-java-advanced -am test -Dtest=learn.advanced.jvm.JvmDemoTest` → Tests run: 2, Failures: 0（Finished at 2026-11-10T03:21:18+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/33-jvm/` —— `JvmDemo` / `JvmExercises`。本地 `javac`/`java`：正文对齐；练习见下。

## 原理（小白话）
**栈像一层层盘子**：调方法就多叠一盘（栈帧），返回就撤掉；`depth(3)` 叠三层再拆回来，所以打印 3。  
**引用 ≠ 对象**：手里拿的是门牌号，房子在堆里；基本类型局部变量多半在栈帧里，但**不能**断言「凡是基本类型都在栈上」（字段、数组元素等另说）。  
**maxMemory ≠ 已用**：上限是天花板高度，不是你堆了多少箱子。  
**System.gc 非保证**：最多「建议收一下」，不保证立刻、也不保证收干净。  
**栈溢 ≠ 堆 OOM**：递归太深炸的是调用栈；堆装不下才是 OutOfMemoryError。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 说「所有基本类型都在栈上」 | 概念错 | 区分局部/字段/数组；对象在堆 |
| 把 maxMemory 当已用 | 误判内存 | 看 used / heap_info，别只看上限 |
| 强制制造 OOM 演示 GC | 进程崩、环境脏 | 小例子 + jcmd 观察即可 |
| 把 `System.gc()` 当同步回收 | 误以为必回收 | 只当提示，不依赖 |
| 递归输入无限增大 | StackOverflowError | 小输入；栈溢 ≠ 堆 OOM |

## 三级练习结果（先预测再跑，对照参考答案）
预测 → 实跑：

1. **模仿**：`depth(4)` → 预测 `调用深度：4` → 实跑 **调用深度：4**（`JvmExercises`）。
2. **修改**：`java -cp 02-java-advanced/target/classes learn.advanced.jvm.JvmDemo --wait`，另侧 `jcmd`：
   - PID：**238785**（`jcmd -l` 见 `learn.advanced.jvm.JvmDemo --wait`）
   - `jcmd 238785 VM.version` → **OpenJDK 64-Bit Server VM version 25.0.5+8-LTS / JDK 25.0.5**
   - 另录：`Thread.print`、`GC.heap_info` 亦通（见下）
3. **独立**：`Thread.print` 里 `"main"` 停在 `FileInputStream.readBytes` → `System.in.read` → `JvmDemo.main`（`--wait` 分支）。因为 `System.in.read()` 阻塞等 Enter，所以快照里 main 在输入调用链；按 Enter 后进程退出，再对同一 PID 做 `jcmd` 会失败，属正常。

本机无「纯交互双终端」时的做法：fifo 保活 stdin + 后台进程，再 `jcmd`；Enter 后正常退出。**无卡点**，三件套（`-l` / `VM.version` / `Thread.print`）均已跑到。

## 卡过啥
- 本课入口在 **02-java-advanced**（进阶篇），主类 `learn.advanced.jvm.JvmDemo`。
- `./mvnw` 正文对齐；`JvmDemoTest` 2 过；练习副本 `javac`/`java` 亦通；`--wait`+`jcmd` 实跑成功。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- Maven exec 下线程名不是裸 `main`，而是 `learn.advanced.jvm.JvmDemo.main()`；以启动方式实测为准。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第34课、未代交验收。
