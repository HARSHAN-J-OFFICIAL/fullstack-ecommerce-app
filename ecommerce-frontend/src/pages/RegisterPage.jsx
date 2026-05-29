import { useState } from "react";

import { registerUser } from "../services/authService";

function RegisterPage() {

    const [formData, setFormData] = useState({

        username: "",
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

            const data = await registerUser(formData);

            alert(data);

        } catch (error) {

            console.log(error);

            alert("Registration failed");
        }
    };

    return (

        <div className="flex justify-center items-center min-h-screen">

            <form
                onSubmit={handleSubmit}
                className="border p-8 rounded-xl w-96 shadow-lg space-y-4"
            >

                <h1 className="text-2xl font-bold text-center">
                    Register
                </h1>

                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    onChange={handleChange}
                    className="w-full border p-2 rounded"
                />

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
                    Register
                </button>

            </form>

        </div>
    );
}

export default RegisterPage;