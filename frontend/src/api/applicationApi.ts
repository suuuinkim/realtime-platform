import { apiFetch } from './client';
import type { ApplicationStatus } from '../data/mockClasses';

export interface ApplicationResponse {
  courseId: string;
  userId: string;
  applicationId: string | null;
  status: ApplicationStatus;
  position: number | null;
  confirmedCount: number;
  waitingCount: number;
  holdTtlSeconds: number | null;
  message: string;
}

export interface ApplicationEvent {
  eventType: string;
  courseId: string;
  userId: string;
  applicationId: string | null;
  status: ApplicationStatus;
  position: number | null;
  message: string;
  holdTtlSeconds: number | null;
}

export interface QueuePositionResponse {
  courseId: string;
  userId: string;
  position: number | null;
  waitingCount: number;
  status: ApplicationStatus;
  holdTtlSeconds: number | null;
}

export function requestApplication(courseId: string): Promise<ApplicationResponse> {
  return apiFetch(`/api/courses/${courseId}/applications`, { method: 'POST' });
}

export function confirmApplication(courseId: string): Promise<ApplicationResponse> {
  return apiFetch(`/api/courses/${courseId}/applications/confirm`, { method: 'POST' });
}

export function getQueuePosition(courseId: string): Promise<QueuePositionResponse> {
  return apiFetch(`/api/courses/${courseId}/applications/queue/position`);
}
