package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.repository.bet.BetRepository;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;

import java.math.BigDecimal;
import java.util.List;

public class AdminSettlementService {
    private final EventRepository eventRepository;
    private final BetRepository betRepository;
    private final OfferRepository offerRepository;
    private final WalletRepository walletRepository;
    private final CommissionPolicy defaultPolicy;

    public AdminSettlementService(EventRepository eventRepository, BetRepository betRepository, OfferRepository offerRepository, WalletRepository walletRepository, CommissionPolicy defaultPolicy) {
        this.eventRepository = eventRepository;
        this.betRepository = betRepository;
        this.offerRepository = offerRepository;
        this.walletRepository = walletRepository;
        this.defaultPolicy = defaultPolicy;
    }

    public BigDecimal calculateTotalLockedStake() {
        BigDecimal total = BigDecimal.ZERO;
        for (Wallet wallet : walletRepository.findAll()) {
            total = total.add(wallet.getReservedBalance().value());
        }
        return total;
    }

    public void settleMarketResults(AdminUser admin, List<String> externalIds) throws ExchangeException {
        validateAdmin(admin);
        for (String extId : externalIds) {
            processEventSettlement(extId);
        }
    }

    private void processEventSettlement(String externalId) throws ExchangeException {
        Event event = eventRepository.getByExternalId(externalId).orElseThrow(() -> new IllegalBetException("Target event not found: " + externalId));
        if (event.getStatus() != EventStatus.COMPLETED) {
            throw new IllegalBetException("State Violation: Event must be COMPLETED before settlement.");
        }
        resolveAllBets(event, defaultPolicy);
        cleanupUnmatchedOffers(event);
        event.markAsSettled();
        eventRepository.save(event);
    }

    private void resolveAllBets(Event event, CommissionPolicy policy) throws ExchangeException {
        List<Bet> bets = betRepository.findAllByOfferEventId(event.getId());
        for (Bet bet : bets) {
            bet.resolve(event.getResult(), policy);
            User maker = bet.getOffer().getMaker();
            User taker = bet.getTaker();
            walletRepository.save(maker.getWallet());
            walletRepository.save(taker.getWallet());
            betRepository.save(bet);
        }
    }

    private void cleanupUnmatchedOffers(Event event) throws ExchangeException {
        for (Offer offer : event.getOffers()) {
            if (offer.getStatus() == OfferStatus.OPEN || offer.getStatus() == OfferStatus.PARTIALLY_TAKEN) {
                offer.cancel();
                Wallet makerWallet = offer.getMaker().getWallet();
                makerWallet.unreserve(offer.getRemainingStake());
                walletRepository.save(makerWallet);
                offerRepository.save(offer);
            }
        }
    }

    private void validateAdmin(AdminUser admin) {
        if (admin == null) throw new SecurityException("Unauthorized admin access.");
    }
}
