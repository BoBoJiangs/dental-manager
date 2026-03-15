package com.company.dental.integration.file;

import com.company.dental.common.enums.ErrorCode;
import com.company.dental.common.exception.BusinessException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MinioFileStorageService implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public MinioFileStorageService(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public String upload(String objectName, InputStream inputStream, String contentType) {
        try {
            ensureBucketExists();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .stream(inputStream, -1, 10 * 1024 * 1024)
                            .contentType(contentType)
                            .build()
            );
            return objectName;
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucketName())
                .build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .build());
        }
    }
}
