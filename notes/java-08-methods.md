# Java 第08课：MethodsDemo（方法）

仓库讲义：`follower-ding/java-learning-lab` → `01-java-basics/.../lesson08_methods/`（只读引用，未改讲义）  
本地跑通：2026-11-10（Asia/Shanghai）· Temurin JDK 25.0.5 + `./mvnw`

## 复述（学了啥）
方法是一段**有名字、可接收参数、可返回结果**的代码块，用来消重复、给操作起清楚名字。  
`MethodsDemo` 定义两个重载 `area`（`int`/`double`）：先拒绝负边长，再相乘返回；`private greeting` 拼「你好，」+ 名字，局部变量 `sentence` 只在方法内可见。  
`main` 先 `area(3,4)` 得 12，再 `area(2.5,4.0)` 得 10.0，最后打印问候。  
正文三行：`整数矩形面积=12` → `小数矩形面积=10.0` → `你好，小明`。

## 跑通
仓根（`_refs/java-learning-lab`）：

```sh
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -pl 01-java-basics compile exec:java '-Dexec.mainClass=learn.lesson08_methods.MethodsDemo'
# 可选：
# ./mvnw -pl 01-java-basics test '-Dtest=learn.lesson08_methods.MethodsDemoTest'
```

正文三行与讲义一致；Maven `BUILD SUCCESS`（Finished at 2026-11-10T02:34:52+08:00）。可选测试 3 例通过（Finished at 2026-11-10T02:34:56+08:00）。  
练习副本（自敲，不进 java 仓）：`/workspace/iume-learning/java/08-methods/` —— `MethodsDemo`（含 square / area(long,long) / clamp）；`SquareProbe`（`square(5)=25`）；`ClampProbe`（`clamp(2/5/9,3,8)` → 3/5/8；`area(3L,4L)` 选 long 重载）。

## 原理（小白话）
**方法=有名可复用的代码块**：像给一段步骤起名；调用时把实参塞进形参，算完 `return` 交回结果。  
**形参 / 实参**：定义括号里是形参（空盒子），调用时传入的 3、4 是实参（往盒子里装的值）。  
**return / void**：`return` 把结果交回并结束方法；返回类型写 `void` 表示不返回值（只做事）。声明了非 void 返回类型，每个分支都得有 `return`，否则编译失败。  
**重载**：同名、参数类型或数量不同 → 编译器按实参选型；**只改返回类型不算重载**。例：`area(3,4)` 仍选 `int`；`area(3L,4L)` 选 `long`。  
**局部变量作用域**：在方法/花括号里声明的变量，离开就不可见；调用者只拿到返回值。`greeting` 里的 `sentence` 出方法后用不了。  
**private**：只供本类内部调用；外面不能直接 `MethodsDemo.greeting(...)`。  
对比 Python（零基础白话）：Python `def area(width, height):` 不写类型；Java 要写返回类型和参数类型。Python 也有默认参数，Java 基础课常用重载代替。两边都有「局部变量只在函数里」；Java 的 `private`/`static` 是类层面的可见性与调用方式，Python 用命名约定（`_name`）表达「内部用」。

## 卡过啥
- 本课 `./mvnw` 一次 `BUILD SUCCESS`，正文三行对齐；可选 `MethodsDemoTest` 3 例通过。
- 提醒：负边长要先 `throw`；`area(3,4)` 不会自动选 `long`——字面量是 int；`3L,4L` 才进 long 重载。
- 练习确认：`square(5)=25`；`clamp(2,3,8)=3`、`clamp(5,3,8)=5`、`clamp(9,3,8)=8`；min>max 抛「min 不能大于 max」。
- 作业笔记写 `iume-learning`；**不改** `java-learning-lab` 讲义；未发知白台、未开第09课。
