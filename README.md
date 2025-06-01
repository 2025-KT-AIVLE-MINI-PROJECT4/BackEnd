# 📚 Book Management Backend (Spring Boot)

이 프로젝트는 도서 정보를 관리하고 RESTful API를 제공하는 백엔드 애플리케이션입니다. Spring Boot를 사용하여 API를 구현하며, JWT 기반의 인증/인가 시스템과 Redis를 활용한 세션 관리가 특징입니다.

## 🚀 주요 기능

* **사용자 관리:** 회원가입, 로그인 (JWT 기반 인증)
* **도서 CRUD:** 도서 등록, 조회 (단일/목록/사용자별), 수정, 삭제 (Soft Delete)
* **보안:** Spring Security를 이용한 JWT 토큰 기반 인증 및 권한 관리
* **세션 관리:** Redis를 활용한 효율적인 세션 관리

## 💻 기술 스택

* **언어:** Java 17
* **프레임워크:** Spring Boot 3.x
* **데이터베이스:** H2 Database (개발용, 인메모리)
* **ORM:** Spring Data JPA
* **보안:** Spring Security, JWT (Json Web Token)
* **캐싱/세션:** Spring Data Redis
* **로깅:** SLF4J, Logback
* **빌드 도구:** Gradle
* **유효성 검사:** Jakarta Validation API
* **개발 편의:** Lombok

## 📁 프로젝트 구조
```
src
└── main
└── java
└── com
└── mini4
└── Book
├── BookApplication.java       # Spring Boot 애플리케이션 시작점
├── config                     # 스프링 설정 (Redis, Security 등)
│   ├── RedisConfig.java
│   └── SecurityConfig.java
├── controller                 # REST API 엔드포인트
│   ├── BookController.java
│   └── UserController.java
├── domain                     # JPA 엔티티 및 도메인 모델
│   ├── Book.java
│   └── User.java
├── dto                        # 데이터 전송 객체 (Request/Response)
│   ├── ApiResponse.java
│   ├── BookListResponseDto.java
│   ├── BookRequestDto.java
│   ├── BookResponseDto.java
│   ├── LoginRequestDto.java
│   ├── UserDto.java
│   └── UserRegisterRequestDto.java
├── exception                  # 커스텀 예외 처리
│   ├── ForbiddenException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidCredentialsException.java
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   └── UserExistsException.java
├── jwt                        # JWT 관련 유틸리티 및 필터
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
├── repository                 # JPA 데이터베이스 리포지토리
│   ├── BookRepository.java
│   └── UserRepository.java
├── security                   # Spring Security 관련 서비스
│   ├── CustomUserDetailsService.java
│   └── CustomUserDetails.java
└── service                    # 비즈니스 로직 서비스
└── impl
├── BookServiceImpl.java
└── UserServiceImpl.java
├── BookService.java
└── UserService.java
└── resources
├── static                                 # 정적 리소스 (프론트엔드 빌드 파일 배포 시 사용)
├── templates                              # Thymeleaf 등 템플릿 파일 (현재 사용 안 함)
└── application.yml                        # 애플리케이션 설정 파일 (DB, JWT, Redis 등)
```

## 🛠️ 개발 환경 설정

