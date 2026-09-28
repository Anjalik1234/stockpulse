// src/components/SummaryCards.jsx
import React from 'react';

const SummaryCards = ({ products, pricingSuggestions, reorderSuggestions }) => {
  // Calculate summary metrics
  const totalProducts = products.length;
  
  const lowStockProducts = products.filter(product => 
    product.stockLevel <= product.reorderThreshold && product.status !== 'OUT_OF_STOCK'
  ).length;
  
  const outOfStockProducts = products.filter(product => 
    product.status === 'OUT_OF_STOCK'
  ).length;
  
  const pendingSuggestions = [...pricingSuggestions, ...reorderSuggestions].filter(
    suggestion => suggestion.status === 'PENDING'
  ).length;

  return (
    <div className="summary-cards">
      <div className="card summary-card">
        <h3>Total Products</h3>
        <p className="summary-value">{totalProducts}</p>
      </div>
      
      <div className="card summary-card">
        <h3>Low Stock Products</h3>
        <p className="summary-value low-stock">{lowStockProducts}</p>
      </div>
      
      <div className="card summary-card">
        <h3>Out of Stock Products</h3>
        <p className="summary-value out-of-stock">{outOfStockProducts}</p>
      </div>
      
      <div className="card summary-card">
        <h3>Pending Suggestions</h3>
        <p className="summary-value pending">{pendingSuggestions}</p>
      </div>
    </div>
  );
};

export default SummaryCards;