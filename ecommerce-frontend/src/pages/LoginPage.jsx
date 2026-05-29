import { useState } from "react";

import { useNavigate } from "react-router-dom";

import { loginUser } from "../services/authService";

import { jwtDecode } from "jwt-decode";
function LoginPage() {

    const navigate = useNavigate();

    const [formData, setFormData] = useState({

        email: "",
        password: "",
    });

    const handleChange = (e) => {

        setFormData({

            ...formData,

            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        try {

            const data = await loginUser(formData);

            // SAVE JWT TOKEN
            // SAVE TOKEN
            localStorage.setItem(
                "token",
                data.token
            );

            // DECODE TOKEN
            const decoded =
                jwtDecode(data.token);

            // SAVE ROLE
            localStorage.setItem(
                "role",
                decoded.role
            );

            alert("Login successful");

            navigate("/");

        } catch (error) {

            console.log(error);

            alert("Invalid credentials");
        }
    };

    return (

        <div className="flex justify-center items-center min-h-screen">

            <form
                onSubmit={handleSubmit}
                className="border p-8 rounded-xl w-96 shadow-lg space-y-4"
            >

                <h1 className="text-2xl font-bold text-center">
                    Login
                </h1>

                <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    onChange={handleChange}
                    className="w-full border p-2 rounded"
                />

                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    onChange={handleChange}
                    className="w-full border p-2 rounded"
                />

                <button
                    type="submit"
                    className="w-full bg-black text-white p-2 rounded"
                >
                    Login
                </button>

            </form>

        </div>
    );
}

export default LoginPage;