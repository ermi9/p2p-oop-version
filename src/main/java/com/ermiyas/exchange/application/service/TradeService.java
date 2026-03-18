package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.vo.Money;

import java.util.Objects;
import java.util.UUID;

public class TradeService {
    private final OfferRepository offerRepository;
    private final WalletRepository walletRepository;
    private final BetRepository betRepository;

    public TradeService(OfferRepository offerRepository, WalletRepository walletRepository, BetRepository betRepository) {
        this.offerRepository = offerRepository;
        this.walletRepository = walletRepository;
        this.betRepository = betRepository;
    }

    public void matchBet(Long offerId, Long takerUserId, Money makerStakeToMatch) throws ExchangeException {
        Offer offer = offerRepository.findByIdWithLock(offerId).orElseThrow(() -> new IllegalBetException("Trade Error: The offer (#" + offerId + ") is no longer available."));
        if (Objects.equals(offer.getMaker().getId(), takerUserId)) {
            throw new IllegalBetException("Trade Violation: You cannot match your own offer. This is considered a wash trade.");
        }
        if (offer.getEvent().getStatus() != EventStatus.OPEN) {
            throw new IllegalBetException("Trade Failed: The market for this event is now " + offer.getEvent().getStatus() + ".");
        }
        Wallet takerWallet = walletRepository.getByUserIdWithLock(takerUserId).orElseThrow(() -> new IllegalBetException("Trade Error: Wallet for user #" + takerUserId + " not found."));
        User taker = takerWallet.getUser();
        String ref = "MATCH_" + UUID.randomUUID().toString().substring(0, 8);
        Bet bet = offer.fill(betRepository.nextId(), makerStakeToMatch, taker, ref);
        takerWallet.reserve(bet.getTakerLiability());
        betRepository.save(bet);
        walletRepository.save(takerWallet);
        offerRepository.save(offer);
    }
}
