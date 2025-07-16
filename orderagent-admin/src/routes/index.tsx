import { createBrowserRouter } from 'react-router-dom';
import Dashboard from '../pages/Dashboard';
import ProductList from '../pages/ProductList';
import OrderLog from '../pages/OrderLog';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Dashboard />,
  },
  {
    path: '/products',
    element: <ProductList />,
  },
  {
    path: '/orders',
    element: <OrderLog />,
  },
]);

export default router;
