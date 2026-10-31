import { Link } from 'react-router-dom';
import { FiShoppingCart, FiTag, FiBox } from 'react-icons/fi';
import { useCart } from '../../context/CartContext.jsx';

export default function ProductCard({ product }) {
  const { addToCart } = useCart();
  const {
    id, name, description, price, category,
    brand, imageUrl, stockQuantity, active,
  } = product;

  const displayImage = imageUrl || `https://placehold.co/400x300/e2e8f0/475569?text=${encodeURIComponent(name)}`;

  return (
    <div className={`product-card ${!active ? 'product-card--inactive' : ''}`}>
      <Link to={`/products/${category}/${id}`} className="product-card__image-link">
        <img
          src={displayImage}
          alt={name}
          className="product-card__image"
          loading="lazy"
          onError={(e) => {
            e.target.src = `https://placehold.co/400x300/e2e8f0/475569?text=${encodeURIComponent(name)}`;
          }}
        />
        {!active && <span className="product-card__badge product-card__badge--inactive">Unavailable</span>}
        {stockQuantity === 0 && active && (
          <span className="product-card__badge product-card__badge--sold-out">Sold Out</span>
        )}
      </Link>

      <div className="product-card__body">
        <div className="product-card__meta">
          <span className="product-card__category">
            <FiTag size={12} /> {category}
          </span>
          {brand && <span className="product-card__brand">{brand}</span>}
        </div>

        <Link to={`/products/${category}/${id}`} className="product-card__name">
          {name}
        </Link>

        {description && (
          <p className="product-card__desc">
            {description.length > 80 ? description.slice(0, 80) + '…' : description}
          </p>
        )}

        <div className="product-card__footer">
          <span className="product-card__price">₹{Number(price).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
          <div className="product-card__stock">
            <FiBox size={13} />
            <span>{stockQuantity > 0 ? `${stockQuantity} in stock` : 'Out of stock'}</span>
          </div>
        </div>

        <button
          className="btn btn-primary btn-full"
          disabled={stockQuantity === 0 || !active}
          onClick={() => addToCart(product)}
        >
          <FiShoppingCart /> {stockQuantity > 0 && active ? 'Add to Cart' : 'Unavailable'}
        </button>
      </div>
    </div>
  );
}
