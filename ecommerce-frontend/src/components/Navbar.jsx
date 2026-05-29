import { Link, useNavigate }
from "react-router-dom";

function Navbar() {

    const navigate =
        useNavigate();

    const token =
        localStorage.getItem("token");

    const role =
        localStorage.getItem("role");

    const handleLogout = () => {

        localStorage.removeItem("token");

        localStorage.removeItem("role");

        navigate("/login");
    };

    return (

        <nav className="flex justify-between items-center p-4 shadow">

            <Link
                to="/"
                className="text-2xl font-bold"
            >
                E-Commerce
            </Link>

            <div className="flex items-center gap-4">

                <Link to="/cart">

                    Cart

                </Link>

                <Link to="/orders">

                    Orders

                </Link>

                {role === "ROLE_ADMIN" && (

                    <>

                        <Link
                            to="/admin/create-product"
                            className="bg-black text-white px-4 py-2 rounded-xl"
                        >
                            Create Product
                        </Link>

                        <Link
                            to="/admin/products"
                            className="bg-blue-600 text-white px-4 py-2 rounded-xl"
                        >
                            Manage Products
                        </Link>

                    </>

                )}

                {!token ? (

                    <>

                        <Link to="/login">

                            Login

                        </Link>

                        <Link to="/register">

                            Register

                        </Link>

                    </>

                ) : (

                    <button
                        onClick={handleLogout}
                        className="bg-red-500 text-white px-4 py-2 rounded-xl"
                    >
                        Logout
                    </button>

                )}

            </div>

        </nav>
    );
}

export default Navbar;