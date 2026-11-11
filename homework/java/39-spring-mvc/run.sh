#!/usr/bin/env bash
set -euo pipefail
export JAVA_HOME=/opt/jdk-25
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS='-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED'
ROOT="$(cd "$(dirname "$0")" && pwd)"
CP="$ROOT:$(cat "$ROOT/classpath.txt")"
cd "$ROOT"
# 不编进讲义仓；classpath 含 Spring MVC 7.0.9、嵌入式 Tomcat 11.0.24、Jackson 3.1.5、spring-test 等
find learn -name '*.class' -delete
javac --class-path "$(cat "$ROOT/classpath.txt")" learn/spring/mvc/*.java
echo "===== MvcDemo ====="
java -cp "$CP" learn.spring.mvc.MvcDemo
echo "===== PracticeOne ====="
java -cp "$CP" learn.spring.mvc.PracticeOne
echo "===== PracticeTwo ====="
java -cp "$CP" learn.spring.mvc.PracticeTwo
echo "===== PracticeThree ====="
java -cp "$CP" learn.spring.mvc.PracticeThree
