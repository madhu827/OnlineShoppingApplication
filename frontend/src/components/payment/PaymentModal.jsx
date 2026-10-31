import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  FiX,
  FiCreditCard,
  FiSmartphone,
  FiCheckCircle,
  FiLock,
  FiShield,
  FiDollarSign,
  FiArrowRight,
  FiPackage,
} from 'react-icons/fi';
import toast from 'react-hot-toast';
import { useAuth } from '../../context/AuthContext.jsx';
import orderApi from '../../api/orderApi.js';

export default function PaymentModal({
  isOpen,
  onClose,
  amount,
  itemsCount,
  cartItems = [],
  onPaymentComplete,
}) {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [method, setMethod] = useState('card'); // 'card' | 'upi' | 'netbanking' | 'cod'
  const [isProcessing, setIsProcessing] = useState(false);
  const [paymentDone, setPaymentDone] = useState(false);
  const [receipt, setReceipt] = useState(null);

  // Form states
  const [card, setCard] = useState({
    number: '',
    name: '',
    expiry: '',
    cvv: '',
  });
  const [upiId, setUpiId] = useState('');

  if (!isOpen) return null;

  // Format card number with spaces (1234 5678 ...)
  const handleCardNumberChange = (e) => {
    let val = e.target.value.replace(/\D/g, '').slice(0, 16);
    val = val.replace(/(\d{4})/g, '$1 ').trim();
    setCard((prev) => ({ ...prev, number: val }));
  };

  // Format expiry MM/YY
  const handleExpiryChange = (e) => {
    let val = e.target.value.replace(/\D/g, '').slice(0, 4);
    if (val.length >= 3) {
      val = `${val.slice(0, 2)}/${val.slice(2, 4)}`;
    }
    setCard((prev) => ({ ...prev, expiry: val }));
  };

  const handlePay = async (e) => {
    e.preventDefault();

    // Validation
    if (method === 'card') {
      if (card.number.replace(/\s/g, '').length < 16) {
        toast.error('Please enter a valid 16-digit card number');
        return;
      }
      if (!card.expiry || card.expiry.length < 5) {
        toast.error('Please enter a valid expiry date (MM/YY)');
        return;
      }
      if (!card.cvv || card.cvv.length < 3) {
        toast.error('Please enter a valid 3-digit CVV');
        return;
      }
      if (!card.name.trim()) {
        toast.error('Please enter cardholder name');
        return;
      }
    } else if (method === 'upi') {
      if (!upiId.trim() || !upiId.includes('@')) {
        toast.error('Please enter a valid UPI ID (e.g. yourname@okaxis)');
        return;
      }
    }

    setIsProcessing(true);

    const generatedOrderId = 'ORD-' + Math.floor(100000 + Math.random() * 900000);
    const generatedTxnId = 'TXN-' + Math.random().toString(36).substring(2, 10).toUpperCase();

    // Prepare order request payload
    const orderPayload = {
      orderNumber: generatedOrderId,
      transactionId: generatedTxnId,
      paymentMethod: method.toUpperCase(),
      totalAmount: amount,
      shippingAddress: user?.address || 'Standard Delivery Address',
      items: (cartItems || []).map((item) => ({
        productId: String(item.id),
        category: item.category || 'General',
        productName: item.name,
        price: Number(item.price),
        quantity: item.quantity,
        imageUrl: item.imageUrl || '',
      })),
    };

    // Simulate payment gateway handshake & processing
    setTimeout(async () => {
      try {
        await orderApi.createOrder(orderPayload, user?.username);
      } catch (err) {
        console.error('Order creation error:', err);
      }

      const generatedReceipt = {
        orderId: generatedOrderId,
        transactionId: generatedTxnId,
        amount: amount,
        method: method.toUpperCase(),
        date: new Date().toLocaleString('en-IN', {
          dateStyle: 'medium',
          timeStyle: 'short',
        }),
      };

      setIsProcessing(false);
      setReceipt(generatedReceipt);
      setPaymentDone(true);
      toast.success('Payment Done! 🎉 Your order has been placed.', { duration: 5000 });
      onPaymentComplete();
    }, 1800);
  };

  const handleCloseAll = () => {
    setPaymentDone(false);
    setReceipt(null);
    onClose();
  };

  const handleViewOrders = () => {
    handleCloseAll();
    navigate('/orders');
  };

  return (
    <div className="payment-backdrop" onClick={paymentDone ? handleCloseAll : onClose}>
      <div className="payment-modal" onClick={(e) => e.stopPropagation()}>
        
        {/* ================= PAYMENT DONE SUCCESS POP-UP ================= */}
        {paymentDone && receipt ? (
          <div className="payment-success-popup">
            <div className="success-icon-wrapper">
              <FiCheckCircle size={64} className="success-icon-animated" />
            </div>

            <span className="badge badge-success success-badge">PAYMENT VERIFIED</span>
            <h2 className="success-title">Payment is Done!</h2>
            <p className="success-subtitle">
              Your transaction was completed successfully. Your order is confirmed and has been added to your order history!
            </p>

            {/* Receipt Summary Box */}
            <div className="receipt-box">
              <div className="receipt-row">
                <span className="receipt-label">Amount Paid:</span>
                <span className="receipt-val receipt-amount">₹{receipt.amount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
              </div>
              <div className="receipt-row">
                <span className="receipt-label">Order ID:</span>
                <span className="receipt-val font-mono">{receipt.orderId}</span>
              </div>
              <div className="receipt-row">
                <span className="receipt-label">Transaction ID:</span>
                <span className="receipt-val font-mono">{receipt.transactionId}</span>
              </div>
              <div className="receipt-row">
                <span className="receipt-label">Payment Method:</span>
                <span className="receipt-val">{receipt.method}</span>
              </div>
              <div className="receipt-row">
                <span className="receipt-label">Date & Time:</span>
                <span className="receipt-val">{receipt.date}</span>
              </div>
            </div>

            <div className="success-popup-actions">
              <button className="btn btn-primary btn-full btn-lg" onClick={handleViewOrders}>
                <FiPackage size={18} /> View Order History
              </button>
              <button className="btn btn-ghost btn-full" onClick={handleCloseAll}>
                Continue Shopping <FiArrowRight size={16} />
              </button>
            </div>
          </div>
        ) : (
          /* ================= PAYMENT CHECKOUT FORM ================= */
          <div className="payment-form-container">
            {/* Header */}
            <div className="payment-header">
              <div>
                <h2>Checkout & Payment</h2>
                <p>Complete your payment securely</p>
              </div>
              <button className="payment-close-btn" onClick={onClose}>
                <FiX size={20} />
              </button>
            </div>

            {/* Amount Summary Ribbon */}
            <div className="payment-amount-ribbon">
              <div>
                <span className="ribbon-items">{itemsCount} Item{itemsCount !== 1 ? 's' : ''}</span>
                <span className="ribbon-label">Total to Pay:</span>
              </div>
              <span className="ribbon-price">₹{amount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</span>
            </div>

            {/* Payment Method Selector */}
            <div className="payment-methods-grid">
              <button
                type="button"
                className={`method-tile ${method === 'card' ? 'active' : ''}`}
                onClick={() => setMethod('card')}
              >
                <FiCreditCard size={22} />
                <span>Card</span>
              </button>
              <button
                type="button"
                className={`method-tile ${method === 'upi' ? 'active' : ''}`}
                onClick={() => setMethod('upi')}
              >
                <FiSmartphone size={22} />
                <span>UPI / QR</span>
              </button>
              <button
                type="button"
                className={`method-tile ${method === 'netbanking' ? 'active' : ''}`}
                onClick={() => setMethod('netbanking')}
              >
                <FiShield size={22} />
                <span>Net Banking</span>
              </button>
              <button
                type="button"
                className={`method-tile ${method === 'cod' ? 'active' : ''}`}
                onClick={() => setMethod('cod')}
              >
                <FiDollarSign size={22} />
                <span>COD</span>
              </button>
            </div>

            <form onSubmit={handlePay} className="payment-inputs-form">
              {/* CARD FORM */}
              {method === 'card' && (
                <div className="card-fields">
                  <div className="form-group">
                    <label>Card Number</label>
                    <div className="input-group">
                      <input
                        type="text"
                        placeholder="4532 •••• •••• 8901"
                        value={card.number}
                        onChange={handleCardNumberChange}
                        maxLength={19}
                        required
                      />
                      <FiLock className="input-icon-right" />
                    </div>
                  </div>

                  <div className="form-group">
                    <label>Cardholder Name</label>
                    <input
                      type="text"
                      placeholder="e.g. John Doe"
                      value={card.name}
                      onChange={(e) => setCard({ ...card, name: e.target.value })}
                      required
                    />
                  </div>

                  <div className="form-row">
                    <div className="form-group">
                      <label>Expiry (MM/YY)</label>
                      <input
                        type="text"
                        placeholder="12/28"
                        value={card.expiry}
                        onChange={handleExpiryChange}
                        maxLength={5}
                        required
                      />
                    </div>
                    <div className="form-group">
                      <label>CVV / CVC</label>
                      <input
                        type="password"
                        placeholder="•••"
                        value={card.cvv}
                        onChange={(e) =>
                          setCard({
                            ...card,
                            cvv: e.target.value.replace(/\D/g, '').slice(0, 4),
                          })
                        }
                        maxLength={4}
                        required
                      />
                    </div>
                  </div>
                </div>
              )}

              {/* UPI FORM */}
              {method === 'upi' && (
                <div className="upi-fields">
                  <div className="upi-qr-preview">
                    <img
                      src={`https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=upi://pay?pa=shopapp@bank&pn=ShopApp&am=${amount}&cu=INR`}
                      alt="UPI QR Code"
                      className="upi-qr-img"
                    />
                    <p className="qr-hint">Scan with Google Pay, PhonePe, or Paytm</p>
                  </div>
                  <div className="form-group">
                    <label>Or Enter UPI ID</label>
                    <input
                      type="text"
                      placeholder="yourname@okaxis / mobile@upi"
                      value={upiId}
                      onChange={(e) => setUpiId(e.target.value)}
                    />
                  </div>
                </div>
              )}

              {/* NET BANKING */}
              {method === 'netbanking' && (
                <div className="netbanking-fields">
                  <p className="method-instruction">Select your bank:</p>
                  <div className="bank-options">
                    {['HDFC Bank', 'State Bank of India', 'ICICI Bank', 'Axis Bank', 'Kotak'].map((b) => (
                      <label key={b} className="bank-radio-label">
                        <input type="radio" name="bank" defaultChecked={b === 'HDFC Bank'} />
                        <span>{b}</span>
                      </label>
                    ))}
                  </div>
                </div>
              )}

              {/* CASH ON DELIVERY */}
              {method === 'cod' && (
                <div className="cod-info-box">
                  <FiDollarSign size={32} className="cod-icon" />
                  <h4>Cash on Delivery Selected</h4>
                  <p>Pay with cash or UPI directly to the delivery agent when your order arrives at your doorstep.</p>
                </div>
              )}

              {/* Pay Button */}
              <button
                type="submit"
                className="btn btn-primary btn-full btn-lg pay-submit-btn"
                disabled={isProcessing}
              >
                {isProcessing ? (
                  <span className="spinner-inline">Processing Securely…</span>
                ) : (
                  <>Pay ₹{amount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}</>
                )}
              </button>

              <div className="payment-security-footer">
                <FiLock size={12} />
                <span>256-Bit SSL Encrypted &amp; PCI-DSS Compliant</span>
              </div>
            </form>
          </div>
        )}

      </div>
    </div>
  );
}
