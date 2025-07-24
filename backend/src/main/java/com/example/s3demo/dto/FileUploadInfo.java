package com.example.s3demo.dto;

import java.time.LocalDateTime;

public class FileUploadInfo {

    private String objectKey;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String bucketName;
    private String url;
    private LocalDateTime uploadTime;

    public FileUploadInfo() {
    }

    public FileUploadInfo(String objectKey, String fileName, String contentType, 
                         Long fileSize, String bucketName, String url) {
        this.objectKey = objectKey;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.bucketName = bucketName;
        this.url = url;
        this.uploadTime = LocalDateTime.now();
    }


    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
} 