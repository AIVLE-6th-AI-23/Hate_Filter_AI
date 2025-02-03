package com.github.aivle6th.ai23.springboot_backend.service;

import com.azure.storage.blob.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class BlobStorageService {

    private final BlobContainerClient containerClient;

    public BlobStorageService(
        @Value("${azure.storage.connection-string}") String connectionString,
        @Value("${azure.storage.container-name}") String containerName
    ) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
    }

    // 파일 업로드 및 URL 반환
    public String uploadFile(MultipartFile file, Long postId) throws IOException {
        String blobName = "Post" + postId + "/" + file.getContentType() + "_" + System.currentTimeMillis() + getFileExtension(file);
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        // Blob Storage에 업로드
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        // Blob URL 반환
        return blobClient.getBlobUrl();
    }

    public String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "";
        }
    
        int dotIndex = originalFilename.lastIndexOf(".");
        return (dotIndex == -1) ? "" : originalFilename.substring(dotIndex);
    }
}
