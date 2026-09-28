// src/components/Header.jsx
import React, { useState, useEffect } from 'react';

const Header = () => {
  const [isConnected, setIsConnected] = useState(false);

  useEffect(() => {
    // Simple check to see if backend is reachable
    const checkConnection = async () => {
      try {
        const response = await fetch('http://localhost:8080/products');
        setIsConnected(response.ok);
      } catch (error) {
        setIsConnected(false);
      }
    };

    checkConnection();
    const interval = setInterval(checkConnection, 5000); // Check every 5 seconds

    return () => clearInterval(interval);
  }, []);

  return (
    <header className="header">
      <div className="header-content">
        <div className="header-text">
          <h1>StockPulse</h1>
          <p className="subtitle">Reactive Commerce Advisor</p>
        </div>
        <div className="connection-indicator">
          <span className={`indicator ${isConnected ? 'connected' : 'disconnected'}`}></span>
          <span className="status-text">
            {isConnected ? 'Connected' : 'Disconnected'}
          </span>
        </div>
      </div>
    </header>
  );
};

export default Header;