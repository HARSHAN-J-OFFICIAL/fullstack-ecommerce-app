import api from "../api/axios";

export const createProduct = async (productData) => {

    const response =
        await api.post(
            "/products",
            productData
        );

    return response.data;
};

export const getProducts = async (

    page = 0,

    size = 8

) => {

    const response =
        await api.get(
            `/products?page=${page}&size=${size}`
        );

    return response.data;
};

export const deleteProduct = async (id) => {

    const response =
        await api.delete(`/products/${id}`);

    return response.data;
};

export const updateProduct = async (
    id,
    productData
) => {

    const response =
        await api.put(
            `/products/${id}`,
            productData
        );

    return response.data;
};

export const getProductById = async (id) => {

    const response =
        await api.get(`/products/${id}`);

    return response.data;
};

export const searchProducts = async (
    keyword
) => {

    const response =
        await api.get(
            `/products/search?keyword=${keyword}`
        );

    return response.data;
};

export const getProductsByCategory =
    async (categoryId) => {

        const response =
            await api.get(
                `/products/category/${categoryId}`
            );

        return response.data;
};