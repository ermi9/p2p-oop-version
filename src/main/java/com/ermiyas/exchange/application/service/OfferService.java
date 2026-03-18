package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.model.*;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.repository.event.EventRepository;
import com.ermiyas.exchange.domain.repository.offer.OfferRepository;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;

import java.util.Objects;

public class OfferService {
    private final OfferRepository offerRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public OfferService(OfferRepository offerRepository, EventRepository eventRepository, UserRepository userRepository, WalletRepository walletRepository) {
        this.offerRepository = offerRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    public Long createOffer(Long eventId, Long makerUserId, Outcome outcome, Odds odds, Money stake) throws ExchangeException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalBetException("Offer Error: Event not found."));
        if (event.getStatus() != EventStatus.OPEN) {
            throw new IllegalBetException("Offer Error: Market is " + event.getStatus() + " and no longer accepting offers.");
        }
        User maker = userRepository.findById(makerUserId).orElseThrow(() -> new IllegalBetException("Offer Error: User not found."));
        maker.getWallet().reserve(stake);
        walletRepository.save(maker.getWallet());
        Offer offer = new Offer(offerRepository.nextId(), maker, event, outcome, stake, stake, odds, OfferStatus.OPEN);
        event.attachOffer(offer);
        offerRepository.save(offer);
        eventRepository.save(event);
        return offer.getId();
    }

    public void cancelOffer(Long offerId, Long userId) throws ExchangeException {
        Offer offer = offerRepository.findById(offerId).orElseThrow(() -> new IllegalBetException("Cancel Error: Offer not found."));
        if (!Objects.equals(offer.getMaker().getId(), userId)) {
            throw new IllegalBetException("Security Violation: Unauthorized attempt to cancel someone else's offer.");
        }
        Money stakeToReturn = offer.getRemainingStake();
        offer.cancel();
        Wallet makerWallet = offer.getMaker().getWallet();
        makerWallet.unreserve(stakeToReturn);
        walletRepository.save(makerWallet);
        offerRepository.save(offer);
    }
}
