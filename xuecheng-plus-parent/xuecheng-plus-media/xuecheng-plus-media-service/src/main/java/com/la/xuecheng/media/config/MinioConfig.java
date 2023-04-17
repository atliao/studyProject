package com.la.xuecheng.media.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LA
 * @createDate 2023-04-13-21:23
 * @description
 */
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    String endpoint;
    @Value("${minio.accessKey}")
    String accessKey;
    @Value(("${minio.secretKey}"))
    String secretKey;

    @Bean
    public MinioClient getMinioClient(){
        MinioClient minioClient = MinioClient
                .builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        return minioClient;
    }

}
