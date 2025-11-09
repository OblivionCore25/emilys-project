/**
 * API Configuration for Drain Adoption Application
 * 
 * This module centralizes all API URL configuration.
 * - In development: Uses http://localhost:8080
 * - In production: Uses the URL set in REACT_APP_API_URL environment variable
 */
const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://dau3cgehnpx9r.cloudfront.net';

/**
 * Helper function to construct full API URLs
 * @param {string} endpoint - The API endpoint path (e.g., '/api/drains')
 * @returns {string} The complete API URL
 */
export const getApiUrl = (endpoint) => {
  // Ensure endpoint starts with /
  const normalizedEndpoint = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
  return `${API_BASE_URL}${normalizedEndpoint}`;
};

/**
 * Check if running in production mode
 */
export const isProduction = () => process.env.NODE_ENV === 'production';

/**
 * Log API configuration (only in development)
 */
if (!isProduction()) {
  console.log('API Configuration:', {
    baseUrl: API_BASE_URL,
    environment: process.env.NODE_ENV,
  });
}

export default API_BASE_URL;
