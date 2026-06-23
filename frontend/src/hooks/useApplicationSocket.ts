import { Client, type IMessage } from '@stomp/stompjs';
import { useEffect, useRef } from 'react';
import type { ApplicationEvent } from '../api/applicationApi';

export function useApplicationSocket(
  courseId: string,
  userId: string | null,
  onUserEvent: (event: ApplicationEvent) => void,
) {
  const onUserEventRef = useRef(onUserEvent);
  onUserEventRef.current = onUserEvent;
  useEffect(() => {
    if (!userId) return;

    const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
    const client = new Client({
      brokerURL: `${protocol}://${window.location.host}/ws-native`,
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem('token') ?? ''}`,
      },
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
      },
    });

    client.activate();
    return () => { client.deactivate(); };
  }, [courseId, userId]);
}
