export type ClassStatus = '신청 예정' | '대기실 오픈' | '신청 가능' | '마감 임박' | '마감';
export type Category = '백엔드' | '프론트엔드' | '데이터';
export type ApplicationStatus = 'WAITING' | 'HOLDING' | 'CONFIRMED' | 'EXPIRED' | 'CANCELLED';

export interface ClassItem {
  id: string;
  title: string;
  shortDescription: string;
  instructor: string;
  category: Category;
  schedule: string;
  capacity: number;
  enrolled: number;
  waiting: number;
  applyStart: string;
  status: ClassStatus;
  visualTone: 'indigo' | 'orange' | 'green' | 'purple' | 'slate';
  recommendedFor: string[];
  curriculum: string[];
}

export interface ApplicationItem {
  id: string;
  classId: string;
  status: ApplicationStatus;
  appliedAt: string;
  meta: string;
}

export const classes: ClassItem[] = [
  {
    id: 'spring-boot',
    title: '실전 Spring Boot 백엔드 과정',
    shortDescription: '운영 API, 데이터 처리, 배치, 성능 개선까지 실무 흐름으로 배우는 백엔드 집중 과정',
    instructor: '이도현',
    category: '백엔드',
    schedule: '2026.06.20 10:00',
    capacity: 30,
    enrolled: 24,
    waiting: 128,
    applyStart: '오늘 20:00',
    status: '대기실 오픈',
    visualTone: 'indigo',
    recommendedFor: ['Spring Boot 실무 API를 정리하고 싶은 분', '배치/데이터 처리 흐름을 경험해보고 싶은 분', '성능 개선과 운영 이슈에 관심 있는 분'],
    curriculum: ['REST API 설계', '데이터 업로드와 검증', 'Spring Batch 처리', 'SQL 성능 개선', 'Redis/Kafka 기반 대기열 구조'],
  },
  {
    id: 'react-components',
    title: 'React 실무 컴포넌트 설계',
    shortDescription: '확장 가능한 컴포넌트 구조와 상태 설계를 실무 사례로 정리하는 과정',
    instructor: '정서윤',
    category: '프론트엔드',
    schedule: '2026.06.22 20:00',
    capacity: 25,
    enrolled: 25,
    waiting: 0,
    applyStart: '마감',
    status: '마감',
    visualTone: 'purple',
    recommendedFor: ['컴포넌트 구조를 정리하고 싶은 분', 'TypeScript 기반 UI 설계가 필요한 분', '프론트엔드 코드 품질을 높이고 싶은 분'],
    curriculum: ['컴포넌트 책임 분리', 'Props 모델링', '상태 관리 패턴', '디자인 시스템 기초', '테스트 가능한 UI 구조'],
  },
  {
    id: 'sql-tuning',
    title: 'SQL 성능 튜닝 입문',
    shortDescription: '느린 쿼리를 읽고 개선하는 방법을 실행 계획과 인덱스 중심으로 배우는 과정',
    instructor: '박지훈',
    category: '데이터',
    schedule: '2026.06.25 10:00',
    capacity: 20,
    enrolled: 16,
    waiting: 42,
    applyStart: '지금 가능',
    status: '신청 가능',
    visualTone: 'green',
    recommendedFor: ['SQL 성능 문제를 자주 만나는 분', '실행 계획을 읽고 싶은 분', '인덱스 설계를 배우고 싶은 분'],
    curriculum: ['실행 계획 읽기', '인덱스 기초', '조인 최적화', '느린 쿼리 분석', '성능 개선 실습'],
  },
  {
    id: 'docker-deploy',
    title: 'Docker로 시작하는 배포 환경',
    shortDescription: '컨테이너 기반 개발/배포 환경을 빠르게 구성하는 실무 입문 과정',
    instructor: '최민재',
    category: '백엔드',
    schedule: '2026.06.27 19:00',
    capacity: 30,
    enrolled: 12,
    waiting: 18,
    applyStart: '내일 10:00',
    status: '신청 예정',
    visualTone: 'slate',
    recommendedFor: ['배포 환경을 처음 정리하는 분', 'Dockerfile과 Compose가 헷갈리는 분', '로컬과 운영 환경 차이를 줄이고 싶은 분'],
    curriculum: ['Dockerfile 작성', 'Compose 구성', '환경 변수 관리', '이미지 빌드', '배포 체크리스트'],
  },
  {
    id: 'kafka-event',
    title: 'Kafka 이벤트 처리 기초',
    shortDescription: 'Producer, Consumer, Topic 설계와 이벤트 기반 알림 흐름을 배우는 과정',
    instructor: '한유진',
    category: '백엔드',
    schedule: '2026.06.28 20:00',
    capacity: 20,
    enrolled: 18,
    waiting: 95,
    applyStart: '오늘 21:00',
    status: '마감 임박',
    visualTone: 'orange',
    recommendedFor: ['비동기 이벤트 구조가 궁금한 분', 'Kafka 기본 개념을 잡고 싶은 분', '알림/신청 완료 이벤트를 설계하고 싶은 분'],
    curriculum: ['Topic 설계', 'Producer 구현', 'Consumer 처리', '이벤트 재처리', '신청 완료 알림 흐름'],
  },
  {
    id: 'redis-queue',
    title: 'Redis로 배우는 대기열 시스템',
    shortDescription: 'Redis Sorted Set과 TTL을 활용해 선착순 대기열과 신청 선점을 설계하는 과정',
    instructor: '김하린',
    category: '백엔드',
    schedule: '2026.06.30 19:30',
    capacity: 20,
    enrolled: 15,
    waiting: 77,
    applyStart: '오늘 20:30',
    status: '대기실 오픈',
    visualTone: 'indigo',
    recommendedFor: ['동시 신청 처리를 구현하고 싶은 분', 'Redis 자료구조 활용법이 궁금한 분', 'TTL 기반 선점 흐름을 배우고 싶은 분'],
    curriculum: ['Sorted Set 대기열', '순번 계산', 'TTL 신청권', '만료 처리', 'WebSocket 상태 알림'],
  },
];

export const applications: ApplicationItem[] = [
  { id: 'app-2401', classId: 'spring-boot', status: 'CONFIRMED', appliedAt: '오늘 20:06', meta: '신청 완료 알림 전송됨' },
  { id: 'app-2402', classId: 'redis-queue', status: 'WAITING', appliedAt: '오늘 20:10', meta: '현재 28번째' },
  { id: 'app-2403', classId: 'kafka-event', status: 'HOLDING', appliedAt: '오늘 20:12', meta: '04:18 안에 확정 필요' },
  { id: 'app-2397', classId: 'sql-tuning', status: 'EXPIRED', appliedAt: '어제 19:55', meta: '신청 기회 만료' },
];

export function findClass(id?: string) {
  return classes.find((item) => item.id === id) ?? classes[0];
}

export function findApplication(id?: string) {
  return applications.find((item) => item.id === id) ?? applications[0];
}
