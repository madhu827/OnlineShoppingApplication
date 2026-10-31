import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { FiEye, FiEyeOff, FiLogIn } from 'react-icons/fi';

export default function LoginForm() {
  const { login, loading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const returnTo = location.state?.from?.pathname || '/';

  const [form, setForm] = useState({ usernameOrEmail: '', password: '' });
  const [showPw, setShowPw] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const e = {};
    if (!form.usernameOrEmail.trim()) e.usernameOrEmail = 'Username or email is required';
    if (!form.password) e.password = 'Password is required';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    if (errors[e.target.name]) setErrors((prev) => ({ ...prev, [e.target.name]: '' }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    const result = await login(form);
    if (result.success) {
      const isAdmin = result.roles?.includes('ROLE_ADMIN');
      navigate(isAdmin ? '/admin' : returnTo, { replace: true });
    }
  };

  return (
    <div className="auth-card">
      <div className="auth-header">
        <h1>Welcome back</h1>
        <p>Sign in to your account</p>
      </div>

      <form onSubmit={handleSubmit} className="auth-form" noValidate>
        {/* Username / Email */}
        <div className="form-group">
          <label htmlFor="usernameOrEmail">Username or Email</label>
          <input
            id="usernameOrEmail"
            name="usernameOrEmail"
            type="text"
            autoComplete="username"
            placeholder="john_doe or john@email.com"
            value={form.usernameOrEmail}
            onChange={handleChange}
            className={errors.usernameOrEmail ? 'input-error' : ''}
          />
          {errors.usernameOrEmail && (
            <span className="field-error">{errors.usernameOrEmail}</span>
          )}
        </div>

        {/* Password */}
        <div className="form-group">
          <label htmlFor="password">Password</label>
          <div className="input-group">
            <input
              id="password"
              name="password"
              type={showPw ? 'text' : 'password'}
              autoComplete="current-password"
              placeholder="••••••••"
              value={form.password}
              onChange={handleChange}
              className={errors.password ? 'input-error' : ''}
            />
            <button
              type="button"
              className="input-icon-btn"
              onClick={() => setShowPw((v) => !v)}
              aria-label="Toggle password visibility"
            >
              {showPw ? <FiEyeOff /> : <FiEye />}
            </button>
          </div>
          {errors.password && (
            <span className="field-error">{errors.password}</span>
          )}
        </div>

        <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
          {loading ? 'Signing in…' : (
            <><FiLogIn /> Sign In</>
          )}
        </button>
      </form>

      <p className="auth-footer">
        Don't have an account?{' '}
        <Link to="/register" className="link">Create one</Link>
      </p>
    </div>
  );
}

