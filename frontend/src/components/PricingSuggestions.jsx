// src/components/PricingSuggestions.jsx
import React from 'react';

const PricingSuggestions = ({ suggestions, onAccept, onReject }) => {
  const pendingSuggestions = suggestions.filter(suggestion => suggestion.status === 'PENDING');

  if (pendingSuggestions.length === 0) {
    return (
      <div className="section">
        <h2>Pricing Suggestions</h2>
        <div className="empty-state">
          <p>No pending pricing suggestions.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="section">
      <h2>Pricing Suggestions</h2>
      <div className="suggestions-list">
        {pendingSuggestions.map((suggestion) => (
          <div key={suggestion.id} className="card suggestion-card">
            <div className="suggestion-info">
              <h3>Pricing Recommendation</h3>
              <p className="sku">Product ID: {suggestion.productId}</p>
              <div className="suggestion-details">
                <div className="detail-row">
                  <span className="label">Current Price:</span>
                  <span className="value">${suggestion.currentPrice.toFixed(2)}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Recommended Price:</span>
                  <span className="value">${suggestion.recommendedPrice.toFixed(2)}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Direction:</span>
                  <span className={`status-badge direction ${suggestion.direction.toLowerCase()}`}>
                    {suggestion.direction.replace('_', ' ')}
                  </span>
                </div>
                <div className="detail-row">
                  <span className="label">Confidence:</span>
                  <span className="value">{(suggestion.confidence * 100).toFixed(0)}%</span>
                </div>
                <div className="detail-row">
                  <span className="label">Trigger Reason:</span>
                  <span className="value">{suggestion.triggerReason.replace('_', ' ')}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Reasoning:</span>
                  <p className="reasoning">{suggestion.reasoning}</p>
                </div>
              </div>
            </div>
            
            <div className="suggestion-actions">
              <button 
                className="btn btn-success"
                onClick={() => onAccept(suggestion.id)}
              >
                Accept
              </button>
              <button 
                className="btn btn-danger"
                onClick={() => onReject(suggestion.id)}
              >
                Reject
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default PricingSuggestions;