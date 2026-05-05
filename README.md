# Realtime Platform                                                                                                                                                                                                              
                                                                                                                                                                                                                                      
  Redis 기반의 실시간 소셜 플랫폼 백엔드 API입니다.                                                                                                                                                                                   
  게시글 조회수, 좋아요, 댓글 기능과 함께 가중치 기반 랭킹 시스템 및 WebSocket 실시간 알림을 구현했습니다.                                                                                                                            

  ---
                                                                                                                                                                                                                                      
  ## 기술 스택                                                                                                                                                                                                                        

  | 분류 | 기술 |
  |------|------|
  | Language | Java 17 |
  | Framework | Spring Boot 4.0.6 |
  | Security | Spring Security + JWT (JJWT 0.12.6) |
  | Cache / DB | Redis 7 (Alpine) |
  | Real-time | WebSocket (STOMP + SockJS) |
  | Messaging | Redis Pub/Sub |
  | Build | Gradle |
  | DevOps | Docker Compose |
  | Util | Lombok, Jackson, BCrypt |

  ---

  ## 주요 기능

  - **JWT 인증** — 로그인 시 Access Token (1시간) + Refresh Token (14일) 발급, Stateless 방식
  - **조회수 트래킹** — 게시글 조회 시 Redis에 조회수 자동 증가
  - **좋아요 시스템** — 사용자별 중복 좋아요 방지, TTL 기반 만료 처리
  - **댓글 시스템** — 댓글 수 집계 및 실시간 이벤트 발행
  - **가중치 랭킹** — Redis Sorted Set 기반 실시간 랭킹 (조회 +1점, 좋아요 +3점)
  - **실시간 알림** — Redis Pub/Sub → WebSocket(STOMP) 연동으로 댓글 알림 브로드캐스트

  ---

  ## 아키텍처

  ```text                                                                                                                                                                                                                             
  ┌─────────────────────────────────────────────────────────┐
  │                      REST Client                         │
  └─────────────────────────┬───────────────────────────────┘
                            │  HTTP + Bearer Token
                            ▼
                ┌───────────────────────┐
                │       JWT Filter       │
                └───────────┬───────────┘
                            │
                            ▼
                ┌───────────────────────┐
                │      Controller       │
                │ Post·Like·Comment·    │
                │ Ranking·Auth·Redis    │
                └───────────┬───────────┘
                            │
                            ▼
                ┌───────────────────────┐
                │        Service        │
                └────┬─────────────┬────┘
                     │             │
         ┌───────────▼──────┐  ┌───▼──────────────────┐
         │      Redis        │  │  NotificationPublisher│
         │                   │  └───────────┬───────────┘
         │  String           │              │
         │  ├ post:views     │              │ channel:post:{id}
         │  ├ post:likes     │              ▼
         │  └ post:comments  │  ┌───────────────────────┐
         │                   │  │    Redis  Pub/Sub      │
         │  Sorted Set       │  └───────────┬───────────┘
         │  └ ranking:posts  │              │
         └───────────────────┘              ▼
                                 ┌───────────────────────┐
                                 │ NotificationSubscriber │
                                 └───────────┬───────────┘
                                             │
                                             ▼
                                 ┌───────────────────────┐
                                 │   WebSocket Broker     │
                                 │  /topic/post/{postId}  │
                                 └───────────┬───────────┘
                                             │
                                 ┌───────────▼───────────┐
                                 │    WebSocket Client    │
                                 └───────────────────────┘
  ```

  **실시간 알림 흐름**
  1. 댓글 작성 API 호출
  2. `CommentService` → Redis 채널 `channel:post:{postId}` 에 이벤트 발행
  3. `NotificationSubscriber` 수신 → `SimpMessagingTemplate` 으로 전달
  4. WebSocket 구독 클라이언트에게 실시간 전송

  ---

  ## 실행 방법

  ```bash
  # 1. Redis 컨테이너 실행
  docker-compose up -d

  # 2. 애플리케이션 실행
  ./gradlew bootRun

  ▎ 기본 접속: http://localhost:8080
  ▎ Redis: localhost:6379

  ---
  Redis 키 설계

  ┌──────────────────────────────────┬────────────┬───────────────────────────────┐
  │           Key Pattern            │    타입    │             설명              │
  ├──────────────────────────────────┼────────────┼───────────────────────────────┤
  │ post:views:{postId}              │ String     │ 게시글 조회수                 │
  ├──────────────────────────────────┼────────────┼───────────────────────────────┤
  │ post:likes:{postId}              │ String     │ 게시글 좋아요 수              │
  ├──────────────────────────────────┼────────────┼───────────────────────────────┤
  │ post:comments:{postId}           │ String     │ 게시글 댓글 수                │
  ├──────────────────────────────────┼────────────┼───────────────────────────────┤
  │ like:post:{postId}:user:{userId} │ String     │ 사용자 좋아요 여부 (TTL: 1일) │
  ├──────────────────────────────────┼────────────┼───────────────────────────────┤
  │ ranking:posts                    │ Sorted Set │ 게시글 랭킹 점수              │
  └──────────────────────────────────┴────────────┴───────────────────────────────┘

  ---
  API 명세

  공통

  - Base URL: http://localhost:8080
  - 인증: Authorization: Bearer {accessToken} 헤더 필수 (로그인 제외)
  - Content-Type: application/json

  ---
  인증 (Auth)

  로그인

  POST /api/v1/login

  Request Body
  {
    "loginId": "admin",
    "password": "1234"
  }

  Response
  {
    "accessToken": "eyJ...",
    "refreshToken": "eyJ..."
  }

  ---
  게시글 조회수 (Post)

  게시글 조회 (조회수 +1)

  GET /api/posts/{postId}

  Response
  {
    "postId": 1,
    "viewCount": 42
  }

  조회수 확인 (증가 없음)

  GET /api/posts/{postId}/views

  Response
  {
    "postId": 1,
    "viewCount": 42
  }

  조회수 초기화 (테스트용)

  DELETE /api/posts/{postId}/views

  Response
  {
    "postId": 1,
    "status": "초기화됨"
  }

  ---
  좋아요 (Like)

  좋아요 추가

  POST /api/posts/{postId}/likes?userId={userId}

  Response
  {
    "postId": 1,
    "userId": "user1",
    "message": "좋아요가 추가되었습니다.",
    "likeCount": 10
  }

  좋아요 취소

  DELETE /api/posts/{postId}/likes?userId={userId}

  Response
  {
    "postId": 1,
    "userId": "user1",
    "message": "좋아요가 취소되었습니다.",
    "likeCount": 9
  }

  좋아요 수 조회

  GET /api/posts/{postId}/likes

  Response
  {
    "postId": 1,
    "likeCount": 10
  }

  좋아요 여부 확인

  GET /api/posts/{postId}/likes/check?userId={userId}

  Response
  {
    "postId": 1,
    "userId": "user1",
    "hasLiked": true
  }

  ---
  댓글 (Comment)

  댓글 작성 (실시간 알림 발행)

  POST /api/posts/{postId}/comments?userId={userId}&content={content}

  Response
  {
    "postId": 1,
    "userId": "user1",
    "content": "댓글 내용",
    "commentCount": 5
  }

  ▎ 댓글 작성 시 WebSocket 채널 /topic/post/{postId} 로 실시간 알림 전송

  댓글 수 조회

  GET /api/posts/{postId}/comments/count

  Response
  {
    "postId": 1,
    "commentCount": 5
  }

  ---
  랭킹 (Ranking)

  상위 게시글 랭킹 조회

  GET /api/ranking/posts?count=10

  Query Params

  ┌──────────┬──────┬────────┬───────────────────────┐
  │ 파라미터 │ 타입 │ 기본값 │         설명          │
  ├──────────┼──────┼────────┼───────────────────────┤
  │ count    │ int  │ 10     │ 조회할 상위 게시글 수 │
  └──────────┴──────┴────────┴───────────────────────┘

  Response
  [
    { "rank": 1, "postId": "5", "score": 35.0 },
    { "rank": 2, "postId": "2", "score": 22.0 },
    { "rank": 3, "postId": "8", "score": 18.0 }
  ]

  랭킹 점수 계산

  ┌─────────────┬──────┐
  │    행동     │ 점수 │
  ├─────────────┼──────┤
  │ 조회        │ +1.0 │
  ├─────────────┼──────┤
  │ 좋아요      │ +3.0 │
  ├─────────────┼──────┤
  │ 좋아요 취소 │ -3.0 │
  └─────────────┴──────┘

  특정 게시글 랭킹 조회

  GET /api/ranking/posts/{postId}

  Response
  {
    "postId": 5,
    "rank": 1,
    "score": 35.0
  }

  ---
  Redis 테스트 (Redis)

  ▎ 개발/테스트 목적 엔드포인트

  키-값 저장

  POST /api/redis/set?key={key}&value={value}&ttl={ttl}

  ┌──────────┬────────┬────────────────┐
  │ 파라미터 │ 기본값 │      설명      │
  ├──────────┼────────┼────────────────┤
  │ ttl      │ 30     │ 만료 시간 (초) │
  └──────────┴────────┴────────────────┘

  Response
  {
    "key": "test:key",
    "value": "hello",
    "ttl": 30
  }

  값 조회

  GET /api/redis/get?key={key}

  Response
  {
    "key": "test:key",
    "value": "hello"
  }

  TTL 확인

  GET /api/redis/ttl?key={key}

  Response
  {
    "key": "test:key",
    "ttl": 25
  }

  ▎ ttl: -1 = 만료 없음, ttl: -2 = 키 없음

  키 삭제

  DELETE /api/redis/delete?key={key}

  Response
  {
    "key": "test:key",
    "status": "삭제됨"
  }

  ---
  WebSocket 연결

  엔드포인트: ws://localhost:8080/ws (SockJS 지원)

  구독 채널:
  /topic/post/{postId}

  수신 메시지 형식:
  {
    "type": "COMMENT",
    "postId": 1,
    "userId": "user1",
    "content": "새 댓글이 달렸습니다."
  }

  ---
  프로젝트 구조

  src/main/java/com/practice/realtimeplatform/
  ├── config/
  │   ├── RedisConfig.java         # Redis 템플릿 & Pub/Sub 리스너 설정
  │   ├── SecurityConfig.java      # Spring Security & JWT 필터 체인
  │   └── WebSocketConfig.java     # STOMP WebSocket 엔드포인트 설정
  ├── controller/
  │   ├── AuthController.java
  │   ├── PostController.java
  │   ├── LikeController.java
  │   ├── CommentController.java
  │   ├── RankingController.java
  │   └── RedisController.java
  ├── service/
  │   ├── AuthService.java
  │   ├── PostService.java
  │   ├── LikeService.java
  │   ├── CommentService.java
  │   ├── RankingService.java
  ├── pubsub/
  │   ├── NotificationEvent.java
  │   ├── NotificationPublisher.java
  │   └── NotificationSubscriber.java
  ├── filter/
  │   └── JwtFilter.java
  ├── dto/
  │   ├── LoginRequestDTO.java
  │   └── LoginResponseDTO.java
  └── util/
      └── JwtUtil.java
