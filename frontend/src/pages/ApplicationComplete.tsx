import { BellRing, CheckCircle2 } from 'lucide-react';
import { Link, useLocation, useParams } from 'react-router-dom';
import StatusBadge from '../components/StatusBadge';
import { findClass } from '../data/mockClasses';

interface LocationState {
  courseId: string;
}

export default function ApplicationComplete() {
  const { applicationId } = useParams();
  const location = useLocation();
  const state = location.state as LocationState | null;
  const courseId = state?.courseId ?? applicationId?.split(':')[0] ?? '';
  const item = findClass(courseId);
  const appliedAt = new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <section className="premium-card relative overflow-hidden p-8 text-center">
        <div className="absolute left-8 top-8 h-3 w-3 rounded-full bg-orange-300/70" />
        <div className="absolute right-12 top-14 h-2 w-2 rounded-full bg-sky-300/80" />
        <div className="absolute bottom-10 left-16 h-2.5 w-2.5 rounded-full bg-indigo-300/70" />
        <div className="mx-auto grid h-20 w-20 place-items-center rounded-3xl bg-emerald-50 text-emerald-600 shadow-[0_16px_34px_rgba(22,163,74,0.14)] ring-1 ring-emerald-100">
          <CheckCircle2 size={42} />
        </div>
        <h1 className="mt-6 text-4xl font-black tracking-tight text-[#111827]">수업 신청이 완료되었습니다.</h1>
        <p className="mt-3 text-base font-bold text-[#6B7280]">신청 상태와 알림 전송 결과를 확인하세요.</p>
      </section>

      <section className="premium-card p-6">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <StatusBadge status="CONFIRMED" />
          <span className="inline-flex items-center gap-2 rounded-full bg-indigo-50 px-3 py-1 text-xs font-black text-indigo-600 ring-1 ring-indigo-100">
            <BellRing size={15} /> 신청 완료 알림 전송됨
          </span>
        </div>
        <div className="mt-5 rounded-[28px] bg-gradient-to-br from-white/90 to-indigo-50/70 p-5 ring-1 ring-white/80">
          <p className="text-xs font-black text-indigo-500">CLASS TICKET</p>
          <h2 className="mt-2 text-2xl font-black text-[#111827]">{item.title}</h2>
          <div className="mt-5 grid gap-3 md:grid-cols-2">
            <Info label="일정" value={item.schedule} />
            <Info label="신청 완료 시간" value={appliedAt} />
          </div>
        </div>
      </section>

      <div className="grid gap-3 sm:grid-cols-2">
        <Link to="/my" className="primary-button">내 신청 내역 보기</Link>
        <Link to="/" className="secondary-button h-14">다른 수업 둘러보기</Link>
      </div>
    </div>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-2xl bg-white/74 p-4 ring-1 ring-[rgba(17,24,39,0.06)]">
      <p className="text-xs font-black text-[#6B7280]">{label}</p>
      <p className="mt-1 text-base font-black text-[#111827]">{value}</p>
    </div>
  );
}
