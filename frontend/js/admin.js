// js/admin.js
import { api, fmt } from './utils.js';
import { UserCard, SettlementCard } from './admin-ui.js';

document.addEventListener('DOMContentLoaded', () => {
    if (localStorage.getItem('userRole') !== 'ADMIN') {
        window.location.href = "login.html";
        return;
    }
    loadDashboard();
    setupAdminActions();
});

async function loadDashboard() {
    const [stats, users, leaguesMap] = await Promise.all([
        api.get("/admin/stats"),
        api.get("/users"), 
        api.get("/markets/leagues") 
    ]);

    if (stats) {
        document.getElementById('stat-users').innerText = stats.totalUsers || 0;
        document.getElementById('stat-fixtures').innerText = stats.activeFixtures || 0;
        document.getElementById('stat-escrow').innerText = `$${fmt(stats.lockedStake || 0)}`;
    }

    renderUsers(users || []);
    renderSettlementList(leaguesMap || {});
}

function renderUsers(users) {
    const list = document.getElementById('admin-user-list');
    let html = "";
    for (let i = 0; i < users.length; i++) {
        html += UserCard(users[i]);
    }
    list.innerHTML = html || '<p class="text-muted">No users found.</p>';
}

function renderSettlementList(leaguesMap) {
    const list = document.getElementById('fixture-settle-list');
    let html = "";

    // Convert keys to array 
    const leagueNames = Object.keys(leaguesMap);
    for (let i = 0; i < leagueNames.length; i++) {
        const league = leagueNames[i];
        const fixtures = leaguesMap[league];
        
        for (let j = 0; j < fixtures.length; j++) {
            const f = fixtures[j];
            // Filter: Only show active bets not yet settled
            if ((f.offerCount || 0) > 0 && f.status !== 'SETTLED') {
                html += SettlementCard(f, league);
            }
        }
    }
    list.innerHTML = html || '<p class="text-muted" style="text-align: center; padding: 20px;">No active markets found.</p>';
}

window.removeUser = async (id) => {
    if (confirm("Permanently delete this user?") && (await api.delete(`/admin/users/${id}`))) {
        location.reload();
    }
};

window.settleMatch = async (externalId) => {
    if (!confirm(`Process automated scores and settle all bets for match ${externalId}?`)) return;
    const res = await api.post("/admin/settle", { externalIds: [externalId] });
    if (res?.ok) {
        alert("Market settled successfully.");
        location.reload();
    }
};

function setupAdminActions() {
    document.querySelector('.btn-sync')?.addEventListener('click', async () => {
        const res = await api.post("/admin/sync", {});
        if (res.ok) {
            alert("External synchronization complete.");
            location.reload();
        }
    });
}