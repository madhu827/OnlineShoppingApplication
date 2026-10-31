import { useState, useEffect } from 'react';
import { FiSave, FiX } from 'react-icons/fi';

const CATEGORIES = [
  'electronics', 'clothing', 'books', 'home-garden',
  'sports', 'beauty', 'toys', 'food', 'jewelry', 'automotive',
];

const emptyForm = {
  name: '', description: '', price: '', category: 'electronics',
  stockQuantity: 0, brand: '', imageUrl: '', sku: '', active: true,
};

export default function ProductForm({ initial = null, onSubmit, onCancel, loading }) {
  const [form, setForm] = useState(initial ? {
    ...initial,
    price: String(initial.price),
    stockQuantity: initial.stockQuantity,
  } : emptyForm);

  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initial) {
      setForm({ ...initial, price: String(initial.price), stockQuantity: initial.stockQuantity });
    }
  }, [initial]);

  const validate = () => {
    const e = {};
    if (!form.name.trim()) e.name = 'Product name is required';
    if (!form.price || isNaN(form.price) || Number(form.price) <= 0)
      e.price = 'Enter a valid price greater than 0';
    if (!form.category.trim()) e.category = 'Category is required';
    if (form.stockQuantity < 0) e.stockQuantity = 'Stock cannot be negative';
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;
    onSubmit({
      ...form,
      price: parseFloat(form.price),
      stockQuantity: parseInt(form.stockQuantity, 10),
    });
  };

  return (
    <form onSubmit={handleSubmit} className="product-form" noValidate>
      <div className="form-row">
        {/* Name */}
        <div className="form-group form-group--grow">
          <label>Product Name <span className="required">*</span></label>
          <input name="name" value={form.name} onChange={handleChange}
            placeholder="e.g. Samsung Galaxy S25" className={errors.name ? 'input-error' : ''} />
          {errors.name && <span className="field-error">{errors.name}</span>}
        </div>

        {/* Category */}
        <div className="form-group">
          <label>Category <span className="required">*</span></label>
          <select name="category" value={form.category} onChange={handleChange}
            className={errors.category ? 'input-error' : ''}>
            {CATEGORIES.map((c) => (
              <option key={c} value={c}>{c.charAt(0).toUpperCase() + c.slice(1)}</option>
            ))}
          </select>
          {errors.category && <span className="field-error">{errors.category}</span>}
        </div>
      </div>

      {/* Description */}
      <div className="form-group">
        <label>Description</label>
        <textarea name="description" value={form.description} onChange={handleChange}
          rows={3} placeholder="Detailed product description…" />
      </div>

      <div className="form-row">
        {/* Price */}
        <div className="form-group">
          <label>Price (₹) <span className="required">*</span></label>
          <input name="price" type="number" step="0.01" min="0.01"
            value={form.price} onChange={handleChange}
            placeholder="0.00" className={errors.price ? 'input-error' : ''} />
          {errors.price && <span className="field-error">{errors.price}</span>}
        </div>

        {/* Stock */}
        <div className="form-group">
          <label>Stock Quantity</label>
          <input name="stockQuantity" type="number" min="0"
            value={form.stockQuantity} onChange={handleChange}
            className={errors.stockQuantity ? 'input-error' : ''} />
          {errors.stockQuantity && <span className="field-error">{errors.stockQuantity}</span>}
        </div>

        {/* Brand */}
        <div className="form-group">
          <label>Brand</label>
          <input name="brand" value={form.brand} onChange={handleChange} placeholder="e.g. Samsung" />
        </div>
      </div>

      <div className="form-row">
        {/* SKU */}
        <div className="form-group">
          <label>SKU</label>
          <input name="sku" value={form.sku} onChange={handleChange} placeholder="SKU-001" />
        </div>

        {/* Image URL */}
        <div className="form-group form-group--grow">
          <label>Image URL</label>
          <input name="imageUrl" value={form.imageUrl} onChange={handleChange}
            placeholder="https://example.com/image.jpg" />
        </div>
      </div>

      {/* Active toggle */}
      <div className="form-group form-check">
        <input id="active" name="active" type="checkbox"
          checked={form.active} onChange={handleChange} />
        <label htmlFor="active">Active (visible to customers)</label>
      </div>

      {/* Actions */}
      <div className="form-actions">
        <button type="button" className="btn btn-ghost" onClick={onCancel}>
          <FiX /> Cancel
        </button>
        <button type="submit" className="btn btn-primary" disabled={loading}>
          <FiSave /> {loading ? 'Saving…' : initial ? 'Update Product' : 'Create Product'}
        </button>
      </div>
    </form>
  );
}

