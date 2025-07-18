import api from './api';

export const fetchProducts = async () => {
  const response = await api.get('/products'); // 예: /api/products
  console.log('응답 데이터:', response.data);
  return Array.isArray(response.data) ? response.data : [];
};
