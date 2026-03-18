package com.ermiyas.exchange.domain.repository.offer;

import com.ermiyas.exchange.domain.model.Offer;
import com.ermiyas.exchange.infrastructure.storage.ExchangeDataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OfferRepositoryImpl implements OfferRepository {
    private final ExchangeDataStore dataStore;

    public OfferRepositoryImpl(ExchangeDataStore dataStore) { this.dataStore = dataStore; }

    @Override
    public Offer save(Offer entity) { dataStore.offers().put(entity.getId(), entity); return entity; }

    @Override
    public Optional<Offer> findById(Long id) { return Optional.ofNullable(dataStore.offers().get(id)); }

    @Override
    public List<Offer> findAll() { return new ArrayList<>(dataStore.offers().values()); }

    @Override
    public void deleteById(Long id) { dataStore.offers().remove(id); }

    @Override
    public long count() { return dataStore.offers().size(); }

    @Override
    public Optional<Offer> findByIdWithLock(Long id) { return findById(id); }

    @Override
    public List<Offer> findAllByEventId(Long eventId) {
        return dataStore.offers().values().stream().filter(o -> o.getEvent().getId().equals(eventId)).collect(Collectors.toList());
    }

    @Override
    public Long nextId() { return dataStore.nextOfferId(); }
}
