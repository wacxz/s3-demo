#!/bin/bash

echo "🏗️  开始构建前后端一体化应用"
echo "================================"

# 检查 Node.js 和 Java 环境
if ! command -v node &> /dev/null; then
    echo "❌ Node.js 未安装"
    exit 1
fi

if ! command -v java &> /dev/null; then
    echo "❌ Java 未安装"
    exit 1
fi

# 构建前端
echo "📦 构建前端应用..."
cd frontend

# 安装依赖
if [ ! -d "node_modules" ]; then
    echo "📥 安装前端依赖..."
    npm install
fi

# 构建前端
echo "🔨 打包前端到后端静态资源目录..."
npm run build

if [ $? -eq 0 ]; then
    echo "✅ 前端构建成功"
else
    echo "❌ 前端构建失败"
    exit 1
fi

cd ..

# 构建后端
echo "🔧 构建后端应用..."
cd backend

# 清理之前的构建
echo "🧹 清理之前的构建..."
mvn clean

# 构建后端
echo "🔨 打包后端应用..."
mvn package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ 后端构建成功"
    echo ""
    echo "🎉 构建完成！"
    echo "================================"
    echo "📦 JAR 文件位置: backend/target/s3-demo-0.0.1-SNAPSHOT.jar"
    echo "🌐 启动命令: java -jar backend/target/s3-demo-0.0.1-SNAPSHOT.jar"
    echo "📱 访问地址: http://localhost:8080"
    echo ""
    echo "💡 提示: 确保 MinIO 服务正在运行"
else
    echo "❌ 后端构建失败"
    exit 1
fi

cd .. 