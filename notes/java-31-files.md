# Java 第31课：FilesDemo（文件与 IO）

仓库讲义：`follower-ding/java-learning-lab` → `02-java-advanced/.../learn/advanced/files/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
文件 IO 就是：**用 `Path` 指位置，用 `Files` 读写**；中文文本要固定 **UTF-8**；失败就让 `IOException` 往外抛，别吞成「空文件」。

| API / 概念 | 干什么 |
| --- | --- |
| `Path` vs 旧 `File` | 新代码优先 `Path`；`File` 偏元数据，本课不主用 |
| `Files.createTempDirectory` | 每次实验建独立临时目录，避免踩自己的文件 |
| `directory.resolve("notes.txt")` | 拼路径（相对拼接） |
| `Files.write(path, lines, UTF_8)` | 小文件写行；写完或失败都会关文件 |
| `Files.readAllLines(path, UTF_8)` | 小文件一次读进内存；大文件改用缓冲流 |
| `Files.createDirectories(parent)` | 写之前先把父目录建好（`writeLines` 里做了） |
| `throws IOException` | 异常交给调用者；`catch {}` 空吞会伪装成空文件 |
| `try/finally` | 只删**本例创建**的文件和目录，不递归清公共临时区 |

本课两个类：
- **TextFiles**：静态工具，`writeLines` / `readLines`，统一 UTF-8，写前建父目录。
- **FilesDemo**：建临时目录 → 写两行 → 读回打印 → `finally` 先删文件再删目录。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 02-java-advanced compile exec:java '-Dexec.mainClass=learn.advanced.files.FilesDemo'
```

正文与讲义一致：

```text
读取结果：[你好, Java]
```

（半角方括号、中文冒号；`List.toString()` 自带 `, `。）

Maven `BUILD SUCCESS`（Finished at 2026-11-10T03:17:13+08:00）。  
可选测试：`./mvnw -pl 02-java-advanced '-Dtest=TextFilesTest' test` → Tests run: 2, Failures: 0（Finished at 2026-11-10T03:17:20+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/31-files/` —— `TextFiles` / `FilesDemo` / `FilesExercises`。本地 `javac`/`java`：正文对齐；练习见下。

## 原理（小白话）
**Path 像地址，Files 像快递员**：你告诉它送到哪、取什么；编码写死 UTF-8，中文才不会在不同系统乱码。  
**小文件一次搬完**：`readAllLines` 方便；大文件别整本搬进内存，改用 `BufferedReader` 一行行读。  
**异常别吞**：读失败返回空列表，调用者会以为文件就是空的——所以 `throws IOException` 往上抛。  
**清理要由内向外**：有了 `notes/day1.txt`，必须先删文件 → 再删 `notes` → 再删临时根；目录非空删不掉。  
**只删自己建的**：`createTempDirectory` 给本次实验专用前缀，`deleteIfExists` 只点名本例路径，别递归扫公共临时目录。

| 坑 | 后果 | 正确姿势 |
| --- | --- | --- |
| 不写 UTF-8 | 中文乱码 | `StandardCharsets.UTF_8` |
| `catch (IOException e) {}` | 伪装成空文件 | 保留 `throws` 或明确处理 |
| 目录非空直接删父目录 | 删除失败 | finally 由内向外依次删 |
| 大文件 `readAllLines` | 内存飙高 | 缓冲流逐行读 |
| 递归删公共临时区 | 误删别人文件 | 只删本例创建的路径 |

## 三级练习结果（先预测再跑，对照参考答案）
预测 → 实跑（`FilesExercises`）：

1. **改两行**：`List.of("上海", "学习 Java")` → `读取结果：[上海, 学习 Java]`（半角方括号）。
2. **嵌套路径**：`directory.resolve("notes/day1.txt")`；`writeLines` 会 `createDirectories` 建好 `notes`。`finally` 依次删 `nested` → `directory.resolve("notes")` → `directory`。实跑读回 `[第一天, 文件 IO]`，父目录存在=true。
3. **读不存在文件**：`TextFiles.readLines(Path.of("不存在的文件.txt"))`，`catch (NoSuchFileException e)` 打印 `e.getClass().getSimpleName()` → **`NoSuchFileException`**；其他异常不吞。

## 卡过啥
- 本课入口在 **02-java-advanced**（不是 01-java-basics），主类 `learn.advanced.files.FilesDemo`。
- `./mvnw` 正文对齐；`TextFilesTest` 2 过；练习副本 `javac`/`java` 亦通。
- PATH 默认无 `java`，须 `JAVA_HOME=/opt/jdk-25`。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第32课、未代交验收。
