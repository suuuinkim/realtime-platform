# ClassQueue

강의 신청이 몰리는 상황을 가정한 실시간 선착순 대기열 플랫폼입니다.

Redis로 강의별 정원, 대기열, 신청권 TTL을 관리하고, Kafka로 신청 상태 변경 이벤트를 발행합니다. 클라이언트는 WebSocket을 통해 강의별 대기열 변화와 사용자별 신청 상태를 실시간으로 받을 수 있습니다.

## Tech Stack

| Area | Tech |
| --- | --- |
| Backend | Java 17, Spring Boot 4.0.6 |
| Frontend | React 19, Vite, TypeScript |
| Cache / Queue State | Redis 7 |
| Messaging | Kafka |
| Real-time | WebSocket, STOMP, SockJS |
| Security | Spring Security, JWT |
| Build | Gradle, npm |
| DevOps | Docker Compose |

## Main Features

- 강의별 선착순 신청 요청
- 정원 여유가 있으면 5분짜리 신청권 발급
- 정원이 가득 차면 Redis Sorted Set 기반 대기열 진입
- 대기 순번 조회
- 신청권 확정 처리
- 상태 변경과 Redis Stream Outbox 이벤트를 Lua로 원자 저장
- Kafka 전송 실패 시 Outbox 메시지 재시도
- Kafka Consumer가 WebSocket으로 실시간 알림 전송
- Redis TTL과 활성 신청권 추적으로 신청권 과발급 방지

## Architecture

```text
Client
  |
  | REST
  v
Spring Boot API
  |
  | 신청 요청 / 확정 / 순번 조회
  v
CourseApplicationService
  |
  | Redis
  | - course:{courseId}:confirmed
  | - course:{courseId}:queue
  | - course:{courseId}:hold:{userId}
  | - course:{courseId}:holds
  | - application:outbox (Redis Stream)
  |
  | ApplicationOutboxDispatcher
  | Kafka publish + success ACK
  v
application-events topic
  |
  | Kafka consume
  v
ApplicationEventConsumer
  |
  | WebSocket
  v
/topic/users/{userId}/applications
```

## Event Flow

1. 사용자가 강의 신청 API를 호출합니다.
2. 정원에 여유가 있으면 `HOLDING` 상태의 신청권을 발급합니다.
3. 정원이 없으면 사용자를 Redis 대기열에 넣고 `WAITING` 상태를 반환합니다.
4. 사용자가 신청권을 확정하면 `CONFIRMED` 상태로 저장합니다.
5. 각 상태 변경과 Outbox 이벤트는 하나의 Redis Lua 실행으로 원자 저장됩니다.
6. Outbox Dispatcher가 Redis Stream consumer group으로 이벤트를 읽어 Kafka에 발행합니다.
7. Kafka 전송 성공 후에만 Outbox 메시지를 ACK·삭제하며, 실패한 메시지는 다음 주기에 재시도합니다.
8. Kafka Consumer가 이벤트를 받아 WebSocket 채널로 전달합니다.

## Redis Key Design

| Key | Type | Description |
| --- | --- | --- |
| `course:{courseId}:capacity` | String | 강의 정원. 없으면 기본값 20 |
| `course:{courseId}:confirmed` | Set | 신청 확정 사용자 목록 |
| `course:{courseId}:queue` | Sorted Set | 대기열. score는 진입 시각 |
| `course:{courseId}:hold:{userId}` | String + TTL | 사용자별 신청권. 기본 TTL 5분 |
| `course:{courseId}:holds` | Sorted Set | 활성 신청권 목록. score는 만료 시각 |
| `course:active` | Set | 만료 이벤트 유실 대비 재조정 대상 강의 목록 |
| `application:outbox` | Stream | Kafka 전송 전 신청 상태 변경 이벤트 |

## Kafka

| Topic | Producer | Consumer | Purpose |
| --- | --- | --- | --- |
| `application-events` | `ApplicationOutboxDispatcher` | `ApplicationEventConsumer` | 신청 상태 변경 이벤트 전달 |

Event payload:

```json
{
  "eventType": "APPLICATION_HOLD_GRANTED",
  "courseId": "spring-boot",
  "userId": "user1",
  "applicationId": "spring-boot:user1",
  "status": "HOLDING",
  "position": null,
  "message": "Application hold granted"
}
```

## API

Base URL:

```text
http://localhost:8080
```

### 신청 요청

```http
POST /api/courses/{courseId}/applications
```

정원 여유가 있으면 `HOLDING`, 정원이 없으면 `WAITING`을 반환합니다.

### 신청 확정

```http
POST /api/courses/{courseId}/applications/confirm
```

활성 신청권이 있을 때 확정합니다. 신청권이 없거나 만료됐으면 `EXPIRED`를 반환합니다.

### 대기 순번 조회

```http
GET /api/courses/{courseId}/applications/queue/position
```

신청·확정·순번 조회의 사용자 ID는 요청 파라미터가 아니라 JWT subject에서 가져옵니다.

Response example:

```json
{
  "courseId": "spring-boot",
  "userId": "user1",
  "position": 3,
  "waitingCount": 42,
  "status": "WAITING",
  "holdTtlSeconds": null
}
```

순번 조회 응답에도 현재 상태와 Hold TTL이 포함되므로 WebSocket 이벤트가 유실돼도 폴링으로 승격 상태를 복구합니다.

## WebSocket

Endpoint:

```text
/ws
```

Subscribe channels:

```text
/topic/users/{userId}/applications
```

WebSocket 연결 시 STOMP `Authorization` 헤더에 Bearer 토큰이 필요하며, 본인의 개인 채널만 구독할 수 있습니다.

## Run

### Infrastructure

```bash
docker compose up -d
```

Starts:

- Redis: `localhost:6379` (AOF `everysec`, `redis-data` 볼륨)
- Kafka: `localhost:9092` (`acks=all`, idempotent producer, `kafka-data` 볼륨)

### Backend

```bash
./gradlew bootRun
```

If Windows uses an older `JAVA_HOME`, set JDK 17 or later before running:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.10'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat bootRun
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Verification

Compile:

```bash
./gradlew compileJava
```

Test:

```bash
./gradlew test
```

단위 테스트는 외부 Redis나 Kafka 없이 실행됩니다. 실제 Outbox 통합 검증 시에는 Docker Compose 인프라를 실행합니다.

## Project Structure

```text
src/main/java/com/practice/realtimeplatform
  application/
    controller/       # Course application APIs
    dto/              # Application responses
    event/            # Kafka event payload
    service/          # Queue and application business logic
  global/
    config/           # Kafka, Redis, Security, WebSocket config
    kafka/            # Application event producer/consumer
    redis/            # Redis utility API and service
    security/         # JWT filter/util
  post/               # Legacy post/like/comment/ranking APIs
frontend/
  src/                # ClassQueue React UI
```
