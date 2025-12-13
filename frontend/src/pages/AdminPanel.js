import React, { useState, useEffect } from 'react';
import { sweetsAPI } from '../services/api';
import './AdminPanel.css';

const AdminPanel = () => {
  const [sweets, setSweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [editingSweet, setEditingSweet] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    category: '',
    price: '',
    quantity: '',
  });

  useEffect(() => {
    loadSweets();
  }, []);

  const loadSweets = async () => {
    try {
      setLoading(true);
      const response = await sweetsAPI.getAll();
      setSweets(response.data);
    } catch (error) {
      setError('Failed to load sweets');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (sweet = null) => {
    if (sweet) {
      setEditingSweet(sweet);
      setFormData({
        name: sweet.name,
        category: sweet.category,
        price: sweet.price.toString(),
        quantity: sweet.quantity.toString(),
      });
    } else {
      setEditingSweet(null);
      setFormData({
        name: '',
        category: '',
        price: '',
        quantity: '',
      });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingSweet(null);
    setFormData({
      name: '',
      category: '',
      price: '',
      quantity: '',
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        name: formData.name,
        category: formData.category,
        price: parseFloat(formData.price),
        quantity: parseInt(formData.quantity),
      };

      if (editingSweet) {
        await sweetsAPI.update(editingSweet.id, data);
      } else {
        await sweetsAPI.create(data);
      }

      await loadSweets();
      handleCloseModal();
      alert(editingSweet ? 'Sweet updated successfully!' : 'Sweet created successfully!');
    } catch (error) {
      alert(error.response?.data?.error || 'Operation failed');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this sweet?')) {
      return;
    }

    try {
      await sweetsAPI.delete(id);
      await loadSweets();
      alert('Sweet deleted successfully!');
    } catch (error) {
      alert(error.response?.data?.error || 'Delete failed');
    }
  };

  const handleRestock = async (id) => {
    const quantity = prompt('Enter quantity to add:');
    if (!quantity || parseInt(quantity) <= 0) {
      return;
    }

    try {
      await sweetsAPI.restock(id, { quantity: parseInt(quantity) });
      await loadSweets();
      alert('Restock successful!');
    } catch (error) {
      alert(error.response?.data?.error || 'Restock failed');
    }
  };

  if (loading) {
    return <div className="container">Loading...</div>;
  }

  return (
    <div className="container">
      <div className="admin-header">
        <h1>Admin Panel</h1>
        <button className="btn btn-primary" onClick={() => handleOpenModal()}>
          Add New Sweet
        </button>
      </div>

      {error && <div className="error">{error}</div>}

      <div className="grid">
        {sweets.length === 0 ? (
          <div className="card">No sweets found</div>
        ) : (
          sweets.map((sweet) => (
            <div key={sweet.id} className="sweet-card admin-card">
              <h3>{sweet.name}</h3>
              <p className="category">{sweet.category}</p>
              <p className="price">${sweet.price.toFixed(2)}</p>
              <p className="quantity">Stock: {sweet.quantity}</p>
              <div className="admin-actions">
                <button
                  className="btn btn-secondary"
                  onClick={() => handleOpenModal(sweet)}
                >
                  Edit
                </button>
                <button
                  className="btn btn-success"
                  onClick={() => handleRestock(sweet.id)}
                >
                  Restock
                </button>
                <button
                  className="btn btn-danger"
                  onClick={() => handleDelete(sweet.id)}
                >
                  Delete
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {showModal && (
        <div className="modal" onClick={handleCloseModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingSweet ? 'Edit Sweet' : 'Add New Sweet'}</h2>
              <button className="close-btn" onClick={handleCloseModal}>
                ×
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Name</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                  required
                />
              </div>
              <div className="form-group">
                <label>Category</label>
                <input
                  type="text"
                  value={formData.category}
                  onChange={(e) =>
                    setFormData({ ...formData, category: e.target.value })
                  }
                  required
                />
              </div>
              <div className="form-group">
                <label>Price</label>
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  value={formData.price}
                  onChange={(e) =>
                    setFormData({ ...formData, price: e.target.value })
                  }
                  required
                />
              </div>
              <div className="form-group">
                <label>Quantity</label>
                <input
                  type="number"
                  min="0"
                  value={formData.quantity}
                  onChange={(e) =>
                    setFormData({ ...formData, quantity: e.target.value })
                  }
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="submit" className="btn btn-primary">
                  {editingSweet ? 'Update' : 'Create'}
                </button>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={handleCloseModal}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPanel;

