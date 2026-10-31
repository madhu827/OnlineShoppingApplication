import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { FiEye, FiEyeOff, FiUserPlus } from 'react-icons/fi';

// Extracted outside the component to prevent re-mounting inputs on every keystroke
function FormField({ id, label, type = 'text', placeholder, required, value, onChange, error }) {
  return (
    <div className="form-group">
      <label htmlFor={id}>
        {label} {required && <span className="required">*</span>}
      </label>
      <input
        id={id}
        name={id}
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        className={error ? 'input-error' : ''}
        autoComplete={id}
      />
      {error && <span className="field-error">{error}</span>}
    </div>
  );
}

export default function RegisterForm() {
  const { register, loading } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phone: '',
    address: '',
  });

  const [showPw, setShowPw] = useState(false);
  const [errors, setErrors] = useState({});

  const validate = () => {
    const e = {};
    if (!form.username.trim() || form.username.length < 3)
      e.username = 'Username must be at least 3 characters';
    if (!form.email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
      e.email = 'A valid email is required';
    if (!form.password || form.password.length < 8)
      e.password = 'Password must be at least 8 characters';
    if (!form.firstName.trim()) e.firstName = 'First name is required';
    if (!form.lastName.trim()) e.lastName = 'Last name is required';
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
    const result = await register(form);
    if (result.success) navigate('/', { replace: true });
  };

  return (
    <div className="auth-card auth-card--wide">
      <div className="auth-header">
        <h1>Create Account</h1>
        <p>Join thousands of happy shoppers</p>
      </div>

      <form onSubmit={handleSubmit} className="auth-form" noValidate>
        <div className="form-row">
          <FormField
            id="firstName"
            label="First Name"
            placeholder="John"
            required
            value={form.firstName}
            onChange={handleChange}
            error={errors.firstName}
          />
          <FormField
            id="lastName"
            label="Last Name"
            placeholder="Doe"
            required
            value={form.lastName}
            onChange={handleChange}
            error={errors.lastName}
          />
        </div>

        <FormField
          id="username"
          label="Username"
          placeholder="john_doe"
          required
          value={form.username}
          onChange={handleChange}
          error={errors.username}
        />
        <FormField
          id="email"
          label="Email"
          type="email"
          placeholder="john@example.com"
          required
          value={form.email}
          onChange={handleChange}
          error={errors.email}
        />

        {/* Password with toggle */}
        <div className="form-group">
          <label htmlFor="password">
            Password <span className="required">*</span>
          </label>
          <div className="input-group">
            <input
              id="password"
              name="password"
              type={showPw ? 'text' : 'password'}
              placeholder="Min 8 characters"
              value={form.password}
              onChange={handleChange}
              className={errors.password ? 'input-error' : ''}
              autoComplete="new-password"
            />
            <button
              type="button"
              className="input-icon-btn"
              onClick={() => setShowPw((v) => !v)}
            >
              {showPw ? <FiEyeOff /> : <FiEye />}
            </button>
          </div>
          {errors.password && <span className="field-error">{errors.password}</span>}
        </div>

        <FormField
          id="phone"
          label="Phone (optional)"
          placeholder="+91 98765 43210"
          value={form.phone}
          onChange={handleChange}
          error={errors.phone}
        />
        <FormField
          id="address"
          label="Address (optional)"
          placeholder="123 Main St, City"
          value={form.address}
          onChange={handleChange}
          error={errors.address}
        />

        <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
          {loading ? 'Creating account…' : <><FiUserPlus /> Create Account</>}
        </button>
      </form>

      <p className="auth-footer">
        Already have an account?{' '}
        <Link to="/login" className="link">Sign in</Link>
      </p>
    </div>
  );
}
