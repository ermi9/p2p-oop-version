package com.ermiyas.exchange.domain.vo;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.IllegalBetException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StandardPercentagePolicy implements CommissionPolicy {
    private final BigDecimal rate;

    public StandardPercentagePolicy(BigDecimal rate) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Rate must be between 0 and 1");
        }
        this.rate = rate.setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public Money apply(Money grossProfit) throws ExchangeException {
        return grossProfit.minus(commissionOf(grossProfit));
    }

    @Override
    public Money commissionOf(Money grossProfit) throws ExchangeException {
        if (grossProfit.isNegative()) {
            throw new IllegalBetException("Gross profit cannot be negative");
        }
        return Money.of(grossProfit.value().multiply(rate));
    }

    public BigDecimal getRate() { return rate; }
}
