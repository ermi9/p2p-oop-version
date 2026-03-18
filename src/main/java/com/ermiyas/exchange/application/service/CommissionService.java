package com.ermiyas.exchange.application.service;

import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.Money;

public class CommissionService {
    public Money estimateNetProfit(Money grossProfit, CommissionPolicy policy) throws ExchangeException {
        return policy.apply(grossProfit);
    }

    public Money estimateCommission(Money grossProfit, CommissionPolicy policy) throws ExchangeException {
        return policy.commissionOf(grossProfit);
    }
}
