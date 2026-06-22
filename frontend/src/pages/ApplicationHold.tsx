import { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import { confirmApplication } from '../api/applicationApi';
import ClassHeroVisual from '../components/ClassHeroVisual';
import CountdownTimer from '../components/CountdownTimer';
import StatusBadge from '../components/StatusBadge';
import { useAuth } from '../context/AuthContext';
import { findClass } from '../data/mockClasses';
import { saveApplication } from '../utils/applicationStorage';

interface LocationState {
  holdTtlSeconds: number;
  applicationId: string | null;
}

export default function ApplicationHold() {
  const { classId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { userId } = useAuth();
  const item = findClass(classId);

  const state = location.state as LocationState | null;
  const totalSeconds = state?.holdTtlSeconds ?? 300;
  const [secondsLeft, setSecondsLeft] = useState(totalSeconds);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const timer = window.setInterval(() => setSecondsLeft((s) => Math.max(0, s - 1)), 1000);
    return () => window.clearInterval(timer);
  }, []);

  async function handleConfirm() {
    if (!userId || !classId) return;
    setLoading(true);
    setError('');
    try {
      const response = await confirmApplication(classId, userId);
      saveApplication(response);
      navigate(`/complete/${response.applicationId ?? classId}`, { state: { courseId: classId } });
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="mx-auto grid max-w-5xl gap-6 lg:grid-cols-[1fr_420px]">
      <section className="space-y-5">
        <ClassHeroVisual item={item} />
        <div className="premium-card p-6">
          <StatusBadge status="HOLDING" />
          <h1 className="mt-4 text-4xl font-black tracking-tight text-[#111827]">신청 기회가 열렸어요.</h1>
          <p className="mt-3 text-base font-bold leading-7 text-[#6B7280]">
            5분 안에 확정하지 않으면 다음 대기자에게 기회가 넘어갑니다.
          </p>
        </div>
      </section>

      <aside className="space-y-5">
        <CountdownTimer secondsLeft={secondsLeft} totalSeconds={totalSeconds} />
        <div className="premium-card p-6">
          <h2 className="text-xl font-black text-[#111827]">{item.title}</h2>
          <p className="mt-2 text-sm font-bold text-[#6B7280]">{item.schedule}</p>
          {error && <p className="mt-3 text-xs font-bold text-red-500">{error}</p>}
          <button
            className="primary-button mt-6 w-full disabled:opacity-50"
            disabled={loading || secondsLeft === 0}
            onClick={handleConfirm}
          >
            {loading ? '처리 중...' : '수업 신청 확정하기'}
          </button>
          <Link to="/" className="secondary-button mt-3 w-full">
            신청 취소
          </Link>
        </div>
      </aside>
    </div>
  );
}
