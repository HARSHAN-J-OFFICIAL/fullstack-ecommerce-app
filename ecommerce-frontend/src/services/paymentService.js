import api from "../api/axios";

export const createPaymentOrder =
    async (amount) => {

        const response =
            await api.post(
                `/payments/create-order?amount=${amount}`
            );

        return response.data;
};