import { CalendarDays, Ticket, Users } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import type { ClassItem } from '../data/mockClasses';
import ClassHeroVisual from './ClassHeroVisual';
import StatusBadge from './StatusBadge';

export default function ClassCard({ item }: { item: ClassItem }) {
  const navigate = useNavigate();
  const seatsLeft = Math.max(0, item.capacity - item.enrolled);
  const disabled = item.status === '마감' || item.status === '신청 예정';
  const target = item.status === '신청 가능' ? `/apply/${item.id}` : `/queue/${item.id}`;
  const action = item.status === '신청 가능' ? '신청하기' : item.status === '대기실 오픈' || item.status === '마감 임박' ? '대기실 입장' : item.status;

  return (
    <article className="overflow-hidden rounded-[28px] bg-white/80 shadow-[0_20px_60px_rgba(15,23,42,0.08)] ring-1 ring-white/70 backdrop-blur-md transition duration-300 hover:-translate-y-1.5 hover:shadow-[0_26px_70px_rgba(79,70,229,0.14)]">
      <div className="p-3 pb-0">
        <ClassHeroVisual item={item} compact />
      </div>
      <div className="p-5">
        <div className="flex items-center justify-between gap-3">
          <span className="rounded-full bg-white/72 px-3 py-1 text-xs font-black text-indigo-600 ring-1 ring-indigo-100">{item.category}</span>
          <StatusBadge status={item.status} />
        </div>

        <h2 className="mt-4 text-2xl font-black leading-tight tracking-tight text-[#111827]">{item.title}</h2>
        <p className="mt-2 text-sm font-bold text-[#6B7280]">{item.instructor} 강사</p>

        <div className="mt-5 grid gap-2 text-sm font-extrabold text-[#111827]">
          <p className="flex items-center justify-between rounded-2xl bg-indigo-50/70 px-4 py-3 text-indigo-700 ring-1 ring-indigo-100">
            <span className="flex items-center gap-2">
              <Ticket size={17} /> 남은 자리
            </span>
            <span>{seatsLeft}명</span>
          </p>
          <p className="flex items-center justify-between rounded-2xl bg-orange-50/70 px-4 py-3 text-orange-600 ring-1 ring-orange-100">
            <span className="flex items-center gap-2">
              <Users size={17} /> 현재 대기
            </span>
            <span>{item.waiting}명</span>
          </p>
          <p className="flex items-center gap-2 rounded-2xl bg-slate-50/80 px-4 py-3 text-[#6B7280] ring-1 ring-slate-100">
            <CalendarDays size={17} className="text-sky-500" />
            {item.schedule}
          </p>
        </div>

        <div className="mt-5 grid grid-cols-2 gap-3">
          <Link to={`/classes/${item.id}`} className="secondary-button">자세히 보기</Link>
          <button className="primary-button h-12 text-sm" disabled={disabled} onClick={() => navigate(target)}>{action}</button>
        </div>
      </div>
    </article>
  );
}
