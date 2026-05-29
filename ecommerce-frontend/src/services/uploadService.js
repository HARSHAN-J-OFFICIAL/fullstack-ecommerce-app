import api from "../api/axios";

export const uploadImage = async (file) => {

    const formData =
        new FormData();

    formData.append(
        "file",
        file
    );

    const response =
        await api.post(
            "/products/upload-image",
            formData
        );

    return response.data;
};