package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.application.service.MarketQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/v1/activity")
public class ActivityController {
    private final MarketQueryService queryService;

    public ActivityController(MarketQueryService queryService) { this.queryService = queryService; }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMyActivity(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(Map.of(
                "openOffers", queryService.getUserOpenOffers(userId),
                "matchedBets", queryService.getUserMatchedBets(userId)
        ));
    }
}
