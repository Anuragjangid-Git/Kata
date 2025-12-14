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
    console.log('Open modal clicked, sweet:', sweet);
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
    
    // Validation
    if (!formData.name || !formData.name.trim()) {
      alert('Please enter a name');
      return;
    }
    if (!formData.category || !formData.category.trim()) {
      alert('Please enter a category');
      return;
    }
    const price = parseFloat(formData.price);
    if (isNaN(price) || price <= 0) {
      alert('Please enter a valid positive price');
      return;
    }
    const quantity = parseInt(formData.quantity);
    if (isNaN(quantity) || quantity < 0) {
      alert('Please enter a valid quantity (0 or greater)');
      return;
    }

    try {
      const data = {
        name: formData.name.trim(),
        category: formData.category.trim(),
        price: price,
        quantity: quantity,
      };

      if (editingSweet) {
        await sweetsAPI.update(editingSweet.id, data);
        alert('Sweet updated successfully!');
      } else {
        await sweetsAPI.create(data);
        alert('Sweet created successfully!');
      }

      await loadSweets();
      handleCloseModal();
    } catch (error) {
      console.error('Error:', error);
      const errorMessage = error.response?.data?.message || 
                         error.response?.data?.error || 
                         error.message || 
                         'Operation failed';
      alert(errorMessage);
    }
  };

  const handleDelete = async (id) => {
    console.log('Delete button clicked for sweet:', id);
    if (!window.confirm('Are you sure you want to delete this sweet?')) {
      return;
    }

    try {
      console.log('Making delete API call...');
      await sweetsAPI.delete(id);
      console.log('Delete successful');
      await loadSweets();
      alert('Sweet deleted successfully!');
    } catch (error) {
      console.error('Delete error:', error);
      const errorMessage = error.response?.data?.message || 
                         error.response?.data?.error || 
                         error.message || 
                         'Delete failed';
      alert(errorMessage);
    }
  };

  const handleRestock = async (id) => {
    console.log('Restock button clicked for sweet:', id);
    const quantity = prompt('Enter quantity to add:');
    if (!quantity || isNaN(quantity) || parseInt(quantity) <= 0) {
      if (quantity !== null) {
        alert('Please enter a valid positive number');
      }
      return;
    }

    try {
      const restockQuantity = parseInt(quantity);
      console.log('Making restock API call with quantity:', restockQuantity);
      await sweetsAPI.restock(id, { quantity: restockQuantity });
      console.log('Restock successful');
      await loadSweets();
      alert(`Successfully added ${restockQuantity} items!`);
    } catch (error) {
      console.error('Restock error:', error);
      const errorMessage = error.response?.data?.message || 
                         error.response?.data?.error || 
                         error.message || 
                         'Restock failed';
      alert(errorMessage);
    }
  };

  if (loading) {
    return <div className="container">Loading...</div>;
  }

  return (
    <div className="container">
      <div className="admin-header">
        <div>
          <h1>🎛️ Admin Panel</h1>
          <p style={{ marginTop: '8px', color: '#7a5a4a', fontSize: '16px' }}>
            Manage your sweet inventory
          </p>
        </div>
        <button 
          type="button"
          className="btn btn-primary" 
          onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            handleOpenModal();
          }}
        >
          ➕ Add New Sweet
        </button>
      </div>

      {error && <div className="error">{error}</div>}

      <div className="grid">
        {sweets.length === 0 ? (
          <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
            <div style={{ fontSize: '64px', marginBottom: '20px' }}>📦</div>
            <h3 style={{ color: '#7a5a4a', fontSize: '24px' }}>No sweets in inventory</h3>
            <p style={{ color: '#9a7a6a', marginTop: '10px', marginBottom: '20px' }}>
              Start by adding your first sweet!
            </p>
            <button 
              type="button"
              className="btn btn-primary" 
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                handleOpenModal();
              }}
            >
              ➕ Add Your First Sweet
            </button>
          </div>
        ) : (
          sweets.map((sweet) => (
            <div key={sweet.id} className="sweet-card admin-card">
              <div style={{ fontSize: '48px', textAlign: 'center', marginBottom: '10px' }}>
                {sweet.category === 'Chocolate' ? '🍫' : 
                 sweet.category === 'Candy' ? '🍬' : 
                 sweet.category === 'Cake' ? '🎂' : 
                 sweet.category === 'Cookie' ? '🍪' : 
                 sweet.category === 'Ice Cream' ? '🍦' : '🍭'}
              </div>
              <h3>{sweet.name}</h3>
              <p className="category">{sweet.category}</p>
              <p className="price">₹{sweet.price.toFixed(2)}</p>
              <p className="quantity">📦 Stock: {sweet.quantity}</p>
              <div className="admin-actions">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    handleOpenModal(sweet);
                  }}
                >
                  ✏️ Edit
                </button>
                <button
                  type="button"
                  className="btn btn-success"
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    handleRestock(sweet.id);
                  }}
                >
                  📥 Restock
                </button>
                <button
                  type="button"
                  className="btn btn-danger"
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    handleDelete(sweet.id);
                  }}
                >
                  🗑️ Delete
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
              <h2>{editingSweet ? '✏️ Edit Sweet' : '➕ Add New Sweet'}</h2>
              <button className="close-btn" onClick={handleCloseModal}>
                ×
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>🍬 Name</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData({ ...formData, name: e.target.value })
                  }
                  required
                  placeholder="Enter sweet name"
                />
              </div>
              <div className="form-group">
                <label>🏷️ Category</label>
                <input
                  type="text"
                  value={formData.category}
                  onChange={(e) =>
                    setFormData({ ...formData, category: e.target.value })
                  }
                  required
                  placeholder="e.g., Chocolate, Candy, Cake"
                />
              </div>
              <div className="form-group">
                <label>💰 Price (₹)</label>
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  value={formData.price}
                  onChange={(e) =>
                    setFormData({ ...formData, price: e.target.value })
                  }
                  required
                  placeholder="0.00"
                />
              </div>
              <div className="form-group">
                <label>📦 Quantity</label>
                <input
                  type="number"
                  min="0"
                  value={formData.quantity}
                  onChange={(e) =>
                    setFormData({ ...formData, quantity: e.target.value })
                  }
                  required
                  placeholder="0"
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

