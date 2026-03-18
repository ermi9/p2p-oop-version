package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.exception.InsufficientFundsException;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.Money;

import java.util.Objects;

public class Wallet {
    private final Long id;
    private final User user;
    private Money totalBalance;
    private Money reservedBalance;

    public Wallet(Long id, User user, Money initialBalance) {
        this.id = id;
        this.user = Objects.requireNonNull(user);
        this.totalBalance = Objects.requireNonNull(initialBalance);
        this.reservedBalance = Money.zero();
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Money getTotalBalance() { return totalBalance; }
    public Money getReservedBalance() { return reservedBalance; }

    public Money availableBalance() {
        try {
            return totalBalance.minus(reservedBalance);
        } catch (IllegalBetException e) {
            throw new IllegalStateException("Reserved funds exceed total balance", e);
        }
    }

    public void reserve(Money amount) throws InsufficientFundsException {
        Money available = availableBalance();
        if (amount.isGreaterThan(available)) {
            throw new InsufficientFundsException("Insufficient Funds: Attempted to reserve $" + amount.value() + ", but your available balance is only $" + available.value() + ".");
        }
        this.reservedBalance = this.reservedBalance.plus(amount);
    }

    public void settleWin(Money stakeToRelease, Money netProfit, CommissionPolicy policy) throws ExchangeException {
        validateReservation(stakeToRelease);
        this.reservedBalance = this.reservedBalance.minus(stakeToRelease);
        Money netGain = policy.apply(netProfit);
        this.totalBalance = this.totalBalance.plus(netGain);
    }

    public void settleLoss(Money stakeToLose) throws ExchangeException {
        validateReservation(stakeToLose);
        this.reservedBalance = this.reservedBalance.minus(stakeToLose);
        this.totalBalance = this.totalBalance.minus(stakeToLose);
    }

    public void deposit(Money amount) {
        if (amount.isZero() || amount.isNegative()) {
            throw new IllegalArgumentException("Deposit failed: Amount must be greater than zero.");
        }
        this.totalBalance = this.totalBalance.plus(amount);
    }

    public void withdraw(Money amount) throws ExchangeException {
        if (amount.isZero() || amount.isNegative()) {
            throw new IllegalArgumentException("Withdrawal failed: Amount must be greater than zero.");
        }
        Money available = availableBalance();
        if (amount.isGreaterThan(available)) {
            throw new InsufficientFundsException("Withdrawal Failed: Attempted to withdraw $" + amount.value() + ", but only $" + available.value() + " is available (the rest is locked in active bets).");
        }
        this.totalBalance = this.totalBalance.minus(amount);
    }

    public void unreserve(Money amount) throws ExchangeException {
        validateReservation(amount);
        this.reservedBalance = this.reservedBalance.minus(amount);
    }

    private void validateReservation(Money amount) throws ExchangeException {
        if (amount.isGreaterThan(reservedBalance)) {
            throw new IllegalBetException("Financial Integrity Error: Attempted to release $" + amount.value() + " from escrow, but only $" + reservedBalance.value() + " is currently reserved.");
        }
    }
}
