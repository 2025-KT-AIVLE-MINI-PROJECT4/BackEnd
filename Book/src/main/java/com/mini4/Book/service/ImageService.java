package com.mini4.Book.service;

import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface ImageService {

    /**
     * 이미지를 스토리지에 업로드하고, 저장된 이미지의 공개 URL을 반환
     *
     * @param file 업로드할 MultipartFile 객체
     * @return 업로드된 이미지의 완전한 URL
     * @throws IllegalArgumentException 파일 유효성 검사 실패 시 (예: 파일 없음, 크기 초과, 이미지 타입 아님)
     * @throws IOException 파일 스트림 처리 중 오류 발생 시
     * @throws MinioException MinIO 서버와의 통신 중 오류 발생 시
     * @throws NoSuchAlgorithmException 암호화 알고리즘 문제 발생 시
     * @throws InvalidKeyException 유효하지 않은 키 사용 시
     */
    String uploadImage(MultipartFile file) throws IllegalArgumentException, IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException;

    /**
     * 스토리지에서 이미지를 삭제
     *
     * @param imageUrl 삭제할 이미지의 URL (전체 URL 또는 MinIO objectName)
     * @throws IOException
     * @throws MinioException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    void deleteImage(String imageUrl) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException;
}