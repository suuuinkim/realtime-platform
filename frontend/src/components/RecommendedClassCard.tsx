import { Link } from 'react-router-dom';
import type { ClassItem } from '../data/mockClasses';
import ClassHeroVisual from './ClassHeroVisual';
import StatusBadge from './StatusBadge';

export default function RecommendedClassCard({ item }: { item: ClassItem }) {
  return (
    <article className="overflow-hidden rounded-[26px] bg-white/78 shadow-[0_18px_48px_rgba(15,23,42,0.07)] ring-1 ring-white/70 backdrop-blur-md">
      <div className="p-3 pb-0">
        <ClassHeroVisual item={item} compact />
      </div>
      <div className="p-5">
        <div className="flex items-center justify-between gap-2">
          <span className="text-xs font-black text-indigo-600">{item.category}</span>
          <StatusBadge status={item.status} />
        </div>
        <h3 className="mt-3 text-lg font-black leading-tight text-[#111827]">{item.title}</h3>
        <p className="mt-2 text-sm font-bold text-[#6B7280]">대기 {item.waiting}명 · {item.schedule}</p>
        <Link to={`/classes/${item.id}`} className="secondary-button mt-4 w-full">자세히 보기</Link>
      </div>
    </article>
  );
}
