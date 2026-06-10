import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import ClassHeroVisual from '../components/ClassHeroVisual';
import CountdownTimer from '../components/CountdownTimer';
import StatusBadge from '../components/StatusBadge';
import { findClass } from '../data/mockClasses';

const totalSeconds = 5 * 60;

export default function ApplicationHold() {
  const { classId } = useParams();
  const navigate = useNavigate();
  const item = findClass(classId);
  const [secondsLeft, setSecondsLeft] = useState(totalSeconds);

  useEffect(() => {
    const timer = window.setInterval(() => setSecondsLeft((seconds) => Math.max(0, seconds - 1)), 1000);
    return () => window.clearInterval(timer);
  }, []);

  return (
    <div className="mx-auto grid max-w-5xl gap-6 lg:grid-cols-[1fr_420px]">
      <section className="space-y-5">
        <ClassHeroVisual item={item} />
        <div className="premium-card p-6">
          <StatusBadge status="HOLDING" />
          <h1 className="mt-4 text-4xl font-black tracking-tight text-[#111827]">신청 기회가 열렸어요.</h1>
          <p className="mt-3 text-base font-bold leading-7 text-[#6B7280]">5분 안에 확정하지 않으면 다음 대기자에게 기회가 넘어갑니다.</p>
        </div>
      </section>

      <aside className="space-y-5">
        <CountdownTimer secondsLeft={secondsLeft} totalSeconds={totalSeconds} />
        <div className="premium-card p-6">
          <h2 className="text-xl font-black text-[#111827]">{item.title}</h2>
          <p className="mt-2 text-sm font-bold text-[#6B7280]">{item.schedule}</p>
          <button className="primary-button mt-6 w-full" onClick={() => navigate('/complete/app-2401')}>수업 신청 확정하기</button>
          <Link to="/" className="secondary-button mt-3 w-full">신청 취소</Link>
        </div>
      </aside>
    </div>
  );
}
