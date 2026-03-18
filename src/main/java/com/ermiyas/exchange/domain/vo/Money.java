package com.ermiyas.exchange.domain.vo;

import com.ermiyas.exchange.domain.exception.IllegalBetException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private final BigDecimal value;

    public Money(BigDecimal value) {
        this.value = normalize(Objects.requireNonNull(value, "value"));
    }

    public BigDecimal value() { return value; }
    public BigDecimal getValue() { return value; }

    public Money plus(Money other) {
        return new Money(this.value.add(other.value));
    }

    public Money minus(Money other) throws IllegalBetException {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalBetException("Cannot have negative money");
        }
        return new Money(result);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(this.value.multiply(factor));
    }

    public boolean isGreaterThan(Money other) {
        return this.value.compareTo(other.value) > 0;
    }

    public boolean isNegative() {
        return this.value.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isZero() {
        return this.value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public static Money of(BigDecimal value) throws IllegalBetException {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalBetException("Money cannot be negative");
        }
        return new Money(value);
    }

    public static Money of(String value) throws IllegalBetException {
        return of(new BigDecimal(value));
    }

    private static BigDecimal normalize(BigDecimal value) {
        return value.setScale(SCALE, ROUNDING);
    }

    @Override
    public int compareTo(Money other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
