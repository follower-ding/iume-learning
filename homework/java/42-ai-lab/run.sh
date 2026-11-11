#!/usr/bin/env bash
set -euo pipefail
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8'
ROOT="$(cd "$(dirname "$0")" && pwd)"
JARS="$(tr -d '\n' < "$ROOT/classpath.txt")"
OUT="$ROOT/out"
rm -rf "$OUT"
mkdir -p "$OUT"
# 与讲义 pom 的 maven.compiler.parameters=true 对齐，否则工具参数绑不上
javac -parameters --class-path "$JARS" -d "$OUT" "$ROOT"/src/main/java/learn/ai/lesson42/*.java
CP="$OUT:$JARS"
# 默认走本机替身，不要加 --live，不要配密钥，不要访问公网
echo "===== AiLabDemo ====="
java -cp "$CP" learn.ai.lesson42.AiLabDemo
echo "===== PracticeOne ====="
java -cp "$CP" learn.ai.lesson42.PracticeOne
echo "===== PracticeTwo ====="
java -cp "$CP" learn.ai.lesson42.PracticeTwo
echo "===== PracticeThree ====="
java -cp "$CP" learn.ai.lesson42.PracticeThree
