package com.jiangou.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);

    @Bean
    @ConfigurationProperties(prefix = "jiangou.minio")
    public MinioProperties minioProperties() {
        return new MinioProperties();
    }

    @Bean
    public MinioClient minioClient(MinioProperties props) {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(props.getEndpoint())
                    .credentials(props.getAccessKey(), props.getSecretKey())
                    .build();
            ensureBucket(client, props.getBucket());
            return client;
        } catch (Exception e) {
            log.warn("MinIO unavailable, media upload disabled until docker compose is up: {}", e.getMessage());
            return null;
        }
    }

    private void ensureBucket(MinioClient client, String bucket) throws Exception {
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info("Created MinIO bucket: {}", bucket);
        }
    }

    @Data
    public static class MinioProperties {
        private String endpoint;
        private String publicEndpoint;
        private String accessKey;
        private String secretKey;
        private String bucket;
    }
}
