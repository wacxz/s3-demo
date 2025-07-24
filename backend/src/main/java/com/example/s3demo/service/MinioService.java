package com.example.s3demo.service;

import com.example.s3demo.dto.FileUploadInfo;
import com.example.s3demo.dto.UploadUrlResponse;
import io.minio.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 生成预签名上传URL
     */
    public UploadUrlResponse generateUploadUrl(String fileName, String contentType, Long fileSize) {
        try {
            // 确保bucket存在
            ensureBucketExists();

            // 生成唯一的对象键
            String objectKey = generateObjectKey(fileName);

            // 生成预签名URL，有效期15分钟
            String uploadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(15, java.util.concurrent.TimeUnit.MINUTES)
                            .build()
            );

            logger.info("Generated upload URL for file: {}, objectKey: {}", fileName, objectKey);

            return new UploadUrlResponse(uploadUrl, objectKey, bucketName);
        } catch (Exception e) {
            logger.error("Error generating upload URL for file: {}", fileName, e);
            throw new RuntimeException("生成上传URL失败", e);
        }
    }

    /**
     * 保存文件上传信息
     */
    public FileUploadInfo saveFileUploadInfo(String objectKey, String fileName, 
                                           String contentType, Long fileSize) {
        try {
            // 构建文件访问URL
            String fileUrl = endpoint + "/" + bucketName + "/" + objectKey;

            FileUploadInfo fileInfo = new FileUploadInfo(
                    objectKey, fileName, contentType, fileSize, bucketName, fileUrl
            );

            logger.info("Saved file upload info: {}", fileInfo);

            return fileInfo;
        } catch (Exception e) {
            logger.error("Error saving file upload info for objectKey: {}", objectKey, e);
            throw new RuntimeException("保存文件上传信息失败", e);
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String objectKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .build()
            );
            logger.info("Deleted file: {}", objectKey);
        } catch (Exception e) {
            logger.error("Error deleting file: {}", objectKey, e);
            throw new RuntimeException("删除文件失败", e);
        }
    }

    /**
     * 确保bucket存在
     */
    private void ensureBucketExists() {
        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                logger.info("Created bucket: {}", bucketName);
            }
        } catch (Exception e) {
            logger.error("Error ensuring bucket exists: {}", bucketName, e);
            throw new RuntimeException("确保bucket存在失败", e);
        }
    }

    /**
     * 生成唯一的对象键
     */
    private String generateObjectKey(String fileName) {
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex);
        }
        
        String baseName = fileName.substring(0, lastDotIndex > 0 ? lastDotIndex : fileName.length());
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("%s_%s_%s%s", baseName, timestamp, uuid, extension);
    }
} 