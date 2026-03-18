package com.ermiyas.exchange.domain.vo;

import com.ermiyas.exchange.domain.exception.IllegalBetException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Odds {
    private final BigDecimal value;

    public Odds(BigDecimal value) {
        Objects.requireNonNull(value, "value");
        if (value.compareTo(BigDecimal.ONE) < 0) {
            throw new IllegalArgumentException("Odds must be >= 1.0");
        }
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal value() { return value; }
    public BigDecimal getValue() { return value; }

    public Money calculatePayout(Money stake) {
        return new Money(stake.value().multiply(value));
    }

    public Money calculateLiability(Money makerStake) throws IllegalBetException {
        BigDecimal factor = value.subtract(BigDecimal.ONE);
        if (factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalBetException("Odds cannot create negative liability");
        }
        return Money.of(makerStake.value().multiply(factor));
    }

    public static Odds of(BigDecimal value) { return new Odds(value); }
    public static Odds of(double value) { return new Odds(BigDecimal.valueOf(value)); }
}
