package com.ermiyas.exchange.domain.model;

import com.ermiyas.exchange.domain.exception.IllegalBetException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Odds {

    private final BigDecimal value;

    public Odds(BigDecimal value) {
        Objects.requireNonNull(value, "value");
        if (value.compareTo(BigDecimal.ONE) < 0)
            throw new IllegalArgumentException("Odds must be >= 1.0");
        this.value = value.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal value() { return value; }

    public Money calculateLiability(Money makerStake) throws IllegalBetException {
        BigDecimal factor = value.subtract(BigDecimal.ONE);
        return Money.of(makerStake.value().multiply(factor).setScale(2, RoundingMode.HALF_UP));
    }

    public static Odds of(double value)  { return new Odds(BigDecimal.valueOf(value)); }
    public static Odds of(String value)  { return new Odds(new BigDecimal(value)); }

    @Override
    public String toString() { return value.toPlainString(); }
}
