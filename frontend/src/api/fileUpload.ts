import axios from 'axios';
import type { 
  UploadUrlRequest, 
  UploadUrlResponse, 
  FileUploadInfo, 
  UploadConfirmRequest 
} from '../types';

// 根据环境使用不同的API基础URL
const API_BASE_URL = import.meta.env.PROD ? '/api/files' : '/api/files';

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.status, response.data);
    return response;
  },
  (error) => {
    console.error('API Response Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

export const fileUploadApi = {
  /**
   * 获取上传URL
   */
  async getUploadUrl(request: UploadUrlRequest): Promise<UploadUrlResponse> {
    const response = await apiClient.post<UploadUrlResponse>('/upload-url', request);
    return response.data;
  },

  /**
   * 确认文件上传完成
   */
  async confirmUpload(request: UploadConfirmRequest): Promise<FileUploadInfo> {
    const response = await apiClient.post<FileUploadInfo>('/confirm-upload', request);
    return response.data;
  },

  /**
   * 删除文件
   */
  async deleteFile(objectKey: string): Promise<{ message: string }> {
    const response = await apiClient.delete<{ message: string }>(`/${objectKey}`);
    return response.data;
  },

  /**
   * 健康检查
   */
  async healthCheck(): Promise<{ status: string; message: string }> {
    const response = await apiClient.get<{ status: string; message: string }>('/health');
    return response.data;
  },

  /**
   * 直接上传文件到MinIO
   */
  async uploadFileToMinio(uploadUrl: string, file: File, onProgress?: (progress: number) => void): Promise<void> {
    await axios.put(uploadUrl, file, {
      headers: {
        'Content-Type': file.type,
      },
      timeout: 600000, // 60秒超时
      onUploadProgress: (progressEvent) => {
        if (progressEvent.total && onProgress) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(progress);
        }
      },
    });
  },
}; 