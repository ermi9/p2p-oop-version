// js/markets.js
import { api, fmt, getLogoPath } from './utils.js';

const userId = localStorage.getItem('userId');

document.addEventListener('DOMContentLoaded', () => {
    if (!userId) return window.location.href = "login.html";
    loadUserBalance();
    loadLiveMarkets();
});

async function loadUserBalance() {
    const wallet = await api.get(`/wallet/${userId}`);
    if (wallet) document.getElementById('user-balance').innerText = `Balance: $${fmt(wallet.availableBalance)}`;
}

async function loadLiveMarkets() {
    const leaguesMap = await api.get("/markets/leagues");
    const container = document.getElementById('leagues-container');
    
    if (!leaguesMap || Object.keys(leaguesMap).length === 0) {
        container.innerHTML = '<p class="text-muted" style="text-align: center; padding: 40px;">No live fixtures available.</p>';
        return;
    }

    container.innerHTML = Object.entries(leaguesMap)
        .map(([name, events], i) => renderLeagueAccordion(name, events, i + 1))
        .join('');
}

function renderLeagueAccordion(leagueName, events, index) {
    // FIX: Filter out COMPLETED matches from the Player view
    const openEvents = events.filter(e => e.status === 'OPEN');
    
    if (openEvents.length === 0) return ""; // Hide league if it only has finished matches

    const matchRowsHtml = openEvents.map(event => {
        const date = new Date(event.startTime);
        const timeStr = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        const homeLogo = getLogoPath(leagueName, event.homeTeam);
        const awayLogo = getLogoPath(leagueName, event.awayTeam);
        
        return `
            <a href="trading.html?matchId=${event.id}&league=${encodeURIComponent(leagueName)}" class="match-row" style="justify-content: center; gap: 20px;">
                <div style="flex: 1; text-align: right; display: flex; align-items: center; justify-content: flex-end; gap: 10px;">
                    <span style="font-weight: bold;">${event.homeTeam}</span>
                    <img src="${homeLogo}" class="logo-img logo-sm">
                </div>

                <div style="text-align: center; min-width: 80px;">
                    <span style="font-weight: bold; font-size: 16px; display: block;">${timeStr}</span>
                    <span class="text-muted" style="font-size: 10px; text-transform: uppercase;">${date.toLocaleDateString([], { month: 'short', day: 'numeric' })}</span>
                </div>

                <div style="flex: 1; text-align: left; display: flex; align-items: center; justify-content: flex-start; gap: 10px;">
                    <img src="${awayLogo}" class="logo-img logo-sm">
                    <span style="font-weight: bold;">${event.awayTeam}</span>
                </div>
            </a>`;
    }).join('');

    return `
        <div class="card accordion-item" style="padding: 0; margin-bottom: 10px;">
            <input type="checkbox" id="league-${index}" class="toggle-input">
            <label for="league-${index}" class="accordion-header">
                <span class="text-gold" style="font-weight: bold;">${leagueName}</span>
                <span class="arrow text-muted">▼</span>
            </label>
            <div class="accordion-content">${matchRowsHtml}</div>
        </div>`;
}