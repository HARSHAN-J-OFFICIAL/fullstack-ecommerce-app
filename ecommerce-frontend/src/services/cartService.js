import api from "../api/axios";

// GET CART
export const getCart = async () => {

    const response =
        await api.get("/cart");

    return response.data;
};

// ADD TO CART
export const addToCart = async (

    productId,

    quantity
) => {

    const response =
        await api.post(

            `/cart/add?productId=${productId}&quantity=${quantity}`
        );

    return response.data;
};

// REMOVE ITEM
export const removeCartItem = async (
    itemId
) => {

    const response =
        await api.delete(

            `/cart/remove/${itemId}`
        );

    return response.data;
};