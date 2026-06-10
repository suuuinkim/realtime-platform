import { DoorOpen, Search, Sparkles, Users } from 'lucide-react';
import { useMemo, useState } from 'react';
import ClassCard from '../components/ClassCard';
import { classes, type Category, type ClassStatus } from '../data/mockClasses';

const categories = ['전체', '백엔드', '프론트엔드', '데이터'] as const;
const statuses = ['전체', '대기실 오픈', '신청 가능', '마감 임박', '신청 예정', '마감'] as const;

export default function Home() {
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState<(typeof categories)[number]>('전체');
  const [status, setStatus] = useState<(typeof statuses)[number]>('전체');

  const filtered = useMemo(
    () =>
      classes.filter((item) => {
        const matchesQuery = item.title.includes(query) || item.instructor.includes(query);
        const matchesCategory = category === '전체' || item.category === (category as Category);
        const matchesStatus = status === '전체' || item.status === (status as ClassStatus);
        return matchesQuery && matchesCategory && matchesStatus;
      }),
    [category, query, status],
  );

  return (
    <div className="space-y-9">
      <section className="grid gap-8 py-8 lg:grid-cols-[1fr_390px] lg:items-end">
        <div>
          <span className="inline-flex items-center gap-2 rounded-full bg-white/70 px-4 py-2 text-sm font-black text-indigo-600 shadow-sm ring-1 ring-white/70 backdrop-blur">
            <Sparkles size={16} /> ClassQueue
          </span>
          <h1 className="mt-6 max-w-3xl text-5xl font-black leading-tight tracking-tight text-[#111827] md:text-6xl">
            인기 수업 신청을 더 공정하게
          </h1>
          <p className="mt-5 max-w-2xl text-lg font-bold leading-8 text-[#6B7280]">
            대기열 순서대로 입장하고, 내 차례가 오면 5분 안에 신청을 확정하세요.
          </p>
        </div>

        <div className="rounded-3xl bg-white/70 p-6 shadow-xl ring-1 ring-white/60 backdrop-blur-xl">
          <div className="flex items-center justify-between">
            <p className="text-sm font-black text-[#6B7280]">오늘 열린 대기실</p>
            <span className="grid h-10 w-10 place-items-center rounded-2xl bg-indigo-50 text-indigo-600">
              <DoorOpen size={20} />
            </span>
          </div>
          <p className="mt-2 text-5xl font-black text-indigo-600">3개</p>
          <div className="mt-5 space-y-3 text-sm font-bold text-[#111827]">
            <p className="rounded-2xl bg-white/68 p-3 ring-1 ring-[rgba(17,24,39,0.06)]">가장 인기 있는 수업: Spring Boot 과정</p>
            <p className="flex items-center gap-2 rounded-2xl bg-orange-50/80 p-3 text-orange-600 ring-1 ring-orange-100">
              <Users size={17} /> 현재 총 대기자 342명
            </p>
          </div>
        </div>
      </section>

      <section className="space-y-4 rounded-[28px] bg-white/44 p-4 shadow-[0_18px_48px_rgba(15,23,42,0.06)] ring-1 ring-white/60 backdrop-blur-xl">
        <label className="relative block max-w-xl">
          <Search className="absolute left-5 top-1/2 -translate-y-1/2 text-[#6B7280]" size={20} />
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="수업명 또는 강사명 검색"
            className="h-14 w-full rounded-full bg-white/80 pl-14 pr-5 font-bold text-[#111827] outline-none ring-1 ring-[rgba(17,24,39,0.08)] backdrop-blur placeholder:text-[#9CA3AF] focus:ring-2 focus:ring-indigo-400"
          />
        </label>
        <div className="flex flex-wrap gap-2">
          <FilterGroup values={categories} selected={category} onSelect={setCategory} />
          <span className="mx-1 hidden h-9 w-px bg-[rgba(17,24,39,0.08)] sm:block" />
          <FilterGroup values={statuses} selected={status} onSelect={setStatus} />
        </div>
      </section>

      <section className="grid gap-7 md:grid-cols-2 xl:grid-cols-3">
        {filtered.map((item) => (
          <ClassCard key={item.id} item={item} />
        ))}
      </section>
    </div>
  );
}

function FilterGroup<T extends readonly string[]>({ values, selected, onSelect }: { values: T; selected: T[number]; onSelect: (value: T[number]) => void }) {
  return (
    <>
      {values.map((value) => (
        <button
          key={value}
          onClick={() => onSelect(value)}
          className={`rounded-full px-4 py-2 text-sm font-extrabold transition ${
            selected === value ? 'bg-gradient-to-r from-indigo-500 to-violet-500 text-white shadow-[0_10px_24px_rgba(79,70,229,0.18)]' : 'bg-white/70 text-[#6B7280] ring-1 ring-white/60 hover:bg-white hover:text-[#111827]'
          }`}
        >
          {value}
        </button>
      ))}
    </>
  );
}
