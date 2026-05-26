package com.ermiyas.exchange.domain.model;

public enum League {
    PREMIER_LEAGUE("Premier League"),
    LA_LIGA("La Liga"),
    BUNDESLIGA("Bundesliga"),
    SERIE_A("Serie A"),
    LIGUE_1("Ligue 1"),
    CHAMPIONS_LEAGUE("Champions League");

    private final String displayName;

    League(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    @Override
    public String toString() { return displayName; }
}
