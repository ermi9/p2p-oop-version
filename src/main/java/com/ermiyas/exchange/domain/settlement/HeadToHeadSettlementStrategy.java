package com.ermiyas.exchange.domain.settlement;

import com.ermiyas.exchange.domain.model.Outcome;

public class HeadToHeadSettlementStrategy implements SettlementStrategy {

    @Override
    public Outcome determineWinner(int homeScore, int awayScore) {
        if (homeScore > awayScore) return Outcome.HOME_WIN;
        if (awayScore > homeScore) return Outcome.AWAY_WIN;
        throw new IllegalStateException("Head-to-head markets cannot end in a draw");
    }
}
