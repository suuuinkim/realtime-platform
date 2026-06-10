function format(seconds: number) {
  const minutes = Math.floor(seconds / 60).toString().padStart(2, '0');
  const remain = (seconds % 60).toString().padStart(2, '0');
  return `${minutes}:${remain}`;
}

export default function CountdownTimer({ secondsLeft, totalSeconds }: { secondsLeft: number; totalSeconds: number }) {
  const percentage = Math.max(0, Math.min(100, (secondsLeft / totalSeconds) * 100));

  return (
    <div className="premium-card p-8 text-center">
      <div className="mx-auto grid h-48 w-48 place-items-center rounded-full bg-gradient-to-br from-white/90 to-indigo-50/80 shadow-[inset_0_1px_0_rgba(255,255,255,0.9),0_22px_48px_rgba(79,70,229,0.14)] ring-1 ring-white/80">
        <div>
          <p className="text-xs font-black text-indigo-500">5분 안에 확정</p>
          <p className="mt-2 text-5xl font-black tracking-tight text-[#111827]">{format(secondsLeft)}</p>
        </div>
      </div>
      <p className="mt-6 text-sm font-black text-[#6B7280]">남은 신청 시간</p>
      <div className="mt-4 h-3 overflow-hidden rounded-full bg-white/70 ring-1 ring-[rgba(17,24,39,0.06)]">
        <div className="h-full rounded-full bg-gradient-to-r from-orange-400 via-pink-400 to-indigo-500 transition-all duration-500" style={{ width: `${percentage}%` }} />
      </div>
    </div>
  );
}
