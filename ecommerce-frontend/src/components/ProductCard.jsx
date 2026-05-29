import { addToCart } from "../services/cartService";
import { toast } from "react-toastify";
import { Link } from "react-router-dom";

function ProductCard({ product }) {

    const handleAddToCart = async () => {

        try {

            await addToCart(
                product.id,
                1
            );

            toast.success("Added to cart");

        } catch (error) {

            console.log(error);

           toast.error("Failed to add to cart");
        }
    };

    return (

        <Link
        to={`/products/${product.id}`}
    >
        <div className="border rounded-2xl p-4 shadow-lg">

        <img
            src={product.imageUrl}
            alt={product.name}
            className="w-full h-52 object-cover rounded-xl"
        />
        
            <h2 className="text-2xl font-bold">
                {product.name}
            </h2>

            <p className="text-gray-600 mt-2">
                {product.description}
            </p>

            <p className="text-xl font-semibold mt-4">
                ₹ {product.price}
            </p>

            <p className="mt-2">
                Stock: {product.stockQuantity}
            </p>

            <button
                onClick={handleAddToCart}
                className="mt-4 bg-black text-white px-4 py-2 rounded-xl w-full"
            >
                Add To Cart
            </button>

        </div>
        </Link>
    );
}

export default ProductCard;