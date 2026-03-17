// 1. Define the Backend Base URL (Matches your Spring Boot default)
const API_BASE_URL = "http://localhost:8080/api/v1";

// 2. Listen for the form submission
document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault(); // Prevent the default page reload

    // 3. Capture the exact values from your HTML input IDs
    const username = document.getElementById('reg-username').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    // 4. Create the JSON payload (Must match RegisterRequest DTO exactly)
    const registerData = {
        username: username,
        email: email,
        password: password
    };

    try {
        // 5. Send the POST request to the UserController endpoint
        const response = await fetch(`${API_BASE_URL}/users/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(registerData)
        });

        // 6. Handle the response from the server
        if (response.ok) {
            const resultText = await response.text(); // e.g., "User created with ID: 5"
            console.log("Success:", resultText);

            // Extract the numeric ID using a regular expression
            const idMatch = resultText.match(/\d+/);
            if (idMatch) {
                // Save the ID so the user stays "logged in" for the session
                localStorage.setItem('userId', idMatch[0]);
            }

            alert("Account created successfully!");
            window.location.href = "markets.html"; // Redirect to the directory
        } else {
            // Handle backend errors (e.g., Username already exists)
            const errorData = await response.json().catch(() => ({ message: "Server error" }));
            alert("Registration Failed: " + (errorData.message || "Unknown error"));
        }
    } catch (error) {
        console.error("Connection Error:", error);
        alert("Could not reach the server. Please ensure your Java backend is running.");
    }
});