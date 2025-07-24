#!/bin/bash

echo "🚀 启动生产环境应用"
echo "================================"

# 检查 JAR 文件是否存在
JAR_FILE="backend/target/s3-demo-0.0.1-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR 文件不存在，请先运行 ./build.sh 构建应用"
    exit 1
fi

# 检查 MinIO 是否运行
echo "🔍 检查 MinIO 服务..."
if ! curl -f http://localhost:9000/minio/health/live > /dev/null 2>&1; then
    echo "⚠️  MinIO 服务未运行，正在启动..."
    docker-compose up -d
    
    # 等待 MinIO 启动
    echo "⏳ 等待 MinIO 启动..."
    sleep 10
    
    if ! curl -f http://localhost:9000/minio/health/live > /dev/null 2>&1; then
        echo "❌ MinIO 启动失败"
        exit 1
    fi
fi

echo "✅ MinIO 服务正常"

# 启动应用
echo "🌐 启动 Spring Boot 应用..."
echo "📱 访问地址: http://localhost:8080"
echo ""

java -jar "$JAR_FILE" 