package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.vo.Odds;

public class BestOddsResult {
    private final Odds homeOdds;
    private final String homeSource;
    private final Odds awayOdds;
    private final String awaySource;
    private final Odds drawOdds;
    private final String drawSource;

    public BestOddsResult(Odds homeOdds, String homeSource, Odds awayOdds, String awaySource, Odds drawOdds, String drawSource) {
        this.homeOdds = homeOdds;
        this.homeSource = homeSource;
        this.awayOdds = awayOdds;
        this.awaySource = awaySource;
        this.drawOdds = drawOdds;
        this.drawSource = drawSource;
    }

    public Odds getHomeOdds() { return homeOdds; }
    public String getHomeSource() { return homeSource; }
    public Odds getAwayOdds() { return awayOdds; }
    public String getAwaySource() { return awaySource; }
    public Odds getDrawOdds() { return drawOdds; }
    public String getDrawSource() { return drawSource; }
}
