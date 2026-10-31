import { useState } from 'react';
import { useCart } from '../../context/CartContext.jsx';
import { FiX, FiTrash2, FiPlus, FiMinus, FiShoppingBag, FiArrowRight, FiCheck } from 'react-icons/fi';
import PaymentModal from '../payment/PaymentModal.jsx';

export default function CartDrawer() {
  const {
    items,
    isCartOpen,
    setIsCartOpen,
    totalItems,
    totalPrice,
    updateQuantity,
    removeFromCart,
    clearCart,
  } = useCart();

  const [isPaymentOpen, setIsPaymentOpen] = useState(false);

  if (!isCartOpen && !isPaymentOpen) return null;

  const freeShippingThreshold = 1000;
  const progressPercent = Math.min((totalPrice / freeShippingThreshold) * 100, 100);
  const deliveryFee = totalPrice >= freeShippingThreshold || totalPrice === 0 ? 0 : 99;
  const finalTotal = totalPrice + deliveryFee;

  const handleOpenPayment = () => {
    setIsPaymentOpen(true);
  };

  const handlePaymentComplete = () => {
    clearCart();
    setIsCartOpen(false);
  };

  return (
    <>
      {isCartOpen && (
        <div className="cart-backdrop" onClick={() => setIsCartOpen(false)}>
          <aside className="cart-drawer" onClick={(e) => e.stopPropagation()}>
            {/* Header */}
            <div className="cart-header">
              <div className="cart-header-title">
                <FiShoppingBag size={20} />
                <h2>Your Cart ({totalItems})</h2>
              </div>
              <button
                className="cart-close-btn"
                onClick={() => setIsCartOpen(false)}
                aria-label="Close cart"
              >
                <FiX size={20} />
              </button>
            </div>

            {/* Free Shipping Banner */}
            <div className="cart-shipping-banner">
              {totalPrice >= freeShippingThreshold ? (
                <p className="shipping-unlocked">
                  <FiCheck /> You unlocked <strong>FREE Delivery</strong>!
                </p>
              ) : (
                <p>
                  Add ₹{(freeShippingThreshold - totalPrice).toLocaleString('en-IN')} more for <strong>FREE Delivery</strong>!
                </p>
              )}
              <div className="shipping-progress-bar">
                <div
                  className="shipping-progress-fill"
                  style={{ width: `${progressPercent}%` }}
                />
              </div>
            </div>

            {/* Cart Item List */}
            {items.length === 0 ? (
              <div className="cart-empty">
                <div className="empty-cart-icon">🛒</div>
                <h3>Your cart is empty</h3>
                <p>Looks like you haven't added anything to your cart yet.</p>
                <button
                  className="btn btn-primary"
                  onClick={() => setIsCartOpen(false)}
                >
                  Explore Products
                </button>
              </div>
            ) : (
              <div className="cart-items-list">
                {items.map((item) => (
                  <div key={item.id} className="cart-item">
                    <img
                      src={item.imageUrl || `https://placehold.co/100x100?text=${encodeURIComponent(item.name)}`}
                      alt={item.name}
                      className="cart-item-img"
                      onError={(e) => {
                        e.target.src = `https://placehold.co/100x100?text=${encodeURIComponent(item.name)}`;
                      }}
                    />
                    <div className="cart-item-details">
                      <h4 className="cart-item-name">{item.name}</h4>
                      <span className="cart-item-category">{item.category}</span>
                      <div className="cart-item-price">
                        ₹{Number(item.price).toLocaleString('en-IN')}
                      </div>
                      <div className="cart-item-actions">
                        <div className="quantity-controller">
                          <button
                            onClick={() => updateQuantity(item.id, item.quantity - 1)}
                            className="qty-btn"
                            aria-label="Decrease quantity"
                          >
                            <FiMinus size={12} />
                          </button>
                          <span className="qty-number">{item.quantity}</span>
                          <button
                            onClick={() => updateQuantity(item.id, item.quantity + 1)}
                            className="qty-btn"
                            aria-label="Increase quantity"
                          >
                            <FiPlus size={12} />
                          </button>
                        </div>
                        <button
                          className="cart-remove-btn"
                          onClick={() => removeFromCart(item.id)}
                          title="Remove item"
                        >
                          <FiTrash2 size={16} />
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            {/* Footer with Summary & Checkout */}
            {items.length > 0 && (
              <div className="cart-footer">
                <div className="cart-summary-row">
                  <span>Subtotal:</span>
                  <span>₹{totalPrice.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
                </div>
                <div className="cart-summary-row">
                  <span>Delivery:</span>
                  <span className={deliveryFee === 0 ? 'text-success' : ''}>
                    {deliveryFee === 0 ? 'FREE' : `₹${deliveryFee}.00`}
                  </span>
                </div>
                <div className="cart-summary-row total-row">
                  <span>Estimated Total:</span>
                  <span className="total-amount">
                    ₹{finalTotal.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </span>
                </div>

                <button
                  className="btn btn-primary btn-full btn-lg checkout-btn"
                  onClick={handleOpenPayment}
                >
                  Proceed to Payment <FiArrowRight />
                </button>

                <button className="btn btn-ghost btn-full cart-clear-all" onClick={clearCart}>
                  Clear Cart
                </button>
              </div>
            )}
          </aside>
        </div>
      )}

      {/* Payment Modal & Pop-up */}
      <PaymentModal
        isOpen={isPaymentOpen}
        onClose={() => setIsPaymentOpen(false)}
        amount={finalTotal}
        itemsCount={totalItems}
        cartItems={items}
        onPaymentComplete={handlePaymentComplete}
      />
    </>
  );
}
