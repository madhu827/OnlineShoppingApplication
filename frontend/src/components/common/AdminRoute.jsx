import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';

/**
 * Wraps routes that require the ROLE_ADMIN role.
 * Non-admin authenticated users see a 403 redirect; guests go to /login.
 */
export default function AdminRoute() {
  const { isAuthenticated, isAdmin } = useAuth();

  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (!isAdmin) return <Navigate to="/" replace />;

  return <Outlet />;
}

