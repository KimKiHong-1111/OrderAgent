// src/pages/ProductList.tsx
import { useEffect, useState } from 'react';
import { fetchProducts } from '../api/ProductApi';

function ProductList() {
  const [products, setProducts] = useState<any[]>([]);

  useEffect(() => {
    fetchProducts()
    .then(setProducts)
    .catch((err) => {
      console.error('Failed to fetch products:', err);
    });
  }, []);

  return (
      <div style={{ padding: '2rem' }}>
        <h1>📦 상품 목록</h1>
        {products.length === 0 ? (
            <p>로딩 중...</p>
        ) : (
            <ul>
              {products.map((p, i) => (
                  <li key={i}>
                    {p.name} - 가격: {p.price}원 {p.soldOut ? '(품절)' : ''}
                  </li>
              ))}
            </ul>
        )}
      </div>
  );
}

export default ProductList;
