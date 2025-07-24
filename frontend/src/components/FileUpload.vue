<template>
  <div class="file-upload-container">
    <el-card class="upload-card">
      <template #header>
        <div class="card-header">
          <h2>文件上传到 MinIO</h2>
          <el-tag :type="healthStatus === 'UP' ? 'success' : 'danger'">
            {{ healthStatus === 'UP' ? '服务正常' : '服务异常' }}
          </el-tag>
        </div>
      </template>

      <!-- 文件选择区域 -->
      <div class="upload-area">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :show-file-list="false"
          :on-change="handleFileChange"
          :before-upload="beforeUpload"
          drag
          class="upload-dragger"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持任意类型文件，单个文件最大 100MB
            </div>
          </template>
        </el-upload>
      </div>

      <!-- 文件信息显示 -->
      <div v-if="selectedFile" class="file-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件名">
            {{ selectedFile.name }}
          </el-descriptions-item>
          <el-descriptions-item label="文件大小">
            {{ formatFileSize(selectedFile.size) }}
          </el-descriptions-item>
          <el-descriptions-item label="文件类型">
            {{ selectedFile.type || '未知' }}
          </el-descriptions-item>
          <el-descriptions-item label="最后修改">
            {{ formatDate(selectedFile.lastModified) }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="upload-actions">
          <el-button 
            type="primary" 
            :loading="uploading" 
            @click="handleUpload"
            :disabled="!selectedFile"
          >
            {{ uploading ? '上传中...' : '开始上传' }}
          </el-button>
          <el-button @click="clearFile">清除文件</el-button>
        </div>
      </div>

      <!-- 上传进度 -->
      <div v-if="uploading" class="upload-progress">
        <el-progress 
          :percentage="uploadProgress" 
          :status="uploadProgress === 100 ? 'success' : ''"
          :stroke-width="8"
          :show-text="false"
        />
        <div class="progress-info">
          <p class="progress-text">{{ uploadStatus }}</p>
          <div class="progress-stats">
            <span v-if="uploadSpeed" class="upload-speed">{{ uploadSpeed }}</span>
            <span class="progress-percentage">{{ uploadProgress }}%</span>
          </div>
        </div>
      </div>

      <!-- 上传结果 -->
      <div v-if="uploadResult" class="upload-result">
        <el-alert
          :title="uploadResult.success ? '上传成功' : '上传失败'"
          :type="uploadResult.success ? 'success' : 'error'"
          :description="uploadResult.message"
          show-icon
          :closable="false"
        />
        
        <div v-if="uploadResult.success && uploadResult.data" class="result-details">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="对象键">
              {{ uploadResult.data.objectKey }}
            </el-descriptions-item>
            <el-descriptions-item label="文件URL">
              <el-link :href="uploadResult.data.url" target="_blank" type="primary">
                {{ uploadResult.data.url }}
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="上传时间">
              {{ uploadResult.data.uploadTime }}
            </el-descriptions-item>
          </el-descriptions>

          <div class="result-actions">
            <el-button 
              type="danger" 
              size="small"
              @click="handleDeleteFile(uploadResult.data.objectKey)"
            >
              删除文件
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import { fileUploadApi } from '../api/fileUpload';
import type { FileUploadInfo } from '../types';

// 响应式数据
const uploadRef = ref();
const selectedFile = ref<File | null>(null);
const uploading = ref(false);
const uploadProgress = ref(0);
const uploadStatus = ref('');
const uploadSpeed = ref('');
const healthStatus = ref('UNKNOWN');
const uploadResult = ref<{
  success: boolean;
  message: string;
  data?: FileUploadInfo;
} | null>(null);

// 健康检查
const checkHealth = async () => {
  try {
    const health = await fileUploadApi.healthCheck();
    healthStatus.value = health.status;
  } catch (error) {
    healthStatus.value = 'DOWN';
    console.error('健康检查失败:', error);
  }
};

// 文件选择处理
const handleFileChange = (file: any) => {
  selectedFile.value = file.raw;
  uploadResult.value = null;
};

// 上传前验证
const beforeUpload = (file: File) => {
  const maxSize = 100 * 1024 * 1024; // 100MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 100MB');
    return false;
  }
  return true;
};

