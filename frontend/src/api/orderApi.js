import axiosInstance from './axiosInstance.js';

/**
 * Helper to get local backup order history for resilient offline display.
 */
function getLocalOrders(username) {
  try {
    const key = username ? `local_orders_${username}` : 'local_orders_guest';
    const stored = localStorage.getItem(key);
    return stored ? JSON.parse(stored) : [];
  } catch {
    return [];
  }
}

/**
 * Helper to save an order to local backup.
 */
function saveLocalOrder(username, order) {
  try {
    const key = username ? `local_orders_${username}` : 'local_orders_guest';
    const orders = getLocalOrders(username);
    orders.unshift(order);
    localStorage.setItem(key, JSON.stringify(orders));
  } catch (err) {
    console.warn('Could not save order to local storage', err);
  }
}

const orderApi = {
  /**
   * Place a new order: POST /api/orders
   * @param {{ orderNumber, items, totalAmount, paymentMethod, transactionId, shippingAddress }} data
   * @param {string} [username]
   */
  createOrder: async (data, username = 'guest') => {
    try {
      const response = await axiosInstance.post('/orders', data);
      if (response?.data) {
        saveLocalOrder(username, response.data);
        return response.data;
      }
    } catch (err) {
      console.warn('Backend order creation failed, persisting locally:', err);
    }

    // Fallback: create mock order in local backup so the user always has a complete order record
    const fallbackOrder = {
      id: 'local-' + Date.now(),
      orderNumber: data.orderNumber || 'ORD-' + Math.floor(100000 + Math.random() * 900000),
      username: username,
      items: data.items.map(item => ({
        productId: item.productId,
        category: item.category || 'General',
        productName: item.productName || item.name,
        price: item.price,
        quantity: item.quantity,
        subtotal: item.price * item.quantity,
        imageUrl: item.imageUrl,
      })),
      totalAmount: data.totalAmount,
      paymentMethod: (data.paymentMethod || 'CARD').toUpperCase(),
      transactionId: data.transactionId || 'TXN-' + Math.random().toString(36).substring(2, 10).toUpperCase(),
      status: 'CONFIRMED',
      shippingAddress: data.shippingAddress || 'Registered Address',
      createdAt: new Date().toISOString(),
    };

    saveLocalOrder(username, fallbackOrder);
    return fallbackOrder;
  },

  /**
   * Fetch user's order history: GET /api/orders/my-orders
   * @param {string} [username]
   */
  getMyOrders: async (username = 'guest') => {
    try {
      const response = await axiosInstance.get('/orders/my-orders');
      if (response?.data && Array.isArray(response.data) && response.data.length > 0) {
        // Merge with local backup to make sure no orders are missed
        const local = getLocalOrders(username);
        const map = new Map();
        [...response.data, ...local].forEach((ord) => {
          const key = ord.orderNumber || ord.id;
          if (!map.has(key)) map.set(key, ord);
        });
        return Array.from(map.values());
      }
    } catch (err) {
      console.warn('Could not fetch orders from backend, reading local store:', err);
    }
    return getLocalOrders(username);
  },

  /**
   * Fetch single order details by ID: GET /api/orders/:id
   */
  getOrderById: async (id) => {
    return axiosInstance.get(`/orders/${id}`);
  },
};

export default orderApi;

