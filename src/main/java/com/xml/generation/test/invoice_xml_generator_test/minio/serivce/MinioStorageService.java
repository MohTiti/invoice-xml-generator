package com.xml.generation.test.invoice_xml_generator_test.minio.serivce;

import com.xml.generation.test.invoice_xml_generator_test.logging.CustomLogging;
import com.xml.generation.test.invoice_xml_generator_test.minio.properties.MinioProperties;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "program.task-type", havingValue = "PUBLISH")
public class MinioStorageService {

    private final MinioClient minioClient;

    private final MinioProperties minioProperties;

    @Value("${publisher.taxpayer}")
    private String taxPayer;

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
                CustomLogging.logInfo(taxPayer, null, null, "Created bucket: {}", minioProperties.getBucketName());
            }
        } catch (Exception e) {
            CustomLogging.logError(taxPayer, null, "Failed to create bucket: {}", minioProperties.getBucketName());
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }

    public void uploadObject(String objectKey, Long invoiceId, String invoiceNumber, byte[] content, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectKey)
                            .stream((InputStream) new ByteArrayInputStream(content), (long) content.length, (long) -1)
                            .contentType(contentType)
                            .build()
            );
            CustomLogging.logInfo(taxPayer, invoiceNumber, invoiceId, "Uploaded to MinIO: {}/{}", minioProperties.getBucketName(), objectKey);
        } catch (Exception e) {
            CustomLogging.logError(taxPayer, invoiceId, "Failed Uploaded to MinIO: {}/{}", minioProperties.getBucketName(), objectKey);
            throw new RuntimeException("Failed to upload to MinIO: " + objectKey, e);
        }
    }

}
