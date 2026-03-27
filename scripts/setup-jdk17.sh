#!/bin/bash

# 设置 JDK 17+ 环境
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH=$JAVA_HOME/bin:$PATH

echo "当前 Java 版本:"
java -version

echo ""
echo "JAVA_HOME: $JAVA_HOME"

echo ""
echo "开始清理并重新编译项目..."

# 清理之前的编译结果
mvn clean

# 重新编译
mvn compile

echo ""
echo "编译完成！"
