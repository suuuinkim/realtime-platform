import { CheckCircle2 } from 'lucide-react';
import { useParams } from 'react-router-dom';
import ApplicationPanel from '../components/ApplicationPanel';
import ClassHeroVisual from '../components/ClassHeroVisual';
import RecommendedClassCard from '../components/RecommendedClassCard';
import StatusBadge from '../components/StatusBadge';
import { classes, findClass } from '../data/mockClasses';

export default function ClassDetail() {
  const { id } = useParams();
  const item = findClass(id);
  const recommendations = classes.filter((classItem) => classItem.id !== item.id).slice(2, 5);

  return (
    <div className="grid gap-8 lg:grid-cols-[minmax(0,1fr)_390px]">
      <div className="space-y-9">
        <section className="grid gap-8 lg:grid-cols-[1fr_1fr] lg:items-center">
          <div>
            <div className="flex flex-wrap items-center gap-3">
              <span className="rounded-full bg-white/70 px-4 py-2 text-sm font-black text-indigo-600 shadow-sm ring-1 ring-white/70 backdrop-blur">{item.category}</span>
              <StatusBadge status={item.status} />
            </div>
            <h1 className="mt-5 text-5xl font-black leading-tight tracking-tight text-[#111827]">{item.title}</h1>
            <p className="mt-4 text-lg font-bold leading-8 text-[#6B7280]">{item.shortDescription}</p>
            <div className="mt-6 flex flex-wrap gap-3 text-sm font-extrabold text-[#111827]">
              <span>{item.instructor} 강사</span>
              <span className="text-[#D1D5DB]">·</span>
              <span>{item.schedule}</span>
            </div>
          </div>
          <ClassHeroVisual item={item} />
        </section>

        <InfoSection title="이런 분께 추천">
          <div className="grid gap-3 md:grid-cols-3">
            {item.recommendedFor.map((copy) => (
              <PointCard key={copy}>{copy}</PointCard>
            ))}
          </div>
        </InfoSection>

        <InfoSection title="수업에서 다루는 내용">
          <div className="grid gap-3 md:grid-cols-2">
            {item.curriculum.map((copy) => (
              <PointCard key={copy}>{copy}</PointCard>
            ))}
          </div>
        </InfoSection>

        <InfoSection title="신청 방식">
          <div className="grid gap-4 md:grid-cols-3">
            {[
              ['1', '대기열 입장', '신청 시간이 되면 대기실에 들어갑니다.'],
              ['2', '내 차례 대기', '순번이 줄어드는 동안 화면에서 상태를 확인합니다.'],
              ['3', '5분 안에 신청 확정', '기회가 열리면 제한 시간 안에 확정합니다.'],
            ].map(([no, title, copy]) => (
              <div key={no} className="premium-card p-5">
                <span className="grid h-10 w-10 place-items-center rounded-2xl bg-gradient-to-br from-indigo-500 to-violet-500 text-lg font-black text-white shadow-[0_12px_24px_rgba(79,70,229,0.18)]">{no}</span>
                <h3 className="mt-4 text-lg font-black text-[#111827]">{title}</h3>
                <p className="mt-2 text-sm font-bold leading-6 text-[#6B7280]">{copy}</p>
              </div>
            ))}
          </div>
        </InfoSection>

        <InfoSection title="함께 보면 좋은 수업">
          <div className="grid gap-5 md:grid-cols-3">
            {recommendations.map((recommendation) => (
              <RecommendedClassCard key={recommendation.id} item={recommendation} />
            ))}
          </div>
        </InfoSection>
      </div>

      <ApplicationPanel item={item} />
    </div>
  );
}

function InfoSection({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <section>
      <h2 className="section-title">{title}</h2>
      <div className="mt-4">{children}</div>
    </section>
  );
}

function PointCard({ children }: { children: React.ReactNode }) {
  return (
    <div className="premium-card flex gap-3 p-5">
      <CheckCircle2 className="mt-0.5 shrink-0 text-emerald-500" size={20} />
      <p className="text-sm font-bold leading-6 text-[#111827]">{children}</p>
    </div>
  );
}
