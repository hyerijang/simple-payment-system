<img src = "https://github.com/user-attachments/assets/208f067e-bc8e-4464-9ac8-c00f3c4f903b" width = "100%"/></a>

# 간단 결제 시스템

<br>

<div align="center">
<img src="https://img.shields.io/badge/Java 17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"/></a>
<img src="https://img.shields.io/badge/MySQL 8-4479A1?style=for-the-badge&logo=MySQL&logoColor=white"/></a>
<img src="https://img.shields.io/badge/Spring Boot 3.4.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white"/></a>
<img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"/></a>

</div>

<br>
평소 관심 있었던 결제 시스템 아키텍처를 분석하고, PG사 API를 활용하여 간단한 결제 시스템을 설계, 구현하였습니다. 

- [간단 결제 시스템 구현 : (1) 결제 시스템 분석해보기](https://dev-jhl.tistory.com/entry/%EA%B2%B0%EC%A0%9C-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EB%B6%84%EC%84%9D%ED%95%B4%EB%B3%B4%EA%B8%B0)
- [간단 결제 시스템 구현 : (2) Mono 제대로 써보기](https://dev-jhl.tistory.com/119?category=1153049)

## 개발 기간
- 2024.12.03 ~ 2024.12.09

## 요구사항 분석
상세 분석은 [간단 결제 시스템 구현 : (1) 결제 시스템 분석해보기](https://dev-jhl.tistory.com/entry/%EA%B2%B0%EC%A0%9C-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EB%B6%84%EC%84%9D%ED%95%B4%EB%B3%B4%EA%B8%B0) 참조

시스템 구조는 면접 사례로 배우는 대규모 설계시스템 기초 2권의 '결제 시스템' 을 참조하였음
![image](https://github.com/user-attachments/assets/90549d0d-e5b8-4fa5-9b23-34437b597088)


[요구사항 분석]
1. 기능 요구사항
    - [요구사항](https://github.com/kst6294/wanted-preonboarding-challenge-backend-26)에는 결제 서비스 중 `대금 수신` 부분만 있음 (대금 정산은 구현할 필요 없음)
    - 또한 원장/ 지갑 / 결제 재시도 등은 요구사항에도 없고 구현하면 너무 복잡해지니 생략함
    - 따라서, `단일 서비스(결제 서비스 + 결제 실행자)`로 구현
        - `결제 서비스`는 결제 이벤트를 수신하고, 다른 내부 시스템 (원장, 지갑)과 통신하며 결제 프로세스를 조율
        - `결제 실행자`는 지급지시서(PaymentOrder)를 관리하면서 실제 결제(Payment)를 수행
        -  내부 서비스간 통신이 없으므로 일단 카프카는 생략한다.
    - 지난 과제에 따르면, 우리 서비스는 당근마켓 같은 서비스 물품 거래 서비스
         - 한 번에 한 번의 물건만 결제/ 환불할 것이므로 지급지시서에 하나의 주문만 들어간다고 가정하겠음
 
2. 비기능 요구사항
    - 간단한 구현을 위해 단일 스레드로 구현
    - 추후 kafka 적용을 고려
        - 주문 서비스 등에서 "이벤트"를 발행하면 결제 서비스, 재고 관리 서비스, 정산 ... 등등이 각자 이를 수신해서 각자 처리하는 구조가 될 예정
    - 결제 실행자는 멱등성을 보장해야 함
        - 이를 위해, 전체 서비스 (주문 ~ 결제) 는 하나의  key을 활용
        - kafka에서 특정 유저의 주문서는 특정 파티션에만 적재될 예정



## 서비스 흐름도
첫번째 그림에서 '가맹점'은 쇼핑몰의 웹페이지를, '결제창'은 각 PG사가 제공하는 결제창(두번째 그림)를 의미

가맹점 서버(백엔드)에서 작업이 필요한 부분은 5, 8번 단계
  - (5) 결제 요청
     - 입력 정보를 바탕으로 지급지시서(paymentOrder) 생성한다.
  - (8) 결제 결과 처리
    - PG사로부터 전달받은 결제(payment) 결과를 검증하고 처리한다.


![image](https://github.com/user-attachments/assets/206ebbc3-6994-41ae-8442-5741dfb1ac9d)
![image](https://github.com/user-attachments/assets/4abbe135-3293-43b0-9f08-1e6b1655594e)


## 구현 과정 

### 개선 1. Mono.block() 수정
원래 내 코드에서 결제 결과 처리는 다음과 같이 구성되어 있다. 
액세스 토큰 발급과 결재내역 단건조회 시에는 외부 API를 호출하게 된다. 지금 보니 코드 중간에 block()이 보이는데, 이러면 응답 받을 때까지 (토큰이 올때까지) 기다리므로 동기와 다를바 없이 동작한다

``` java
  private void processPayment(String impUid, String merchantUid) {

        // 1. 포트원 API 엑세스 토큰 발급
        String accessToken = portOneService.getAccessToken().block();

        // 2. 포트원 결제내역 단건조회 API 호출
        PortOnePaymentResponse payment = portOneService.getPaymentMono(impUid, accessToken).block();

        // 3. 고객사 내부 주문 데이터의 가격과 실제 지불된 금액을 비교하여 검증
        PaymentOrder paymentOrder = paymentOrderService.findByMerchantUid(merchantUid);
        BigDecimal amount = payment.getResponse().getAmount(); // 실제 결제 된 금액
        BigDecimal amountToBePaid = paymentOrder.getAmount(); // 결제 되어야하는 금액
        verifyPayment(merchantUid, amount, amountToBePaid, payment, paymentOrder);
    
```



### Mono.block() 공식 문서
``` java
Mono.block()?
@Nullable
publicT block()
Subscribe to this Mono and block indefinitely until a next signal is received. Returns that value, or null if the Mono completes empty. In case the Mono errors, the original exception is thrown (wrapped in a RuntimeException if it was a checked exception).
```

> 이 Mono를 구독하고 다음 신호를 받을 때까지 무기한으로 블록 합니다. 그 값을 반환하거나, Mono가 빈 상태로 완료되면 null을 반환합니다. Mono가 에러를 발생시키는 경우, 원래의 예외가 (체크된 예외인 경우 RuntimeException으로 래핑 되어) 던져집니다.


공식 문서 에 따르면 block을 하면 무기한으로 블록 하게 된다고 한다. 즉, 결과를 응답받은 뒤에야 다음 코드를 실행한다. 
block() 메서드를 사용하지 않고 비동기 방식으로 처리하기 위해, flatMap을 사용하여 비동기 체인을 만들었다.

### 수정 결과
 ``` java
    private void processPaymentAsync1(String impUid, String merchantUid) {
        long start = System.currentTimeMillis(); // FIXME 수행시간 측정 위한 임시 코드

        // 1. 포트원 API 엑세스 토큰 발급
        Mono<String> accessTokenMono = portOneService.getAccessToken();

        // 2. 포트원 결제내역 단건조회 API 호출
        accessTokenMono.flatMap(accessToken -> portOneService.getPaymentMono(impUid, accessToken)).flatMap(payment -> {
                // 3. 고객사 내부 주문 데이터의 가격과 실제 지불된 금액을 비교하여 검증
                PaymentOrder paymentOrder = paymentOrderService.findByMerchantUid(merchantUid);
                BigDecimal amount = payment.getResponse().getAmount(); // 실제 결제 된 금액
                BigDecimal amountToBePaid = paymentOrder.getAmount(); // 결제 되어야하는 금액
                verifyPayment(merchantUid, amount, amountToBePaid, payment, paymentOrder);
                return Mono.empty();
            })
            .doOnTerminate(() -> log.info("completeAsync1 수행시간 : {}ms",
                System.currentTimeMillis() - start)) // FIXME 수행시간 측정 위한 임시 로그
            .subscribe();
    }
```

### 수행시간 측정 결과
이전 코드 / 변경한 코드의 수행시간을 비교해 봤다. Mono를 제대로 쓰니까 토큰발급과 단건조회가 동시에 이루어져서 확실히 빨라졌다. (33.6% 향상)
- 기존 코드 응답시간 : 378ms
- block 제거 후 응답 시간 : 251ms  
