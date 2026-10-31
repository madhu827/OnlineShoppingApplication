import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import userApi from '../api/userApi.js';
import Spinner from '../components/common/Spinner.jsx';
import { FiUser, FiMail, FiPhone, FiMapPin, FiShield, FiPackage, FiArrowRight } from 'react-icons/fi';

export default function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    userApi.getMyProfile()
      .then(({ data }) => setProfile(data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Spinner text="Loading profile…" />;
  if (!profile) return <p className="text-center">Could not load profile.</p>;

  const { username, email, firstName, lastName, phone, address, roles, createdAt } = profile;

  return (
    <div className="profile-page">
      <div className="profile-card">
        {/* Avatar */}
        <div className="profile-avatar">
          {(firstName?.[0] || username?.[0] || 'U').toUpperCase()}
        </div>

        <h1 className="profile-name">{firstName} {lastName}</h1>
        <p className="profile-username">@{username}</p>

        {/* Roles */}
        <div className="profile-roles">
          {roles?.map((r) => (
            <span key={r} className={`badge ${r === 'ROLE_ADMIN' ? 'badge-warning' : 'badge-info'}`}>
              <FiShield size={11} /> {r.replace('ROLE_', '')}
            </span>
          ))}
        </div>

        {/* Details */}
        <div className="profile-details">
          <div className="profile-detail-row">
            <FiMail />
            <span>{email}</span>
          </div>
          {phone && (
            <div className="profile-detail-row">
              <FiPhone />
              <span>{phone}</span>
            </div>
          )}
          {address && (
            <div className="profile-detail-row">
              <FiMapPin />
              <span>{address}</span>
            </div>
          )}
          <div className="profile-detail-row">
            <FiUser />
            <span>Member since {new Date(createdAt).toLocaleDateString('en-IN', { year: 'numeric', month: 'long' })}</span>
          </div>
        </div>

        <div style={{ marginTop: '20px', width: '100%' }}>
          <Link to="/orders" className="btn btn-primary btn-full">
            <FiPackage size={16} /> View Order History <FiArrowRight size={14} />
          </Link>
        </div>
      </div>
    </div>
  );
}

