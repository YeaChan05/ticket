## 기능 명세서
### 1. 기능 정의

1.1. 기능명

- 판매자 등록 기능

1.2. 기능 설명 (요약)

- 판매자가 회원가입을 할 때, 판매자 정보를 등록할 수 있는 기능. 이 기능은 판매자의 기본 정보와 추가 정보를 입력받아 데이터베이스에 저장된다.


### 2. 개발 배경 및 목표

2.1. 현황 및 문제점 (AS-IS)

- 공연 정보 등록을 할 주체인 판매자 정보가 등록되지 않아 공연 정보 등록이 불가능하다. 현재는 판매자 정보를 수동으로 입력받아야 하며, 이 과정에서 오류가 발생할 수 있다. 또한, 판매자 정보가 없으면 공연 정보의 신뢰성이 떨어진다.

2.2. 개선 방향 및 기대 효과 (TO-BE)

- 판매자 정보를 등록할 수 있는 기능을 구현한다. 이를 통해 판매자는 자신의 정보를 쉽게 등록할 수 있으며, 공연 정보 등록 시 자동으로 판매자 정보가 연결된다. 이로 인해 공연 정보의 신뢰성이 높아지고, 사용자 경험이 개선된다. 또한, 판매자 정보 등록 과정에서 발생할 수 있는 오류를 최소화한다.

2.3. 핵심 목표

- 판매자 정보 등록.

### 3. 구현할 기능 목록 (체크리스트)

3.1. 사용자 관점 기능

- 판매자 회원가입 페이지 접근
- 판매자 정보 입력 (이름, 이메일, 비밀번호, 연락처, 사업자 등록번호 등)
- 판매자 정보 제출 시 자동으로 수행되는 검증:
  - 이메일 형식 검증 (RFC 5322 준수)
  - 이메일 중복 확인
  - 이름 중복 확인
  - 비밀번호 강도 확인 (최소 8자 이상, 대문자, 소문자, 숫자, 특수문자 포함)
  - 연락처 유효성 검사 (010-XXXX-XXXX 형식)
  - 연락처 중복 확인
  - 사업자 등록번호 유효성 검사

3.2. 시스템 관점 기능

- 판매자 회원가입 요청 처리 시 내부적으로 수행되는 검증
  - 이메일 형식 검증 (RFC 5322 준수)
  - 이메일 중복 확인
  - 이름 중복 확인
  - 비밀번호 강도 확인 (최소 8자 이상, 대문자, 소문자, 숫자, 특수문자 포함)
  - 연락처 형식 검증 (010-XXXX-XXXX)
  - 연락처 중복 확인
  - 사업자 등록번호 유효성 검사

3.3. 비기능적 요구사항 (체크리스트)

성능 요구사항:

보안:
- 비밀번호 암호화 저장 (BCrypt 등 안전한 해시 알고리즘 사용)
- 비밀번호 정책: 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함
- 입력 데이터 검증을 통한 인젝션 공격 방지
- CSRF 보호 적용
- 이메일 중복 불가 처리
- 이름 중복 불가 처리
- 이메일 형식 검증 (RFC 5322 준수)
- 연락처 중복 불가 및 형식(010-XXXX-XXXX) 검증 처리


사용성:

신뢰성: 

### 4. 상세 설계

4.1. 정책

- 판매자 정보 등록 기능은 사용자가 회원가입을 할 때 판매자 정보를 입력받아 데이터베이스에 저장한다.

4.2. 데이터 모델 (해당되는 경우)

테이블/컬렉션 변경 사항:

- 테이블명 host -> seller

새로운 데이터 구조:

-

4.3. API 설계 (해당되는 경우)


요청
- 메서드: POST
- 경로: /api/v1/sellers/sign-up
- 헤더
  ```
  Content-Type: application/json
  ```
- 본문
  ```json
    {
        "sellerName": "string",
        "email": "string",
        "password": "string",
        "contact": "string"
    }
  ```
- curl 명령 예시
  ```bash
  curl -i -X POST '/api/v1/sellers/sign-up' \
  -H 'Content-Type: application/json' \
  -d '{
    "sellerName": "홍길동",
    "email": "test@example.com",
    "password": "tesT!1234",
    "contact": "010-1234-5678"
  }'
  ```

성공 응답
- 본문
  ```json
  {
    "status": "Success",
    "data": {
      "sellerName": "홍길동",
      "email": "test@example.com"
    },
  "timestamp": "2025-01-01T00:00:00Z"
  }
  ```

실패 응답
- 이메일 형식 오류
  ```json
    {
        "status": "CONSTRAINT_VIOLATION",
        "message": "이메일 형식이 올바르지 않습니다.",
        "timestamp": "2025-01-01T00:00:00Z"
    }
  ```
- 이메일 중복 오류
  ```json
    {
        "status": "SELLER-001",
        "message": "이미 사용중인 이메일입니다.",
        "timestamp": "2025-01-01T00:00:00Z"
    }
  ```
- 이름 중복 오류
  ```json
    {
        "status": "SELLER-002",
        "message": "이미 사용중인 이름입니다.",
        "timestamp": "2025-01-01T00:00:00Z"
    }
  ```
- 비밀번호 강도 부족
  ```json
    {
        "status": "CONSTRAINT_VIOLATION",
        "message": "비밀번호는 최소 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다.",
        "timestamp": "2025-01-01T00:00:00Z"
    }
  ```
- 연락처 형식 오류
  ```json
    {
        "status": "CONSTRAINT_VIOLATION",
        "message": "연락처는 010-XXXX-XXXX 형식이어야 합니다.",
        "timestamp": "2025-01-01T00:00:00Z"
    }
  ```
  
- 연락처 중복 오류
  ```json
    {
        "status": "SELLER-003",
        "message": "이미 사용중인 연락처입니다.",
        "timestamp": "2025-01-01T00:00:00Z"
    }
  ```

### 5. 개발 및 테스트 체크리스트

5.1. 테스트

테스트
- [x] 판매자 회원가입 요청은 성공적으로 처리된다
- [x] 판매자의 정보는 요청된 내용과 동일하게 데이터베이스에 저장된다
- [x] 판매자 회원가입 요청 시 필수 필드가 누락된 경우 CONSTRAINT_VIOLATION이 응답된다
- [x] 이메일이 RFC 5322 형식을 만족하지 않은 경우 CONSTRAINT_VIOLATION이 응답된다
- [x] 연락처의 형식이 010-XXXX-XXXX이 아닌경우 CONSTRAINT_VIOLATION이 응답된다
- [x] 중복된 이메일로 가입 시 에러 코드 SELLER-001을 반환한다
- [x] 중복된 이름으로 가입 시 에러 코드 SELLER-002를 반환한다
- [x] 중복된 연락처로 가입 시 에러 코드 SELLER-003을 반환한다
- [x] 비밀번호는 최소 8자 이상 대문자 소문자 숫자 특수문자를 포함해야 한다
- [x] 비밀번호는 안전하게 암호화되어 데이터베이스에 저장된다

### 6. 주요 고려사항 및 위험 요소

-

### 7. 범위 외 항목

-

### 8. 참고 자료

- 
