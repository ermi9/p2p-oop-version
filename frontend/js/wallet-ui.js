// js/wallet-ui.js
import { fmt, getLogoPath } from './utils.js';

/**
 * Generates the HTML for a single transaction/activity card.
 * @param {Object} item - The bet or offer object.
 * @param {string} userId - Current logged-in user ID.
 */
export const TransactionCard = (item, userId) => {
    const isOffer = item.entryType === 'OFFER';
    const isSettled = item.status === 'SETTLED';
    const ev = isOffer ? item.event : item.offer?.event;
    
    const league = ev?.leagueName || ""; 
    const hLogo = getLogoPath(league, ev?.homeTeam);
    const aLogo = getLogoPath(league, ev?.awayTeam);
    
    let amount = 0;
    let label = "";
    let isPositive = false;
    
    if (isOffer) {
        amount = item.remainingStake || 0;
        label = "Offer Posted (Escrow)";
    } else {
        const isMaker = parseInt(userId) === item.offer?.maker?.id;
        amount = isMaker ? (item.makerStake || 0) : (item.takerLiability || 0);
        
        if (isSettled) {
            label = "Match Result (Settled)";
            isPositive = true; 
        } else {
            label = "P2P Trade Match (Escrow)";
        }
    }
    
    const selection = (isOffer ? item.outcome : item.offer?.outcome) || "BET";

    return `
    <div class="card" style="padding: 15px 25px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; background: var(--bg-card);">
        <div style="display: flex; align-items: center; gap: 20px;">
            <div class="transaction-logos">
                <img src="${hLogo}" class="logo-img logo-xs" onerror="this.src='data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'">
                <span class="text-muted" style="font-size: 8px;">vs</span>
                <img src="${aLogo}" class="logo-img logo-xs" onerror="this.src='data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'">
            </div>
            <div>
                <span style="display: block; font-weight: bold; font-size: 13px; color: var(--gold);">${label}</span>
                <span class="text-muted" style="font-size: 11px;">Match: ${ev?.homeTeam} vs ${ev?.awayTeam} • Selection: ${selection.replace('_', ' ')}</span>
            </div>
        </div>
        <span class="${isPositive ? 'text-success' : 'text-danger'}" style="font-weight: bold; font-size: 16px;">
            ${isPositive ? '+' : '-'}$${fmt(amount)}
        </span>
    </div>`;
};