package com.ermiyas.exchange.domain.repository.bet;

import com.ermiyas.exchange.domain.model.Bet;
import com.ermiyas.exchange.infrastructure.storage.ExchangeDataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BetRepositoryImpl implements BetRepository {
    private final ExchangeDataStore dataStore;

    public BetRepositoryImpl(ExchangeDataStore dataStore) { this.dataStore = dataStore; }

    @Override
    public Bet save(Bet entity) { dataStore.bets().put(entity.getId(), entity); return entity; }

    @Override
    public Optional<Bet> findById(Long id) { return Optional.ofNullable(dataStore.bets().get(id)); }

    @Override
    public List<Bet> findAll() { return new ArrayList<>(dataStore.bets().values()); }

    @Override
    public void deleteById(Long id) { dataStore.bets().remove(id); }

    @Override
    public long count() { return dataStore.bets().size(); }

    @Override
    public List<Bet> findAllByOfferEventId(Long eventId) {
        return dataStore.bets().values().stream().filter(b -> b.getOffer().getEvent().getId().equals(eventId)).collect(Collectors.toList());
    }

    @Override
    public Long nextId() { return dataStore.nextBetId(); }
}
