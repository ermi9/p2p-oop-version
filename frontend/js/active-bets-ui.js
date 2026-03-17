// js/active-bets-ui.js
import { fmt, getLogoPath } from './utils.js';

export const BetCard = (item, userId, isLive) => {
    const ev = isLive ? item.offer?.event : item.event;
    const league = ev?.leagueName || ""; 
    const hLogo = getLogoPath(league, ev?.homeTeam);
    const aLogo = getLogoPath(league, ev?.awayTeam);
    const date = ev?.startTime ? new Date(ev.startTime) : null;
    
    // Logic Sync: Standardized Risk/Win calculation
    let risk = 0, win = 0;
    if (isLive) {
        const isTkr = parseInt(userId) === item.taker?.id;
        risk = isTkr ? (item.takerLiability || 0) : (item.makerStake || 0);
        win = isTkr ? (item.makerStake || 0) : (item.takerLiability || 0);
    } else {
        risk = item.remainingStake || 0;
        win = risk * ((item.odds || 1) - 1);
    }

    const rawOutcome = (isLive ? item.offer?.outcome : item.outcome) || "BET";
    const displayOutcome = typeof rawOutcome === 'string' ? rawOutcome.replace('_', ' ') : "MATCH";

    return `
    <div class="card" style="border-left: 4px solid var(--${isLive ? 'success' : 'gold'}); margin-bottom: 15px;">
        <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 10px; font-weight: bold;">
            <span class="text-gold uppercase">SELECTION: ${displayOutcome}</span>
            <span class="${isLive ? 'text-success' : 'text-muted'}">${isLive ? '• LIVE MATCH' : '• WAITING'}</span>
        </div>
        
        <div style="text-align: center; padding: 10px 0;">
            <div style="display: flex; align-items: center; justify-content: center; gap: 15px; margin-bottom: 8px;">
                <div style="display: flex; align-items: center; gap: 8px; flex: 1; justify-content: flex-end;">
                    <span style="font-weight: bold; font-size: 13px;">${ev?.homeTeam || "Team"}</span>
                    <img src="${hLogo}" class="logo-img logo-sm" onerror="this.src='data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'">
                </div>
                <span class="text-muted" style="font-size: 11px; font-weight: bold;">VS</span>
                <div style="display: flex; align-items: center; gap: 8px; flex: 1; justify-content: flex-start;">
                    <img src="${aLogo}" class="logo-img logo-sm" onerror="this.src='data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'">
                    <span style="font-weight: bold; font-size: 13px;">${ev?.awayTeam || "Team"}</span>
                </div>
            </div>
            <div style="font-size: 11px;" class="text-muted">
                ${date ? date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' | ' + date.toLocaleDateString([], { month: 'short', day: 'numeric' }) : "Date TBD"}
            </div>
        </div>

        <div style="display: flex; justify-content: space-between; background: var(--bg-dark); padding: 12px; border-radius: 8px; margin-top: 15px; text-align: center;">
            <div><label class="text-muted" style="font-size: 9px; display: block;">LIABILITY</label><b class="text-danger">$${fmt(risk)}</b></div>
            <div><label class="text-muted" style="font-size: 9px; display: block;">PROFIT</label><b class="text-success">$${fmt(win)}</b></div>
            <div><label class="text-muted" style="font-size: 9px; display: block;">ODDS</label><b>@ ${fmt(item.odds)}</b></div>
        </div>
        ${!isLive ? `<button class="btn btn-danger" style="width: 100%; margin-top: 15px; font-size: 11px;" onclick="window.cancelOffer(${item.id})">Cancel Offer</button>` : ''}
    </div>`;
};