package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.infrastructure.sports.dto.TheOddsApiOddsDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class H2HMarketStrategy implements MarketStrategy {
    private static final double OUTLIER_THRESHOLD = 3.0;
    private final FixtureNameMatcher nameMatcher;

    public H2HMarketStrategy(FixtureNameMatcher nameMatcher) {
        this.nameMatcher = nameMatcher;
    }

    @Override
    public boolean supports(MarketType type) {
        return type == MarketType.THREE_WAY;
    }

    @Override
    public String getMarketKey() {
        return "h2h";
    }

    @Override
    public BestOddsResult calculateBestOddsWithSources(TheOddsApiOddsDto dto) {
        List<PricePoint> homePool = new ArrayList<>();
        List<PricePoint> awayPool = new ArrayList<>();
        List<PricePoint> drawPool = new ArrayList<>();
        if (dto.getBookmakers() == null) return null;
        for (TheOddsApiOddsDto.Bookmaker bm : dto.getBookmakers()) {
            String providerTitle = bm.getTitle() != null ? bm.getTitle() : "Unknown";
            if (bm.getMarkets() == null) continue;
            for (TheOddsApiOddsDto.Market market : bm.getMarkets()) {
                if (market.getOutcomes() == null) continue;
                for (TheOddsApiOddsDto.Outcome outcome : market.getOutcomes()) {
                    double price = outcome.getPrice();
                    if (nameMatcher.namesMatch(outcome.getName(), dto.getHomeTeam())) {
                        homePool.add(new PricePoint(price, providerTitle));
                    } else if (nameMatcher.namesMatch(outcome.getName(), dto.getAwayTeam())) {
                        awayPool.add(new PricePoint(price, providerTitle));
                    } else if (outcome.getName() != null && outcome.getName().equalsIgnoreCase("Draw")) {
                        drawPool.add(new PricePoint(price, providerTitle));
                    }
                }
            }
        }
        PricePoint bestHome = findBestRealistic(homePool);
        PricePoint bestAway = findBestRealistic(awayPool);
        PricePoint bestDraw = findBestRealistic(drawPool);
        return new BestOddsResult(Odds.of(bestHome.getPrice()), bestHome.getSource(), Odds.of(bestAway.getPrice()), bestAway.getSource(), Odds.of(bestDraw.getPrice()), bestDraw.getSource());
    }

    private PricePoint findBestRealistic(List<PricePoint> pool) {
        if (pool.isEmpty()) return new PricePoint(1.0, "N/A");
        if (pool.size() == 1) return pool.get(0);
        pool.sort(Comparator.comparingDouble(PricePoint::getPrice));
        double median;
        int size = pool.size();
        if (size % 2 == 0) {
            median = (pool.get(size / 2 - 1).getPrice() + pool.get(size / 2).getPrice()) / 2.0;
        } else {
            median = pool.get(size / 2).getPrice();
        }
        PricePoint best = pool.get(0);
        for (PricePoint p : pool) {
            if (p.getPrice() <= median * OUTLIER_THRESHOLD && p.getPrice() > best.getPrice()) {
                best = p;
            }
        }
        return best;
    }

    @Override
    public List<Odds> calculateBestOdds(TheOddsApiOddsDto dto) {
        BestOddsResult result = calculateBestOddsWithSources(dto);
        List<Odds> oddsList = new ArrayList<>();
        oddsList.add(result.getHomeOdds());
        oddsList.add(result.getAwayOdds());
        oddsList.add(result.getDrawOdds());
        return oddsList;
    }

    private static class PricePoint {
        private final double price;
        private final String source;
        PricePoint(double price, String source) { this.price = price; this.source = source; }
        double getPrice() { return price; }
        String getSource() { return source; }
    }
}
