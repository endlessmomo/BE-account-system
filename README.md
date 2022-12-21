# 📱 계좌 시스템
- 계좌 시스템은 사용자와 계좌의 정보를 저장하고 외부 시스템에서 거래를 요청하는 경우 거래의 정보를 받아 계좌들의 값들을 수정해주는 거래 관리 기능을 제공하는 시스템입니다.
- 계좌 시스템은 현재 계좌 추가, 해지, 확인 기능을 제공합니다.
 - 한 사용자는 최대 10개의 계좌를 생성할 수 있습니다.

### 💻 사용기술 및 개발환경

개발 환경
- 운영체제 : mac M1 
- IDE : Intelli J
- JDK : Open JDK 11
- Spring Boot : 2.6.8
- Database : Mysql, H2(testDB)

BE
- Spring Boot, Spring MVC
- Spring JPA
- Redis
- Mysql, H2(test)
- Junit, Mokito


### 주요 기능
- 계좌 생성, 해지, 확인 API 기능 
  - 유저 당 최대 계좌 생성은 10개로 제한한다.
- 트랜잭션을 통한 계좌 사용, 사용 취소 API 기능
- 동시성 이슈 케이스를 방지하기 위한 AOP
  - Redis를 통한 트랜잭션 제외
- 일관성 있는 예외처리를 하기 위해 에러의 상태코드를 상황에 따라 분리
  - client에게 명확한 Error를 명시하기 위해 Response 메세지 형태를 획일화
