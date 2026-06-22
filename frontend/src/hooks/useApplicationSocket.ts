import { Client, type IMessage } from '@stomp/stompjs';
import { useEffect, useRef } from 'react';
import type { ApplicationEvent } from '../api/applicationApi';

export function useApplicationSocket(
  courseId: string,
  userId: string | null,
  onUserEvent: (event: ApplicationEvent) => void,
  onCourseEvent?: (event: ApplicationEvent) => void,
) {
  const onUserEventRef = useRef(onUserEvent);
  onUserEventRef.current = onUserEvent;
  const onCourseEventRef = useRef(onCourseEvent);
  onCourseEventRef.current = onCourseEvent;

  useEffect(() => {
    if (!userId) return;

    const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
    const client = new Client({
      brokerURL: `${protocol}://${window.location.host}/ws-native`,
      reconnectDelay: 5000,
      onConnect: () => {
        // 유저 개인 이벤트 (HOLDING 전환 등)
        client.subscribe(`/topic/users/${userId}/applications`, (msg: IMessage) => {
          try {
            const event: ApplicationEvent = JSON.parse(msg.body);
            if (event.courseId === courseId) {
              onUserEventRef.current(event);
            }
          } catch {
            // ignore malformed messages
          }
        });

        // 강의 전체 이벤트 (다른 유저 HOLDING → 내 순번 변동 감지)
        if (onCourseEventRef.current) {
          client.subscribe(`/topic/courses/${courseId}`, (msg: IMessage) => {
            try {
              const event: ApplicationEvent = JSON.parse(msg.body);
              onCourseEventRef.current?.(event);
            } catch {
              // ignore malformed messages
            }
          });
        }
      },
    });

    client.activate();
    return () => { client.deactivate(); };
  }, [courseId, userId]);
}
