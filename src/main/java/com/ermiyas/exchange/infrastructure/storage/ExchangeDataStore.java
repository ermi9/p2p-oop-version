package com.ermiyas.exchange.infrastructure.storage;

import com.ermiyas.exchange.domain.model.Bet;
import com.ermiyas.exchange.domain.model.Event;
import com.ermiyas.exchange.domain.model.Offer;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.model.user.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ExchangeDataStore {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Map<Long, Wallet> wallets = new ConcurrentHashMap<>();
    private final Map<Long, Event> events = new ConcurrentHashMap<>();
    private final Map<Long, Offer> offers = new ConcurrentHashMap<>();
    private final Map<Long, Bet> bets = new ConcurrentHashMap<>();
    private final AtomicLong userIdCounter = new AtomicLong(1);
    private final AtomicLong walletIdCounter = new AtomicLong(1);
    private final AtomicLong eventIdCounter = new AtomicLong(1);
    private final AtomicLong offerIdCounter = new AtomicLong(1);
    private final AtomicLong betIdCounter = new AtomicLong(1);

    public Map<Long, User> users() { return users; }
    public Map<Long, Wallet> wallets() { return wallets; }
    public Map<Long, Event> events() { return events; }
    public Map<Long, Offer> offers() { return offers; }
    public Map<Long, Bet> bets() { return bets; }
    public long nextUserId() { return userIdCounter.getAndIncrement(); }
    public long nextWalletId() { return walletIdCounter.getAndIncrement(); }
    public long nextEventId() { return eventIdCounter.getAndIncrement(); }
    public long nextOfferId() { return offerIdCounter.getAndIncrement(); }
    public long nextBetId() { return betIdCounter.getAndIncrement(); }
}
