팀원 소개 

- 김명찬(리더) |  [Github](https://github.com/mingtian-chan)
- 강다온(부리더) |   [Github](https://github.com/jays1144)   |   [Notion](https://www.notion.so/4f000a62c0884d558b1196e7de18e741?pvs=21)
- 김진욱 |   [Github](https://github.com/naraspc)  |   [Blog](https://www.notion.so/189d8ed0848d44c9b3a0718127f13b02?pvs=21)   |   [Notion](https://www.notion.so/d15f10c0996e4df89d2408f68c2565b3?pvs=21)
- 이진우 |   [leegitjw](https://github.com/leegitjw)  |  [Github](https://github.com/leegitjw)  |  [Notion](https://www.notion.so/a1855b8c5c4e4a98ba5cb782feb902d4?pvs=21)

---

# 서비스 소개

<aside>
💡 MarketHub

</aside>

- 백만건 이상의 대용량 트래픽을 처리하는 이커머스 쇼핑몰

---

## 🥃 도메인 주소

[markethubsite.shop](https://markethubsite.shop/)

## 🥃 Github 주소

‣https://github.com/hanghae-markethub/markethub

## 🥃 팀 노션 주소

[Market Hub](https://www.notion.so/Market-Hub-890957603b8d47249b5a1e469328d8ae?pvs=21) 

---

# 주요 기능

| 할인 특가 | Task Scheduler를 사용하여 특정 시간마다 할인&특가 상품 생성 |
| --- | --- |
| 비회원 장바구니 | Redis를 활용하여 비회원으로 장바구니 담기까지 가능, 결제는 회원가입을 유도하기위해 회원만 가능하게 설계 |
| 인증인가 | JWT, Security, Redis를 활용하여 Access Token, Refresh Token 구현 (CSRF, XSS를 통한 토큰탈취 대비) Access : 1시간, Refresh : 2주 |
| 결제 구현 | IamPort API를 사용하여 결제 API 구성, Redisson을 활용하여 RLock구현으로 동시성 제어, Request 변조공격을 대비한 가격 검증로직 작성 |
| HTTPS 적용 | HTTPS를 적용해 도메인 보안성/노출순위 향상 |
| 상품 검색 및 조회  | 서버 배포 시 Post Constructor를 사용하여 DB의 데이터를 Elastic Search와 Redis로 마이그레이션하고,  Elastic Search를 사용하여 키워드 를 포함하는 
모든 상품에 대해 페이징 된 검색기능을 제공 및 Redis를 활용하여 
아이템 검색성능을 최적화 하고 이후에 이루어진 수정, 삭제는 자동으로 업데이트되게 구현 |

<p align= "center">
<img src ="https://github.com/hanghae-markethub/markethub/assets/113502944/19a2483e-9566-4a8f-9601-2e04487e3946">
</p>

