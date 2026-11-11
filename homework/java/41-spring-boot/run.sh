#!/usr/bin/env bash
set -euo pipefail
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED'
ROOT="$(cd "$(dirname "$0")" && pwd)"
JARS="$(tr -d '\n' < "$ROOT/classpath.txt")"
OUT="$ROOT/out"
rm -rf "$OUT"
mkdir -p "$OUT"
# yml / schema.sql / Mapper XML 必须从 resources 进 classpath
cp -a "$ROOT/src/main/resources/." "$OUT/"
javac --class-path "$JARS" -d "$OUT" "$ROOT"/src/main/java/learn/boot/*.java
CP="$OUT:$JARS"
# 默认 --demo 或练习入口，用完退出；不要去掉 --demo 去占 8085
echo "===== LearningApplication --demo ====="
java -cp "$CP" learn.boot.LearningApplication --demo
echo "===== PracticeOne ====="
java -cp "$CP" learn.boot.PracticeOne
echo "===== PracticeTwo ====="
java -cp "$CP" learn.boot.PracticeTwo
echo "===== PracticeThree ====="
java -cp "$CP" learn.boot.PracticeThree
