package com.ermiyas.exchange.domain.repository.event;

import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.repository.GenericRepository;

import java.util.Optional;

public interface EventRepository extends GenericRepository<Event, Long> {
    Optional<Event> getByExternalId(String externalId);
    Long nextId();
}
