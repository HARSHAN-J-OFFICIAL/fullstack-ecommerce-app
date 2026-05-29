import { useEffect, useState } from "react";

import {getProducts,deleteProduct}from "../services/productService";
import { toast } from "react-toastify";
import { Link } from "react-router-dom";
function AdminProductsPage() {

    const [products, setProducts] =
        useState([]);

    const fetchProducts = async () => {

        try {

            const data =
                await getProducts();

            setProducts(data.content);

        } catch (error) {

            console.log(error);
        }
    };

    useEffect(() => {

        fetchProducts();

    }, []);

    const handleDelete = async (id) => {

        try {

            await deleteProduct(id);

            toast.success(
                "Product deleted"
            );

            fetchProducts();

        } catch (error) {

            console.log(error);

            toast.error(
                "Delete failed"
            );
        }
    };

    return (

        <div className="p-8">

            <h1 className="text-4xl font-bold mb-8">

                Admin Products

            </h1>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

                {products.map((product) => (

                    <div
                        key={product.id}
                        className="border rounded-xl p-4 shadow"
                    >

                        <img
                            src={product.imageUrl}
                            alt={product.name}
                            className="w-full h-48 object-cover rounded-xl"
                        />

                        <h2 className="text-2xl font-bold mt-4">

                            {product.name}

                        </h2>

                        <p className="mt-2">

                            ₹ {product.price}

                        </p>

                        <p className="mt-2">

                            Stock:
                            {" "}
                            {product.stockQuantity}

                        </p>
                        <Link
                            to={`/admin/edit-product/${product.id}`}
                            className="bg-blue-600 text-white px-4 py-2 rounded-xl mt-4 block text-center"
                        >
                            Edit
                        </Link>
                        <button
                            onClick={() =>
                                handleDelete(product.id)}
                            className="bg-red-500 text-white px-4 py-2 rounded-xl mt-4 w-full"
                        >
                            Delete
                        </button>

                    </div>
                ))}

            </div>

        </div>
    );
}

export default AdminProductsPage;