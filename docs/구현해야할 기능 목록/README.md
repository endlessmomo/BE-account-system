## 💻 구현해야할 기능 목록

### controller
- [x] AccountController : 계좌와 관련된 API url 설정

### entity
- [x] Account : 계좌를 표현하는 클래스 생성
- [x] AccountStatus : 계좌의 상태를 표현하는 Enum 클래스 생성
- [x] BaseEntity : 여러개의 도메인에서 공통적인 멤버변수를 관리할 클래스 생성
  - 유연성을 부여하기 위한 Entity 클래스
- [x] AccountUser : 게좌의 사용자를 표현하는 클래스 생성


### dto 
- [x] CreateAccount : 계좌 생성에 대한 요청/응답 처리 클래스 생성
- [x] AccountDto : 계좌 데이터를 전달하기 위한 클래스 생성

### repository
- [x] AccountRepository: 계좌들을 관리하는 래파지토리 생성 
- [x] AccountUserRepository : 계좌 유저들을 관리하는 래파지토리 생성

### service
- [x] AccountService : 계좌 생성 및 해지와 관련된 API 구현 