package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.user.User;

public class Bet {

    private final Long id;
    private final Offer offer;
    private final User taker;
    private final Money makerStake;
    private final Money takerLiability;
    private final Odds odds;
    private final String reference;
    private BetStatus status;

    public Bet(Long id, Offer offer, User taker, Money makerStake, Money takerLiability, Odds odds, String reference) {
        this.id             = id;
        this.offer          = offer;
        this.taker          = taker;
        this.makerStake     = makerStake;
        this.takerLiability = takerLiability;
        this.odds           = odds;
        this.reference      = reference;
        this.status         = BetStatus.MATCHED;
    }

    public Long      getId()             { return id; }
    public Offer     getOffer()          { return offer; }
    public User      getTaker()          { return taker; }
    public User      getMaker()          { return offer.getMaker(); }
    public Money     getMakerStake()     { return makerStake; }
    public Money     getTakerLiability() { return takerLiability; }
    public Odds      getOdds()           { return odds; }
    public String    getReference()      { return reference; }
    public BetStatus getStatus()         { return status; }

    public void resolve(Outcome result, Wallet makerWallet, Wallet takerWallet, CommissionPolicy policy)
            throws ExchangeException {
        if (result == offer.getPredictedOutcome()) {
            makerWallet.settleWin(makerStake, takerLiability, policy);
            takerWallet.settleLoss(takerLiability);
        } else {
            makerWallet.settleLoss(makerStake);
            takerWallet.settleWin(takerLiability, makerStake, policy);
        }
        this.status = BetStatus.SETTLED;
    }
}
