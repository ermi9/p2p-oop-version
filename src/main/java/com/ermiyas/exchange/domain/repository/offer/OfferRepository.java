package com.ermiyas.exchange.domain.repository.offer;

import com.ermiyas.exchange.domain.model.Offer;
import com.ermiyas.exchange.domain.repository.GenericRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends GenericRepository<Offer, Long> {
    Optional<Offer> findByIdWithLock(Long id);
    List<Offer> findAllByEventId(Long eventId);
    Long nextId();
}
