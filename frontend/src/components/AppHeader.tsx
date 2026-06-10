import { BookOpenCheck } from 'lucide-react';
import { Link, NavLink, Outlet } from 'react-router-dom';

const navClass = ({ isActive }: { isActive: boolean }) =>
  `rounded-full px-4 py-2 text-sm font-extrabold transition ${
    isActive ? 'bg-gradient-to-r from-indigo-500 to-violet-500 text-white shadow-[0_10px_24px_rgba(79,70,229,0.22)]' : 'text-[#6B7280] hover:bg-white/80 hover:text-[#111827]'
  }`;

export default function AppHeader() {
  return (
    <div className="min-h-screen bg-[radial-gradient(circle_at_top_left,#FFF1C8_0%,transparent_28%),radial-gradient(circle_at_top_right,#DDEBFF_0%,transparent_30%),radial-gradient(circle_at_bottom_left,#FFE0D6_0%,transparent_26%),linear-gradient(135deg,#FFFDF7_0%,#F7FAFF_45%,#FFF6F0_100%)] text-[#111827]">
      <header className="sticky top-0 z-30 bg-white/18 backdrop-blur-xl">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-5 sm:px-6 lg:px-8">
          <Link to="/" className="flex items-center gap-3">
            <span className="grid h-11 w-11 place-items-center rounded-2xl bg-gradient-to-br from-indigo-500 via-sky-400 to-orange-300 text-white shadow-[0_14px_28px_rgba(79,70,229,0.18)]">
              <BookOpenCheck size={23} />
            </span>
            <span>
              <span className="block text-xl font-black tracking-tight">ClassQueue</span>
              <span className="block text-xs font-bold text-[#6B7280]">인기 수업 선착순 신청</span>
            </span>
          </Link>

          <nav className="hidden items-center rounded-full bg-white/72 p-1 shadow-[0_14px_35px_rgba(15,23,42,0.08)] ring-1 ring-white/70 backdrop-blur-xl md:flex">
            <NavLink to="/" className={navClass}>수업 목록</NavLink>
            <NavLink to="/my" className={navClass}>내 신청 내역</NavLink>
          </nav>

          <Link to="/my" className="secondary-button hidden sm:inline-flex">내 신청 내역</Link>
        </div>
      </header>

      <main className="page-shell">
        <Outlet />
      </main>
    </div>
  );
}
