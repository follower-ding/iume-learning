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
# XML 必须从 resources 进 classpath，不要只放在 src/main/java
cp -a "$ROOT/src/main/resources/." "$OUT/"
javac --class-path "$JARS" -d "$OUT" "$ROOT"/src/main/java/learn/mybatis/*.java
CP="$OUT:$JARS"
echo "===== MyBatisDemo ====="
java -cp "$CP" learn.mybatis.MyBatisDemo
echo "===== PracticeOne ====="
java -cp "$CP" learn.mybatis.PracticeOne
echo "===== PracticeTwo ====="
java -cp "$CP" learn.mybatis.PracticeTwo
echo "===== PracticeThree ====="
java -cp "$CP" learn.mybatis.PracticeThree
