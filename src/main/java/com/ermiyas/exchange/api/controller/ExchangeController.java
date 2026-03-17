package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos;
import com.ermiyas.exchange.application.service.CommissionService;
import com.ermiyas.exchange.application.service.OfferService;
import com.ermiyas.exchange.application.service.TradeService;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.vo.CommissionPolicy;
import com.ermiyas.exchange.domain.vo.Money;
import com.ermiyas.exchange.domain.vo.Odds;
import com.ermiyas.exchange.domain.vo.StandardPercentagePolicy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

//@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/v1/exchange")
public class ExchangeController {
    private final OfferService offerService;
    private final TradeService tradeService;
    private final CommissionService commissionService;

    public ExchangeController(OfferService offerService, TradeService tradeService, CommissionService commissionService) {
        this.offerService = offerService;
        this.tradeService = tradeService;
        this.commissionService = commissionService;
    }

    @PostMapping("/offers")
    public ResponseEntity<Long> createOffer(@RequestBody ExchangeDtos.CreateOfferRequest request) throws ExchangeException {
        Long id = offerService.createOffer(request.getEventId(), request.getMakerId(), request.getOutcome(), Odds.of(request.getOdds()), Money.of(request.getStake()));
        return ResponseEntity.ok(id);
    }

    @PostMapping("/trades/match")
    public ResponseEntity<String> matchTrade(@RequestBody ExchangeDtos.MatchBetRequest request) throws ExchangeException {
        tradeService.matchBet(request.getOfferId(), request.getTakerId(), Money.of(request.getAmountToMatch()));
        return ResponseEntity.ok("Trade matched successfully.");
    }

    @DeleteMapping("/offers/{offerId}")
    public ResponseEntity<Void> cancelOffer(@PathVariable Long offerId, @RequestHeader(value = "X-User-Id", required = false) Long userId) throws ExchangeException {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        offerService.cancelOffer(offerId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/commission-preview")
    public ResponseEntity<Map<String, BigDecimal>> getPreview(@RequestParam String profit, @RequestParam BigDecimal rate) throws ExchangeException {
        Money grossProfit = Money.of(profit);
        CommissionPolicy policy = new StandardPercentagePolicy(rate);
        return ResponseEntity.ok(Map.of(
                "netProfit", commissionService.estimateNetProfit(grossProfit, policy).value(),
                "commissionCharge", commissionService.estimateCommission(grossProfit, policy).value()
        ));
    }
}
