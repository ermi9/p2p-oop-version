// js/utils.js
import { teamNameMap } from './mapping/mapping.js';

const API_BASE_URL = "http://localhost:8080/api/v1";

const api = {
    get headers() {
        const id = localStorage.getItem('userId');
        return { 
            'X-User-Id': id, 
            'X-Admin-Id': id, 
            'Content-Type': 'application/json' 
        };
    },

    async get(path) {
        try {
            const res = await fetch(`${API_BASE_URL}${path}`, { headers: this.headers });
            if (!res.ok) {
                const error = await res.json().catch(() => ({ message: "Unknown error" }));
                console.error(`GET ${path} failed:`, error);
                return null;
            }
            return await res.json();
        } catch (e) {
            console.error("Connection Error:", e);
            return null;
        }
    },

    async post(path, body) {
        return fetch(`${API_BASE_URL}${path}`, { 
            method: 'POST', 
            headers: this.headers, 
            body: JSON.stringify(body) 
        });
    },

    async patch(path, body) {
        return fetch(`${API_BASE_URL}${path}`, { 
            method: 'PATCH', 
            headers: this.headers, 
            body: JSON.stringify(body) 
        });
    },

    async delete(path) {
        const res = await fetch(`${API_BASE_URL}${path}`, { method: 'DELETE', headers: this.headers });
        return res.ok;
    }
};

const getLogoPath = (leagueName, teamName) => {
    const leagueFolderMap = {
        "Premier League": "England - Premier League",
        "La Liga": "Spain - LaLiga", 
        "Bundesliga": "Germany - Bundesliga",
        "Serie A": "Italy - Serie A",
        "Ligue 1": "France - Ligue 1"
    };

    const folder = leagueFolderMap[leagueName];
    


    const fileName = teamNameMap[teamName] || teamName;
    return `logos/${folder}/${fileName}.png`;
};

const fmt = (num) => (num || 0).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });

export { api, fmt, getLogoPath };