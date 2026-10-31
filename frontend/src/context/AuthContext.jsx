import { createContext, useContext, useState, useCallback, useEffect } from 'react';
import authApi from '../api/authApi.js';
import toast from 'react-hot-toast';

/**
 * AuthContext provides global authentication state and helpers to the
 * entire React application.
 *
 * Stored in localStorage:
 *  - accessToken  : short-lived JWT
 *  - refreshToken : long-lived opaque token
 *  - user         : JSON serialised user object { id, username, email, roles }
 */
const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const stored = localStorage.getItem('user');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });

  const [loading, setLoading] = useState(false);

  // ── Derived helpers ──────────────────────────────────────────────────────
  const isAuthenticated = Boolean(user && localStorage.getItem('accessToken'));
  const isAdmin = user?.roles?.includes('ROLE_ADMIN') ?? false;

  // ── Actions ──────────────────────────────────────────────────────────────

  const persistSession = useCallback((data) => {
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    const userObj = {
      id: data.userId,
      username: data.username,
      email: data.email,
      roles: data.roles,
    };
    localStorage.setItem('user', JSON.stringify(userObj));
    setUser(userObj);
  }, []);

  const register = useCallback(async (formData) => {
    setLoading(true);
    try {
      const { data } = await authApi.register(formData);
      persistSession(data);
      toast.success(`Welcome, ${data.username}! Account created.`);
      return { success: true };
    } catch (err) {
      let msg = 'Registration failed. Please try again.';
      if (!err.response) {
        msg = 'Backend server is not running! Please start Spring Boot on port 8080.';
      } else if (err.response.status === 502 || err.response.status === 504 || err.response.status === 500 && !err.response.data?.message) {
        msg = 'Cannot connect to backend server at http://localhost:8080. Is Spring Boot running?';
      } else if (err.response?.data?.message) {
        msg = err.response.data.message;
        if (err.response.data.fieldErrors) {
          const firstErr = Object.values(err.response.data.fieldErrors)[0];
          if (firstErr) msg += `: ${firstErr}`;
        }
      }
      toast.error(msg, { duration: 5000 });
      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  }, [persistSession]);

  const login = useCallback(async (credentials) => {
    setLoading(true);
    try {
      const { data } = await authApi.login(credentials);
      persistSession(data);
      toast.success(`Welcome back, ${data.username}!`);
      return { success: true, roles: data.roles };
    } catch (err) {
      const msg =
        err.response?.data?.message || 'Invalid credentials. Please try again.';
      toast.error(msg);
      return { success: false, message: msg };
    } finally {
      setLoading(false);
    }
  }, [persistSession]);

  const logout = useCallback(async () => {
    try {
      await authApi.logout();
    } catch {
      // Ignore – clear local state regardless
    }
    localStorage.clear();
    setUser(null);
    toast.success('Logged out successfully.');
  }, []);

  return (
    <AuthContext.Provider
      value={{ user, isAuthenticated, isAdmin, loading, register, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

/** Convenience hook — use inside any component. */
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>');
  return ctx;
}

