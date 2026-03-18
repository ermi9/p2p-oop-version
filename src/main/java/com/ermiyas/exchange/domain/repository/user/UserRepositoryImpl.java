package com.ermiyas.exchange.domain.repository.user;

import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.infrastructure.storage.ExchangeDataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    private final ExchangeDataStore dataStore;

    public UserRepositoryImpl(ExchangeDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public User save(User entity) {
        dataStore.users().put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(Long id) { return Optional.ofNullable(dataStore.users().get(id)); }

    @Override
    public List<User> findAll() { return new ArrayList<>(dataStore.users().values()); }

    @Override
    public void deleteById(Long id) {
        dataStore.users().remove(id);
        dataStore.wallets().values().removeIf(w -> w.getUser().getId().equals(id));
    }

    @Override
    public long count() { return dataStore.users().size(); }

    @Override
    public Optional<User> findByUsername(String username) {
        return dataStore.users().values().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    @Override
    public Long nextId() { return dataStore.nextUserId(); }
}
