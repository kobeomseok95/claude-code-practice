---
name: msa-modify
description: "Kotlin/Spring Boot MSA 서비스의 코드를 수정하는 스킬. 레이어드 아키텍처(Entity, Repository, Service, Controller, DTO)에 맞춰 기능 추가, API 변경, 엔티티 수정을 수행한다. '서비스 수정', 'API 추가', '필드 추가', '엔드포인트 변경' 등의 요청 시 사용."
---

# MSA Service Modify

Kotlin/Spring Boot 레이어드 아키텍처 기반 MSA 서비스를 수정하는 절차.

## 프로젝트 구조

```
{service-name}/
├── build.gradle.kts
├── src/main/kotlin/com/example/{domain}/
│   ├── {Domain}Application.kt
│   ├── controller/{Entity}Controller.kt
│   ├── service/{Entity}Service.kt
│   ├── repository/{Entity}Repository.kt
│   ├── entity/{Entity}.kt
│   └── dto/{Entity}Response.kt, Create{Entity}Request.kt
└── src/main/resources/application.yml
```

## 수정 절차

### 1. 현재 코드 파악
수정 대상 서비스의 기존 코드를 Read로 확인한다. 특히:
- entity/ 의 필드 구조
- dto/ 의 요청/응답 형태
- service/ 의 비즈니스 로직
- controller/ 의 API 엔드포인트

### 2. 변경 영향 분석
변경이 어느 레이어에 영향을 미치는지 판단한다:

| 변경 유형 | 영향 레이어 |
|----------|-----------|
| 필드 추가/삭제 | Entity → DTO → Service (매핑) |
| API 추가 | Controller → Service → (필요시 Repository) |
| 비즈니스 로직 변경 | Service |
| 조회 조건 추가 | Repository → Service → Controller |

### 3. 바텀업 수정
Entity부터 위로 올라가며 수정한다:
1. **Entity** -- 필드 추가/변경
2. **Repository** -- 쿼리 메서드 추가 (필요 시)
3. **DTO** -- Request/Response 필드 반영
4. **Service** -- 매핑 로직, 비즈니스 로직 수정
5. **Controller** -- 엔드포인트 추가/변경

### 4. 빌드 검증
수정 완료 후 반드시 `./gradlew compileKotlin`으로 컴파일을 확인한다.

## 코딩 규칙
- JPA Entity는 일반 class로 작성 (data class 아님)
- DTO는 data class로 작성
- Entity → DTO 변환은 Service 레이어 또는 DTO companion object의 from() 함수에서 수행
- Repository는 JpaRepository를 확장
- Controller는 @RestController + @RequestMapping 사용
- 테스트 코드는 작성하지 않는다
