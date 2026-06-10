import { RefreshCw } from 'lucide-react';
import { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import ClassHeroVisual from '../components/ClassHeroVisual';
import QueueProgress from '../components/QueueProgress';
import StatusBadge from '../components/StatusBadge';
import { findClass } from '../data/mockClasses';

const steps = [128, 74, 28, 3, 0];

export default function QueueRoom() {
  const { classId } = useParams();
  const navigate = useNavigate();
  const item = findClass(classId);
  const [step, setStep] = useState(0);
  const rank = steps[step];
  const ready = rank === 0;
  const progress = useMemo(() => Math.round((step / (steps.length - 1)) * 100), [step]);

  useEffect(() => {
    const timer = window.setInterval(() => setStep((current) => Math.min(current + 1, steps.length - 1)), 1400);
    return () => window.clearInterval(timer);
  }, []);

  useEffect(() => {
    if (!ready) return;
    const timer = window.setTimeout(() => navigate(`/apply/${item.id}`), 1600);
    return () => window.clearTimeout(timer);
  }, [ready, item.id, navigate]);

  return (
    <div className="mx-auto max-w-4xl space-y-6">
      <section className="premium-card grid gap-5 p-5 md:grid-cols-[220px_1fr] md:items-center">
        <ClassHeroVisual item={item} compact />
        <div>
          <div className="flex flex-wrap gap-2">
            <StatusBadge status={item.status} />
            <span className="rounded-full bg-white/72 px-3 py-1 text-xs font-black text-indigo-600 ring-1 ring-indigo-100">자동 갱신 중</span>
          </div>
          <h1 className="mt-4 text-3xl font-black tracking-tight text-[#111827]">대기열에 입장했습니다.</h1>
          <p className="mt-2 text-sm font-bold leading-6 text-[#6B7280]">{item.title} · 내 차례가 되면 신청 화면으로 이동할 수 있어요.</p>
        </div>
      </section>

      <section className="premium-card p-8 text-center">
        <p className="text-sm font-black text-[#6B7280]">현재 내 순번</p>
        <p className="mt-2 text-8xl font-black tracking-tight text-indigo-600 md:text-9xl">{ready ? 'READY' : rank}</p>
        {!ready && <p className="mt-2 text-2xl font-black text-[#111827]">번째</p>}
        <p className="mt-4 text-lg font-extrabold text-[#111827]">{ready ? '신청 화면으로 이동할 준비가 되었어요.' : '예상 대기 시간: 약 2분'}</p>
        <div className="mt-8">
          <QueueProgress value={progress} />
        </div>
        <p className="mt-5 inline-flex items-center gap-2 rounded-full bg-white/72 px-4 py-2 text-sm font-black text-[#6B7280] ring-1 ring-white/70">
          <RefreshCw size={16} /> 순번을 자동으로 확인하고 있어요
        </p>
      </section>

      <Link to={`/apply/${item.id}`} className={`primary-button w-full ${ready ? '' : 'pointer-events-none opacity-45'}`}>
        신청 화면으로 이동
      </Link>
    </div>
  );
}
