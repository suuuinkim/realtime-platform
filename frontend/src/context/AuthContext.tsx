import { createContext, useContext, useState, type ReactNode } from 'react';

interface AuthState {
  token: string | null;
  userId: string | null;
  login: (loginId: string, token: string) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthState>({
  token: null,
  userId: null,
  login: () => {},
  logout: () => {},
});

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));
  const [userId, setUserId] = useState<string | null>(() => localStorage.getItem('userId'));

  function login(loginId: string, newToken: string) {
    localStorage.setItem('token', newToken);
    localStorage.setItem('userId', loginId);
    setToken(newToken);
    setUserId(loginId);
  }

  function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    setToken(null);
    setUserId(null);
  }

  return (
    <AuthContext.Provider value={{ token, userId, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
