# Java 第07课：ArraysDemo（数组）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson07_arrays/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
数组用一个变量保存多个**相同类型**的元素，创建后**长度固定**。  
`main` 建 `int[] scores = {72, 95, 81}`，先打印长度 3，再按下标 0/1/2 逐个打印，最后调用 `max(scores)` 得到 95。  
`max` 先拒绝空数组；用 `values[0]` 作初值，从下标 1 比到末尾，更大才更新——不能写死成 0（全负数会错）。  
正文五行：长度 → 三个 `scores[i]=…` → `最高分=95`。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson07_arrays.ArraysDemo'
# 可选：
# ./mvnw -pl 01-java-basics test '-Dtest=learn.lesson07_arrays.ArraysDemoTest'
```

正文五行与讲义一致；Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:33:19+08:00）。可选测试 2 例通过（Finished at 2026-11-10T02:33:24+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/07-arrays/` —— `ArraysDemo`；`SumProbe`（总分=248）；`MaxIndexProbe`（最高分下标=1）；`AverageProbe`（`average({1,2})=1.5`，`(double)sum/length` 防整除丢小数）。

## 原理（小白话）
**数组=同类型一排格子**：`int[] scores` 像一排固定格，每格一个 int；创建后 **length 固定**，不能像 Python `list` 那样随时 `append`。  
**下标从 0**：长度 3 的有效下标是 0、1、2；访问 `scores[3]` → **`ArrayIndexOutOfBoundsException`**。读长度用 `scores.length`（字段，不是方法）。  
**遍历两种**：需要下标用 `for (int i = 0; i < scores.length; i++)`；只要元素用增强 for `for (int score : scores)`（不知道当前位置，也不适合按位改写）。  
**max 初值**：必须用 `values[0]`，再从下标 1 比；写死 `0` 时若全是负数会错误返回 0。空数组没有第一个元素 → 明确 `throw IllegalArgumentException`。  
**average**：先求和再 `(double) sum / length`，否则两个 int 相除丢掉小数。  
对比 Python（零基础白话）：Python `list` 可增删、可混类型；Java 数组长度固定、元素同类型。Python 也是下标从 0；越界两边都会炸，只是异常名不同。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文五行对齐；可选 `ArraysDemoTest` 2 例通过。
- 提醒：`max` 初值别写死 0；空数组要先拒绝；`average` 记得 `(double)` 转型。
- 练习确认：总分 248；最高分下标 1（值 95）；`average(1,2)=1.5`；空数组 `max` 抛「数组不能为空」。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第08课。
