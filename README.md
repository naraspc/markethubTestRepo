# 서비스 소개

<aside>
💡 MarketHub
</aside>

- 백만건 이상의 대용량 트래픽을 처리하는 이커머스 쇼핑몰

팀 프로젝트를 기반으로 지속적으로 개선해 나가기 위한 개인 리포지토리 입니다. 

기존 트러블 슈팅과 기술적 의사결정의 경우 아래 프로젝트 리포지토리 Read.me 참고 부탁드립니다. 

성능 개선 사항은 기술 블로그에도 작성 중이니, 편하게 읽으시려면 아래 블로그 방문 부탁드리겠습니다.

‣https://blog.naver.com/wodnrtkfka

---
## 🥃 Github 주소

‣https://github.com/hanghae-markethub/markethub

## 🥃 팀 노션 주소

[Market Hub](https://www.notion.so/Market-Hub-890957603b8d47249b5a1e469328d8ae?pvs=21) 

---

# 성능 개선 정리 👇

<summary>1. Purchase Entity에 Index를 도입하여 조회 성능을 높혀보자.!</summary>
<details>
- MySQL의 경우 아래와 같은 이유로 인해 PK에 자동으로 Index가 걸려 있습니다.

```  
데이터 검색 성능 향상: 
기본키를 기반으로 데이터를 검색할때 성능응 향상시키기 위해

고유성 보장: 
기본 키는 테이블에서 각 레코드를 고유하게 식별하는 데 사용한다.
중복된 값을 허용하지 않는 기본 키를 인덱스로 설정함으로서 데이터 무결성을 보장한다.

데이터 정렬: 
데이터는 기본 키의 인덱스에 따라 정렬된다. 
따라서 특정 순서로 데이터에 액세스하는 쿼리의 성능을 향상시킬 수 있다.

조인 및 정렬의 효율성: 
기본 키에 대한 인덱스는 조인 및 정렬 작업의 성능을 향상시킨다.
```

제가 담당한 Purchase 도메인의 경우 조회가 발생하는 경우의 수는 아래 두개입니다.

1. 유저가 아이템을 주문 하기 전 주문서
2. 유저가 아이템을 주문 한 후 주문서

각각의 경우 Email과 Status를 기준으로 조회하여 가져오게 됩니다.

```
@Transactional(readOnly = true)
public List<PurchaseResponseDto> findAllPurchaseByEmail(String email) {
  List<Purchase> purchase = purchaseRepository.findByStatusAndEmailOrder(Status.EXIST,email);
        if (purchase == null) {
            throw new EntityNotFoundException("Purchase not found for email: " + email);
        }
        return PurchaseResponseDto.fromListPurchaseEntity(purchase);
    }
```

성능 향상을 위해 Index를 찾아 보면서 처음에는 Email을 기준으로 검색을 하니까
email에 인덱스를 걸면 성능이 향상될것이다 라는 생각을 하게 되었고

이는 좋지않은 결과를 초래했습니다.

purchase 도메인의 특성상 email 컬럼의 경우 중복이 잦은 편이기에
email에 인덱스를 건다면 DB의 크기보다 인덱스의 크기가 더 커지는 오버헤드 현상이 발생한 것 입니다.

그로인해 어떻게 하면 해결 할 수 있을까 라는 생각을 하던 중,
복합 인덱스라는 개념을 찾을 수 있었습니다.

Email과 Status를 함께 인덱싱하여 조회하면 로직 상 어떤 경우에서도 중복된 값중 하나를 고르는 경우가 없고,
Email과 Status를 기반으로 검색한 모든 값을 항상 전부 보여주기때문에

오버헤드문제를 해결 할 수 있고 더 나아가 성능이 개선될거란 예상이 들었습니다.
    
![image](https://github.com/naraspc/markethubTestRepo/assets/140101271/bfbae8d6-e3ca-4084-b37f-d4125a676db5)

이렇게 각각의 컬럼에 인덱스를 걸어주었습니다.

성능이 얼마나 개선되었는지 확인 해 보겠습니다.
![image](https://github.com/naraspc/markethubTestRepo/assets/140101271/ad50d9ee-b07d-44d1-a980-aa9d27434d4e)
인덱스의 적용 전 6.8초의 시간이 소요되었고,

적용 후

![image](https://github.com/naraspc/markethubTestRepo/assets/140101271/9b654cf5-6f77-46e3-b2d0-f24edbf3a266)

3.8초가 소요되었습니다.

만약 데이터가 더 많아진다면 더더욱 두드러지게 결과가 나타날것이라고 판단됩니다.

추가로, MySQL에서 사용하는 스토리지 엔진인 InnoDB의 경우

update쿼리가 날아왔을때 index를 전부 잠궈버립니다.

예를들어 purchase 엔티티에서 이메일만 인덱스로 처리를 했다고 가정해봅시다.
그리고, test@test.com 이메일의 값을 바꾸는 쿼리를 날렸다고 가정해봅시다.

test 이메일 데이터가 1000개가 있다면 1000개의 데이터 모두가 잠기게됩니다..

만약 purchase 테이블에 데이터가 10만개가 있고, 인덱스를 따로 걸어주지 않았다면
test@test.com 이메일의 변경사항이 완료될때까지 10만개의 데이터가 전부 lock걸려버리게 됩니다..

즉, 효울적으로 InnoDB를 사용하기 위해선
인덱스를 신중하게 그리고 적절하게 걸어줘야한다는 것을 알 수 있습니다.

</details>

<summary>2. AOP를 적용하여 공통 관심사를 분리해보자!</summary>
<details>
  AOP란 관심사 분리 기법으로,

주된 목표를 가지는 각각의 클래스들의 부가적인 관심사를 따로 분리해내는 기법이다.

즉, A,B,C라는 명확한 목적을 가진 클래스들에서

이 모든 클래스들의 호출시간이라던지 등을 체크하고싶을때

모든 클래스에 호출시간을 불러오는 로직을 작성하면 되게 번거롭고,
효율도 떨어지고 해당 클래스의 목적과 맞지않는 기능이 될것이기에 따로 AOP로 빼서 인터셉트하게 구현한거같다

작성방법은 되게 간단하다!
```
@Aspect
@Component
public class TimeTraceAop {

    @Around("execution(* org..hanghae..markethub..domain..*(..))")
    public Object execute(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis();
        System.out.println("Time : " + (end - start));
        return result;
    }

}
```

위의 소스코드처럼 @Aspect 를 선언하여 해당 클래스가 AOP임을 선언해주고,

@Component 를 선언해서 컴포넌트 스캔을 적용하자

이후 원하는 메소드를 정의하고, 그 위에 @Around 어노테이선을 선언해서 범위를 지정해주자.

해당 프로젝트의 경우 domain안에 있는 로직들만 확인하고 싶어서
org..hanghae..markethub..domain..

경로로 작성했다.
만약 경로를 커스터마이징하고 싶다면 구글링을 통해서 원하는 대로 커스터마이징을 하자.

![image](https://github.com/naraspc/markethubTestRepo/assets/140101271/9eac94e9-2443-4acb-9d37-d3fb788e8424)
잘 작동함을 확인 할 수 있다

</details>


<summary>3. DIP를 준수하는 할인정책을 구현해보자!</summary>
<details>
멤버의 등급에 따라 할인을 차등적용 해보자!

우선 discount 패키지를 작성해주자.
![image](https://github.com/naraspc/markethubTestRepo/assets/140101271/a5b34597-7e48-4e09-a2dd-4647dc539b9a)


이후 DiscountPolicy Interface와 FixDiscountPolicy를 정의해주자.

```
public interface DiscountPolicy {
    /*
     * @return 할인 대상 금액
     */
    BigDecimal discount (User user, BigDecimal price);
}


@Service
public class FixDiscountPolicy implements DiscountPolicy{

    private BigDecimal discountFixAmount = new BigDecimal(100);
    @Override
    public BigDecimal discount(User user, BigDecimal price) {
        if (user.getRole().equals(Role.USER)) {
            return price.subtract(discountFixAmount);
        }
        return BigDecimal.ZERO;
    }
}
```
나는 Purchase entity에서 금액을 BigDecimal 타입으로 사용하고있어서 이렇게 정의했다.

이후, 유저가 주문했을때 purchase 엔티티의 생성부분을

```
    private final PurchaseRepository purchaseRepository;
    private final ItemService itemService;
    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final UserRepository userRepository;
    
    @Transactional
    public PurchaseResponseDto createOrder(PurchaseRequestDto purchaseRequestDto, String email) {

        List<Purchase> existingPurchases = purchaseRepository.findAllByStatusAndEmail(Status.EXIST, email);
        // 조회된 구매건 삭제
        checkListPurchaseExist(existingPurchases);

        Purchase purchase = Purchase.builder()
                .status(purchaseRequestDto.status())
                .email(email)
                .itemName(purchaseRequestDto.itemName())
                .quantity(purchaseRequestDto.quantity())
                .price(discountPolicy.discount(userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("can't find user")),purchaseRequestDto.price()))
                .itemId(purchaseRequestDto.itemId())
                .build();

        purchaseRepository.save(purchase);
        return PurchaseResponseDto.fromPurchase(purchase);

    }
```
자바의 다형성을 활용해 private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
이런식으로 언제든 할인정책을 변경해서 갈아끼울 수 있도록 작성했다!

/* 토막상식
* 자바의 다형성은 기본적으로 overwrite된 메소드를 우선시하여 실행한다!
* 또한 자식부터 시작해 제일 위의 부모까지 다 불러온다.
*/

이제 user등급의 회원과 Admin 등급의 회원으로 각각 주문신청을 해, 가격이 어떻게 들어오는지 확인해보자
![image](https://github.com/naraspc/markethubTestRepo/assets/140101271/5e9e81a0-9695-44d9-b077-8721bba6c4b4)
21000원짜리 치킨 구매 시(사진은 로컬환경에서 정리하다 유실됐다.. 양해바람)

![image](https://github.com/naraspc/markethubTestRepo/assets/140101271/f56eb655-060b-4a34-9a56-b0a7c5a09441)

20,900원 으로 100원 할인됨을 확인할 수 있었다.

이제 Admin 계정으로 확인해볼까?
![image](https://github.com/naraspc/markethubTestRepo/assets/140101271/2f7298cd-7b99-491f-b9e0-ae13b6047ea5)

개인정보는 가렸다.
기대한 대로 잘 작동함을 확인할 수 있었다!


이젠 구매한 상품 가격의 10%를 할인해주는 할인 정책을 만들어보자!

public class RateDiscountPolicy implements DiscountPolicy{
    @Override
    public BigDecimal discount(User user, BigDecimal price) {
        BigDecimal discount = new BigDecimal(10);
        if (user.getRole().equals(Role.USER)) {
            return price.subtract(price.divide(discount,1, RoundingMode.HALF_EVEN));
        }
        return BigDecimal.ZERO;
    }
}


할인정책을 만들었다!

이제 모듈을 갈아 껴 보자.

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ItemService itemService;
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    private final UserRepository userRepository;

    @Transactional
    public PurchaseResponseDto createOrder(PurchaseRequestDto purchaseRequestDto, String email) {

        List<Purchase> existingPurchases = purchaseRepository.findAllByStatusAndEmail(Status.EXIST, email);
        // 조회된 구매건 삭제
        checkListPurchaseExist(existingPurchases);

        Purchase purchase = Purchase.builder()
                .status(purchaseRequestDto.status())
                .email(email)
                .itemName(purchaseRequestDto.itemName())
                .quantity(purchaseRequestDto.quantity())
                .price(discountPolicy.discount(userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("can't find user")),purchaseRequestDto.price()))
                .itemId(purchaseRequestDto.itemId())
                .build();

        purchaseRepository.save(purchase);
        return PurchaseResponseDto.fromPurchase(purchase);

    }
다른 코드는 그대로 유지한채
private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

이 부분만 갈아끼워보았다.
![image](https://github.com/naraspc/markethubTestRepo/assets/140101271/d073304d-9c77-4307-a366-21492335b534)

정상적으로 10% 할인되는 모습을 볼 수 있었다.

이래서 구현을 사용하는구나. 한가지의 깨달음을 얻을 수 있었다.


// 2024 03 23 추가
```
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ItemService itemService;
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();
    private final UserRepository userRepository;

    @Transactional
    public PurchaseResponseDto createOrder(PurchaseRequestDto purchaseRequestDto, String email) {

        List<Purchase> existingPurchases = purchaseRepository.findAllByStatusAndEmail(Status.EXIST, email);
        // 조회된 구매건 삭제
        checkListPurchaseExist(existingPurchases);

        Purchase purchase = Purchase.builder()
                .status(purchaseRequestDto.status())
                .email(email)
                .itemName(purchaseRequestDto.itemName())
                .quantity(purchaseRequestDto.quantity())
                .price(discountPolicy.discount(userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("can't find user")),purchaseRequestDto.price()))
                .itemId(purchaseRequestDto.itemId())
                .build();

        purchaseRepository.save(purchase);
        return PurchaseResponseDto.fromPurchase(purchase);

    }
```

위 코드를 보면 나는 DIP를 준수하고있지 않았다!

인터페이스를 사용하고 그때그때 갈아끼면서 의존성을 제거한줄 알았는데

생각해보니 New RateDiscountPolicy를 선언한 순간 구현체를 동시에 선언한거였다.


```
@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ItemService itemService;
    private final DiscountPolicy discountPolicy;
    private final UserRepository userRepository;



    @Transactional
    public PurchaseResponseDto createOrder(PurchaseRequestDto purchaseRequestDto, String email) {

        List<Purchase> existingPurchases = purchaseRepository.findAllByStatusAndEmail(Status.EXIST, email);
        // 조회된 구매건 삭제
        checkListPurchaseExist(existingPurchases);

        Purchase purchase = Purchase.builder()
                .status(purchaseRequestDto.status())
                .email(email)
                .itemName(purchaseRequestDto.itemName())
                .quantity(purchaseRequestDto.quantity())
                .price(discountPolicy.discount(userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("can't find user")),purchaseRequestDto.price()))
                .itemId(purchaseRequestDto.itemId())
                .build();

        purchaseRepository.save(purchase);
        return PurchaseResponseDto.fromPurchase(purchase);

    }
```
코드를 이렇게 수정하고
```
package org.hanghae.markethub.domain.discount;

import org.hanghae.markethub.domain.user.entity.User;
import org.hanghae.markethub.global.constant.Role;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Primary
@Component
public class RateDiscountPolicy implements DiscountPolicy{
    @Override
    public BigDecimal discount(User user, BigDecimal price) {
        BigDecimal discount = new BigDecimal(10);
        if (user.getRole().equals(Role.USER)) {
            return price.subtract(price.divide(discount,1, RoundingMode.HALF_EVEN));
        }
        return BigDecimal.ZERO;
    }
}
```
@Primary를 선언해주도록 하자.

@Primary는 빈 스캔의 우선순위를 부여해준다.

</details>

---

# 트러블 슈팅 정리 👇


<summary>1. DIP와 AOP의 충돌</summary>
<details>
기존 PaymentController 코드를 DIP를 위배하지않게 리팩토링을 진행하면서 하나의 이슈와 맞닥들이게 되었다.


기존코드
```
@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    // test init
    private final PurchaseService purchaseService;
    private final ItemService itemService;
    private IamportClient iamportClient;
    private final RedissonClient redissonClient; // Redisson 클라이언트 주입
    private final JwtUtil jwtUtil;

    //2월 29일 작업목록 1. 시크릿키, api키 변수화
    @Value("${secret.sec.key}")
    private String secretKey ;
    @Value("${api.api.key}")
    private String apiKey ;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }
```
이런식으로, DIP를 준수하는듯 하였으나 PaymentController (고수준 모듈)에서
IamportClient(저수준 모듈)를 직접 주입하는 형식을 취해

DIP를 위배하고있었다.

이를

```
@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    // test init
    private final PurchaseService purchaseService;
    private final ItemService itemService;
    private final IamportClient iamportClient;
    private final RedissonClient redissonClient; // Redisson 클라이언트 주입
    private final JwtUtil jwtUtil;
    private final IamportConfig iamportConfig;

```

이렇게 리팩토링 하고,
```
package org.hanghae.markethub.domain.purchase.config;

import com.siot.IamportRestClient.IamportClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class IamportConfig {

    @Value("${secret.sec.key}")
    private String secretKey ;
    @Value("${api.api.key}")
    private String apiKey ;

    @Bean
    public IamportClient iamportClient() {
        return new IamportClient(apiKey, secretKey);
    }

    public String getApiKey() {
        return apiKey;
    }
    public String getSecretKey() {
        return secretKey;
    }

}
```

IamportConfig 를 정의하는식으로 리팩토링을 했다.

이때, iamportConfig가 계속 null로 찍히는 문제가 발생하게 되었다.

안될 이유가 없는데 왜 안될까. 라는 생각으로 4시간정도 투자해서 계속 알아보았고
이유를 찾아냈다. 바로 AOP의 작동 시점과 의존성 주입 시점에 차이가 발생하게되면 의도치않게 값이 빠질수있다는것.

조금 더 자세히 말하자면

AOP 로직이 @Value로 주입된 프로퍼티 값에 접근하기 전에 실행되어 값을 정상적으로 참조할 수 없는 상황이 발생할 수 있다는 것.

이 문제에 대해 자세히 알아보려면 스프링 프레임워크의 빈(Bean) 생명주기, AOP의 작동 방식, 그리고 프로퍼티 값의 주입 시점에 대해 이해할 필요가 있다.

스프링 빈의 생명 주기
```
스프링 컨테이너에서의 빈의 생명주기는 다음과 같다.

빈 인스턴스화: 스프링 컨테이너가 빈의 클래스를 기반으로 인스턴스를 생성한다.
의존성 주입: @Autowired, @Resource, @Value 등을 통해 필요한 의존성을 주입한다.
초기화 메서드 실행: 사용자가 정의한 초기화 메서드(@PostConstruct 등)가 실행된다.
빈 사용: 애플리케이션이 실행되면서 빈이 필요한 시점에 사용된다.
소멸 메서드 실행: 컨테이너가 종료되면서 @PreDestroy 등의 소멸 메서드가 실행된다.

AOP의 작동 방식
AOP는 특정 로직(어드바이스)을 애플리케이션의 다른 부분(조인 포인트)에 동적으로 적용하는 방식으로 동작한다.
스프링 AOP는 주로 프록시 패턴을 사용하여 구현된다.
이는 실제 객체 대신 프록시 객체를 생성하고, 해당 프록시를 통해 실제 객체의 메서드를 호출하는 방식으로, AOP 로직을 끼워 넣는다.

프로퍼티 값의 주입 시점
@Value를 사용한 프로퍼티 값의 주입은 의존성 주입 단계에서 이루어진다.
즉, 빈 인스턴스화 이후, 초기화 메서드 실행 이전에 수행된다.

문제 발생 원인
문제 발생 원인은 아래와 같다.

프록시 객체와 실제 객체 사이의 주입 시점 차이: AOP 프록시가 생성되는 시점과 실제 빈에 @Value로 값이 주입되는 시점 사이에 차이가 있을 때, AOP 로직 실행 시점에 프로퍼티 값이 아직 주입되지 않았을 수 있다.

AOP 적용 대상 선택의 오류: @Aspect 설정에서 AOP 적용 대상을 잘못 지정하여, 의존성 주입이 완료되기 전에 AOP 로직이 실행되는 경우이다.

빈의 생성 및 초기화 과정에서의 특수한 케이스: 특정 빈의 생성 및 초기화 과정에서 AOP 로직이 의존성 주입보다 먼저 실행되도록 구성된 경우, 예를 들어, 사용자 정의 초기화 메서드 내부에서 AOP 로직이 트리거될 때 등이 있다.
```

일전에 해결한 Value어노테이션을 사용하는 중 발생한 트러블슈팅에서 정리한 내용을 참고해서 생각해보자.


문제가 발생한 코드는 아래와같다.
```
@PostMapping("/api/payment/cancel")
    private boolean cancelPayment(@RequestBody RefundRequestDto refundRequestDto) throws JsonProcessingException {
        System.out.println("1");
        System.out.println(iamportConfig);
        RestTemplate restTemplate = new RestTemplate();


        String url = "https://api.iamport.kr/payments/cancel";

        // 요청 파라미터 설정
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("impUid", refundRequestDto.imp_uid());
        formData.add("checksum", String.valueOf(refundRequestDto.checksum()));
        formData.add("reason", refundRequestDto.reason());


        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        System.out.println("2");
        String token = getAccessToken(new PaymentRequestDto.getToken(iamportConfig.getApiKey(), iamportConfig.getSecretKey()));
        headers.set("Authorization", "Bearer " + token);

        // 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        // 요청 보내기
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        // 응답 처리
        if (response.getStatusCode() == HttpStatus.OK) {
            // 각 아이템에 대해 수량 롤백 진행
            purchaseService.rollbackItemsQuantity(refundRequestDto.imp_uid());
            // 주문 상태 변경
            purchaseService.ChangeStatusToCancelled(refundRequestDto.imp_uid());
            return true;
        } else {
            return false;
        }
    }
```
String token = getAccessToken(new PaymentRequestDto.getToken(iamportConfig.getApiKey(), iamportConfig.getSecretKey()));

여기서 iamportConfig가 계속 null이었다.

위에서 알아본 바에 따르면,

AOP가 실행되는 시점은 새로운 PaymentRequestDto 인스턴스가 생성되기 전 이므로
AOP 프록시 객체가 cancelPayment를 감쌀때 PaymentRequestDto는 null이게 된다.

그 후, 프록시 객체가 실행되기 때문에 null인채로 실행되는거지.

AOP를 적용할때 프로퍼티의 적용 시점에 대해서 잘 생각하고 적용해야할거같다.

항상 느끼는건데, 전역으로 무언가를 적용한다면 내가 예상하지못한 동작을 할 가능성이 항상 존재하는것 같다.

난 이 불확실성이 싫다. global적인 무언가를 추가할때는 정말 많은걸 고려해야하는것 같다.
</details>
