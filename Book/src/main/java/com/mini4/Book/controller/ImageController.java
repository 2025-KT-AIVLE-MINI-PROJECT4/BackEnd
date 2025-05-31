package com.mini4.Book.controller;

import com.mini4.Book.service.ImageService;
import io.minio.errors.MinioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger; // Logger 클래스 임포트
import org.slf4j.LoggerFactory; // LoggerFactory 클래스 임포트

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    // ImageController 클래스의 로거 인스턴스를 생성합니다.
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * 단일 이미지 파일 업로드 API.
     * 클라이언트로부터 MultipartFile 형태의 이미지 파일을 받아 MinIO에 업로드합니다.
     * 성공 시 업로드된 이미지의 공개 URL을 반환하고, 실패 시 상세한 오류 메시지를 반환합니다.
     *
     * @param file 업로드할 MultipartFile 객체 (HTML form의 'file' 필드에 해당)
     * @return 업로드된 이미지의 URL (성공 시) 또는 오류 메시지 (실패 시)를 담은 ResponseEntity
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imageService.uploadImage(file);
            // 성공 시, 이미지 URL을 JSON 형태로 반환합니다.
            return ResponseEntity.ok(Collections.singletonMap("imageUrl", imageUrl));
        } catch (IllegalArgumentException e) {
            // ImageService에서 파일 유효성 검사 (크기, 타입 등) 실패 시 발생하는 예외를 처리합니다.
            logger.warn("이미지 업로드 유효성 검사 실패: {}", e.getMessage()); // 경고 레벨로 로그 기록
            // 클라이언트에게 Bad Request (400) 상태와 오류 메시지를 반환합니다.
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            // MinIO 관련 오류 (MinioException) 또는 파일 입출력 오류 (IOException),
            // 암호화/키 관련 오류가 발생했을 때 처리합니다.
            // 에러 레벨로 상세 스택 트레이스를 포함하여 로그를 기록합니다.
            logger.error("이미지 업로드 중 MinIO 또는 IO 오류 발생", e);
            // 클라이언트에게 Internal Server Error (500) 상태와 구체적인 오류 메시지를 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "이미지 업로드 중 오류가 발생했습니다: " + e.getMessage()));
        } catch (Exception e) {
            // 위에서 명시된 예외 외에 발생할 수 있는 모든 예상치 못한 예외를 처리합니다.
            logger.error("이미지 업로드 중 예상치 못한 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "예상치 못한 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 이미지 파일 삭제 API.
     * 주어진 이미지 URL에 해당하는 파일을 MinIO에서 삭제합니다.
     *
     * @param imageUrl 삭제할 이미지의 전체 URL 또는 MinIO 객체 이름
     * @return 성공 메시지 또는 오류 메시지를 담은 ResponseEntity
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            imageService.deleteImage(imageUrl);
            // 성공 시, 성공 메시지를 JSON 형태로 반환합니다.
            return ResponseEntity.ok(Collections.singletonMap("message", "이미지가 성공적으로 삭제되었습니다."));
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            // MinIO 관련 오류 또는 파일 입출력 오류, 암호화/키 관련 오류 발생 시 처리합니다.
            logger.error("이미지 삭제 중 MinIO 또는 IO 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "이미지 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        } catch (Exception e) {
            // 모든 예상치 못한 예외를 처리합니다.
            logger.error("이미지 삭제 중 예상치 못한 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "예상치 못한 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}