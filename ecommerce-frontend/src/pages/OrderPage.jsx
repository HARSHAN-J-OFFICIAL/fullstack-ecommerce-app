import { useEffect, useState } from "react";

import { getMyOrders } from "../services/orderService";

function OrdersPage() {

    const [orders, setOrders] =
        useState([]);

    const fetchOrders = async () => {

        try {

            const data =
                await getMyOrders();

            setOrders(data);

        } catch (error) {

            console.log(error);
        }
    };

    useEffect(() => {

        fetchOrders();

    }, []);

    return (

        <div className="p-8">

            <h1 className="text-4xl font-bold mb-8">

                My Orders

            </h1>

            <div className="space-y-6">

                {orders.map((order) => (

                    <div
                        key={order.id}
                        className="border rounded-xl p-6 shadow"
                    >

                        <h2 className="text-2xl font-bold">
                            Order #{order.id}
                        </h2>

                        <p className="mt-2">
                            Status:
                            {" "}
                            {order.status}
                        </p>

                        <div className="mt-4 space-y-2">

                            {order.items.map((item) => (

                                <div
                                    key={item.id}
                                    className="border-b pb-2"
                                >

                                    <p>
                                        {item.product.name}
                                    </p>

                                    <p>
                                        Quantity:
                                        {" "}
                                        {item.quantity}
                                    </p>

                                </div>
                            ))}

                        </div>

                    </div>
                ))}

            </div>

        </div>
    );
}

export default OrdersPage;