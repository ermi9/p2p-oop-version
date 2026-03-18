package com.ermiyas.exchange.domain.vo;

import com.ermiyas.exchange.domain.exception.ExchangeException;

public interface CommissionPolicy {
    Money apply(Money grossProfit) throws ExchangeException;
    Money commissionOf(Money grossProfit) throws ExchangeException;
}
