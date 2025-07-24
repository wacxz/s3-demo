#!/bin/bash

echo "🚀 启动 S3 文件上传演示项目"
echo "================================"

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker 未运行，请先启动 Docker"
    exit 1
fi

# 启动 MinIO
echo "📦 启动 MinIO 容器..."
docker-compose up -d

# 等待 MinIO 启动
echo "⏳ 等待 MinIO 启动..."
sleep 10

# 检查 MinIO 是否启动成功
if curl -f http://localhost:9000/minio/health/live > /dev/null 2>&1; then
    echo "✅ MinIO 启动成功"
    echo "   MinIO 控制台: http://localhost:9001"
    echo "   用户名: minioadmin"
    echo "   密码: minioadmin"
else
    echo "❌ MinIO 启动失败"
    exit 1
fi

echo ""
echo "🔧 启动后端服务..."
cd backend

# 检查 Java 版本
if ! java -version 2>&1 | grep -q "version \"17"; then
    echo "❌ 需要 Java 17，当前版本:"
    java -version
    exit 1
fi

# 启动后端
echo "📡 启动 Spring Boot 后端 (端口: 8080)..."
./mvnw spring-boot:run &
BACKEND_PID=$!

# 等待后端启动
echo "⏳ 等待后端启动..."
sleep 15

# 检查后端是否启动成功
if curl -f http://localhost:8080/api/files/health > /dev/null 2>&1; then
    echo "✅ 后端启动成功"
else
    echo "❌ 后端启动失败"
    kill $BACKEND_PID 2>/dev/null
    exit 1
fi

echo ""
echo "🎨 启动前端服务..."
cd ../frontend

# 安装依赖
if [ ! -d "node_modules" ]; then
    echo "📦 安装前端依赖..."
    npm install
fi

# 启动前端
echo "🌐 启动 Vue 前端 (端口: 3000)..."
npm run dev &
FRONTEND_PID=$!

# 等待前端启动
echo "⏳ 等待前端启动..."
sleep 10

echo ""
echo "🎉 项目启动完成！"
echo "================================"
echo "📱 前端地址: http://localhost:3000 (本地访问)"
echo "📱 前端地址: http://$(hostname -I | awk '{print $1}'):3000 (网络访问)"
echo "🔧 后端地址: http://localhost:8080"
echo "📦 MinIO 控制台: http://localhost:9001"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待用户中断
trap 'echo ""; echo "🛑 正在停止服务..."; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; docker-compose down; echo "✅ 所有服务已停止"; exit 0' INT

# 保持脚本运行
wait 