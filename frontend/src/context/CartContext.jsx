import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import toast from 'react-hot-toast';

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const [items, setItems] = useState(() => {
    try {
      const stored = localStorage.getItem('shopping_cart');
      return stored ? JSON.parse(stored) : [];
    } catch {
      return [];
    }
  });

  const [isCartOpen, setIsCartOpen] = useState(false);

  // Persist cart to localStorage whenever items change
  useEffect(() => {
    try {
      localStorage.setItem('shopping_cart', JSON.stringify(items));
    } catch (e) {
      console.error('Failed to save cart to localStorage', e);
    }
  }, [items]);

  // Add product to cart
  const addToCart = useCallback((product, qty = 1) => {
    setItems((prevItems) => {
      const existingIndex = prevItems.findIndex((item) => item.id === product.id);

      if (existingIndex > -1) {
        const updated = [...prevItems];
        const newQty = updated[existingIndex].quantity + qty;
        
        // Prevent exceeding stock
        if (product.stockQuantity && newQty > product.stockQuantity) {
          toast.error(`Only ${product.stockQuantity} items in stock!`);
          return prevItems;
        }

        updated[existingIndex] = {
          ...updated[existingIndex],
          quantity: newQty,
        };
        toast.success(`Updated "${product.name}" quantity (${newQty})`);
        return updated;
      } else {
        toast.success(`Added "${product.name}" to cart! 🛍️`);
        return [...prevItems, { ...product, quantity: qty }];
      }
    });

    setIsCartOpen(true);
  }, []);

  // Remove single item
  const removeFromCart = useCallback((productId) => {
    setItems((prev) => prev.filter((item) => item.id !== productId));
    toast.success('Item removed from cart');
  }, []);

  // Update item quantity
  const updateQuantity = useCallback((productId, newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(productId);
      return;
    }

    setItems((prev) =>
      prev.map((item) => {
        if (item.id === productId) {
          if (item.stockQuantity && newQuantity > item.stockQuantity) {
            toast.error(`Only ${item.stockQuantity} items available!`);
            return item;
          }
          return { ...item, quantity: newQuantity };
        }
        return item;
      })
    );
  }, [removeFromCart]);

  // Clear all items
  const clearCart = useCallback(() => {
    setItems([]);
  }, []);

  // Computed properties
  const totalItems = items.reduce((sum, item) => sum + item.quantity, 0);
  const totalPrice = items.reduce((sum, item) => sum + Number(item.price) * item.quantity, 0);

  return (
    <CartContext.Provider
      value={{
        items,
        totalItems,
        totalPrice,
        isCartOpen,
        setIsCartOpen,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
}

