import api from "../api/axios";

// PLACE ORDER
export const placeOrder = async () => {

    const response =
        await api.post("/orders");

    return response.data;
};

// GET MY ORDERS
export const getMyOrders = async () => {

    const response =
        await api.get("/orders/my-orders");

    return response.data;
};