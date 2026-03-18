package com.ermiyas.exchange.domain.logic;

import com.ermiyas.exchange.domain.model.MarketType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettlementStrategyFactory {
    private final Map<MarketType, SettlementStrategy> strategies = new HashMap<>();

    public SettlementStrategyFactory(List<SettlementStrategy> strategyList) {
        for (SettlementStrategy strategy : strategyList) {
            strategies.put(strategy.getMarketType(), strategy);
        }
    }

    public SettlementStrategy getStrategy(MarketType type) {
        SettlementStrategy strategy = strategies.get(type);
        return strategy != null ? strategy : new ThreeWaySettlementStrategy();
    }
}
