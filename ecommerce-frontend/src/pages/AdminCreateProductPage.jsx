import {
    useEffect,
    useState
} from "react";

import { uploadImage }
from "../services/uploadService";

import { createProduct }
from "../services/productService";

import { getCategories }
from "../services/categoryService";

import { toast }
from "react-toastify";

function AdminCreateProductPage() {

    const [name, setName] =
        useState("");

    const [description, setDescription] =
        useState("");

    const [price, setPrice] =
        useState("");

    const [stockQuantity, setStockQuantity] =
        useState("");

    const [image, setImage] =
        useState(null);

    const [loading, setLoading] =
        useState(false);

    const [categories, setCategories] =
        useState([]);

    const [categoryId, setCategoryId] =
        useState("");

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

        fetchCategories();

    }, []);

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            setLoading(true);

            let imageUrl = "";

            // Upload image
            if (image) {

                imageUrl =
                    await uploadImage(image);
            }

            // Create product
            await createProduct({

                name,
                description,
                price,
                stockQuantity,
                imageUrl,
                categoryId,
            });

            toast.success(
                "Product created"
            );

            // Reset form
            setName("");

            setDescription("");

            setPrice("");

            setStockQuantity("");

            setImage(null);

            setCategoryId("");

        } catch (error) {

            console.log(error);

            toast.error(
                "Creation failed"
            );

        } finally {

            setLoading(false);
        }
    };

    return (

        <div className="p-8 max-w-xl mx-auto">

            <h1 className="text-4xl font-bold mb-8">

                Create Product

            </h1>

            <form
                onSubmit={handleSubmit}
                className="space-y-4"
            >

                <input
                    type="text"
                    placeholder="Name"
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
                    placeholder="Stock"
                    value={stockQuantity}
                    onChange={(e) =>
                        setStockQuantity(e.target.value)}
                    className="border p-3 w-full rounded-xl"
                />

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

                <input
                    type="file"
                    onChange={(e) =>
                        setImage(e.target.files[0])}
                    className="border p-3 w-full rounded-xl"
                />

                <button
                    type="submit"
                    disabled={loading}
                    className="bg-black text-white px-6 py-3 rounded-xl w-full"
                >

                    {loading
                        ? "Creating..."
                        : "Create Product"}

                </button>

            </form>

        </div>
    );
}

export default AdminCreateProductPage;