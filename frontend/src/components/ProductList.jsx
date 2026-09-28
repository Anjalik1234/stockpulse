// src/components/ProductList.jsx
import React from 'react';
import ProductCard from './ProductCard';

const ProductList = ({ products, onSale, onUpdateStock }) => {
  if (products.length === 0) {
    return (
      <div className="section">
        <h2>Products</h2>
        <div className="empty-state">
          <p>No products found.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="section">
      <h2>Products</h2>
      <div className="product-grid">
        {products.map((product) => (
          <ProductCard
            key={product.id}
            product={product}
            onSale={onSale}
            onUpdateStock={onUpdateStock}
          />
        ))}
      </div>
    </div>
  );
};

export default ProductList;