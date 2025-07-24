<template>
  <div id="app">
    <header class="app-header">
      <div class="header-content">
        <h1>S3 文件上传演示</h1>
        <p>使用 Vue 3 + Spring Boot + MinIO 构建的文件上传系统</p>
      </div>
    </header>
    
    <main class="app-main">
      <FileUpload />
    </main>
    
    <footer class="app-footer">
      <p>&copy; 2026 S3 文件上传演示项目</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import FileUpload from './components/FileUpload.vue'
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
  color: #303133;
  line-height: 1.6;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 40px 20px;
  text-align: center;
}

.header-content h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 300;
}

.header-content p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.app-main {
  flex: 1;
  padding: 40px 0;
}

.app-footer {
  background-color: #303133;
  color: white;
  text-align: center;
  padding: 20px;
  margin-top: auto;
}

.app-footer p {
  opacity: 0.8;
}

/* Element Plus 样式覆盖 */
.el-card {
  border-radius: 8px;
}

.el-button {
  border-radius: 6px;
}

.el-upload-dragger {
  border-radius: 6px;
}
</style> 