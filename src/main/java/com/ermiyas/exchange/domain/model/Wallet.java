package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;
import com.ermiyas.exchange.domain.exception.InsufficientFundsException;

public class Wallet {

    private final Long id;
    private Money totalBalance;
    private Money reservedBalance;

    public Wallet(Long id, Money initialBalance) {
        this.id              = id;
        this.totalBalance    = initialBalance;
        this.reservedBalance = Money.zero();
    }

    public Long  getId()              { return id; }
    public Money getTotalBalance()    { return totalBalance; }
    public Money getReservedBalance() { return reservedBalance; }

    public Money availableBalance() {
        try {
            return totalBalance.minus(reservedBalance);
        } catch (IllegalBetException e) {
            throw new IllegalStateException("Reserved balance exceeds total — data integrity error", e);
        }
    }

    public void deposit(Money amount) {
        if (amount.isZero() || amount.isNegative())
            throw new IllegalArgumentException("Deposit amount must be positive");
        this.totalBalance = this.totalBalance.plus(amount);
    }

    public void withdraw(Money amount) throws ExchangeException {
        if (amount.isZero() || amount.isNegative())
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        if (amount.isGreaterThan(availableBalance()))
            throw new InsufficientFundsException(
                "Cannot withdraw " + amount + " — only " + availableBalance() + " available");
        this.totalBalance = this.totalBalance.minus(amount);
    }

    public void reserve(Money amount) throws InsufficientFundsException {
        if (amount.isGreaterThan(availableBalance()))
            throw new InsufficientFundsException(
                "Cannot reserve " + amount + " — only " + availableBalance() + " available");
        this.reservedBalance = this.reservedBalance.plus(amount);
    }

    public void unreserve(Money amount) throws ExchangeException {
        if (amount.isGreaterThan(reservedBalance))
            throw new IllegalBetException("Cannot unreserve more than is reserved");
        this.reservedBalance = this.reservedBalance.minus(amount);
    }

    public void settleWin(Money stakeReleased, Money grossProfit, CommissionPolicy policy) throws ExchangeException {
        this.reservedBalance = this.reservedBalance.minus(stakeReleased);
        this.totalBalance    = this.totalBalance.plus(policy.apply(grossProfit));
    }

    public void settleLoss(Money stakeForfeited) throws ExchangeException {
        this.reservedBalance = this.reservedBalance.minus(stakeForfeited);
        this.totalBalance    = this.totalBalance.minus(stakeForfeited);
    }
}
