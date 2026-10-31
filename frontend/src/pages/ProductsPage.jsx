import { useEffect, useState, useCallback } from 'react';
import productApi from '../api/productApi.js';
import ProductCard from '../components/product/ProductCard.jsx';
import Spinner from '../components/common/Spinner.jsx';
import { FiSearch, FiFilter } from 'react-icons/fi';

const CATEGORIES = [
  'all', 'electronics', 'clothing', 'books', 'home-garden',
  'sports', 'beauty', 'toys', 'food', 'jewelry', 'automotive',
];

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [search, setSearch] = useState('');

  const fetchProducts = useCallback(async (category) => {
    setLoading(true);
    try {
      const { data } = category === 'all'
        ? await productApi.getAllActive()
        : await productApi.getByCategory(category);
      setProducts(data);
      setFiltered(data);
    } catch {
      setProducts([]);
      setFiltered([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchProducts(selectedCategory); }, [selectedCategory, fetchProducts]);

  // Client-side search filter
  useEffect(() => {
    if (!search.trim()) { setFiltered(products); return; }
    const q = search.toLowerCase();
    setFiltered(
      products.filter(
        (p) =>
          p.name.toLowerCase().includes(q) ||
          p.description?.toLowerCase().includes(q) ||
          p.brand?.toLowerCase().includes(q)
      )
    );
  }, [search, products]);

  return (
    <div className="products-page">
      {/* Page Header */}
      <div className="page-header">
        <h1>All Products</h1>
        <p>{filtered.length} product{filtered.length !== 1 ? 's' : ''} found</p>
      </div>

      {/* Controls */}
      <div className="products-controls">
        {/* Search */}
        <div className="search-box">
          <FiSearch className="search-icon" />
          <input
            type="text"
            placeholder="Search products…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>

        {/* Category Filter */}
        <div className="category-filter">
          <FiFilter />
          {CATEGORIES.map((cat) => (
            <button
              key={cat}
              onClick={() => { setSelectedCategory(cat); setSearch(''); }}
              className={`category-btn ${selectedCategory === cat ? 'active' : ''}`}
            >
              {cat === 'all' ? 'All' : cat.charAt(0).toUpperCase() + cat.slice(1).replace('-', ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Grid */}
      {loading ? (
        <Spinner text="Loading products…" />
      ) : filtered.length === 0 ? (
        <div className="empty-state">
          <p>No products found{search ? ` for "${search}"` : ''}.</p>
          {search && (
            <button className="btn btn-ghost" onClick={() => setSearch('')}>
              Clear search
            </button>
          )}
        </div>
      ) : (
        <div className="product-grid">
          {filtered.map((p) => <ProductCard key={p.id} product={p} />)}
        </div>
      )}
    </div>
  );
}

