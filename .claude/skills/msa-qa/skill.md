---
name: msa-qa
description: "MSA 서비스 통합 품질 검증 스킬. 빌드 검증, 서비스 간 API 정합성, 데이터 흐름 교차 검증을 수행한다. 개발 작업 완료 후 품질 확인, '검증해줘', '빌드 확인', '정합성 체크' 요청 시 사용."
---

# MSA QA -- 통합 품질 검증

3개 MSA 서비스(product, order, payment)의 통합 품질을 검증하는 절차.

## 검증 단계

### 1. 빌드 검증
각 서비스 디렉토리에서 실행:
```bash
cd {service}/ && ./gradlew compileKotlin
```
3개 모두 BUILD SUCCESSFUL이어야 PASS.

### 2. 엔티티 정합성 검증
서비스 간 참조 관계를 교차 확인한다:

| 참조 | 소스 서비스 | 대상 서비스 | 확인 항목 |
|------|-----------|-----------|----------|
| productId | order, payment | product | Long 타입 일치 |
| paymentId | order | payment | Long 타입 일치 |

각 서비스의 Entity 파일을 Read하여 필드 타입이 일치하는지 비교.

### 3. DTO 정합성 검증
서비스 A의 Response DTO 필드가 서비스 B의 Request에서 기대하는 값과 호환되는지 확인:
- ProductResponse.id → CreatePaymentRequest.productId (타입 일치)
- ProductResponse.price → CreatePaymentRequest.amount (타입 일치)
- PaymentResponse.id → CreateOrderRequest.paymentId (타입 일치)

### 4. 설정 검증
- 포트 충돌 없음: product(8081), order(8082), payment(8083)
- DB 설정 동일: 같은 MySQL 인스턴스, 같은 데이터베이스

### 5. API 경로 일관성
- RESTful 패턴 준수: /api/{resource}
- HTTP 메서드 적절성: GET(조회), POST(생성)

## 보고서 형식

```markdown
# QA Report

## 빌드 검증
- product-service: PASS/FAIL
- order-service: PASS/FAIL
- payment-service: PASS/FAIL

## 정합성 검증
- [항목]: PASS/FAIL -- [상세]

## 종합
- 총 {N}개 항목 중 {M}개 PASS
- 수정 필요 사항: [목록]
```
