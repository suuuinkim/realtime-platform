import type { ApplicationStatus, ClassStatus } from '../data/mockClasses';

const styles: Record<string, string> = {
  '대기실 오픈': 'bg-indigo-50 text-indigo-600 ring-indigo-100',
  '신청 가능': 'bg-emerald-50 text-emerald-600 ring-emerald-100',
  '마감 임박': 'bg-orange-50 text-orange-600 ring-orange-100',
  '신청 예정': 'bg-sky-50 text-sky-600 ring-sky-100',
  '마감': 'bg-slate-100 text-slate-500 ring-slate-200',
  WAITING: 'bg-indigo-50 text-indigo-600 ring-indigo-100',
  HOLDING: 'bg-orange-50 text-orange-600 ring-orange-100',
  CONFIRMED: 'bg-emerald-50 text-emerald-600 ring-emerald-100',
  EXPIRED: 'bg-slate-100 text-slate-500 ring-slate-200',
  CANCELLED: 'bg-slate-100 text-slate-500 ring-slate-200',
};

export default function StatusBadge({ status }: { status: ClassStatus | ApplicationStatus }) {
  return <span className={`inline-flex rounded-full px-3 py-1 text-xs font-black ring-1 ${styles[status]}`}>{status}</span>;
}
