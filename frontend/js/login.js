// js/login.js
import { api } from './utils.js';

document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const loginData = {
        username: document.getElementById('login-user').value,
        password: document.getElementById('login-pass').value
    };

    try {
        const response = await api.post("/users/login", loginData);

        if (response.ok) {
            const user = await response.json(); 
            
            // Store session data exactly as returned by UserController
            localStorage.setItem('userId', user.id);
            localStorage.setItem('username', user.username);
            localStorage.setItem('userRole', user.role); 

            if (user.role === 'ADMIN') {
                window.location.href = "admin-dashboard.html";
            } else {
                window.location.href = "markets.html";
            }
        } else {
            const errorData = await response.json().catch(() => ({ message: "Invalid credentials" }));
            alert("Login Failed: " + errorData.message);
        }
    } catch (error) {
        alert("Connection failed. Check if Java backend is running on 8080.");
    }
});