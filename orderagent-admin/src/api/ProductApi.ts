import api from 'axios';

export const fetchProducts = async () => {
  const response = await api.get('/products'); // 예: /api/products
  return response.data;
};
