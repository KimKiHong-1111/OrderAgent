#🚀 OrderAgent 프로젝트


AWS LAMBDA 기반 자동 재고관리시스템입니다.
---

## 프로젝트 개요
[문제 상황] 

재고 관리가 필요한데, 만들어진 소프트웨어가 없다. OpenAPI가 넉넉하지 않고, ip 기반으로 받아와야 하면 비용이 비싼 문제가 발생.


또한, 제품수가 많아지면 사람이 관리하기가 점점 어려워짐. 미리 재고관리를 못해서 품절이 발생하면 손해 발생

[해결 대안]
 
1. 직원 고용
 
    단점 : 사람이 해도 문제가 생길 수 있으며 직원의 실수를 막을 방법이 없음. 해결한다고해도 돈이 300만원/월 비용 발생(인건비) 

2. 소프트웨어 개발
   
   장점 : 소프트웨어는 제어가능 한 실수를 극복할 수 있음 . 재고 관리를 자동으로 해주기 때문에 품절이 발생할 가능성도 낮음.




## 패키지 구조
```
OrderAgent
├── build.gradle
├── template.yaml              # Lambda 배포용 SAM 템플릿
└── src/main/java/api/orderagent
    ├── crawler/              # 크롤러 로직 (삼성 유니폼 등)
    ├── domain/
    │   ├── entity/           # Product, OptionStock, OrderLog 등 JPA 엔티티
    │   └── repository/       # JpaRepository 인터페이스
    ├── lambda/               # Lambda 핸들러 구현 (스케줄 실행용)
    ├── scheduler/            # 정기 스케줄링 실행 트리거
    └── service/              # Product 관련 서비스 로직
```

### 기술 스택

- Java 17 (Temurin)

- Spring Boot 3.5.3

- Spring Data JPA

- MySQL

- AWS SAM CLI (Serverless Application Model)

- AWS Lambda (Java Function)

- Selenium + Jsoup (Web Crawler)

## 기능 설명 

설정해둔 시간마다 스케줄러로 해당 웹사이트를 크롤링한 후 제품 정보 와 재고를 DB에 저장합니다.

동시에 재고가 품절이거나, 일정 수 이하로 떨어지게 되면 자동으로 발주를 넣습니다.

