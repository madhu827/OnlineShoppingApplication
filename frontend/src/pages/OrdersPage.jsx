import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  FiPackage,
  FiClock,
  FiCheckCircle,
  FiTruck,
  FiShoppingBag,
  FiArrowRight,
  FiPrinter,
  FiRefreshCw,
  FiSearch,
} from 'react-icons/fi';
import toast from 'react-hot-toast';
import { useAuth } from '../context/AuthContext.jsx';
import { useCart } from '../context/CartContext.jsx';
import orderApi from '../api/orderApi.js';
import Spinner from '../components/common/Spinner.jsx';

export default function OrdersPage() {
  const { user } = useAuth();
  const { addToCart } = useCart();

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL'); // 'ALL' | 'CONFIRMED' | 'DELIVERED'
  const [searchQuery, setSearchQuery] = useState('');

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const data = await orderApi.getMyOrders(user?.username);
      setOrders(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('Failed to load orders', err);
      toast.error('Could not load orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [user]);

  const handleReorder = (item) => {
    addToCart({
      id: item.productId,
      name: item.productName,
      price: item.price,
      imageUrl: item.imageUrl,
      category: item.category,
    });
    toast.success(`Added "${item.productName}" back to your cart!`);
  };

  const handlePrint = (order) => {
    window.print();
  };

  // Filter orders by search and status
  const filteredOrders = orders.filter((order) => {
    const matchesFilter = filter === 'ALL' || (order.status || 'CONFIRMED').toUpperCase() === filter;
    const matchesSearch =
      !searchQuery ||
      order.orderNumber?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      order.items?.some((item) =>
        item.productName?.toLowerCase().includes(searchQuery.toLowerCase())
      );
    return matchesFilter && matchesSearch;
  });

  return (
    <div className="orders-page-container container">
      {/* Page Header */}
      <div className="orders-header">
        <div>
          <h1 className="orders-title">
            <FiPackage className="title-icon" /> Order History
          </h1>
          <p className="orders-subtitle">
            Review your past purchases, track active deliveries, and manage your receipts.
          </p>
        </div>
        <button
          className="btn btn-outline refresh-btn"
          onClick={fetchOrders}
          title="Refresh orders"
        >
          <FiRefreshCw size={16} /> Refresh
        </button>
      </div>

      {/* Filter & Search Bar */}
      <div className="orders-filter-bar">
        <div className="orders-search-input-wrap">
          <FiSearch className="search-icon" />
          <input
            type="text"
            placeholder="Search by Order ID or Product Name…"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div className="orders-status-filters">
          {['ALL', 'CONFIRMED', 'DELIVERED'].map((st) => (
            <button
              key={st}
              className={`filter-pill ${filter === st ? 'active' : ''}`}
              onClick={() => setFilter(st)}
            >
              {st === 'ALL' ? 'All Orders' : st}
            </button>
          ))}
        </div>
      </div>

      {/* Main Content Area */}
      {loading ? (
        <div className="orders-loading">
          <Spinner />
          <p>Loading your order history…</p>
        </div>
      ) : filteredOrders.length === 0 ? (
        <div className="orders-empty-state">
          <div className="empty-package-icon">
            <FiShoppingBag size={56} />
          </div>
          <h3>No Orders Found</h3>
          <p>
            {orders.length === 0
              ? "You haven't placed any orders yet. Start exploring our catalog to make your first purchase!"
              : 'No orders matched your current search or filter criteria.'}
          </p>
          <Link to="/products" className="btn btn-primary btn-lg empty-shop-btn">
            Browse Products <FiArrowRight />
          </Link>
        </div>
      ) : (
        <div className="orders-list">
          {filteredOrders.map((order) => {
            const formattedDate = order.createdAt
              ? new Date(order.createdAt).toLocaleDateString('en-IN', {
                  year: 'numeric',
                  month: 'short',
                  day: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit',
                })
              : 'Recent Order';

            return (
              <div key={order.id || order.orderNumber} className="order-card">
                {/* Order Card Top Bar */}
                <div className="order-card-header">
                  <div className="order-header-left">
                    <div className="order-meta-item">
                      <span className="order-meta-label">ORDER PLACED</span>
                      <span className="order-meta-value">{formattedDate}</span>
                    </div>
                    <div className="order-meta-item">
                      <span className="order-meta-label">TOTAL AMOUNT</span>
                      <span className="order-meta-value total-highlight">
                        ₹{Number(order.totalAmount || 0).toLocaleString('en-IN', {
                          minimumFractionDigits: 2,
                        })}
                      </span>
                    </div>
                    <div className="order-meta-item">
                      <span className="order-meta-label">PAYMENT METHOD</span>
                      <span className="order-meta-value badge-method">
                        {order.paymentMethod || 'CARD'}
                      </span>
                    </div>
                  </div>

                  <div className="order-header-right">
                    <div className="order-id-badge">
                      <span className="order-id-label">ORDER #</span>
                      <span className="order-id-value">{order.orderNumber}</span>
                    </div>
                    <span className="badge badge-success order-status-pill">
                      <FiCheckCircle size={14} /> {order.status || 'CONFIRMED'}
                    </span>
                  </div>
                </div>

                {/* Items in this Order */}
                <div className="order-items-wrapper">
                  {(order.items || []).map((item, idx) => (
                    <div key={idx} className="order-item-row">
                      <img
                        src={
                          item.imageUrl ||
                          `https://placehold.co/100x100?text=${encodeURIComponent(
                            item.productName || 'Product'
                          )}`
                        }
                        alt={item.productName}
                        className="order-item-thumb"
                        onError={(e) => {
                          e.target.src = `https://placehold.co/100x100?text=${encodeURIComponent(
                            item.productName || 'Product'
                          )}`;
                        }}
                      />
                      <div className="order-item-info">
                        <span className="order-item-cat">{item.category || 'Product'}</span>
                        <h4 className="order-item-title">{item.productName}</h4>
                        <div className="order-item-pricing">
                          <span className="item-unit-price">
                            ₹{Number(item.price || 0).toLocaleString('en-IN')} × {item.quantity}
                          </span>
                          <span className="item-subtotal">
                            ₹{Number((item.price || 0) * item.quantity).toLocaleString('en-IN', {
                              minimumFractionDigits: 2,
                            })}
                          </span>
                        </div>
                      </div>

                      <div className="order-item-actions">
                        <button
                          className="btn btn-outline btn-sm reorder-btn"
                          onClick={() => handleReorder(item)}
                          title="Add this item back into your cart"
                        >
                          <FiShoppingBag size={14} /> Buy Again
                        </button>
                      </div>
                    </div>
                  ))}
                </div>

                {/* Card Footer / Shipping & Actions */}
                <div className="order-card-footer">
                  <div className="order-footer-details">
                    {order.transactionId && (
                      <span className="order-txn">
                        Txn ID: <code>{order.transactionId}</code>
                      </span>
                    )}
                    {order.shippingAddress && (
                      <span className="order-address">
                        <FiTruck size={14} /> Ship to: {order.shippingAddress}
                      </span>
                    )}
                  </div>

                  <div className="order-footer-btns">
                    <button
                      className="btn btn-ghost btn-sm print-receipt-btn"
                      onClick={() => handlePrint(order)}
                    >
                      <FiPrinter size={14} /> Print Receipt
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

