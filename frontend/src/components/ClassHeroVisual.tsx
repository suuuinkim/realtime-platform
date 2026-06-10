import type { ClassItem } from '../data/mockClasses';

const coverGradients: Record<string, string> = {
  'spring-boot': 'linear-gradient(135deg,#C7F9CC 0%,#80ED99 45%,#38BDF8 100%)',
  'react-components': 'linear-gradient(135deg,#DDD6FE 0%,#A78BFA 45%,#60A5FA 100%)',
  'sql-tuning': 'linear-gradient(135deg,#FDE68A 0%,#FDBA74 45%,#FB7185 100%)',
  'docker-deploy': 'linear-gradient(135deg,#DBEAFE 0%,#93C5FD 45%,#67E8F9 100%)',
  'kafka-event': 'linear-gradient(135deg,#FED7AA 0%,#FDBA74 45%,#F472B6 100%)',
  'redis-queue': 'linear-gradient(135deg,#FBCFE8 0%,#C4B5FD 45%,#93C5FD 100%)',
};

const keywordMap: Record<string, string> = {
  'spring-boot': 'BACKEND',
  'react-components': 'FRONTEND',
  'sql-tuning': 'SQL',
  'docker-deploy': 'DOCKER',
  'kafka-event': 'KAFKA',
  'redis-queue': 'REDIS',
};

export default function ClassHeroVisual({ item, compact = false }: { item: ClassItem; compact?: boolean }) {
  const gradient = coverGradients[item.id] ?? 'linear-gradient(135deg,#DBEAFE 0%,#A7F3D0 45%,#FDE68A 100%)';
  const keyword = keywordMap[item.id] ?? item.category.toUpperCase();

  return (
    <div
      className={`relative overflow-hidden ${compact ? 'h-40 rounded-[26px]' : 'h-[400px] rounded-[32px]'} p-6 text-white shadow-[inset_0_1px_0_rgba(255,255,255,0.55),0_20px_55px_rgba(79,70,229,0.12)] ring-1 ring-white/60`}
      style={{ background: gradient }}
    >
      <div className="absolute -left-16 -top-20 h-56 w-56 rounded-full bg-white/38 blur-2xl" />
      <div className="absolute right-8 top-8 h-24 w-24 rounded-full bg-white/30 blur-xl" />
      <div className="absolute -bottom-16 right-2 h-48 w-48 rounded-full bg-white/18 blur-2xl" />
      <div className="absolute inset-x-6 bottom-6 border-t border-white/35" />
      <div className="absolute left-6 top-6 rounded-full bg-white/24 px-3 py-1 text-xs font-black text-white shadow-sm ring-1 ring-white/45 backdrop-blur">
        {item.category}
      </div>
      <div className="absolute right-6 top-6 rounded-full bg-white/24 px-3 py-1 text-xs font-black text-white shadow-sm ring-1 ring-white/45 backdrop-blur">
        TICKET
      </div>
      <div className="relative z-10 flex h-full flex-col justify-between">
        <span />
        <div>
          <p className={`${compact ? 'text-4xl' : 'text-7xl'} font-black uppercase leading-none tracking-tight text-white drop-shadow-[0_2px_14px_rgba(15,23,42,0.18)]`}>
            {keyword}
          </p>
          {!compact && <p className="mt-4 max-w-md text-lg font-extrabold leading-7 text-white/92 drop-shadow-sm">{item.title}</p>}
        </div>
      </div>
    </div>
  );
}
