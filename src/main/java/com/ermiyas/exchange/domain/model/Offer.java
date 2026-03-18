package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;

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

    public Offer(Long id, User maker, Event event, Outcome predictedOutcome, Money originalStake, Money remainingStake, Odds odds, OfferStatus status) {
        this.id = id;
        this.maker = maker;
        this.event = event;
        this.predictedOutcome = predictedOutcome;
        this.originalStake = originalStake;
        this.remainingStake = remainingStake;
        this.odds = odds;
        this.status = status;
        this.bets = new ArrayList<>();
    }

    public Long getId() { return id; }
    public User getMaker() { return maker; }
    public Event getEvent() { return event; }
    public Outcome getPredictedOutcome() { return predictedOutcome; }
    public Money getOriginalStake() { return originalStake; }
    public Money getRemainingStake() { return remainingStake; }
    public Odds getOdds() { return odds; }
    public OfferStatus getStatus() { return status; }
    public List<Bet> getBets() { return Collections.unmodifiableList(bets); }

    public Bet fill(Long betId, Money makerStakeToMatch, User taker, String reference) throws ExchangeException {
        validateFillable(makerStakeToMatch);
        Money takerLiability = odds.calculateLiability(makerStakeToMatch);
        this.remainingStake = this.remainingStake.minus(makerStakeToMatch);
        updateStatusAfterFill();
        Bet bet = new Bet(betId, this, taker, makerStakeToMatch, takerLiability, this.odds, reference, BetStatus.MATCHED);
        this.bets.add(bet);
        return bet;
    }

    public void cancel() throws IllegalBetException {
        if (this.status == OfferStatus.TAKEN) {
            throw new IllegalBetException("Cancellation Failed: This offer has already been fully matched by other users.");
        }
        this.status = OfferStatus.CANCELLED;
    }

    private void validateFillable(Money amount) throws IllegalBetException {
        Objects.requireNonNull(amount, "amount");
        if (this.status == OfferStatus.CANCELLED) {
            throw new IllegalBetException("Trade Failed: This offer was cancelled by the maker and is no longer active.");
        }
        if (this.status == OfferStatus.TAKEN) {
            throw new IllegalBetException("Trade Failed: This offer has already been fully matched by another user.");
        }
        if (amount.isGreaterThan(remainingStake)) {
            throw new IllegalBetException("Trade Failed: Requested match of $" + amount.value() + " exceeds the remaining available stake of $" + remainingStake.value() + ".");
        }
    }

    private void updateStatusAfterFill() {
        this.status = this.remainingStake.isZero() ? OfferStatus.TAKEN : OfferStatus.PARTIALLY_TAKEN;
    }
}
