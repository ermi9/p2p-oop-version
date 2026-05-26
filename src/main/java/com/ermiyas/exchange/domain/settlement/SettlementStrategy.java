package com.ermiyas.exchange.domain.settlement;

import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.model.Outcome;

public interface SettlementStrategy {
    Outcome determineWinner(int homeScore, int awayScore);
    MarketType getMarketType();
}
