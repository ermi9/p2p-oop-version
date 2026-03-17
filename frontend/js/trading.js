// js/trading.js
import { api, fmt } from './utils.js';
import { MatchHeader, OfferCard } from './trading-ui.js';

const urlParams = new URLSearchParams(window.location.search);
const [matchId, leagueName, userId] = [urlParams.get('matchId'), urlParams.get('league') || "League", localStorage.getItem('userId')];

document.addEventListener('DOMContentLoaded', () => {
    if (!userId || !matchId) return window.location.href = "markets.html";
    loadData();
    setupListeners();
});

async function loadData() {
    const [wallet, data] = await Promise.all([api.get(`/wallet/${userId}`), api.get(`/markets/fixtures/${matchId}`)]);
    if (wallet) document.getElementById('user-balance-nav').innerText = `Wallet: $${fmt(wallet.availableBalance)}`;
    if (!data) return;

    document.getElementById('match-title').innerHTML = MatchHeader(data, leagueName);
    
//refernence odds
    document.getElementById('reference-odds-container').innerHTML = [
        { l: '1', v: data.homeOdds, s: data.homeSource },
        { l: 'X', v: data.drawOdds, s: data.drawSource },
        { l: '2', v: data.awayOdds, s: data.awaySource }
    ].map(o => `
        <div style="flex:1; text-align:center; background:var(--bg-dark); padding:6px; border-radius:8px; min-width: 60px;">
            <span class="text-muted" style="font-size:9px; display:block;">${o.l}</span>
            <span style="font-size:8px; color:var(--text-muted); display:block; height: 10px;">${o.s || ''}</span>
            <strong class="text-gold" style="font-size:16px;">${fmt(o.v)}</strong>
        </div>`).join('');

    const active = (data.offers || []).filter(o => !['TAKEN', 'CANCELLED'].includes(o.status));
    document.getElementById('offers-grid').innerHTML = active.length ? active.map(o => OfferCard(o, userId)).join('') : '<p class="text-muted">No offers yet.</p>';

    document.querySelectorAll('.match-amount-input').forEach(i => i.oninput = (e) => {
        const risk = (parseFloat(e.target.value) || 0) * (parseFloat(e.target.dataset.odds) - 1);
        document.getElementById(`risk-display-${e.target.dataset.id}`).innerText = `$${fmt(risk)}`;
    });
}

window.showAcceptStake = (id) => { toggle(id, true); };
window.hideAcceptStake = (id) => { toggle(id, false); };
const toggle = (id, s) => {
    document.getElementById(`action-area-${id}`).style.display = s ? 'none' : 'block';
    document.getElementById(`stake-entry-${id}`).style.display = s ? 'block' : 'none';
};


window.cancelOffer = async (id) => {
    if (confirm("Cancel this offer?")) {
        const res = await api.delete(`/exchange/offers/${id}`);
        if (res.ok) location.reload();
    }
};

function setupListeners() {
    document.getElementById('create-offer-form').onsubmit = async (e) => {
        e.preventDefault();
        const body = { makerId: parseInt(userId), eventId: parseInt(matchId), outcome: e.target[0].value, odds: parseFloat(e.target[1].value), stake: parseFloat(e.target[2].value) };
        const res = await api.post("/exchange/offers", body);
        res.ok ? location.reload() : alert((await res.json()).message);
    };

    document.getElementById('offers-grid').onclick = async (e) => {
        if (!e.target.classList.contains('btn-match')) return;
        const id = e.target.dataset.id;
        const amount = document.getElementById(`match-input-${id}`).value;
        const res = await api.post("/exchange/trades/match", { takerId: parseInt(userId), offerId: parseInt(id), amountToMatch: parseFloat(amount) });
        res.ok ? location.reload() : alert((await res.json()).message || "Match Failed");
    };
}