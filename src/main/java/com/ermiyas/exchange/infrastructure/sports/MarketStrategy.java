package com.ermiyas.exchange.infrastructure.sports;

import com.ermiyas.exchange.domain.model.MarketType;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.infrastructure.sports.dto.TheOddsApiOddsDto;

import java.util.List;

public interface MarketStrategy {
    boolean supports(MarketType type);
    String getMarketKey();
    BestOddsResult calculateBestOddsWithSources(TheOddsApiOddsDto dto);
    List<Odds> calculateBestOdds(TheOddsApiOddsDto dto);
}
