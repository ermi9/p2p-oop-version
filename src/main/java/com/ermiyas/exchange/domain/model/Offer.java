package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.model.user.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Offer {

    private final Long id;
    private final User maker;
    private final Event event;
    private final Outcome predictedOutcome;
    private final Money originalStake;
    private Money remainingStake;
    private final Odds odds;
    private OfferStatus status;
    private final List<Bet> bets;

    public Offer(Long id, User maker, Event event, Outcome predictedOutcome, Money stake, Odds odds) {
        this.id               = id;
        this.maker            = Objects.requireNonNull(maker, "maker");
        this.event            = Objects.requireNonNull(event, "event");
        this.predictedOutcome = Objects.requireNonNull(predictedOutcome, "outcome");
        this.originalStake    = Objects.requireNonNull(stake, "stake");
        this.remainingStake   = stake;
        this.odds             = Objects.requireNonNull(odds, "odds");
        this.status           = OfferStatus.OPEN;
        this.bets             = new ArrayList<>();
    }

    public Long       getId()               { return id; }
    public User       getMaker()            { return maker; }
    public Event      getEvent()            { return event; }
    public Outcome    getPredictedOutcome() { return predictedOutcome; }
    public Money      getOriginalStake()    { return originalStake; }
    public Money      getRemainingStake()   { return remainingStake; }
    public Odds       getOdds()             { return odds; }
    public OfferStatus getStatus()          { return status; }
    public List<Bet>  getBets()             { return Collections.unmodifiableList(bets); }

    public Bet fill(Long betId, Money makerStakeToMatch, User taker, String reference) throws ExchangeException {
        validateFillable(makerStakeToMatch, taker);
        Money takerLiability  = odds.calculateLiability(makerStakeToMatch);
        this.remainingStake   = this.remainingStake.minus(makerStakeToMatch);
        refreshStatus();
        Bet bet = new Bet(betId, this, taker, makerStakeToMatch, takerLiability, odds, reference);
        this.bets.add(bet);
        return bet;
    }

    public void cancel() throws IllegalBetException {
        if (this.status == OfferStatus.TAKEN)
            throw new IllegalBetException("Cannot cancel a fully matched offer");
        this.status = OfferStatus.CANCELLED;
    }

    private void validateFillable(Money amount, User taker) throws IllegalBetException {
        Objects.requireNonNull(amount, "amount");
        if (this.status == OfferStatus.CANCELLED)
            throw new IllegalBetException("This offer has been cancelled");
        if (this.status == OfferStatus.TAKEN)
            throw new IllegalBetException("This offer is already fully matched");
        if (amount.isGreaterThan(remainingStake))
            throw new IllegalBetException(
                "Requested " + amount + " exceeds remaining stake of " + remainingStake);
        if (Objects.equals(taker.getId(), maker.getId()))
            throw new IllegalBetException("You cannot match your own offer");
    }

    private void refreshStatus() {
        this.status = remainingStake.isZero() ? OfferStatus.TAKEN : OfferStatus.PARTIALLY_TAKEN;
    }
}