// 清除文件
const clearFile = () => {
  selectedFile.value = null;
  uploadResult.value = null;
  uploadProgress.value = 0;
  uploadStatus.value = '';
  uploadSpeed.value = '';
  if (uploadRef.value) {
    uploadRef.value.clearFiles();
  }
};

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 格式化速度
const formatSpeed = (bytesPerSecond: number): string => {
  if (bytesPerSecond === 0) return '0 B/s';
  const k = 1024;
  const sizes = ['B/s', 'KB/s', 'MB/s', 'GB/s'];
  const i = Math.floor(Math.log(bytesPerSecond) / Math.log(k));
  return parseFloat((bytesPerSecond / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 格式化日期
const formatDate = (timestamp: number): string => {
  return new Date(timestamp).toLocaleString('zh-CN');
};

// 上传文件
const handleUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件');
    return;
  }

  uploading.value = true;
  uploadProgress.value = 0;
  uploadStatus.value = '正在获取上传链接...';
  uploadSpeed.value = '';
  uploadResult.value = null;

  try {
    // 步骤1: 获取上传URL
    uploadProgress.value = 0;
    uploadStatus.value = '获取上传链接...';
    
    const uploadUrlResponse = await fileUploadApi.getUploadUrl({
      fileName: selectedFile.value.name,
      contentType: selectedFile.value.type,
      fileSize: selectedFile.value.size,
    });

    // 步骤2: 上传文件到MinIO
    uploadProgress.value = 0;
    uploadStatus.value = '正在上传文件到MinIO...';
    
    let startTime = Date.now();
    let lastLoaded = 0;
    
    await fileUploadApi.uploadFileToMinio(
      uploadUrlResponse.uploadUrl, 
      selectedFile.value,
      (progress) => {
        // 直接使用真实的上传进度
        uploadProgress.value = progress;
        uploadStatus.value = `正在上传文件到MinIO...`;
        
        // 计算上传速度
        const currentTime = Date.now();
        const timeElapsed = (currentTime - startTime) / 1000; // 秒
        const loaded = (progress / 100) * selectedFile.value!.size;
        const loadedDiff = loaded - lastLoaded;
        
        if (timeElapsed > 0 && loadedDiff > 0) {
          const speed = loadedDiff / timeElapsed;
          uploadSpeed.value = formatSpeed(speed);
        }
        
        startTime = currentTime;
        lastLoaded = loaded;
      }
    );

    // 步骤3: 确认上传完成
    uploadStatus.value = '确认上传完成...';
    
    const fileInfo = await fileUploadApi.confirmUpload({
      objectKey: uploadUrlResponse.objectKey,
      fileName: selectedFile.value.name,
      contentType: selectedFile.value.type,
      fileSize: selectedFile.value.size,
    });

    // 步骤4: 完成
    uploadProgress.value = 100;
    uploadStatus.value = '上传完成！';
    
    uploadResult.value = {
      success: true,
      message: '文件上传成功！',
      data: fileInfo,
    };

    ElMessage.success('文件上传成功！');
  } catch (error: any) {
    console.error('上传失败:', error);
    uploadProgress.value = 0;
    uploadStatus.value = '上传失败';
    
    uploadResult.value = {
      success: false,
      message: error.response?.data?.message || error.message || '上传失败',
    };

    ElMessage.error('文件上传失败: ' + uploadResult.value.message);
  } finally {
    uploading.value = false;
  }
};

// 删除文件
const handleDeleteFile = async (objectKey: string) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个文件吗？此操作不可恢复。',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    await fileUploadApi.deleteFile(objectKey);
    ElMessage.success('文件删除成功');
    uploadResult.value = null;
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除文件失败: ' + (error.response?.data?.message || error.message));
    }
  }
};

// 组件挂载时检查健康状态
onMounted(() => {
  checkHealth();
});
</script>

<style scoped>
.file-upload-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.upload-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  color: #303133;
}

.upload-area {
  margin-bottom: 20px;
}

.upload-dragger {
  width: 100%;
}

.file-info {
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.upload-actions {
  margin-top: 20px;
  text-align: center;
}

.upload-progress {
  margin-top: 20px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.progress-text {
  color: #606266;
  margin: 0;
  flex: 1;
}

.progress-stats {
  display: flex;
  align-items: center;
  gap: 15px;
}

.upload-speed {
  color: #67c23a;
  font-size: 12px;
  font-weight: 500;
}

.progress-percentage {
  color: #409eff;
  font-weight: bold;
  margin: 0;
  min-width: 50px;
  text-align: right;
}

.upload-result {
  margin-top: 20px;
}

.result-details {
  margin-top: 20px;
  padding: 20px;
  background-color: #f0f9ff;
  border-radius: 4px;
}

.result-actions {
  margin-top: 20px;
  text-align: center;
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 200px;
}

:deep(.el-descriptions__label) {
  font-weight: bold;
  color: #606266;
}
</style> 