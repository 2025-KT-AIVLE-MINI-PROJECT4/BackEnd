package com.mini4.Book.service.impl;

import com.mini4.Book.config.MinioProperties;
import com.mini4.Book.service.ImageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service // 이 클래스가 스프링 빈으로 등록되도록 Service 어노테이션 추가
public class ImageServiceImpl implements ImageService { // ImageService 인터페이스 구현

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public ImageServiceImpl(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override // 인터페이스 메서드 오버라이드 명시
    public String uploadImage(MultipartFile file) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        // 파일 유효성 검사 (크기, 타입 등)
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }
        // minioProperties.getMaxFileSize()는 DataSize 객체를 반환하므로, toBytes()를 사용하여 long 타입으로 변환해야 합니다.
        if (file.getSize() > minioProperties.getMaxFileSize().toBytes()) {
            throw new IllegalArgumentException("파일 크기가 " + minioProperties.getMaxFileSize().toMegabytes() + "MB를 초과합니다.");
        }
        // 이미지 파일 타입만 허용 (간단한 예시)
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다. (현재 타입: " + file.getContentType() + ")");
        }

        // 파일 이름 고유하게 생성 (UUID 사용)
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // MinIO 객체 이름에는 경로 구분자 '/'를 포함할 수 있으므로, UUID만으로도 충분히 고유합니다.
        // 확장자를 붙여서 이미지 파일임을 명확히 할 수 있습니다.
        String objectName = UUID.randomUUID().toString() + extension;

        // MinIO에 파일 업로드
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        }

        // 업로드된 이미지의 공개 URL 반환
        return minioProperties.getPublicUrlPrefix() + objectName;
    }

    @Override // 인터페이스 메서드 오버라이드 명시
    public void deleteImage(String imageUrl) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        String objectName = getObjectNameFromImageUrl(imageUrl);

        if (objectName != null && !objectName.isEmpty()) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build());
        }
    }

    /**
     * 이미지 URL에서 MinIO objectName (파일 이름)을 추출합니다.
     * 이 메서드는 인터페이스의 일부가 아니므로 private으로 유지됩니다.
     * @param imageUrl 이미지의 전체 URL
     * @return MinIO objectName (예: UUID.jpg)
     */
    private String getObjectNameFromImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        // publicUrlPrefix 뒤에 있는 부분 추출
        if (imageUrl.startsWith(minioProperties.getPublicUrlPrefix())) {
            return imageUrl.substring(minioProperties.getPublicUrlPrefix().length());
        }
        // 또는 URL에서 마지막 / 뒤의 이름만 추출 (더 일반적인 방법)
        int lastSlashIndex = imageUrl.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < imageUrl.length() - 1) {
            return imageUrl.substring(lastSlashIndex + 1);
        }
        return imageUrl; // 접두사가 없으면 URL 자체가 objectName이라고 가정 (주의: 이 경우 정확한 객체 이름이 아닐 수 있음)
    }
}