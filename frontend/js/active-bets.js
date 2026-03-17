// js/active-bets.js
import { api } from './utils.js';
import { BetCard } from './active-bets-ui.js';

const userId = localStorage.getItem('userId');

document.addEventListener('DOMContentLoaded', () => userId ? load() : location.href = "login.html");

async function load() {
    const data = await api.get("/activity/summary");
    
    // Explicit error check
    if (!data) {
        document.getElementById('matched-list').innerHTML = '<p class="text-danger">Failed to load activity.</p>';
        return;
    }
    
    // Filter logic preserved from previous fix
    const liveMatches = (data.matchedBets || []).filter(b => b.status === 'MATCHED');

    renderList('unmatched-list', data.openOffers || [], false);
    renderList('matched-list', liveMatches, true);
}

function renderList(containerId, items, isLive) {
    const container = document.getElementById(containerId);
    if (!items?.length) {
        container.innerHTML = '<p class="text-muted">No records found.</p>';
        return;
    }

    // Delegate to UI Component
    container.innerHTML = items.map(item => BetCard(item, userId, isLive)).join('');
}

window.cancelOffer = async (id) => {
    if (confirm("Cancel this offer?")) {
        const res = await api.delete(`/exchange/offers/${id}`);
        if (res.ok) {
            location.reload();
        } else {
            const error = await res.json();
            alert(error.message || "Failed to cancel offer.");
        }
    }
};