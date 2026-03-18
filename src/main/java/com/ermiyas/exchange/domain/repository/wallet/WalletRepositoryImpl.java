package com.ermiyas.exchange.domain.repository.wallet;

import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.infrastructure.storage.ExchangeDataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WalletRepositoryImpl implements WalletRepository {
    private final ExchangeDataStore dataStore;

    public WalletRepositoryImpl(ExchangeDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public Wallet save(Wallet entity) {
        dataStore.wallets().put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Wallet> findById(Long id) { return Optional.ofNullable(dataStore.wallets().get(id)); }

    @Override
    public List<Wallet> findAll() { return new ArrayList<>(dataStore.wallets().values()); }

    @Override
    public void deleteById(Long id) { dataStore.wallets().remove(id); }

    @Override
    public long count() { return dataStore.wallets().size(); }

    @Override
    public Optional<Wallet> getByUserId(Long userId) {
        return dataStore.wallets().values().stream().filter(w -> w.getUser().getId().equals(userId)).findFirst();
    }

    @Override
    public Optional<Wallet> getByUserIdWithLock(Long userId) { return getByUserId(userId); }

    @Override
    public Long nextId() { return dataStore.nextWalletId(); }
}
