package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.ExchangeException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CommissionPolicy {

    private final BigDecimal rate;

    public CommissionPolicy(BigDecimal rate) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0)
            throw new IllegalArgumentException("Rate must be between 0.0 and 1.0");
        this.rate = rate.setScale(4, RoundingMode.HALF_UP);
    }

    public Money apply(Money grossProfit) throws ExchangeException {
        return grossProfit.minus(commissionOf(grossProfit));
    }

    public Money commissionOf(Money grossProfit) {
        return new Money(grossProfit.value().multiply(rate).setScale(2, RoundingMode.HALF_UP));
    }

    public BigDecimal getRate() { return rate; }

    public String getRateAsPercent() { return rate.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%"; }

    public static CommissionPolicy fivePercent() {
        return new CommissionPolicy(new BigDecimal("0.05"));
    }
}
