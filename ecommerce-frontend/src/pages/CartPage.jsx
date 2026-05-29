import { useEffect, useState } from "react";

import {getCart,removeCartItem} from "../services/cartService";

import { placeOrder } from "../services/orderService";

import {createPaymentOrder} from "../services/paymentService";

function CartPage() {

    const [cart, setCart] = useState(null);

    const fetchCart = async () => {

        try {

            const data = await getCart();

            setCart(data);

        } catch (error) {

            console.log(error);
        }
    };

    useEffect(() => {

        fetchCart();

    }, []);

    const handleRemove = async (itemId) => {

        try {

            await removeCartItem(itemId);

            fetchCart();

        } catch (error) {

            console.log(error);
        }
    };

    const handleCheckout = async () => {

        try {

            await placeOrder();

            alert("Order placed successfully");

            fetchCart();

        } catch (error) {

            console.log(error);

            alert("Checkout failed");
        }
    };

     if (!cart) {

        return <div>Loading...</div>;
    }

    const totalPrice =
    cart.items.reduce(

        (total, item) =>

            total +
            (
                item.product.price *
                item.quantity
            ),

        0
);

    const handlePayment = async () => {

    try {

        console.log("STEP 1");

        const order =
            await createPaymentOrder(
                totalPrice
            );

        console.log("ORDER:", order);

        const options = {

            key: "YOUR_SECRET",

            amount: order.amount,

            currency: order.currency,

            name: "Ecommerce App",

            description: "Test Payment",

            order_id: order.id,

            handler: async function (
                response
            ) {

                console.log(
                    "PAYMENT SUCCESS",
                    response
                );

                await placeOrder();

                alert(
                    "Order placed"
                );
            }
        };

        console.log("STEP 2");

        const razorpay =
            new window.Razorpay(
                options
            );

        console.log("STEP 3");

        razorpay.open();

        console.log("STEP 4");

    } catch (error) {

        console.log(
            "PAYMENT ERROR",
            error
        );
    }
};

    return (

        <div className="p-8">

            <h1 className="text-4xl font-bold mb-8">
                My Cart
            </h1>

            {cart.items.length === 0 ? (

                <p>Cart is empty</p>

            ) : (

                <div className="space-y-4">

                    {cart.items.map((item) => (

                        <div
                            key={item.id}
                            className="border p-4 rounded flex justify-between items-center"
                        >

                            <div>

                                <h2 className="text-xl font-bold">
                                    {item.product.name}
                                </h2>

                                <p>
                                    Quantity: {item.quantity}
                                </p>

                                <p>
                                    ₹ {item.product.price}
                                </p>

                            </div>

                            <button
                                onClick={() =>
                                    handleRemove(item.id)
                                }
                                className="bg-red-500 text-white px-4 py-2 rounded"
                            >
                                Remove
                            </button>

                        </div>
                    ))}

                    <div className="mt-8">

                        <button
                            onClick={handleCheckout}
                            className="bg-black text-white px-6 py-3 rounded-xl"
                        >
                            Place Order
                        </button>

                        <button
                            onClick={handlePayment}
                            className="bg-green-600 text-white px-6 py-3 rounded-xl"
                        >
                            Checkout
                        </button>

                    </div>

                </div>
            )}

        </div>
    );
}

export default CartPage;