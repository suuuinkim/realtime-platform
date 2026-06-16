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
- 확정/신청권/대기열 진입 이벤트 Kafka 발행
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
  |
  | Kafka publish
  v
application-events topic
  |
  | Kafka consume
  v
ApplicationEventConsumer
  |
  | WebSocket
  v
/topic/courses/{courseId}
/topic/users/{userId}/applications
```

## Event Flow

1. 사용자가 강의 신청 API를 호출합니다.
2. 정원에 여유가 있으면 `HOLDING` 상태의 신청권을 발급합니다.
3. 정원이 없으면 사용자를 Redis 대기열에 넣고 `WAITING` 상태를 반환합니다.
4. 사용자가 신청권을 확정하면 `CONFIRMED` 상태로 저장합니다.
5. 각 상태 변경은 Kafka `application-events` 토픽으로 발행됩니다.
6. Kafka Consumer가 이벤트를 받아 WebSocket 채널로 전달합니다.

## Redis Key Design

| Key | Type | Description |
| --- | --- | --- |
| `course:{courseId}:capacity` | String | 강의 정원. 없으면 기본값 20 |
| `course:{courseId}:confirmed` | Set | 신청 확정 사용자 목록 |
| `course:{courseId}:queue` | Sorted Set | 대기열. score는 진입 시각 |
| `course:{courseId}:hold:{userId}` | String + TTL | 사용자별 신청권. 기본 TTL 5분 |
| `course:{courseId}:holds` | Sorted Set | 활성 신청권 목록. score는 만료 시각 |

## Kafka

| Topic | Producer | Consumer | Purpose |
| --- | --- | --- | --- |
| `application-events` | `ApplicationEventProducer` | `ApplicationEventConsumer` | 신청 상태 변경 이벤트 전달 |

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
POST /api/courses/{courseId}/applications?userId={userId}
```

정원 여유가 있으면 `HOLDING`, 정원이 없으면 `WAITING`을 반환합니다.

### 신청 확정

```http
POST /api/courses/{courseId}/applications/confirm?userId={userId}
```

활성 신청권이 있을 때 확정합니다. 신청권이 없거나 만료됐으면 `EXPIRED`를 반환합니다.

### 대기열 승급

```http
POST /api/courses/{courseId}/applications/queue/advance
```

정원 여유가 생겼을 때 대기열 첫 번째 사용자에게 신청권을 발급합니다.

### 대기 순번 조회

```http
GET /api/courses/{courseId}/applications/queue/position?userId={userId}
```

Response example:

```json
{
  "courseId": "spring-boot",
  "userId": "user1",
  "position": 3,
  "waitingCount": 42
}
```

## WebSocket

Endpoint:

```text
/ws
```

Subscribe channels:

```text
/topic/courses/{courseId}
/topic/users/{userId}/applications
```

## Run

### Infrastructure

```bash
docker compose up -d
```

Starts:

- Redis: `localhost:6379`
- Kafka: `localhost:9092`

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

The context load test requires Redis to be running because the Redis listener starts during application boot.

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
