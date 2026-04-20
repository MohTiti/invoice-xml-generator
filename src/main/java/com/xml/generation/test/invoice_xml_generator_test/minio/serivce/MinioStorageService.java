package com.xml.generation.test.invoice_xml_generator_test.minio.serivce;

import com.xml.generation.test.invoice_xml_generator_test.minio.properties.MinioProperties;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * Initialize: ensure the bucket exists on startup.
     */
    @PostConstruct
    public void init() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioProperties.getBucketName())
                                .build()
                );
                log.info("Created bucket: {}", minioProperties.getBucketName());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }

    /**
     * Upload a file (replaces writing to desktop path).
     *
     * @param objectKey  the "path" in the bucket, e.g. "2026/04/INV-001.xml"
     * @param content    the file bytes
     * @param contentType e.g. "application/xml"
     */
    public void uploadObject(String objectKey, byte[] content, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectKey)
                            .stream((InputStream) new ByteArrayInputStream(content), (long) content.length, (long) -1)
                            .contentType(contentType)
                            .build()
            );
            log.info("Uploaded to MinIO: {}/{}", minioProperties.getBucketName(), objectKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to MinIO: " + objectKey, e);
        }
    }

    /**
     * Upload from an InputStream (useful for large files).
     */
    public void uploadObject(String objectKey, InputStream inputStream,
                             long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectKey)
                            .stream(inputStream, size, (long) -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to MinIO: " + objectKey, e);
        }
    }

    /**
     * Download an object as bytes.
     */
    public byte[] downloadObject(String objectKey) {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectKey)
                        .build())) {
            return stream.readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download from MinIO: " + objectKey, e);
        }
    }

    /**
     * Generate a presigned download URL (e.g., for frontend to download directly).
     */
    public String getPresignedUrl(String objectKey, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioProperties.getBucketName())
                            .object(objectKey)
                            .expiry(expiryMinutes, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL: " + objectKey, e);
        }
    }

    /**
     * Check if an object exists.
     */
    public boolean objectExists(String objectKey) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectKey)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new RuntimeException("Error checking object existence", e);
        } catch (Exception e) {
            throw new RuntimeException("Error checking object existence", e);
        }
    }
}
