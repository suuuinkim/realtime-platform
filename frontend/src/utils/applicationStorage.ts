import type { ApplicationItem } from '../data/mockClasses';
import type { ApplicationResponse } from '../api/applicationApi';

const STORAGE_KEY = 'my_applications';

export function saveApplication(response: ApplicationResponse): void {
  const items = loadApplications();
  const id = response.applicationId ?? `${response.courseId}:${response.userId}`;
  const idx = items.findIndex((item) => item.id === id);

  const stored: ApplicationItem = {
    id,
    classId: response.courseId,
    status: response.status,
    appliedAt: new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' }),
    meta: buildMeta(response),
  };

  if (idx >= 0) {
    items[idx] = stored;
  } else {
    items.unshift(stored);
  }

  localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
}

export function loadApplications(): ApplicationItem[] {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) ?? '[]');
  } catch {
    return [];
  }
}

function buildMeta(response: ApplicationResponse): string {
  switch (response.status) {
    case 'WAITING': return `현재 ${response.position}번째`;
    case 'HOLDING': return `${response.holdTtlSeconds ?? 300}초 안에 확정 필요`;
    case 'CONFIRMED': return '신청 완료 알림 전송됨';
    case 'EXPIRED': return '신청 기회 만료';
    default: return '';
  }
}
