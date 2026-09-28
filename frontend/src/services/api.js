// src/services/api.js
const API_BASE_URL = 'http://localhost:8080';

// Helper function to handle API responses
const handleResponse = async (response) => {
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  return await response.json();
};

// Products API
export const getProducts = () => {
  return fetch(`${API_BASE_URL}/products`)
    .then(handleResponse);
};

export const simulateSale = (id, quantity) => {
  return fetch(`${API_BASE_URL}/products/${id}/orders`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ quantity }),
  }).then(handleResponse);
};

export const updateStock = (id, stockLevel) => {
  return fetch(`${API_BASE_URL}/products/${id}/stock`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ stockLevel }),
  }).then(handleResponse);
};

// Pricing Suggestions API
export const getPricingSuggestions = () => {
  return fetch(`${API_BASE_URL}/pricing-suggestions`)
    .then(handleResponse);
};

export const updatePricingSuggestion = (id, status) => {
  return fetch(`${API_BASE_URL}/pricing-suggestions/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ status }),
  }).then(handleResponse);
};

// Reorder Suggestions API
export const getReorderSuggestions = () => {
  return fetch(`${API_BASE_URL}/reorder-suggestions`)
    .then(handleResponse);
};

export const updateReorderSuggestion = (id, status) => {
  return fetch(`${API_BASE_URL}/reorder-suggestions/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ status }),
  }).then(handleResponse);
};