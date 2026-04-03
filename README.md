# MSA Harness

Claude Code의 병렬 에이전트 시스템을 활용하여, 기본 틀만 갖춰진 MSA 서비스들을 단계적으로 고도화하는 프로젝트입니다.

## 프로젝트 구조

```
msa-harness/
├── product-service/    # 상품 서비스 (Port 8081)
├── order-service/      # 주문 서비스 (Port 8082)
├── payment-service/    # 결제 서비스 (Port 8083)
└── docker-compose.yml  # MySQL + 3개 서비스 통합 실행
```

**기술 스택:** Kotlin 1.9.25, Spring Boot 3.4.4, JPA, MySQL 8.0, Gradle

## 현재 서비스 상태

| 서비스 | 구현된 API | 비고 |
|--------|-----------|------|
| Product | `GET /api/products`, `GET /api/products/{id}` | 조회만 가능, CUD 미구현 |
| Order | `GET /api/orders`, `GET /api/orders/{id}`, `POST /api/orders` | 서비스 간 검증 없음 |
| Payment | `GET /api/payments`, `GET /api/payments/{id}`, `POST /api/payments` | 서비스 간 검증 없음 |

- 서비스 간 통신 없음 (RestTemplate, WebClient 등 미적용)
- 에러 핸들링 미비
- API 문서화 없음

## 병렬 에이전트 구성

Claude Code 하네스를 통해 **3개의 전담 개발 에이전트**와 **1개의 QA 에이전트**가 병렬로 작업합니다.

| 에이전트 | 역할 |
|----------|------|
| `product-dev` | Product Service 전담 개발 |
| `order-dev` | Order Service 전담 개발 |
| `payment-dev` | Payment Service 전담 개발 |
| `qa` | 빌드 검증, API 정합성 확인, 서비스 간 데이터 흐름 교차 검증 |

오케스트레이터(`msa-orchestrator`)가 과제를 분석하고 각 에이전트에 작업을 분배한 뒤, QA 에이전트가 통합 검증을 수행합니다.

## 고도화 과제

### 과제 1. 리스트 응답 페이지네이션 전환

모든 서비스의 리스트 조회 API(`GET /api/products`, `GET /api/orders`, `GET /api/payments`)의 응답을 단순 List가 아닌 **Page 기반 응답**으로 전환합니다.

- `page`, `size`, `sort` 파라미터 지원
- Spring Data JPA의 `Pageable` 활용
- 응답에 `totalElements`, `totalPages`, `currentPage` 등 페이징 메타데이터 포함

---

### 과제 2. Product Service CRUD 완성

현재 조회만 가능한 Product Service에 **생성, 수정, 삭제** API를 추가합니다.

- `POST /api/products` - 상품 생성
- `PUT /api/products/{id}` - 상품 수정
- `DELETE /api/products/{id}` - 상품 삭제
- 요청 DTO 및 입력값 검증(`@Valid`) 적용

---

### 과제 3. 글로벌 예외 처리 및 에러 응답 통일

3개 서비스 모두에 **통일된 에러 응답 형식**과 글로벌 예외 처리를 적용합니다.

- `@RestControllerAdvice` 기반 글로벌 예외 핸들러 구현
- 공통 에러 응답 형식 정의 (`code`, `message`, `timestamp`)
- 404 Not Found, 400 Bad Request, 500 Internal Server Error 등 케이스별 처리
- 비즈니스 예외 클래스 정의 및 활용

---

### 과제 4. Kotlin 및 Spring Boot 버전업

3개 서비스의 **Java, Kotlin, Spring Boot 버전을 최신으로 업그레이드**합니다.

- Java `21` → `25`
- Kotlin `1.9.25` → `2.1.20`
- Spring Boot `3.4.4` → `4.0.5`
- Java 25 대응을 위한 Gradle toolchain 설정 변경
- Kotlin 2.x 마이그레이션에 따른 `build.gradle.kts` 설정 변경 (`kotlinOptions` → `compilerOptions` 등)
- Deprecated API 대응 및 빌드 정상 동작 확인

---

### 과제 5. 서비스 간 통신 구현

Order, Payment 서비스에서 **Product Service를 호출하여 상품 존재 여부를 검증**하는 서비스 간 통신을 구현합니다.

- `RestClient`를 활용한 서비스 간 HTTP 통신
- 주문 생성 시 productId에 해당하는 상품이 존재하는지 검증
- 결제 생성 시 productId에 해당하는 상품이 존재하는지, amount가 상품 가격과 일치하는지 검증
- 외부 서비스 호출 실패 시 적절한 에러 처리

---

### 과제 6. 주문-결제 연동 플로우 구현

주문 생성 시 **결제를 자동으로 생성**하고, 주문과 결제의 상태를 연동하는 플로우를 구현합니다.

- 주문 생성 API 호출 시 Payment Service를 호출하여 결제 자동 생성
- 주문 상태 흐름: `CREATED` → `PAID` → `COMPLETED` / `CANCELLED`
- 결제 상태 흐름: `PENDING` → `COMPLETED` / `FAILED`
- 결제 실패 시 주문 상태를 `CANCELLED`로 변경

---

### 과제 7. Swagger(OpenAPI) 문서화

3개 서비스 모두에 **Swagger UI 기반 API 문서**를 자동 생성합니다.

- `springdoc-openapi` 의존성 추가
- 각 API 엔드포인트에 `@Operation`, `@ApiResponse` 등 어노테이션 적용
- 요청/응답 DTO에 `@Schema` 어노테이션으로 필드 설명 추가
- Swagger UI 경로: `/swagger-ui/index.html`
