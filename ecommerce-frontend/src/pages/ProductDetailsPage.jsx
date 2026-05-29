import {
    useEffect,
    useState
}
from "react";

import {
    useParams
}
from "react-router-dom";

import {
    getProductById
}
from "../services/productService";

import {
    addToCart
}
from "../services/cartService";

import { toast }
from "react-toastify";

function ProductDetailsPage() {

    const { id } =
        useParams();

    const [product, setProduct] =
        useState(null);

    const fetchProduct = async () => {

        try {

            const data =
                await getProductById(id);

            setProduct(data);

        } catch (error) {

            console.log(error);
        }
    };

    useEffect(() => {

        fetchProduct();

    }, []);

    const handleAddToCart =
        async () => {

            try {

                await addToCart(
                    product.id,
                    1
                );

                toast.success(
                    "Added to cart"
                );

            } catch (error) {

                console.log(error);

                toast.error(
                    "Failed"
                );
            }
    };

    if (!product) {

        return (
            <div className="p-8">

                Loading...

            </div>
        );
    }

    return (

        <div className="p-8 max-w-6xl mx-auto">

            <div className="grid md:grid-cols-2 gap-10">

                <img
                    src={product.imageUrl}
                    alt={product.name}
                    className="w-full h-[500px] object-cover rounded-2xl shadow-xl"
                />

                <div>

                    <h1 className="text-5xl font-bold">

                        {product.name}

                    </h1>

                    <p className="text-gray-500 mt-4 text-lg">

                        {product.categoryName}

                    </p>

                    <p className="text-3xl font-bold mt-6">

                        ₹ {product.price}

                    </p>

                    <p className="mt-6 text-lg leading-8">

                        {product.description}

                    </p>

                    <p className="mt-6 text-lg">

                        Stock:
                        {" "}
                        {product.stockQuantity}

                    </p>

                    <button
                        onClick={handleAddToCart}
                        className="mt-8 bg-black text-white px-8 py-4 rounded-2xl text-lg"
                    >
                        Add To Cart
                    </button>

                </div>

            </div>

        </div>
    );
}

export default ProductDetailsPage;