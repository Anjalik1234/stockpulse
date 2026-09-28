// src/App.jsx
import React, { useState, useEffect } from 'react';
import Header from './components/Header';
import SummaryCards from './components/SummaryCards';
import ProductList from './components/ProductList';
import PricingSuggestions from './components/PricingSuggestions';
import ReorderSuggestions from './components/ReorderSuggestions';
import {
  getProducts,
  simulateSale,
  updateStock,
  getPricingSuggestions,
  updatePricingSuggestion,
  getReorderSuggestions,
  updateReorderSuggestion
} from './services/api';
import './App.css';

function App() {
  const [products, setProducts] = useState([]);
  const [pricingSuggestions, setPricingSuggestions] = useState([]);
  const [reorderSuggestions, setReorderSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [processingActions, setProcessingActions] = useState(new Set());

  // Fetch all data
  const fetchData = async () => {
    try {
      setLoading(true);
      const [productsData, pricingData, reorderData] = await Promise.all([
        getProducts(),
        getPricingSuggestions(),
        getReorderSuggestions()
      ]);
      
      setProducts(productsData);
      setPricingSuggestions(pricingData);
      setReorderSuggestions(reorderData);
      setError(null);
    } catch (err) {
      setError('Failed to fetch data. Please make sure the backend is running.');
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  // Initial data fetch
  useEffect(() => {
    fetchData();
  }, []);

  // Poll for suggestions every 3 seconds
  useEffect(() => {
    const interval = setInterval(() => {
      Promise.all([
        getPricingSuggestions(),
        getReorderSuggestions()
      ]).then(([pricingData, reorderData]) => {
        setPricingSuggestions(pricingData);
        setReorderSuggestions(reorderData);
      }).catch(err => {
        console.error('Error polling suggestions:', err);
      });
    }, 3000);

    return () => clearInterval(interval);
  }, []);
  // Handle simulate sale
  const handleSale = async (productId, quantity) => {
    const actionKey = `sale-${productId}`;
    if (processingActions.has(actionKey)) return;

    try {
      setProcessingActions(prev => new Set([...prev, actionKey]));
      await simulateSale(productId, quantity);
      await fetchData(); // Refresh all data
    } catch (err) {
      setError('Failed to process sale. Please try again.');
      console.error('Error processing sale:', err);
    } finally {
      setProcessingActions(prev => {
        const newSet = new Set(prev);
        newSet.delete(actionKey);
        return newSet;
      });
    }
  };

  // Handle update stock
  const handleUpdateStock = async (productId, stockLevel) => {
    const actionKey = `stock-${productId}`;
    if (processingActions.has(actionKey)) return;

    try {
      setProcessingActions(prev => new Set([...prev, actionKey]));
      await updateStock(productId, stockLevel);
      await fetchData(); // Refresh all data
    } catch (err) {
      setError('Failed to update stock. Please try again.');
      console.error('Error updating stock:', err);
    } finally {
      setProcessingActions(prev => {
        const newSet = new Set(prev);
        newSet.delete(actionKey);
        return newSet;
      });
    }
  };

  // Handle accept pricing suggestion
  const handleAcceptPricing = async (suggestionId) => {
    const actionKey = `accept-pricing-${suggestionId}`;
    if (processingActions.has(actionKey)) return;

    try {
      setProcessingActions(prev => new Set([...prev, actionKey]));
      await updatePricingSuggestion(suggestionId, 'ACCEPTED');
      await fetchData(); // Refresh all data
    } catch (err) {
      setError('Failed to accept pricing suggestion. Please try again.');
      console.error('Error accepting pricing suggestion:', err);
    } finally {
      setProcessingActions(prev => {
        const newSet = new Set(prev);
        newSet.delete(actionKey);
        return newSet;
      });
    }
  };

  // Handle reject pricing suggestion
  const handleRejectPricing = async (suggestionId) => {
    const actionKey = `reject-pricing-${suggestionId}`;
    if (processingActions.has(actionKey)) return;

    try {
      setProcessingActions(prev => new Set([...prev, actionKey]));
      await updatePricingSuggestion(suggestionId, 'REJECTED');
      await fetchData(); // Refresh all data
    } catch (err) {
      setError('Failed to reject pricing suggestion. Please try again.');
      console.error('Error rejecting pricing suggestion:', err);
    } finally {
      setProcessingActions(prev => {
        const newSet = new Set(prev);
        newSet.delete(actionKey);
        return newSet;
      });
    }
  };

  // Handle accept reorder suggestion
  const handleAcceptReorder = async (suggestionId) => {
    const actionKey = `accept-reorder-${suggestionId}`;
    if (processingActions.has(actionKey)) return;

    try {
      setProcessingActions(prev => new Set([...prev, actionKey]));
      await updateReorderSuggestion(suggestionId, 'ACCEPTED');
      await fetchData(); // Refresh all data
    } catch (err) {
      setError('Failed to accept reorder suggestion. Please try again.');
      console.error('Error accepting reorder suggestion:', err);
    } finally {
      setProcessingActions(prev => {
        const newSet = new Set(prev);
        newSet.delete(actionKey);
        return newSet;
      });
    }
  };

  // Handle reject reorder suggestion
  const handleRejectReorder = async (suggestionId) => {
    const actionKey = `reject-reorder-${suggestionId}`;
    if (processingActions.has(actionKey)) return;

    try {
      setProcessingActions(prev => new Set([...prev, actionKey]));
      await updateReorderSuggestion(suggestionId, 'REJECTED');
      await fetchData(); // Refresh all data
    } catch (err) {
      setError('Failed to reject reorder suggestion. Please try again.');
      console.error('Error rejecting reorder suggestion:', err);
    } finally {
      setProcessingActions(prev => {
        const newSet = new Set(prev);
        newSet.delete(actionKey);
        return newSet;
      });
    }
  };

  if (loading) {
    return (
      <div className="app">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading StockPulse dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="app">
        <div className="error-container">
          <h2>Error</h2>
          <p>{error}</p>
          <button className="btn btn-primary" onClick={fetchData}>
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="app">
      <Header />
      
      <main className="main-content">
        <SummaryCards 
          products={products}
          pricingSuggestions={pricingSuggestions}
          reorderSuggestions={reorderSuggestions}
        />
        
        <ProductList 
          products={products}
          onSale={handleSale}
          onUpdateStock={handleUpdateStock}
        />
        
        <PricingSuggestions 
          suggestions={pricingSuggestions}
          onAccept={handleAcceptPricing}
          onReject={handleRejectPricing}
        />
        
        <ReorderSuggestions 
          suggestions={reorderSuggestions}
          onAccept={handleAcceptReorder}
          onReject={handleRejectReorder}
        />
      </main>
    </div>
  );
}

export default App;
