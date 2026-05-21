package com.ermiyas.exchange.domain.model;

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

    // ad-hoc polymorphism: same method name, different parameter type
    public Money plus(Money other) {
        return new Money(this.value.add(other.value));
    }

    public Money plus(BigDecimal amount) {
        return new Money(this.value.add(amount).setScale(SCALE, ROUNDING));
    }

    public Money multiply(BigDecimal factor) {
        return new Money(this.value.multiply(factor).setScale(SCALE, ROUNDING));
    }

    public Money minus(Money other) throws IllegalBetException {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalBetException("Result would be negative");
        return new Money(result);
    }

    public boolean isGreaterThan(Money other) { return this.value.compareTo(other.value) > 0; }
    public boolean isZero()                   { return this.value.compareTo(BigDecimal.ZERO) == 0; }
    public boolean isNegative()               { return this.value.compareTo(BigDecimal.ZERO) < 0; }

    public static Money zero() { return new Money(BigDecimal.ZERO); }

    // coercion polymorphism: String coerced to BigDecimal transparently
    public static Money of(String value) throws IllegalBetException {
        return of(new BigDecimal(value));
    }

    public static Money of(BigDecimal value) throws IllegalBetException {
        if (value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalBetException("Money cannot be negative");
        return new Money(value);
    }

    private static BigDecimal normalize(BigDecimal v) {
        return v.setScale(SCALE, ROUNDING);
    }

    @Override
    public int compareTo(Money other) { return this.value.compareTo(other.value); }

    @Override
    public String toString() { return "$" + value.toPlainString(); }
}
