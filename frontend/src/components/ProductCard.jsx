// src/components/ProductCard.jsx
import React, { useState } from 'react';

const ProductCard = ({ product, onSale, onUpdateStock }) => {
  const [isSaleModalOpen, setIsSaleModalOpen] = useState(false);
  const [isStockModalOpen, setIsStockModalOpen] = useState(false);
  const [saleQuantity, setSaleQuantity] = useState(1);
  const [newStockLevel, setNewStockLevel] = useState(product.stockLevel);

  const handleSaleSubmit = (e) => {
    e.preventDefault();
    onSale(product.id, saleQuantity);
    setIsSaleModalOpen(false);
    setSaleQuantity(1);
  };

  const handleStockSubmit = (e) => {
    e.preventDefault();
    onUpdateStock(product.id, newStockLevel);
    setIsStockModalOpen(false);
  };

  return (
    <>
      <div className="card product-card">
        <div className="product-info">
          <h3>{product.name}</h3>
          <p className="sku">SKU: {product.sku}</p>
          <div className="product-details">
            <div className="detail-row">
              <span className="label">Category:</span>
              <span className="value">{product.category}</span>
            </div>
            <div className="detail-row">
              <span className="label">Price:</span>
              <span className="value">${product.currentPrice.toFixed(2)}</span>
            </div>
            <div className="detail-row">
              <span className="label">Stock:</span>
              <span className={`value ${product.stockLevel <= product.reorderThreshold ? 'low-stock' : ''}`}>
                {product.stockLevel}
              </span>
            </div>
            <div className="detail-row">
              <span className="label">Reorder Threshold:</span>
              <span className="value">{product.reorderThreshold}</span>
            </div>
            <div className="detail-row">
              <span className="label">Demand Velocity:</span>
              <span className="value">{product.demandVelocity}</span>
            </div>
            <div className="detail-row">
              <span className="label">Status:</span>
              <span className={`status-badge ${product.status.toLowerCase()}`}>
                {product.status.replace('_', ' ')}
              </span>
            </div>
          </div>
        </div>
        
        <div className="product-actions">
          <button 
            className="btn btn-primary"
            onClick={() => setIsSaleModalOpen(true)}
          >
            Simulate Sale
          </button>
          <button 
            className="btn btn-secondary"
            onClick={() => setIsStockModalOpen(true)}
          >
            Update Stock
          </button>
        </div>
      </div>

      {/* Sale Modal */}
      {isSaleModalOpen && (
        <div className="modal-overlay" onClick={() => setIsSaleModalOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Simulate Sale</h2>
            <p>Product: {product.name}</p>
            <form onSubmit={handleSaleSubmit}>
              <div className="form-group">
                <label htmlFor="quantity">Quantity:</label>
                <input
                  type="number"
                  id="quantity"
                  min="1"
                  value={saleQuantity}
                  onChange={(e) => setSaleQuantity(Math.max(1, parseInt(e.target.value) || 1))}
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setIsSaleModalOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Process Sale
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Stock Update Modal */}
      {isStockModalOpen && (
        <div className="modal-overlay" onClick={() => setIsStockModalOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Update Stock</h2>
            <p>Product: {product.name}</p>
            <form onSubmit={handleStockSubmit}>
              <div className="form-group">
                <label htmlFor="stockLevel">New Stock Level:</label>
                <input
                  type="number"
                  id="stockLevel"
                  min="0"
                  value={newStockLevel}
                  onChange={(e) => setNewStockLevel(Math.max(0, parseInt(e.target.value) || 0))}
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setIsStockModalOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Update Stock
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
};

export default ProductCard;