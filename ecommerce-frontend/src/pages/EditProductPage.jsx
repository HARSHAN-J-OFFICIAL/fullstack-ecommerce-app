import {
    useEffect,
    useState
}
from "react";

import {
    useParams,
    useNavigate
}
from "react-router-dom";

import {
    getProductById,
    updateProduct
}
from "../services/productService";

import { getCategories }
from "../services/categoryService";

import { uploadImage }
from "../services/uploadService";

import { toast }
from "react-toastify";

function EditProductPage() {

    const { id } =
        useParams();

    const navigate =
        useNavigate();

    const [name, setName] =
        useState("");

    const [description, setDescription] =
        useState("");

    const [price, setPrice] =
        useState("");

    const [stockQuantity, setStockQuantity] =
        useState("");

    const [imageUrl, setImageUrl] =
        useState("");

    const [image, setImage] =
        useState(null);

    const [categories, setCategories] =
        useState([]);

    const [categoryId, setCategoryId] =
        useState("");

    // FETCH PRODUCT
    const fetchProduct = async () => {

        try {

            const product =
                await getProductById(id);

            setName(
                product.name
            );

            setDescription(
                product.description
            );

            setPrice(
                product.price
            );

            setStockQuantity(
                product.stockQuantity
            );

            setImageUrl(
                product.imageUrl
            );

            setCategoryId(
                product.categoryId
            );

        } catch (error) {

            console.log(error);
        }
    };

    // FETCH CATEGORIES
    const fetchCategories = async () => {

        try {

            const data =
                await getCategories();

            setCategories(data);

        } catch (error) {

            console.log(error);
        }
    };

    useEffect(() => {

        fetchProduct();

        fetchCategories();

    }, []);

    // UPDATE PRODUCT
    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            let finalImageUrl =
                imageUrl;

            // Upload new image if selected
            if (image) {

                finalImageUrl =
                    await uploadImage(image);
            }

            await updateProduct(
                id,
                {
                    name,
                    description,
                    price,
                    stockQuantity,
                    imageUrl: finalImageUrl,
                    categoryId,
                }
            );

            toast.success(
                "Product updated"
            );

            navigate(
                "/admin/products"
            );

        } catch (error) {

            console.log(error);

            toast.error(
                "Update failed"
            );
        }
    };

    return (

        <div className="p-8 max-w-xl mx-auto">

            <h1 className="text-4xl font-bold mb-8">

                Edit Product

            </h1>

            <form
                onSubmit={handleSubmit}
                className="space-y-4"
            >

                <input
                    type="text"
                    placeholder="Product Name"
                    value={name}
                    onChange={(e) =>
                        setName(e.target.value)}
                    className="border p-3 w-full rounded-xl"
                />

                <textarea
                    placeholder="Description"
                    value={description}
                    onChange={(e) =>
                        setDescription(e.target.value)}
                    className="border p-3 w-full rounded-xl"
                />

                <input
                    type="number"
                    placeholder="Price"
                    value={price}
                    onChange={(e) =>
                        setPrice(e.target.value)}
                    className="border p-3 w-full rounded-xl"
                />

                <input
                    type="number"
                    placeholder="Stock Quantity"
                    value={stockQuantity}
                    onChange={(e) =>
                        setStockQuantity(e.target.value)}
                    className="border p-3 w-full rounded-xl"
                />

                {/* CATEGORY DROPDOWN */}
                <select
                    value={categoryId}
                    onChange={(e) =>
                        setCategoryId(e.target.value)}
                    className="border p-3 w-full rounded-xl"
                >

                    <option value="">
                        Select Category
                    </option>

                    {categories.map((category) => (

                        <option
                            key={category.id}
                            value={category.id}
                        >
                            {category.name}
                        </option>
                    ))}

                </select>

                {/* IMAGE PREVIEW */}
                <img
                    src={imageUrl}
                    alt="preview"
                    className="w-full h-52 object-cover rounded-xl"
                />

                {/* IMAGE INPUT */}
                <input
                    type="file"
                    onChange={(e) =>
                        setImage(e.target.files[0])}
                    className="border p-3 w-full rounded-xl"
                />

                <button
                    type="submit"
                    className="bg-black text-white px-6 py-3 rounded-xl w-full"
                >
                    Update Product
                </button>

            </form>

        </div>
    );
}

export default EditProductPage;