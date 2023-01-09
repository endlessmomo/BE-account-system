## 💻 구현해야할 기능 목록

### controller
- [x] AccountController : 계좌와 관련된 API url 설정
  - 계좌 생성
  - 계좌 해지
  - 계좌 정보 보기
- [x] TransactionController : 계좌 거래와 관련된 API url 설정
  - 계좌 잔고 사용/실패
  
### entity
- [x] Account : 계좌를 표현하는 클래스 생성
- [x] AccountStatus : 계좌의 상태를 표현하는 Enum 클래스 생성
- [x] BaseEntity : 여러개의 도메인에서 공통적인 멤버변수를 관리할 클래스 생성
  - 유연성을 부여하기 위한 Entity 클래스
- [x] AccountUser : 게좌의 사용자를 표현하는 클래스 생성
- [x] Transaction : 계좌의 거래를 표현하는 클래스 생성

### dto 
- [x] AccountDto : 계좌 데이터를 전달하기 위한 클래스 생성
- [x] CreateAccount : 계좌 생성에 대한 요청/응답 처리를 위한 데이터를 전달하기 위한 클래스 생성
- [x] DeleteAccount : 계좌 삭제에 대한 요청/응답 처리를 위한 데이터를 전달하기 위한 클래스 생성
- [x] AccountInfo : 데이터 계좌의 정보를 전달하기 위한 클래스 생성
- [x] UseBalance : 계좌 사용을 위한 응답/요청 처리를 위한 데이터를 전달하기 위한 클래스 생성
- [x] TransactionDto : 계좌 데이터를 전달하기 위한 클래스 생성

### repository
- [x] AccountRepository: 계좌들을 관리하는 래파지토리 생성 
- [x] AccountUserRepository : 계좌 유저들을 관리하는 래파지토리 생성
- [x] TransactionRepository : 계좌의 거래들을 관리하는 래파지토리 생성

### service
- [x] AccountService : 계좌 생성 및 해지와 관련된 API 구현
- [x] TransactionService : 계좌 사용과 관련된 API 구현
---

## TDD

### Controller 
- [x] : AccountControllerTest 
  - 계좌 생성 성공 확인 
  - 계좌 생성 실패 확인

### Service
- [x] : AccountServiceTest
  - 계좌 생성 성공 확인
  - 계좌 생성 실패 확인, 에러코드 확인
  - 계좌 최대 개수 10개 확인