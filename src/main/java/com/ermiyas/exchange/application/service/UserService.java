package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IdentityConflictException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.model.user.StandardUser;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.model.user.UserFactory;
import com.ermiyas.exchange.domain.repository.user.UserRepository;
import com.ermiyas.exchange.domain.repository.wallet.WalletRepository;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Password;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public UserService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    public User registerStandardUser(String username, String email, String password) throws ExchangeException {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IdentityConflictException("Registration Failed: Username '" + username + "' is already taken.");
        }
        StandardUser standardUser = UserFactory.createStandard(userRepository.nextId(), username, email, password);
        Wallet wallet = new Wallet(walletRepository.nextId(), standardUser, Money.zero());
        standardUser.setWallet(wallet);
        walletRepository.save(wallet);
        return userRepository.save(standardUser);
    }

    public User getUserById(Long id) throws ExchangeException {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            throw new UserNotFoundException("Lookup Failed: No user found with ID: " + id);
        }
        return userOpt.get();
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }

    public void updatePassword(Long userId, String newRawPassword) throws ExchangeException {
        User user = getUserById(userId);
        user.updatePassword(Password.create(newRawPassword));
        userRepository.save(user);
    }

    public User login(String username, String password) throws ExchangeException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Invalid username or password."));
        if (!user.authenticate(password)) {
            throw new UserNotFoundException("Invalid username or password.");
        }
        return user;
    }
}
