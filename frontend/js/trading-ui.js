// js/trading-ui.js
import { fmt, getLogoPath } from './utils.js';

/**
 * Renders the top match header with team logos and start time.
 */
export const MatchHeader = (data, leagueName) => {
    const homeLogo = getLogoPath(leagueName, data.homeTeam);
    const awayLogo = getLogoPath(leagueName, data.awayTeam);
    const date = new Date(data.startTime);

    return `
        <div style="display: flex; align-items: center; justify-content: center; gap: 20px;">
            <div style="flex: 1; text-align: right; display: flex; align-items: center; justify-content: flex-end; gap: 12px;">
                <span style="font-weight: bold; font-size: 28px;">${data.homeTeam}</span>
                <img src="${homeLogo}" class="logo-img logo-md" onerror="this.src='logos/default-placeholder.png'">
            </div>
            <div style="text-align: center; min-width: 130px; border-left: 1px solid var(--border-muted); border-right: 1px solid var(--border-muted); padding: 0 20px;">
                <span style="font-weight: bold; font-size: 16px; display: block; color: var(--gold);">${date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                <span class="text-muted" style="font-size: 11px; text-transform: uppercase;">${date.toLocaleDateString([], { month: 'short', day: 'numeric' })}</span>
            </div>
            <div style="flex: 1; text-align: left; display: flex; align-items: center; justify-content: flex-start; gap: 12px;">
                <img src="${awayLogo}" class="logo-img logo-md" onerror="this.src='logos/default-placeholder.png'">
                <span style="font-weight: bold; font-size: 28px;">${data.awayTeam}</span>
            </div>
        </div>`;
};

/**
 * Renders the Peer-to-Peer Offer card.
 * Refactored Logic: Correctly calculates Win Potential vs Stake based on Odds.
 */
export const OfferCard = (o, userId) => {
    const isMyOffer = parseInt(o.maker.id) === parseInt(userId);
    const outcome = typeof o.outcome === 'string' ? o.outcome.replace('_', ' ') : "MATCH";
    
    // In an exchange, Win Potential = Stake * (Odds - 1)
    // The "Stake" is what the Maker risks to win that potential profit.
    const oddsValue = parseFloat(o.odds) || 1;
    const stakeValue = parseFloat(o.remainingStake) || 0;
    const potentialWin = stakeValue * (oddsValue - 1);

    return `
    <div class="card" style="padding: 20px; border: ${isMyOffer ? '1px solid var(--gold)' : '1px solid var(--border-muted)'};">
        <div style="display:flex; justify-content:space-between; margin-bottom:12px;">
            <span class="text-muted" style="font-size:11px;">${isMyOffer ? 'Your Active Offer' : 'User #' + o.maker.id}</span>
            <span class="text-gold" style="font-weight:bold;">@ ${fmt(o.odds)}</span>
        </div>
        <p style="font-size: 14px; margin-bottom: 15px;">Selection: <strong class="text-gold">${outcome}</strong></p>
        
        <div style="display:flex; justify-content:space-between; background:var(--bg-dark); padding:10px; border-radius:8px; margin-bottom:15px;">
            <div>
                <label class="text-muted" style="font-size:9px; display:block;">Win Potential</label>
                <b class="text-success">$${fmt(potentialWin)}</b>
            </div>
            <div style="text-align:right;">
                <label class="text-muted" style="font-size:9px; display:block;">${isMyOffer ? 'Your Stake (Risk)' : 'Liquidity Available'}</label>
                <b class="text-danger">$${fmt(stakeValue)}</b>
            </div>
        </div>

        <div id="action-area-${o.id}">
            ${isMyOffer ? 
                `<button class="btn btn-danger" style="width: 100%; font-size: 12px;" onclick="window.cancelOffer(${o.id})">Cancel Offer</button>` : 
                `<button class="btn" style="width: 100%; background-color: var(--success); color: white; font-size: 12px;" onclick="window.showAcceptStake(${o.id})">Accept Offer</button>`
            }
        </div>

        <div id="stake-entry-${o.id}" style="display: none; margin-top: 10px; border-top: 1px solid var(--border-muted); padding-top: 15px;">
            <div style="display:flex; gap:8px;">
                <input type="number" id="match-input-${o.id}" placeholder="Stake to Match" 
                    class="form-group match-amount-input" 
                    data-id="${o.id}" 
                    data-odds="${o.odds}" 
                    style="margin:0; flex:1; padding:8px;">
                <button class="btn btn-gold btn-match" data-id="${o.id}">Confirm</button>
            </div>
            <div style="display: flex; justify-content: space-between; margin-top: 8px;">
                <span class="text-muted" style="font-size: 10px;">Your Required Liability:</span>
                <b id="risk-display-${o.id}" class="text-danger" style="font-size: 11px;">$0.00</b>
            </div>
            <button class="btn btn-outline" style="width: 100%; margin-top: 10px; font-size: 10px; padding: 4px;" onclick="window.hideAcceptStake(${o.id})">Cancel</button>
        </div>
    </div>`;
};