package com.mini4.Book.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.unit.DataSize; // Spring Boot 2.x 이상에서 사용

@Getter
@Setter
@Component
@Validated // @ConfigurationProperties 유효성 검사 활성화
@ConfigurationProperties(prefix = "minio") // application.yml의 minio 접두사와 매핑
@NoArgsConstructor
@AllArgsConstructor
public class MinioProperties {

    @NotBlank(message = "MinIO URL은 필수입니다.")
    private String url;

    @NotBlank(message = "MinIO Access Key는 필수입니다.")
    private String accessKey;

    @NotBlank(message = "MinIO Secret Key는 필수입니다.")
    private String secretKey;

    @NotBlank(message = "MinIO Bucket Name은 필수입니다.")
    private String bucketName;

    @NotBlank(message = "MinIO Public URL Prefix는 필수입니다.")
    private String publicUrlPrefix;

    @NotNull(message = "MinIO Max File Size는 필수입니다.")
    private DataSize maxFileSize;

}
