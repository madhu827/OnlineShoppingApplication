import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import productApi from '../api/productApi.js';
import ProductCard from '../components/product/ProductCard.jsx';
import Spinner from '../components/common/Spinner.jsx';
import { FiArrowRight, FiShoppingBag, FiShield, FiTruck } from 'react-icons/fi';

const FEATURES = [
  { icon: FiTruck, title: 'Free Delivery', desc: 'On all orders above ₹500' },
  { icon: FiShield, title: 'Secure Payments', desc: 'JWT-secured checkout' },
  { icon: FiShoppingBag, title: 'Easy Returns', desc: '30-day return policy' },
];

export default function HomePage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    productApi.getAllActive()
      .then(({ data }) => setProducts(data.slice(0, 8)))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="home-page">
      {/* Hero */}
      <section className="hero">
        <div className="hero-content">
          <h1 className="hero-title">
            Shop Smarter.<br />
            <span className="hero-accent">Live Better.</span>
          </h1>
          <p className="hero-subtitle">
            Discover thousands of products across electronics, clothing, books, and more.
          </p>
          <div className="hero-actions">
            <Link to="/products" className="btn btn-primary btn-lg">
              Shop Now <FiArrowRight />
            </Link>
            <Link to="/register" className="btn btn-outline btn-lg">
              Join Free
            </Link>
          </div>
        </div>
        <div className="hero-illustration">🛍️</div>
      </section>

      {/* Features */}
      <section className="features">
        {FEATURES.map(({ icon: Icon, title, desc }) => (
          <div key={title} className="feature-card">
            <Icon size={32} className="feature-icon" />
            <h3>{title}</h3>
            <p>{desc}</p>
          </div>
        ))}
      </section>

      {/* Featured Products */}
      <section className="section">
        <div className="section-header">
          <h2>Featured Products</h2>
          <Link to="/products" className="link">View all <FiArrowRight /></Link>
        </div>

        {loading ? (
          <Spinner text="Loading products…" />
        ) : products.length === 0 ? (
          <div className="empty-state">
            <p>No products available yet.</p>
            <Link to="/admin/products" className="btn btn-primary">Add Products</Link>
          </div>
        ) : (
          <div className="product-grid">
            {products.map((p) => <ProductCard key={p.id} product={p} />)}
          </div>
        )}
      </section>
    </div>
  );
}

