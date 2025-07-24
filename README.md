# S3 文件上传演示项目

这是一个使用 Vue 3 + Vite + TypeScript 前端和 Spring Boot 3.4 + Java 17 后端的文件上传系统，集成 MinIO 对象存储。

![](https://image.101723.xyz/202507/0724091149112.png)
![](https://image.101723.xyz/202507/0724091221761.png)

## 项目结构

```
s3-demo/
├── frontend/          # Vue 3 + Vite + TypeScript 前端
├── backend/           # Spring Boot 3.4 + Java 17 后端
└── docker-compose.yml # MinIO 容器配置
```

## 功能特性

1. 前端上传文件到 MinIO 中
2. 前端不直接与 MinIO 交互，由后端生成临时上传链接
3. 前端使用临时链接上传文件，上传成功后将信息写入后端

## 快速开始

### 开发环境

#### 方式一：一键启动（推荐）
```bash
./start.sh
```

#### 方式二：手动启动

1. **启动 MinIO**
```bash
docker-compose up -d
```

2. **启动后端**
```bash
cd backend
./mvnw spring-boot:run
```

3. **启动前端**
```bash
cd frontend
npm install
npm run dev
```

#### 停止服务
```bash
./stop.sh
```

### 生产环境

#### 构建一体化应用
```bash
./build.sh
```

#### 启动生产环境
```bash
./run-prod.sh
```

#### 手动启动
```bash
# 确保 MinIO 运行
docker-compose up -d

# 启动应用
java -jar backend/target/s3-demo-0.0.1-SNAPSHOT.jar
```

## 技术栈

### 前端
- Vue 3
- Vite
- TypeScript
- Axios
- Element Plus

### 后端
- Spring Boot 3.4
- Java 17
- MinIO Java Client
- Spring Web

### 存储
- MinIO (S3 兼容的对象存储)

## 系统架构

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   前端      │    │   后端      │    │   MinIO     │
│  Vue 3      │◄──►│ Spring Boot │◄──►│  存储服务   │
│  (3000)     │    │   (8080)    │    │   (9000)    │
└─────────────┘    └─────────────┘    └─────────────┘
```

## 工作流程

1. **前端选择文件** → 显示文件信息
2. **前端请求后端** → 获取预签名上传URL
3. **前端直接上传** → 使用预签名URL上传到MinIO
4. **前端确认上传** → 通知后端上传完成
5. **后端验证文件** → 检查文件是否存在于MinIO
6. **后端保存信息** → 记录文件元数据

## 访问地址

### 开发环境
- **前端应用**: 
  - 本地访问: http://localhost:3000
  - 网络访问: http://[您的IP地址]:3000
- **后端API**: http://localhost:8080
- **MinIO控制台**: http://localhost:9001 (用户名: minioadmin, 密码: minioadmin)

### 生产环境
- **应用地址**: http://localhost:8080
- **MinIO控制台**: http://localhost:9001 (用户名: minioadmin, 密码: minioadmin)

## API 接口

### 获取上传URL
```
POST /api/files/upload-url
Content-Type: application/json

{
  "fileName": "example.jpg",
  "contentType": "image/jpeg",
  "fileSize": 1024000
}
```

### 确认上传完成
```
POST /api/files/confirm-upload
Content-Type: application/json

{
  "objectKey": "example_1234567890_abc123.jpg",
  "fileName": "example.jpg",
  "contentType": "image/jpeg",
  "fileSize": 1024000
}
```

### 删除文件
```
DELETE /api/files/{objectKey}
```

### 健康检查
```
GET /api/files/health
``` 