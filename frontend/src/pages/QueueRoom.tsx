import { RefreshCw } from 'lucide-react';
import { useCallback, useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import type { ApplicationEvent } from '../api/applicationApi';
import { getQueuePosition } from '../api/applicationApi';
import ClassHeroVisual from '../components/ClassHeroVisual';
import QueueProgress from '../components/QueueProgress';
import StatusBadge from '../components/StatusBadge';
import { useAuth } from '../context/AuthContext';
import { findClass } from '../data/mockClasses';
import { useApplicationSocket } from '../hooks/useApplicationSocket';

const POLL_INTERVAL_MS = 10_000;

interface LocationState {
  position: number | null;
  waitingCount: number | null;
}

export default function QueueRoom() {
  const { classId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { userId } = useAuth();
  const item = findClass(classId);

  const state = location.state as LocationState | null;
  const [position, setPosition] = useState<number | null>(state?.position ?? null);
  const [waitingCount, setWaitingCount] = useState<number | null>(state?.waitingCount ?? null);
  const [refreshing, setRefreshing] = useState(false);
  const isMounted = useRef(true);

  const fetchPosition = useCallback(async (showSpinner = false) => {
    if (!userId || !classId) return;
    if (showSpinner) setRefreshing(true);
    try {
      const res = await getQueuePosition(classId);
      if (!isMounted.current) return;
      if (res.status === 'HOLDING') {
        navigate(`/apply/${classId}`, {
          state: { holdTtlSeconds: res.holdTtlSeconds ?? 300, applicationId: `${classId}:${userId}` },
        });
        return;
      }
      if (res.position !== null) setPosition(res.position);
      setWaitingCount(res.waitingCount);
    } catch {
      // 네트워크 오류는 무시하고 다음 폴링에서 재시도
    } finally {
      if (showSpinner) setRefreshing(false);
    }
  }, [classId, navigate, userId]);

  // 최초 진입 시 position이 없으면 즉시 조회
  useEffect(() => {
    isMounted.current = true;
    if (position === null) fetchPosition();
    return () => { isMounted.current = false; };
  }, []);

  // 주기적 폴링 (10초)
  useEffect(() => {
    const timer = setInterval(() => fetchPosition(), POLL_INTERVAL_MS);
    return () => clearInterval(timer);
  }, [fetchPosition]);

  const handleUserEvent = useCallback(
    (event: ApplicationEvent) => {
      if (event.status === 'HOLDING') {
        navigate(`/apply/${classId}`, {
          state: { holdTtlSeconds: event.holdTtlSeconds ?? 300, applicationId: event.applicationId },
        });
      }
    },
    [classId, navigate],
  );

  useApplicationSocket(classId!, userId, handleUserEvent);

  const progress =
    waitingCount && position ? Math.round((1 - (position - 1) / waitingCount) * 100) : 0;

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <section className="premium-card grid gap-5 p-5 md:grid-cols-[220px_1fr] md:items-center">
        <ClassHeroVisual item={item} compact />
        <div>
          <div className="flex flex-wrap gap-2">
            <StatusBadge status={item.status} />
            <span className="rounded-full bg-white/72 px-3 py-1 text-xs font-black text-indigo-600 ring-1 ring-indigo-100">
              자동 갱신 중
            </span>
          </div>
          <h1 className="mt-4 text-3xl font-black tracking-tight text-[#111827]">대기열에 입장했습니다.</h1>
          <p className="mt-2 text-sm font-bold leading-6 text-[#6B7280]">
            {item.title} · 내 차례가 되면 신청 화면으로 이동할 수 있어요.
          </p>
        </div>
      </section>

      <section className="premium-card p-8 text-center">
        <p className="text-sm font-black text-[#6B7280]">현재 내 순번</p>
        <p className="mt-2 text-8xl font-black tracking-tight text-indigo-600 md:text-9xl">
          {position ?? '...'}
        </p>
        {position !== null && (
          <p className="mt-2 text-2xl font-black text-[#111827]">
            번째 {waitingCount !== null && <span className="text-lg text-[#6B7280]">/ 총 {waitingCount}명</span>}
          </p>
        )}
        <p className="mt-4 text-lg font-extrabold text-[#111827]">내 차례를 기다리고 있어요.</p>
        <div className="mt-8">
          <QueueProgress value={progress} />
        </div>
        <button
          className="mt-5 inline-flex items-center gap-2 rounded-full bg-white/72 px-4 py-2 text-sm font-black text-[#6B7280] ring-1 ring-white/70 hover:bg-white/90 disabled:opacity-50"
          onClick={() => fetchPosition(true)}
          disabled={refreshing}
        >
          <RefreshCw size={16} className={refreshing ? 'animate-spin' : ''} />
          {refreshing ? '확인 중...' : '순번을 자동으로 확인하고 있어요'}
        </button>
      </section>
    </div>
  );
}
