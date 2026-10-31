import { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import productApi from '../api/productApi.js';
import { useCart } from '../context/CartContext.jsx';
import Spinner from '../components/common/Spinner.jsx';
import { FiArrowLeft, FiShoppingCart, FiTag, FiBox, FiCalendar } from 'react-icons/fi';

export default function ProductDetailPage() {
  const { category, id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    productApi.getById(category, id)
      .then(({ data }) => setProduct(data))
      .catch(() => setError('Product not found.'))
      .finally(() => setLoading(false));
  }, [category, id]);

  if (loading) return <Spinner text="Loading product…" />;

  if (error) return (
    <div className="error-page">
      <h2>404</h2>
      <p>{error}</p>
      <Link to="/products" className="btn btn-primary">Back to Products</Link>
    </div>
  );

  const {
    name, description, price, brand, imageUrl,
    stockQuantity, sku, active, createdAt,
  } = product;

  const displayImage = imageUrl || `https://placehold.co/600x450/e2e8f0/475569?text=${encodeURIComponent(name)}`;

  return (
    <div className="detail-page">
      <button className="btn btn-ghost back-btn" onClick={() => navigate(-1)}>
        <FiArrowLeft /> Back
      </button>

      <div className="detail-container">
        {/* Image */}
        <div className="detail-image-wrapper">
          <img
            src={displayImage}
            alt={name}
            className="detail-image"
            onError={(e) => {
              e.target.src = `https://placehold.co/600x450/e2e8f0/475569?text=${encodeURIComponent(name)}`;
            }}
          />
        </div>

        {/* Info */}
        <div className="detail-info">
          <div className="detail-meta">
            <span className="product-card__category"><FiTag size={13} /> {category}</span>
            {brand && <span className="product-card__brand">{brand}</span>}
          </div>

          <h1 className="detail-name">{name}</h1>
          <p className="detail-price">₹{Number(price).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</p>

          {description && <p className="detail-desc">{description}</p>}

          <div className="detail-attrs">
            {sku && (
              <div className="attr">
                <span className="attr-label">SKU</span>
                <span className="attr-val">{sku}</span>
              </div>
            )}
            <div className="attr">
              <span className="attr-label"><FiBox size={13} /> Stock</span>
              <span className={`attr-val ${stockQuantity === 0 ? 'text-danger' : 'text-success'}`}>
                {stockQuantity > 0 ? `${stockQuantity} available` : 'Out of stock'}
              </span>
            </div>
            <div className="attr">
              <span className="attr-label">Status</span>
              <span className={`badge ${active ? 'badge-success' : 'badge-danger'}`}>
                {active ? 'Active' : 'Unavailable'}
              </span>
            </div>
            {createdAt && (
              <div className="attr">
                <span className="attr-label"><FiCalendar size={13} /> Added</span>
                <span className="attr-val">{new Date(createdAt).toLocaleDateString('en-IN')}</span>
              </div>
            )}
          </div>

          <button
            className="btn btn-primary btn-lg btn-full"
            disabled={stockQuantity === 0 || !active}
            onClick={() => addToCart(product)}
          >
            <FiShoppingCart />
            {stockQuantity > 0 && active ? 'Add to Cart' : 'Unavailable'}
          </button>
        </div>
      </div>
    </div>
  );
}

