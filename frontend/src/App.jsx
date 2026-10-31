import { Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/common/Navbar.jsx';
import Footer from './components/common/Footer.jsx';
import CartDrawer from './components/cart/CartDrawer.jsx';
import ProtectedRoute from './components/common/ProtectedRoute.jsx';
import AdminRoute from './components/common/AdminRoute.jsx';

// Pages
import HomePage from './pages/HomePage.jsx';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import ProductsPage from './pages/ProductsPage.jsx';
import ProductDetailPage from './pages/ProductDetailPage.jsx';
import ProfilePage from './pages/ProfilePage.jsx';
import OrdersPage from './pages/OrdersPage.jsx';
import AdminDashboard from './pages/admin/AdminDashboard.jsx';
import ProductManagePage from './pages/admin/ProductManagePage.jsx';
import NotFoundPage from './pages/NotFoundPage.jsx';

import './styles/App.css';

function App() {
  return (
    <div className="app-wrapper">
      <Navbar />
      <CartDrawer />
      <main className="main-content">
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/products/:category/:id" element={<ProductDetailPage />} />

          {/* Protected Routes (any logged-in user) */}
          <Route element={<ProtectedRoute />}>
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/orders" element={<OrdersPage />} />
          </Route>

          {/* Admin-only Routes */}
          <Route element={<AdminRoute />}>
            <Route path="/admin" element={<AdminDashboard />} />
            <Route path="/admin/products" element={<ProductManagePage />} />
          </Route>

          {/* Fallback */}
          <Route path="/404" element={<NotFoundPage />} />
          <Route path="*" element={<Navigate to="/404" replace />} />
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default App;

