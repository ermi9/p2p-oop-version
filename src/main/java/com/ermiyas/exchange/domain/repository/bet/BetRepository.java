package com.ermiyas.exchange.domain.repository.bet;

import com.ermiyas.exchange.domain.model.Bet;
import com.ermiyas.exchange.domain.repository.GenericRepository;

import java.util.List;

public interface BetRepository extends GenericRepository<Bet, Long> {
    List<Bet> findAllByOfferEventId(Long eventId);
    Long nextId();
}
