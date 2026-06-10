import { Link } from 'react-router-dom';
import StatusBadge from '../components/StatusBadge';
import { applications, findClass } from '../data/mockClasses';

export default function MyApplications() {
  const confirmed = applications.filter((item) => item.status === 'CONFIRMED').length;
  const waiting = applications.filter((item) => item.status === 'WAITING' || item.status === 'HOLDING').length;
  const expired = applications.filter((item) => item.status === 'EXPIRED').length;

  return (
    <div className="space-y-6">
      <section>
        <h1 className="text-4xl font-black tracking-tight text-[#111827]">내 신청 내역</h1>
        <p className="mt-2 text-base font-bold text-[#6B7280]">신청 완료, 대기 중, 만료된 신청을 한눈에 확인하세요.</p>
      </section>

      <section className="grid gap-4 md:grid-cols-3">
        <Summary label="신청 완료" value={confirmed} tone="emerald" />
        <Summary label="대기 중" value={waiting} tone="indigo" />
        <Summary label="만료" value={expired} tone="slate" />
      </section>

      <section className="grid gap-4">
        {applications.map((application) => {
          const item = findClass(application.classId);
          return (
            <article key={application.id} className="premium-card grid gap-4 p-5 md:grid-cols-[1fr_auto] md:items-center">
              <div>
                <StatusBadge status={application.status} />
                <h2 className="mt-3 text-xl font-black text-[#111827]">{item.title}</h2>
                <p className="mt-2 text-sm font-bold text-[#6B7280]">{item.schedule}</p>
                <p className="mt-2 text-sm font-extrabold text-indigo-600">{application.appliedAt} · {application.meta}</p>
              </div>
              <Link to={`/classes/${item.id}`} className="secondary-button">상세 보기</Link>
            </article>
          );
        })}
      </section>
    </div>
  );
}

function Summary({ label, value, tone }: { label: string; value: number; tone: 'emerald' | 'indigo' | 'slate' }) {
  const tones = {
    emerald: 'text-emerald-600 bg-emerald-50/70 ring-emerald-100',
    indigo: 'text-indigo-600 bg-indigo-50/70 ring-indigo-100',
    slate: 'text-slate-500 bg-slate-50/80 ring-slate-100',
  };

  return (
    <div className={`rounded-3xl p-6 shadow-[0_18px_48px_rgba(15,23,42,0.07)] ring-1 backdrop-blur ${tones[tone]}`}>
      <p className="text-sm font-black text-[#6B7280]">{label}</p>
      <p className="mt-2 text-4xl font-black">{value}</p>
    </div>
  );
}
