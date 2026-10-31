import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import productApi from '../../api/productApi.js';
import userApi from '../../api/userApi.js';
import { FiPackage, FiUsers, FiShoppingBag, FiArrowRight } from 'react-icons/fi';
import Spinner from '../../components/common/Spinner.jsx';

export default function AdminDashboard() {
  const [stats, setStats] = useState({ products: 0, users: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.allSettled([
      productApi.getAllActive(),
      userApi.getAll(),
    ]).then(([prodRes, userRes]) => {
      setStats({
        products: prodRes.status === 'fulfilled' ? prodRes.value.data.length : 0,
        users: userRes.status === 'fulfilled' ? userRes.value.data.length : 0,
      });
    }).finally(() => setLoading(false));
  }, []);

  if (loading) return <Spinner text="Loading dashboard…" />;

  return (
    <div className="admin-page">
      <div className="admin-header">
        <h1>Admin Dashboard</h1>
        <p>Manage your online store</p>
      </div>

      {/* Stat Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <FiPackage size={32} className="stat-icon stat-icon--blue" />
          <div>
            <p className="stat-value">{stats.products}</p>
            <p className="stat-label">Active Products</p>
          </div>
        </div>
        <div className="stat-card">
          <FiUsers size={32} className="stat-icon stat-icon--green" />
          <div>
            <p className="stat-value">{stats.users}</p>
            <p className="stat-label">Registered Users</p>
          </div>
        </div>
        <div className="stat-card">
          <FiShoppingBag size={32} className="stat-icon stat-icon--purple" />
          <div>
            <p className="stat-value">0</p>
            <p className="stat-label">Orders (coming soon)</p>
          </div>
        </div>
      </div>

      {/* Quick Links */}
      <div className="admin-links">
        <Link to="/admin/products" className="admin-link-card">
          <FiPackage size={24} />
          <div>
            <h3>Manage Products</h3>
            <p>Create, edit and delete Cassandra product catalog</p>
          </div>
          <FiArrowRight />
        </Link>
        <Link to="/products" className="admin-link-card">
          <FiShoppingBag size={24} />
          <div>
            <h3>View Store</h3>
            <p>See the public product listing</p>
          </div>
          <FiArrowRight />
        </Link>
      </div>
    </div>
  );
}

