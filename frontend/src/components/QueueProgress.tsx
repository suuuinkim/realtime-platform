export default function QueueProgress({ value }: { value: number }) {
  return (
    <div>
      <div className="h-3 overflow-hidden rounded-full bg-white/70 ring-1 ring-[rgba(17,24,39,0.06)]">
        <div className="h-full rounded-full bg-gradient-to-r from-indigo-500 via-sky-400 to-orange-300 transition-all duration-700" style={{ width: `${value}%` }} />
      </div>
      <div className="mt-2 flex justify-between text-xs font-black text-[#6B7280]">
        <span>대기 진행률</span>
        <span>{value}%</span>
      </div>
    </div>
  );
}
