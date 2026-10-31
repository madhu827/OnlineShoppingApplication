import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import { useCart } from '../../context/CartContext.jsx';
import {
  FiShoppingCart,
  FiUser,
  FiLogOut,
  FiLogIn,
  FiSettings,
  FiPackage,
} from 'react-icons/fi';

export default function Navbar() {
  const { isAuthenticated, isAdmin, user, logout } = useAuth();
  const { totalItems, setIsCartOpen } = useCart();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        {/* Brand */}
        <Link to="/" className="navbar-brand">
          <FiShoppingCart className="brand-icon" />
          <span>ShopApp</span>
        </Link>

        {/* Nav Links */}
        <ul className="navbar-links">
          <li>
            <NavLink to="/" end className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              Home
            </NavLink>
          </li>
          <li>
            <NavLink to="/products" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              Products
            </NavLink>
          </li>
          {isAuthenticated && (
            <li>
              <NavLink to="/orders" className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
                <FiPackage size={15} /> Orders
              </NavLink>
            </li>
          )}
          {isAdmin && (
            <li>
              <NavLink to="/admin" className={({ isActive }) => isActive ? 'nav-link active admin-link' : 'nav-link admin-link'}>
                <FiSettings size={14} /> Admin
              </NavLink>
            </li>
          )}
        </ul>

        {/* Auth & Cart Actions */}
        <div className="navbar-auth">
          {/* Cart Trigger Button with badge */}
          <button
            className="navbar-cart-btn"
            onClick={() => setIsCartOpen(true)}
            aria-label="View Shopping Cart"
            title="Shopping Cart"
          >
            <FiShoppingCart size={20} />
            {totalItems > 0 && (
              <span className="cart-badge">{totalItems}</span>
            )}
          </button>

          {isAuthenticated ? (
            <>
              <Link to="/profile" className="btn btn-ghost">
                <FiUser size={16} />
                <span>{user?.username}</span>
              </Link>
              <button onClick={handleLogout} className="btn btn-outline-danger">
                <FiLogOut size={16} />
                <span>Logout</span>
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="btn btn-ghost">
                <FiLogIn size={16} /> Login
              </Link>
              <Link to="/register" className="btn btn-primary">
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
