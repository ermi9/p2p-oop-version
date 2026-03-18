package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.League;
import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.vo.Odds;

import java.util.List;
import java.util.Map;

public interface SportsDataProvider {
    boolean supports(SportRequest request);
    List<Event> fetchUpcomingFixtures(SportRequest request);
    Map<String, BestOddsResult> fetchBestOddsWithSources(SportRequest request);
    Map<String, List<Odds>> fetchBestOdds(SportRequest request);
    Map<String, Integer[]> fetchScores(SportRequest request);

    class SportRequest {
        private final League league;
        private final MarketType marketType;

        public SportRequest(League league, MarketType marketType) {
            this.league = league;
            this.marketType = marketType;
        }

        public League getLeague() { return league; }
        public MarketType getMarketType() { return marketType; }
    }
}
