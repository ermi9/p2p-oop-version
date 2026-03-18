package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;

public class Bet {
    private final Long id;
    private final Offer offer;
    private final User taker;
    private final Money makerStake;
    private final Money takerLiability;
    private final Odds odds;
    private final String reference;
    private BetStatus status;

    public Bet(Long id, Offer offer, User taker, Money makerStake, Money takerLiability, Odds odds, String reference, BetStatus status) {
        this.id = id;
        this.offer = offer;
        this.taker = taker;
        this.makerStake = makerStake;
        this.takerLiability = takerLiability;
        this.odds = odds;
        this.reference = reference;
        this.status = status;
    }

    public Long getId() { return id; }
    public Offer getOffer() { return offer; }
    public User getTaker() { return taker; }
    public Money getMakerStake() { return makerStake; }
    public Money getTakerLiability() { return takerLiability; }
    public Odds getOdds() { return odds; }
    public String getReference() { return reference; }
    public BetStatus getStatus() { return status; }
    public User getMaker() { return offer.getMaker(); }

    public void resolve(Outcome eventResult, CommissionPolicy policy) throws ExchangeException {
        if (eventResult == offer.getPredictedOutcome()) {
            handleMakerWin(policy);
        } else {
            handleTakerWin(policy);
        }
        this.status = BetStatus.SETTLED;
    }

    private void handleMakerWin(CommissionPolicy policy) throws ExchangeException {
        Wallet makerWallet = getMaker().getWallet();
        Wallet takerWallet = taker.getWallet();
        makerWallet.settleWin(makerStake, takerLiability, policy);
        takerWallet.settleLoss(takerLiability);
    }

    private void handleTakerWin(CommissionPolicy policy) throws ExchangeException {
        Wallet makerWallet = getMaker().getWallet();
        Wallet takerWallet = taker.getWallet();
        makerWallet.settleLoss(makerStake);
        takerWallet.settleWin(takerLiability, makerStake, policy);
    }
}
