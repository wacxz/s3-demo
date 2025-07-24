#!/bin/bash

echo "🛑 停止 S3 文件上传演示项目"
echo "================================"

# 停止前端和后端进程
echo "🔧 停止后端服务..."
pkill -f "spring-boot:run" 2>/dev/null

echo "🎨 停止前端服务..."
pkill -f "vite" 2>/dev/null

# 停止 MinIO 容器
echo "📦 停止 MinIO 容器..."
docker-compose down

echo "✅ 所有服务已停止" 