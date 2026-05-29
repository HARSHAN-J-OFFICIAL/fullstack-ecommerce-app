import { BrowserRouter, Routes, Route, Link } from "react-router-dom";

import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import Navbar from "./components/Navbar";
import CartPage from "./pages/CartPage";
import OrdersPage from "./pages/OrderPage";
import ProtectedRoute from "./components/ProtectedRoute";
import AdminCreateProductPage from "./pages/AdminCreateProductPage";
import AdminRoute from "./components/AdminRoute";
import AdminProductsPage from "./pages/AdminProductsPage";
import EditProductPage from "./pages/EditProductPage";
import ProductDetailsPage from "./pages/ProductDetailsPage";

function App() {

  return (

    <BrowserRouter>

      <Navbar />
      <Routes>

        <Route
          path="/"
          element={<HomePage />}
        />

        <Route
        path="/orders"
        element={
            <ProtectedRoute>

                <OrdersPage />

            </ProtectedRoute>
        }
      />

          <Route
      path="/cart"
      element={
        <ProtectedRoute>

            <CartPage />

        </ProtectedRoute>
    }
/>

        <Route
          path="/login"
          element={<LoginPage />}
        />

        <Route
          path="/register"
          element={<RegisterPage />}
        />
        
        <Route
            path="/admin/create-product"
            element={
                <AdminRoute>

                    <AdminCreateProductPage />

                </AdminRoute>
            }
        />   

        <Route
            path="/admin/products"
            element={
                <AdminRoute>

                    <AdminProductsPage />

                </AdminRoute>
            }
        />

        <Route
            path="/admin/edit-product/:id"
            element={
                <AdminRoute>

                    <EditProductPage />

                </AdminRoute>
            }
        />

        <Route
            path="/products/:id"
            element={<ProductDetailsPage />}
        />
      </Routes>
      <Link to="/orders">
        Orders
        </Link>

    </BrowserRouter>
  );
}

export default App;