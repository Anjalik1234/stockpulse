// src/components/ReorderSuggestions.jsx
import React from 'react';

const ReorderSuggestions = ({ suggestions, onAccept, onReject }) => {
  const pendingSuggestions = suggestions.filter(suggestion => suggestion.status === 'PENDING');

  if (pendingSuggestions.length === 0) {
    return (
      <div className="section">
        <h2>Reorder Suggestions</h2>
        <div className="empty-state">
          <p>No pending reorder suggestions.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="section">
      <h2>Reorder Suggestions</h2>
      <div className="suggestions-list">
        {pendingSuggestions.map((suggestion) => (
          <div key={suggestion.id} className="card suggestion-card">
            <div className="suggestion-info">
              <h3>Reorder Recommendation</h3>
              <p className="sku">Product ID: {suggestion.productId}</p>
              <div className="suggestion-details">
                <div className="detail-row">
                  <span className="label">Current Stock:</span>
                  <span className="value">{suggestion.currentStock}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Recommended Quantity:</span>
                  <span className="value">{suggestion.recommendedQuantity}</span>
                </div>
                <div className="detail-row">
                  <span className="label">Suggested Lead Time:</span>
                  <span className="value">{suggestion.suggestedLeadTimeDays} days</span>
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

export default ReorderSuggestions;