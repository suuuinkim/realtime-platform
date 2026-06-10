import { Clock, Users } from 'lucide-react';
import { Link } from 'react-router-dom';
import type { ClassItem } from '../data/mockClasses';
import StatusBadge from './StatusBadge';

export default function ApplicationPanel({ item }: { item: ClassItem }) {
  const seatsLeft = Math.max(0, item.capacity - item.enrolled);

  return (
    <aside className="premium-card sticky top-28 p-6">
      <div className="flex items-center justify-between">
        <StatusBadge status={item.status} />
        <span className="text-sm font-extrabold text-[#6B7280]">선착순 대기열</span>
      </div>

      <div className="mt-6 grid grid-cols-2 gap-3">
        <Metric label="정원" value={`${item.capacity}명`} tone="sky" />
        <Metric label="현재 신청" value={`${item.enrolled}명`} tone="violet" />
        <Metric label="남은 자리" value={`${seatsLeft}명`} tone="indigo" highlight />
        <Metric label="현재 대기" value={`${item.waiting}명`} tone="orange" />
      </div>

      <div className="mt-5 rounded-3xl bg-white/68 p-4 ring-1 ring-[rgba(17,24,39,0.06)]">
        <p className="flex items-center gap-2 text-sm font-extrabold text-[#111827]">
          <Clock size={18} className="text-indigo-500" />
          신청 시작 {item.applyStart}
        </p>
        <p className="mt-2 flex items-center gap-2 text-sm font-extrabold text-[#111827]">
          <Users size={18} className="text-orange-500" />
          신청 방식: 선착순 대기열
        </p>
      </div>

      <Link to={`/queue/${item.id}`} className="primary-button mt-6 w-full">대기실 입장하기</Link>

      <div className="mt-5 space-y-2 text-sm font-bold leading-6 text-[#6B7280]">
        <p>내 차례가 오면 5분 동안 신청 기회가 유지됩니다.</p>
        <p>시간 안에 확정하지 않으면 다음 대기자에게 기회가 넘어갑니다.</p>
      </div>
    </aside>
  );
}

function Metric({ label, value, tone, highlight = false }: { label: string; value: string; tone: 'indigo' | 'orange' | 'sky' | 'violet'; highlight?: boolean }) {
  const tones = {
    indigo: 'bg-indigo-50/80 text-indigo-600 ring-indigo-100',
    orange: 'bg-orange-50/80 text-orange-600 ring-orange-100',
    sky: 'bg-sky-50/80 text-sky-600 ring-sky-100',
    violet: 'bg-violet-50/80 text-violet-600 ring-violet-100',
  };

  return (
    <div className={`rounded-3xl p-4 ring-1 ${tones[tone]} ${highlight ? 'shadow-[0_14px_30px_rgba(79,70,229,0.12)]' : ''}`}>
      <p className="text-xs font-black text-[#6B7280]">{label}</p>
      <p className="mt-1 text-2xl font-black">{value}</p>
    </div>
  );
}
