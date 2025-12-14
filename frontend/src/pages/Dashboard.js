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
    const quantity = purchaseQuantity[sweetId] || 1;

    if (quantity <= 0) {
      alert('Quantity must be greater than 0');
      return;
    }

    try {
      setPurchasing({ ...purchasing, [sweetId]: true });
      await sweetsAPI.purchase(sweetId, { quantity });
      await loadSweets();
      setPurchaseQuantity({ ...purchaseQuantity, [sweetId]: '' });
      alert('Purchase successful!');
    } catch (error) {
      alert(error.response?.data?.error || 'Purchase failed');
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
      <h1>Sweet Shop Dashboard</h1>

      <div className="search-bar">
        <input
          type="text"
          placeholder="Search by name..."
          value={searchName}
          onChange={(e) => setSearchName(e.target.value)}
        />
        <select
          value={searchCategory}
          onChange={(e) => setSearchCategory(e.target.value)}
        >
          <option value="">All Categories</option>
          {categories.map((cat) => (
            <option key={cat} value={cat}>
              {cat}
            </option>
          ))}
        </select>
        <input
          type="number"
          placeholder="Min Price"
          value={minPrice}
          onChange={(e) => setMinPrice(e.target.value)}
          step="0.01"
        />
        <input
          type="number"
          placeholder="Max Price"
          value={maxPrice}
          onChange={(e) => setMaxPrice(e.target.value)}
          step="0.01"
        />
      </div>

      {error && <div className="error">{error}</div>}

      <div className="grid">
        {filteredSweets.length === 0 ? (
          <div className="card">No sweets found</div>
        ) : (
          filteredSweets.map((sweet) => (
            <div key={sweet.id} className="sweet-card">
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
                Stock: {sweet.quantity}
              </p>
              <div className="purchase-section">
                <input
                  type="number"
                  min="1"
                  max={sweet.quantity}
                  value={purchaseQuantity[sweet.id] || ''}
                  onChange={(e) =>
                    setPurchaseQuantity({
                      ...purchaseQuantity,
                      [sweet.id]: parseInt(e.target.value) || '',
                    })
                  }
                  placeholder="Quantity"
                  disabled={sweet.quantity === 0}
                />
                <button
                  className="btn btn-primary"
                  onClick={() => handlePurchase(sweet.id)}
                  disabled={
                    sweet.quantity === 0 ||
                    purchasing[sweet.id] ||
                    !purchaseQuantity[sweet.id] ||
                    purchaseQuantity[sweet.id] <= 0
                  }
                >
                  {purchasing[sweet.id] ? 'Purchasing...' : 'Purchase'}
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

