import React, { useState, useEffect } from 'react';
import { sweetsAPI } from '../services/api';
import './Dashboard.css';

const Dashboard = () => {
  const [sweets, setSweets] = useState([]);
  const [filteredSweets, setFilteredSweets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchName, setSearchName] = useState('');
  const [searchCategory, setSearchCategory] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [purchaseQuantity, setPurchaseQuantity] = useState({});
  const [purchasing, setPurchasing] = useState({});

  useEffect(() => {
    loadSweets();
  }, []);

  useEffect(() => {
    filterSweets();
  }, [sweets, searchName, searchCategory, minPrice, maxPrice]);

  const loadSweets = async () => {
    try {
      setLoading(true);
      const response = await sweetsAPI.getAll();
      setSweets(response.data);
      setFilteredSweets(response.data);
    } catch (error) {
      setError('Failed to load sweets');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const filterSweets = () => {
    let filtered = [...sweets];

    if (searchName) {
      filtered = filtered.filter((sweet) =>
        sweet.name.toLowerCase().includes(searchName.toLowerCase())
      );
    }

    if (searchCategory) {
      filtered = filtered.filter(
        (sweet) => sweet.category.toLowerCase() === searchCategory.toLowerCase()
      );
    }

    if (minPrice) {
      filtered = filtered.filter((sweet) => sweet.price >= parseFloat(minPrice));
    }

    if (maxPrice) {
      filtered = filtered.filter((sweet) => sweet.price <= parseFloat(maxPrice));
    }

    setFilteredSweets(filtered);
  };

  const handlePurchase = async (sweetId) => {
    console.log('Purchase button clicked for sweet:', sweetId);
    const quantity = purchaseQuantity[sweetId];
    console.log('Purchase quantity:', quantity);

    if (!quantity || quantity <= 0) {
      alert('Please enter a valid quantity greater than 0');
      return;
    }

    const sweet = sweets.find(s => s.id === sweetId);
    if (sweet && quantity > sweet.quantity) {
      alert(`Insufficient stock! Only ${sweet.quantity} items available.`);
      return;
    }

    try {
      console.log('Making purchase API call...');
      setPurchasing({ ...purchasing, [sweetId]: true });
      const response = await sweetsAPI.purchase(sweetId, { quantity: parseInt(quantity) });
      console.log('Purchase response:', response);
      await loadSweets();
      setPurchaseQuantity({ ...purchaseQuantity, [sweetId]: '' });
      alert(`Successfully purchased ${quantity} item(s)!`);
    } catch (error) {
      console.error('Purchase error:', error);
      const errorMessage = error.response?.data?.message || 
                         error.response?.data?.error || 
                         error.message || 
                         'Purchase failed';
      alert(errorMessage);
    } finally {
      setPurchasing({ ...purchasing, [sweetId]: false });
    }
  };

  const categories = [...new Set(sweets.map((sweet) => sweet.category))];

  if (loading) {
    return <div className="container">Loading...</div>;
  }

  return (
    <div className="container">
      <div style={{ textAlign: 'center', marginBottom: '30px' }}>
        <h1 style={{ 
          fontSize: '42px', 
          fontWeight: 800, 
          marginBottom: '10px',
          background: 'linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          backgroundClip: 'text'
        }}>
          🍬 Sweet Shop Dashboard 🍭
        </h1>
        <p style={{ color: '#7a5a4a', fontSize: '18px', fontWeight: 500 }}>
          Discover and purchase your favorite sweets!
        </p>
      </div>

      <div className="search-bar">
        <input
          type="text"
          placeholder="🔍 Search by name..."
          value={searchName}
          onChange={(e) => setSearchName(e.target.value)}
        />
        <select
          value={searchCategory}
          onChange={(e) => setSearchCategory(e.target.value)}
        >
          <option value="">🏷️ All Categories</option>
          {categories.map((cat) => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </select>
        <input
          type="number"
          placeholder="💰 Min Price"
          value={minPrice}
          onChange={(e) => setMinPrice(e.target.value)}
          step="0.01"
        />
        <input
          type="number"
          placeholder="💰 Max Price"
          value={maxPrice}
          onChange={(e) => setMaxPrice(e.target.value)}
          step="0.01"
        />
      </div>

      {error && <div className="error">{error}</div>}

      <div className="grid">
        {filteredSweets.length === 0 ? (
          <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
            <div style={{ fontSize: '64px', marginBottom: '20px' }}>🍰</div>
            <h3 style={{ color: '#7a5a4a', fontSize: '24px' }}>No sweets found</h3>
            <p style={{ color: '#9a7a6a', marginTop: '10px' }}>Try adjusting your search filters</p>
          </div>
        ) : (
          filteredSweets.map((sweet) => (
            <div key={sweet.id} className="sweet-card">
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
              <p
                className={`quantity ${
                  sweet.quantity === 0
                    ? 'low'
                    : sweet.quantity < 10
                    ? 'low'
                    : 'available'
                }`}
              >
                📦 Stock: {sweet.quantity}
              </p>
              <div className="purchase-section">
                <input
                  type="number"
                  min="1"
                  max={sweet.quantity}
                  value={purchaseQuantity[sweet.id] ?? ''}
                  onChange={(e) => {
                    const value = e.target.value;
                    setPurchaseQuantity({
                      ...purchaseQuantity,
                      [sweet.id]: value === '' ? '' : parseInt(value) || '',
                    });
                  }}
                  placeholder="🔢 Quantity"
                  disabled={sweet.quantity === 0}
                />
                <button
                  type="button"
                  className="btn btn-primary"
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    handlePurchase(sweet.id);
                  }}
                  disabled={
                    sweet.quantity === 0 ||
                    purchasing[sweet.id] ||
                    !purchaseQuantity[sweet.id] ||
                    purchaseQuantity[sweet.id] === '' ||
                    parseInt(purchaseQuantity[sweet.id]) <= 0
                  }
                >
                  {purchasing[sweet.id] ? '⏳ Purchasing...' : '🛒 Purchase'}
                </button>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default Dashboard;

