package com.example.s3demo.dto;

public class UploadUrlResponse {

    private String uploadUrl;
    private String objectKey;
    private String bucketName;

    public UploadUrlResponse() {
    }

    public UploadUrlResponse(String uploadUrl, String objectKey, String bucketName) {
        this.uploadUrl = uploadUrl;
        this.objectKey = objectKey;
        this.bucketName = bucketName;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
} 