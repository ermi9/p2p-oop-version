package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.logic.SettlementStrategy;
import com.ermiyas.exchange.domain.vo.Odds;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event {
    private final Long id;
    private final String externalId;
    private final String homeTeam;
    private final String awayTeam;
    private final LocalDateTime startTime;
    private final League league;
    private final MarketType marketType;
    private EventStatus status;
    private Outcome result;
    private Odds refHomeOdds;
    private Odds refAwayOdds;
    private Odds refDrawOdds;
    private String refHomeSource;
    private String refAwaySource;
    private String refDrawSource;
    private Integer finalHomeScore;
    private Integer finalAwayScore;
    private final List<Offer> offers;

    public Event(Long id, String externalId, String homeTeam, String awayTeam, LocalDateTime startTime, League league, MarketType marketType) {
        this.id = id;
        this.externalId = externalId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.startTime = startTime;
        this.league = league;
        this.marketType = marketType;
        this.status = EventStatus.OPEN;
        this.offers = new ArrayList<>();
    }

    public Long getId() { return id; }
    public String getExternalId() { return externalId; }
    public String getHomeTeam() { return homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public LocalDateTime getStartTime() { return startTime; }
    public League getLeague() { return league; }
    public MarketType getMarketType() { return marketType; }
    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
    public Outcome getResult() { return result; }
    public Odds getRefHomeOdds() { return refHomeOdds; }
    public Odds getRefAwayOdds() { return refAwayOdds; }
    public Odds getRefDrawOdds() { return refDrawOdds; }
    public String getRefHomeSource() { return refHomeSource; }
    public String getRefAwaySource() { return refAwaySource; }
    public String getRefDrawSource() { return refDrawSource; }
    public Integer getFinalHomeScore() { return finalHomeScore; }
    public Integer getFinalAwayScore() { return finalAwayScore; }
    public List<Offer> getOffers() { return Collections.unmodifiableList(offers); }

    public void attachOffer(Offer offer) {
        this.offers.add(offer);
    }

    public void updateReferenceOdds(Odds homeOdds, String homeSource, Odds awayOdds, String awaySource, Odds drawOdds, String drawSource) {
        this.refHomeOdds = homeOdds;
        this.refHomeSource = homeSource;
        this.refAwayOdds = awayOdds;
        this.refAwaySource = awaySource;
        this.refDrawOdds = drawOdds;
        this.refDrawSource = drawSource;
    }

    public void processResult(int homeScore, int awayScore, SettlementStrategy strategy) throws ExchangeException {
        validateSettlementState();
        this.finalHomeScore = homeScore;
        this.finalAwayScore = awayScore;
        this.result = strategy.determineWinner(homeScore, awayScore);
        this.status = EventStatus.COMPLETED;
    }

    public void markAsSettled() throws ExchangeException {
        if (this.status != EventStatus.COMPLETED) {
            throw new IllegalBetException("State Violation: Cannot settle an event that is not COMPLETED.");
        }
        for (Offer offer : offers) {
            OfferStatus offerStatus = offer.getStatus();
            if (offerStatus == OfferStatus.OPEN || offerStatus == OfferStatus.PARTIALLY_TAKEN) {
                throw new IllegalBetException("Integrity Error: Offer #" + offer.getId() + " is still active. Finalize or Cancel it first.");
            }
        }
        this.status = EventStatus.SETTLED;
    }

    private void validateSettlementState() throws IllegalBetException {
        if (this.status == EventStatus.SETTLED) {
            throw new IllegalBetException("Integrity Error: Event is already settled and immutable.");
        }
    }
}
