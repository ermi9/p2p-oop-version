package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.settlement.SettlementStrategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event {

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("dd MMM HH:mm");

    private final Long          id;
    private final String        homeTeam;
    private final String        awayTeam;
    private final LocalDateTime kickOff;
    private final League        league;
    private final MarketType    marketType;
    private EventStatus         status;
    private Outcome             result;
    private Integer             homeScore;
    private Integer             awayScore;
    private final List<Offer>   offers;

    public Event(Long id, String homeTeam, String awayTeam, LocalDateTime kickOff,
                 League league, MarketType marketType) {
        this.id         = id;
        this.homeTeam   = homeTeam;
        this.awayTeam   = awayTeam;
        this.kickOff    = kickOff;
        this.league     = league;
        this.marketType = marketType;
        this.status     = EventStatus.OPEN;
        this.offers     = new ArrayList<>();
    }

    public Long          getId()         { return id; }
    public String        getHomeTeam()   { return homeTeam; }
    public String        getAwayTeam()   { return awayTeam; }
    public LocalDateTime getKickOff()    { return kickOff; }
    public League        getLeague()     { return league; }
    public MarketType    getMarketType() { return marketType; }
    public EventStatus   getStatus()     { return status; }
    public Outcome       getResult()     { return result; }
    public Integer       getHomeScore()  { return homeScore; }
    public Integer       getAwayScore()  { return awayScore; }
    public List<Offer>   getOffers()     { return Collections.unmodifiableList(offers); }

    public List<Offer> getOpenOffers() {
        List<Offer> result = new ArrayList<>();
        for (Offer o : offers) {
            if (o.getStatus() == OfferStatus.OPEN || o.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                result.add(o);
            }
        }
        return result;
    }

    public List<Bet> getMatchedBets() {
        List<Bet> result = new ArrayList<>();
        for (Offer o : offers) {
            for (Bet b : o.getBets()) {
                if (b.getStatus() == BetStatus.MATCHED) {
                    result.add(b);
                }
            }
        }
        return result;
    }

    public void attachOffer(Offer offer) {
        this.offers.add(offer);
    }

    public void processResult(int homeScore, int awayScore, SettlementStrategy strategy) throws ExchangeException {
        if (this.status != EventStatus.OPEN)
            throw new IllegalBetException("Event is not OPEN — cannot process result");
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.result    = strategy.determineWinner(homeScore, awayScore);
        this.status    = EventStatus.COMPLETED;
    }

    public void markSettled() throws ExchangeException {
        if (this.status != EventStatus.COMPLETED)
            throw new IllegalBetException("Event must be COMPLETED before it can be SETTLED");
        this.status = EventStatus.SETTLED;
    }

    public String getDisplayName()  { return homeTeam + " vs " + awayTeam; }
    public String getKickOffLabel() { return kickOff.format(DISPLAY_FMT); }
}
