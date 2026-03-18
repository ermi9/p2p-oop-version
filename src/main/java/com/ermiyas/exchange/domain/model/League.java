package com.ermiyas.exchange.domain.model;

public enum League {
    PREMIER_LEAGUE("Premier League", "soccer_epl"),
    LA_LIGA("La Liga", "soccer_spain_la_liga"),
    BUNDESLIGA("Bundesliga", "soccer_germany_bundesliga"),
    SERIE_A("Serie A", "soccer_italy_serie_a"),
    LIGUE_1("Ligue 1", "soccer_france_ligue_one");

    private final String displayName;
    private final String apiKey;

    League(String displayName, String apiKey) {
        this.displayName = displayName;
        this.apiKey = apiKey;
    }

    public String getDisplayName() { return displayName; }
    public String getApiKey() { return apiKey; }
}
