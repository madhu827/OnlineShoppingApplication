import { useEffect, useState } from 'react';
import productApi from '../../api/productApi.js';
import ProductForm from '../../components/product/ProductForm.jsx';
import Spinner from '../../components/common/Spinner.jsx';
import toast from 'react-hot-toast';
import { FiPlus, FiEdit2, FiTrash2, FiX, FiCheckCircle } from 'react-icons/fi';

export default function ProductManagePage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [formLoading, setFormLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [deleteConfirm, setDeleteConfirm] = useState(null); // { category, id, name }

  const fetchAll = async () => {
    setLoading(true);
    try {
      const { data } = await productApi.getAllActive();
      setProducts(data);
    } catch {
      toast.error('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchAll(); }, []);

  const handleCreate = async (formData) => {
    setFormLoading(true);
    try {
      await productApi.create(formData);
      toast.success('Product created!');
      setShowForm(false);
      fetchAll();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to create product');
    } finally {
      setFormLoading(false);
    }
  };

  const handleUpdate = async (formData) => {
    setFormLoading(true);
    try {
      await productApi.update(editingProduct.category, editingProduct.id, formData);
      toast.success('Product updated!');
      setEditingProduct(null);
      fetchAll();
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to update product');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await productApi.delete(deleteConfirm.category, deleteConfirm.id);
      toast.success(`"${deleteConfirm.name}" deleted`);
      setDeleteConfirm(null);
      fetchAll();
    } catch {
      toast.error('Failed to delete product');
    }
  };

  return (
    <div className="admin-page">
      {/* Header */}
      <div className="admin-header">
        <div>
          <h1>Product Management</h1>
          <p>Cassandra-backed product catalog — {products.length} products</p>
        </div>
        <button
          className="btn btn-primary"
          onClick={() => { setShowForm(true); setEditingProduct(null); }}
        >
          <FiPlus /> New Product
        </button>
      </div>

      {/* Create Form */}
      {showForm && !editingProduct && (
        <div className="admin-form-panel">
          <h2>Create New Product</h2>
          <ProductForm
            onSubmit={handleCreate}
            onCancel={() => setShowForm(false)}
            loading={formLoading}
          />
        </div>
      )}

      {/* Edit Form */}
      {editingProduct && (
        <div className="admin-form-panel">
          <h2>Edit Product</h2>
          <ProductForm
            initial={editingProduct}
            onSubmit={handleUpdate}
            onCancel={() => setEditingProduct(null)}
            loading={formLoading}
          />
        </div>
      )}

      {/* Delete Confirm */}
      {deleteConfirm && (
        <div className="modal-overlay" onClick={() => setDeleteConfirm(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>Delete Product?</h3>
            <p>Are you sure you want to delete <strong>"{deleteConfirm.name}"</strong>? This action cannot be undone.</p>
            <div className="modal-actions">
              <button className="btn btn-ghost" onClick={() => setDeleteConfirm(null)}>
                <FiX /> Cancel
              </button>
              <button className="btn btn-danger" onClick={handleDelete}>
                <FiTrash2 /> Delete
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Products Table */}
      {loading ? (
        <Spinner text="Loading products…" />
      ) : products.length === 0 ? (
        <div className="empty-state">
          <p>No products yet. Create your first product above.</p>
        </div>
      ) : (
        <div className="table-wrapper">
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Brand</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {products.map((p) => (
                <tr key={p.id}>
                  <td className="td-name">{p.name}</td>
                  <td><span className="badge badge-info">{p.category}</span></td>
                  <td className="td-price">₹{Number(p.price).toLocaleString('en-IN')}</td>
                  <td className={p.stockQuantity === 0 ? 'text-danger' : ''}>
                    {p.stockQuantity}
                  </td>
                  <td>{p.brand || '—'}</td>
                  <td>
                    <span className={`badge ${p.active ? 'badge-success' : 'badge-danger'}`}>
                      {p.active ? <><FiCheckCircle size={11} /> Active</> : 'Inactive'}
                    </span>
                  </td>
                  <td className="td-actions">
                    <button
                      className="btn btn-sm btn-ghost"
                      onClick={() => { setEditingProduct(p); setShowForm(false); }}
                    >
                      <FiEdit2 />
                    </button>
                    <button
                      className="btn btn-sm btn-danger-ghost"
                      onClick={() => setDeleteConfirm({ category: p.category, id: p.id, name: p.name })}
                    >
                      <FiTrash2 />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

