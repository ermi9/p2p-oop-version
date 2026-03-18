package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos;
import com.ermiyas.exchange.application.service.MarketQueryService;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/v1/markets")
public class MarketController {
    private final MarketQueryService marketQueryService;

    public MarketController(MarketQueryService marketQueryService) { this.marketQueryService = marketQueryService; }

    @GetMapping("/leagues")
    public ResponseEntity<Map<String, List<ExchangeDtos.EventSummaryResponse>>> getMarketsByLeague() {
        return ResponseEntity.ok(marketQueryService.getEventsByLeague());
    }

    @GetMapping("/fixtures/{id}")
    public ResponseEntity<Map<String, Object>> getFixtureDetail(@PathVariable Long id) throws ExchangeException {
        return ResponseEntity.ok(marketQueryService.getFixtureDetailSnapshot(id));
    }
}
