// js/admin-ui.js
import { fmt, getLogoPath } from './utils.js';

export const UserCard = (user) => {
    return `
    <div class="card" style="padding: 15px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
        <div>
            <strong style="display: block; font-size: 13px;">@${user.username}</strong>
            <span class="text-muted" style="font-size: 10px;">ID: ${user.id} | ${user.roleName || 'USER'}</span>
        </div>
        <button class="btn btn-danger" style="padding: 6px 12px; font-size: 11px;" onclick="window.removeUser(${user.id})">Delete</button>
    </div>`;
};

export const SettlementCard = (f, league) => {
    const hLogo = getLogoPath(league, f.homeTeam);
    const aLogo = getLogoPath(league, f.awayTeam);
    const isCompleted = f.status === 'COMPLETED';

    return `
    <div class="card" style="padding: 15px; display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px;">
        <div style="display: flex; align-items: center; gap: 15px;">
            <div style="display: flex; align-items: center; gap: 5px; width: 60px; justify-content: center;">
                <img src="${hLogo}" class="logo-img logo-xs" onerror="this.src='data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'">
                <img src="${aLogo}" class="logo-img logo-xs" onerror="this.src='data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7'">
            </div>
            <div>
                <span style="font-weight: bold; font-size: 13px;">${f.homeTeam} vs ${f.awayTeam}</span>
                <span class="text-muted" style="display: block; font-size: 10px;">
                    ${isCompleted ? 
                        `Ready to Settle: ${f.finalHomeScore} - ${f.finalAwayScore}` : 
                        `Status: ${f.status} | Activity: ${f.offerCount} Offers`}
                </span>
            </div>
        </div>
        ${isCompleted ? 
            `<button class="btn btn-gold" style="padding: 8px 16px; font-size: 11px;" onclick="window.settleMatch('${f.externalId}')">Settle Market</button>` :
            `<span class="text-muted" style="font-size: 11px; font-weight: bold; border: 1px solid var(--border-muted); padding: 4px 12px; border-radius: 4px;">LIVE MARKET</span>`
        }
    </div>`;
};