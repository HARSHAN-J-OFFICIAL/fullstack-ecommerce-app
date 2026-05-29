import { useEffect, useState } from "react";

import ProductCard from "../components/ProductCard";

// import { getProducts } from "../services/productService";


import { getCategories } from "../services/categoryService";

import {getProducts,getProductsByCategory,searchProducts} from "../services/productService";

function HomePage() {

    const [products, setProducts] =
        useState([]);

    const [search, setSearch] =
        useState("");

    const [categories, setCategories] =
        useState([]);

    const [selectedCategory,setSelectedCategory] =
        useState("");

    const [page, setPage] =
        useState(0);

    const [totalPages, setTotalPages] =
        useState(0);

    const fetchProducts = async (
    currentPage = 0
) => {

    try {

        const data =
            await getProducts(
                currentPage
            );

        setProducts(
            data.content
        );

        setTotalPages(
            data.totalPages
        );

        setPage(
            data.number
        );

    } catch (error) {

        console.log(error);

        setProducts([]);
    }
};

    const handleSearch = async () => {

    try {

        if (!search.trim()) {

            fetchProducts();
            return;
        }

        const data =
            await searchProducts(search);

        setProducts(data.content);

    } catch (error) {

        console.log(error);
    }
};

const fetchCategories = async () => {

    try {

        const data =
            await getCategories();

        setCategories(data);

    } catch (error) {

        console.log(error);
    }
};

const handleCategoryFilter =
    async (categoryId) => {

        try {

            setSelectedCategory(
                categoryId
            );

            if (!categoryId) {

                fetchProducts();
                return;
            }

            const data =
                await getProductsByCategory(
                    categoryId
                );

            setProducts(
                data.content
            );

        } catch (error) {

            console.log(error);
        }
};

    useEffect(() => {

    fetchProducts(page);

    fetchCategories();

}, [page]);

    return (

        

        <div className="p-8">

            <h1 className="text-5xl font-bold mb-10">

    Products

</h1>

<div className="flex gap-4 mb-8">

    <input
        type="text"
        placeholder="Search products..."
        value={search}
        onChange={(e) =>
            setSearch(e.target.value)}
        className="border p-3 rounded-xl flex-1"
    />

    <button
        onClick={handleSearch}
        className="bg-black text-white px-6 rounded-xl"
    >
        Search
    </button>

</div>

<div className="flex justify-center gap-4 mt-10">

    <button
        disabled={page === 0}
        onClick={() =>
            setPage(page - 1)}
        className="bg-gray-200 px-4 py-2 rounded-xl disabled:opacity-50"
    >
        Previous
    </button>

    <p className="text-lg font-semibold">

        Page {page + 1}
        {" "}
        of
        {" "}
        {totalPages}

    </p>

    <button
        disabled={page + 1 === totalPages}
        onClick={() =>
            setPage(page + 1)}
        className="bg-black text-white px-4 py-2 rounded-xl disabled:opacity-50"
    >
        Next
    </button>

</div>

<div className="flex gap-4 mb-8 flex-wrap">

    <button
        onClick={() =>
            handleCategoryFilter("")}
        className="bg-black text-white px-4 py-2 rounded-xl"
    >
        All
    </button>

    {categories.map((category) => (

        <button
            key={category.id}
            onClick={() =>
                handleCategoryFilter(category.id)}
            className={`px-4 py-2 rounded-xl ${
                selectedCategory === category.id
                    ? "bg-black text-white"
                    : "bg-gray-200"
            }`}
        >
            {category.name}
        </button>
    ))}

</div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">

                {products?.map((product) => (

                    <ProductCard
                        key={product.id}
                        product={product}
                    />
                ))}

            </div>

        </div>
        
    );
}

export default HomePage;