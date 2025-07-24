package com.example.s3demo.controller;

import com.example.s3demo.dto.FileUploadInfo;
import com.example.s3demo.dto.UploadUrlRequest;
import com.example.s3demo.dto.UploadUrlResponse;
import com.example.s3demo.service.MinioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private MinioService minioService;

    /**
     * 获取文件上传URL
     */
    @PostMapping("/upload-url")
    public ResponseEntity<UploadUrlResponse> getUploadUrl(@Valid @RequestBody UploadUrlRequest request) {
        try {
            logger.info("Requesting upload URL for file: {}, size: {}", request.getFileName(), request.getFileSize());
            
            UploadUrlResponse response = minioService.generateUploadUrl(
                    request.getFileName(),
                    request.getContentType(),
                    request.getFileSize()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error generating upload URL", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 确认文件上传完成
     */
    @PostMapping("/confirm-upload")
    public ResponseEntity<FileUploadInfo> confirmUpload(@RequestBody Map<String, Object> request) {
        try {
            String objectKey = (String) request.get("objectKey");
            String fileName = (String) request.get("fileName");
            String contentType = (String) request.get("contentType");
            Long fileSize = Long.valueOf(request.get("fileSize").toString());

            logger.info("Confirming upload for objectKey: {}, fileName: {}", objectKey, fileName);

            // 检查文件是否真的存在于MinIO中
            if (!minioService.fileExists(objectKey)) {
                logger.warn("File not found in MinIO: {}", objectKey);
                return ResponseEntity.badRequest().build();
            }

            // 保存文件上传信息
            FileUploadInfo fileInfo = minioService.saveFileUploadInfo(
                    objectKey, fileName, contentType, fileSize
            );

            return ResponseEntity.ok(fileInfo);
        } catch (Exception e) {
            logger.error("Error confirming upload", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{objectKey}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String objectKey) {
        try {
            logger.info("Deleting file: {}", objectKey);
            
            minioService.deleteFile(objectKey);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "文件删除成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deleting file: {}", objectKey, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "文件上传服务运行正常");
        return ResponseEntity.ok(response);
    }
} 