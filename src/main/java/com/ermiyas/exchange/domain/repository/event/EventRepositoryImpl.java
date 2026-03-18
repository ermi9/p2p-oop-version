package com.ermiyas.exchange.domain.repository.event;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.infrastructure.storage.ExchangeDataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventRepositoryImpl implements EventRepository {
    private final ExchangeDataStore dataStore;

    public EventRepositoryImpl(ExchangeDataStore dataStore) { this.dataStore = dataStore; }

    @Override
    public Event save(Event entity) { dataStore.events().put(entity.getId(), entity); return entity; }

    @Override
    public Optional<Event> findById(Long id) { return Optional.ofNullable(dataStore.events().get(id)); }

    @Override
    public List<Event> findAll() { return new ArrayList<>(dataStore.events().values()); }

    @Override
    public void deleteById(Long id) { dataStore.events().remove(id); }

    @Override
    public long count() { return dataStore.events().size(); }

    @Override
    public Optional<Event> getByExternalId(String externalId) {
        return dataStore.events().values().stream().filter(e -> e.getExternalId().equals(externalId)).findFirst();
    }

    @Override
    public Long nextId() { return dataStore.nextEventId(); }
}
