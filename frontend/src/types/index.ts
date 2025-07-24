// 上传URL请求
export interface UploadUrlRequest {
  fileName: string;
  contentType: string;
  fileSize: number;
}

// 上传URL响应
export interface UploadUrlResponse {
  uploadUrl: string;
  objectKey: string;
  bucketName: string;
}

// 文件上传信息
export interface FileUploadInfo {
  objectKey: string;
  fileName: string;
  contentType: string;
  fileSize: number;
  bucketName: string;
  url: string;
  uploadTime: string;
}

// 上传确认请求
export interface UploadConfirmRequest {
  objectKey: string;
  fileName: string;
  contentType: string;
  fileSize: number;
}

// API响应
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
} 