1.  **JDK 17 설치:** Java Development Kit 17 버전 이상이 설치되어 있어야 합니다.
2.  **Gradle 설정:** 프로젝트는 Gradle을 빌드 도구로 사용합니다.
3.  **Redis 설치 및 실행:** Redis 서버가 로컬에 설치 및 실행되어 있어야 합니다. `application.yml`에 설정된 `host`와 `port` (기본 `localhost:6379`)로 접속합니다.
    * **Windows 사용자 (WSL 권장):**
        1.  **WSL 설치:**
            * 관리자 권한으로 PowerShell 또는 명령 프롬프트를 열고 다음 명령어를 실행합니다:
                ```bash
                wsl --install
                ```
            * 컴퓨터 재부팅 후, Linux 배포판 (Ubuntu가 기본) 설치가 진행됩니다. 사용자 이름과 비밀번호를 설정합니다.
        2.  **Redis 설치 (WSL 내부):**
            * WSL 터미널을 열고 다음 명령어를 실행하여 Redis를 설치합니다:
                ```bash
                sudo apt update
                sudo apt install redis-server
                ```
        3.  **Redis 서비스 시작:**
            * Redis 서비스를 시작합니다:
                ```bash
                sudo service redis-server start
                ```
            * (선택) Redis 서비스 상태 확인:
                ```bash
                sudo service redis-server status
                ```
            * WSL을 재시작할 때마다 `sudo service redis-server start` 명령어를 실행하여 Redis 서버를 수동으로 시작해야 합니다.
    * **Redis CLI를 이용한 접속 및 데이터 확인:**
        * Redis 서버가 실행 중인 상태에서 WSL 터미널에서 다음 명령어를 입력하여 Redis CLI에 접속합니다.
            ```bash
            redis-cli
            ```
        * Redis CLI 프롬프트(`127.0.0.1:6379>`)가 나타나면 다음 명령어를 사용하여 데이터를 확인할 수 있습니다.
            * 모든 키 조회:
                ```redis
                KEYS *
                ```
            * 특정 키의 값 조회 (예: `Spring:session:expires:12345...` 와 같은 세션 키):
                ```redis
                GET [조회할_키_이름]
                ```
            * CLI 종료:
                ```redis
                QUIT
                ```
4.  **환경 변수 설정:**
    * `src/main/resources/application.yml` 파일에 다음 내용이 올바르게 설정되어 있는지 확인합니다.
        ```yaml
        # JWT 설정 (안전한 비밀 키로 변경하세요)
        jwt:
          secret: ODQ0NzQwMzY5MjI1Mzk3YTZlYjAwY2ZhOTFhOTc0MDYzY2Q5Yzc4YzlmNjI1YjAwMDFlMjFlNzY0MmY5YTM2Mg== # 제공된 시크릿 키
        # ... (생략)
        ```
        `secret` 값은 이미 제공된 긴 문자열로 설정되어 있습니다.
    * `spring.jpa.hibernate.ddl-auto: update` 설정으로 개발 환경에서 엔티티 변경 시 스키마가 자동으로 업데이트됩니다.
    * H2 Console은 `http://localhost:8080/h2-console`에서 접근 가능하며, JDBC URL, 사용자 이름, 비밀번호는 `application.yml`에 명시된 대로 사용합니다.

## ▶️ 애플리케이션 실행

프로젝트 루트 디렉토리에서 다음 Gradle 명령어를 실행하여 애플리케이션을 빌드하고 실행할 수 있습니다.

```bash
./gradlew bootRun
또는 IDE (IntelliJ IDEA 등)에서 BookApplication.java 파일을 직접 실행할 수도 있습니다.

애플리케이션은 기본적으로 http://localhost:8080 포트에서 실행됩니다.

🔑 API 엔드포인트 (주요 API)
인증 및 사용자:
POST /api/v1/auth/register : 사용자 회원가입
POST /api/v1/auth/login : 사용자 로그인 (JWT 토큰 발급)
도서 관리:
POST /api/v1/books : 새 도서 등록 (인증 필요)
GET /api/v1/books/{id} : 특정 도서 조회
GET /api/v1/books : 모든 도서 목록 조회
GET /api/v1/books/my : 로그인한 사용자가 등록한 도서 목록 조회 (인증 필요)
PUT /api/v1/books/{id} : 도서 정보 수정 (인증 및 도서 소유자만 가능)
DELETE /api/v1/books/{id} : 도서 삭제 (Soft Delete, 인증 및 도서 소유자만 가능)
API 요청 시 Authorization: Bearer <JWT_TOKEN> 헤더에 로그인 시 발급받은 JWT 토큰을 포함해야 합니다.