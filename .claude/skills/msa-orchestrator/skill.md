---
name: msa-orchestrator
description: "MSA 서비스 수정/고도화 에이전트 팀을 조율하는 오케스트레이터. 3개 서비스(product, order, payment)에 대한 수정 요청을 분석하고, 전담 에이전트에게 작업을 분배하며, QA로 검증한다. 'MSA 수정', '서비스 고도화', '기능 추가', '전체 서비스 변경' 등 3개 서비스에 걸친 작업 요청 시 반드시 이 스킬을 사용할 것."
---

# MSA Orchestrator

3개 MSA 서비스(product, order, payment)의 수정/고도화를 에이전트 팀으로 조율하는 오케스트레이터.

## 실행 모드: 서브 에이전트

3개 dev 에이전트가 각자 담당 서비스를 독립적으로 수정하고, QA가 통합 검증한다. 서비스별 수정이 독립적이므로 서브 에이전트 모드로 병렬 실행한다.

## 에이전트 구성

| 에이전트 | subagent_type | 역할 | 스킬 | 대상 |
|---------|--------------|------|------|------|
| product-dev | product-dev | 상품 서비스 수정 | msa-modify | product-service/ |
| order-dev | order-dev | 주문 서비스 수정 | msa-modify | order-service/ |
| payment-dev | payment-dev | 결제 서비스 수정 | msa-modify | payment-service/ |
| qa | qa | 통합 검증 | msa-qa | 전체 |

## 워크플로우

### Phase 1: 준비
1. 사용자의 수정 요청을 분석한다
2. 각 서비스별로 어떤 변경이 필요한지 분류한다
3. `_workspace/` 디렉토리를 생성한다
4. 서비스 간 의존 순서를 파악한다:
   - product: 최상단 (의존 없음)
   - payment: product에 의존
   - order: product + payment에 의존

### Phase 2: 병렬 개발

**실행 방식:** 병렬 (독립 작업) 또는 순차 (의존 작업)

변경이 서비스 간 독립적이면 3개 에이전트를 동시 실행:

```
Agent(product-dev, subagent_type: "product-dev", model: "opus", run_in_background: true)
Agent(order-dev, subagent_type: "order-dev", model: "opus", run_in_background: true)
Agent(payment-dev, subagent_type: "payment-dev", model: "opus", run_in_background: true)
```

변경이 서비스 간 의존적이면 의존 순서대로 실행:
1. product-dev 먼저 실행 (상위 도메인)
2. payment-dev 실행 (product 결과 참조)
3. order-dev 실행 (product + payment 결과 참조)

각 에이전트의 프롬프트에 포함할 내용:
- 구체적인 수정 요청 내용
- Skill 도구로 /msa-modify 호출하여 수정 절차를 따를 것
- 수정 완료 후 `./gradlew compileKotlin`으로 빌드 확인할 것
- 변경 사항 요약을 반환할 것

### Phase 3: QA 검증

모든 dev 에이전트 완료 후 QA 에이전트를 실행:

```
Agent(qa, subagent_type: "qa", model: "opus")
```

QA 프롬프트에 포함할 내용:
- Skill 도구로 /msa-qa 호출하여 검증 절차를 따를 것
- 3개 서비스 빌드 검증
- 서비스 간 정합성 교차 검증
- 검증 결과를 `_workspace/qa_report.md`에 저장

### Phase 4: 수정 반영 (필요 시)

QA에서 문제가 발견되면:
1. QA 보고서를 읽는다
2. 문제가 있는 서비스의 dev 에이전트를 다시 호출하여 수정
3. 수정 후 QA 재실행
4. 최대 2회 반복

### Phase 5: 완료

1. `_workspace/` 보존
2. 사용자에게 결과 요약 보고:
   - 각 서비스별 변경 사항
   - QA 결과
   - 빌드 상태

## 데이터 흐름

```
사용자 요청 → [리더: 분석/분류]
                 ├→ [product-dev] → product-service/ 수정
                 ├→ [payment-dev] → payment-service/ 수정
                 └→ [order-dev]   → order-service/ 수정
                          │
                          ↓
                 [qa] → 빌드 + 정합성 검증
                          │
                          ↓ (문제 시)
                 [해당 dev] → 수정 → [qa] 재검증
                          │
                          ↓
                 사용자에게 결과 보고
```

## 에러 핸들링

| 상황 | 전략 |
|------|------|
| dev 에이전트 1개 실패 | 1회 재시도. 재실패 시 나머지 결과로 진행, 실패 서비스 명시 |
| QA 빌드 실패 | 해당 서비스 dev 에이전트에게 에러 로그와 함께 수정 요청 |
| 정합성 불일치 | 의존 관계상 하위 서비스(order > payment > product)를 수정 |
| 전체 실패 | 사용자에게 알리고 진행 여부 확인 |

## 테스트 시나리오

### 정상 흐름
1. 사용자가 "3개 서비스에 description 필드를 추가해줘" 요청
2. Phase 1에서 각 서비스별 변경 사항 분류
3. Phase 2에서 3개 dev 에이전트 병렬 실행, 각 서비스 Entity/DTO에 필드 추가
4. Phase 3에서 QA가 빌드 + 정합성 검증 → 전체 PASS
5. Phase 5에서 사용자에게 결과 보고

### 에러 흐름
1. Phase 2에서 order-dev가 필드 타입을 잘못 설정
2. Phase 3에서 QA가 정합성 불일치 발견 (paymentId 타입 불일치)
3. Phase 4에서 order-dev 재호출하여 수정
4. QA 재검증 → PASS
5. Phase 5에서 사용자에게 결과 보고 (1회 수정 반영 포함)
