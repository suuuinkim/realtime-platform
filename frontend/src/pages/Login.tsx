import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [loginId, setLoginId] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const res = await fetch('/api/v1/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ loginId, password }),
      });
      if (!res.ok) throw new Error('아이디 또는 비밀번호를 확인하세요.');
      const data = await res.json();
      login(loginId, data.accessToken);
      navigate('/');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : '로그인 실패');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-indigo-50 to-violet-50 px-4">
      <div className="w-full max-w-sm rounded-3xl bg-white/80 p-8 shadow-[0_24px_64px_rgba(79,70,229,0.12)] ring-1 ring-white/80 backdrop-blur">
        <h1 className="text-3xl font-black tracking-tight text-[#111827]">ClassQueue</h1>
        <p className="mt-2 text-sm font-bold text-[#6B7280]">수업 신청 대기열 플랫폼</p>
        <form className="mt-8 space-y-4" onSubmit={handleSubmit}>
          <div>
            <label className="block text-xs font-black text-[#6B7280]">아이디</label>
            <input
              className="mt-1 w-full rounded-2xl border-0 bg-white/70 px-4 py-3 text-sm font-bold text-[#111827] ring-1 ring-[rgba(17,24,39,0.1)] focus:outline-none focus:ring-2 focus:ring-indigo-400"
              value={loginId}
              onChange={(e) => setLoginId(e.target.value)}
              placeholder="admin"
              autoComplete="username"
            />
          </div>
          <div>
            <label className="block text-xs font-black text-[#6B7280]">비밀번호</label>
            <input
              type="password"
              className="mt-1 w-full rounded-2xl border-0 bg-white/70 px-4 py-3 text-sm font-bold text-[#111827] ring-1 ring-[rgba(17,24,39,0.1)] focus:outline-none focus:ring-2 focus:ring-indigo-400"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••"
              autoComplete="current-password"
            />
          </div>
          {error && <p className="text-xs font-bold text-red-500">{error}</p>}
          <button
            type="submit"
            disabled={loading}
            className="primary-button w-full disabled:opacity-50"
          >
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>
      </div>
    </div>
  );
}
