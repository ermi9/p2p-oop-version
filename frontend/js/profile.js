// js/profile.js
import { api, fmt } from './utils.js';

const userId = localStorage.getItem('userId');

document.addEventListener('DOMContentLoaded', () => {
    if (!userId) return window.location.href = "login.html";
    loadData();
    setupListeners();
});

async function loadData() {
    // Fetching current user details and match stats
    const [user, activity] = await Promise.all([
        api.get(`/users/${userId}`),
        api.get("/activity/summary")
    ]);

    if (user) {
        document.getElementById('prof-username').value = user.username || "";
        document.getElementById('prof-email').value = user.email || "";
    }

    if (activity) {
        const total = (activity.openOffers?.length || 0) + (activity.matchedBets?.length || 0);
        document.getElementById('stat-total-bets').innerText = total;
    }
}

function setupListeners() {
    //  Persistent password change
    document.getElementById('update-password-btn').addEventListener('click', async () => {
        const password = document.getElementById('new-password').value;
        if (!password || password.length < 8) return alert("Min. 8 characters required.");

        // Sync: Backend expects JSON body {"password": "..."}
        const res = await api.patch(`/users/${userId}/password`, { password: password });
        
        if (res.ok) {
            alert("Password successfully updated and persisted!");
            document.getElementById('new-password').value = "";
        } else {
            alert("Failed to update password. Please try again.");
        }
    });

    document.getElementById('profile-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        alert("Personal info update feature coming soon (Requires backend Controller update).");
    });
}