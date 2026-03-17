// js/wallets.js
import { api, fmt } from './utils.js';
import { TransactionCard } from './wallet-ui.js';

const userId = localStorage.getItem('userId');

document.addEventListener('DOMContentLoaded', () => {
    if (!userId) return window.location.href = "login.html";
    loadWalletData();
    setupActions();
});

async function loadWalletData() {
    // API calls remain the same
    const [wallet, activity] = await Promise.all([
        api.get(`/wallet/${userId}`),
        api.get("/activity/summary")
    ]);

    if (wallet) {
        document.getElementById('available-balance').innerText = fmt(wallet.availableBalance);
        document.getElementById('reserved-balance').innerText = fmt(wallet.reservedBalance);
    }

    const list = document.getElementById('transaction-list');
    
    // Combine data types
    const history = [
        ...(activity.openOffers || []).map(o => ({ ...o, entryType: 'OFFER' })),
        ...(activity.matchedBets || []).map(b => ({ ...b, entryType: 'TRADE' }))
    ];

    if (history.length === 0) {
        list.innerHTML = '<p class="text-muted" style="text-align: center; padding: 20px;">No recent activity found.</p>';
        return;
    }

    // STACK LOGIC: Sort by ID Descending (Newest at top)
    history.sort((a, b) => (b.id || 0) - (a.id || 0));

    // Render using the UI Helper
    let html = "";
    for (let i = 0; i < history.length; i++) {
        html += TransactionCard(history[i], userId);
    }
    list.innerHTML = html;
}

function setupActions() {
    document.getElementById('deposit-btn').addEventListener('click', async () => {
        const amount = prompt("Enter deposit amount ($):");
        if (amount && !isNaN(amount) && amount > 0) {
            const res = await api.post(`/wallet/${userId}/deposit`, { amount: parseFloat(amount) });
            if (res.ok) {
                alert("Deposit successful!");
                loadWalletData();
            }
        }
    });

    document.getElementById('withdraw-btn').addEventListener('click', async () => {
        const amount = prompt("Enter withdrawal amount ($):");
        if (amount && !isNaN(amount) && amount > 0) {
            const res = await api.post(`/wallet/${userId}/withdraw`, { amount: parseFloat(amount) });
            if (res.ok) {
                alert("Withdrawal successful!");
                loadWalletData();
            } else {
                alert("Withdrawal failed: Check your available balance.");
            }
        }
    });
}