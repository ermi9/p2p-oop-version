package com.ermiyas.exchange.domain.logic;

import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.model.Outcome;

public class ThreeWaySettlementStrategy implements SettlementStrategy {
    @Override
    public Outcome determineWinner(int homeScore, int awayScore) {
        if (homeScore > awayScore) return Outcome.HOME_WIN;
        if (awayScore > homeScore) return Outcome.AWAY_WIN;
        return Outcome.DRAW;
    }

    @Override
    public MarketType getMarketType() {
        return MarketType.THREE_WAY;
    }
}
