package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos;
import com.ermiyas.exchange.application.service.AdminSettlementService;
import com.ermiyas.exchange.application.service.AdminSyncService;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.exception.UserNotFoundException;
import com.ermiyas.exchange.domain.model.user.AdminUser;
import com.ermiyas.exchange.domain.model.user.User;
import com.ermiyas.exchange.domain.repository.user.UserRepository;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminSyncService syncService;
    private final AdminSettlementService settlementService;
    private final UserRepository userRepository;

    public AdminController(AdminSyncService syncService, AdminSettlementService settlementService, UserRepository userRepository) {
        this.syncService = syncService;
        this.settlementService = settlementService;
        this.userRepository = userRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<ExchangeDtos.DashboardStatsResponse> getStats() {
        ExchangeDtos.DashboardStatsResponse response = new ExchangeDtos.DashboardStatsResponse();
        response.setTotalUsers(userRepository.count());
        response.setActiveFixtures(syncService.getActiveFixtureCount());
        response.setLockedStake(settlementService.calculateTotalLockedStake());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync")
public ResponseEntity<?> syncAll() {
    try {
        System.out.println("[ADMIN] Sync started");
        long beforeCount = syncService.getActiveFixtureCount();
        System.out.println("[ADMIN] Events before sync: " + beforeCount);
        
        syncService.syncAllFixtures();
        
        long afterCount = syncService.getActiveFixtureCount();
        System.out.println("[ADMIN] Events after sync: " + afterCount);
        
        if (afterCount == beforeCount) {
            return ResponseEntity.ok(Map.of(
                "status", "warning",
                "message", "Sync completed but no new fixtures. Check API key!",
                "eventsCount", afterCount
            ));
        }
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Synchronization triggered successfully.",
            "newFixtures", afterCount - beforeCount,
            "totalEvents", afterCount
        ));
    } catch (Exception e) {
        System.err.println("[ADMIN] Sync failed: " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(500).body(Map.of(
            "status", "error",
            "message", e.getMessage()
        ));
    }
}

    @PostMapping("/settle")
    public ResponseEntity<String> settleMarkets(@RequestBody ExchangeDtos.AdminSettleRequest request, @RequestHeader(value = "X-Admin-Id", required = false) Long adminId) throws ExchangeException {
        AdminUser admin;
        if (adminId == null) {
            admin = findFallbackAdmin();
        } else {
            User found = userRepository.findById(adminId).orElseThrow(() -> new UserNotFoundException("Admin not found."));
            admin = (AdminUser) found;
        }
        settlementService.settleMarketResults(admin, request.getExternalIds());
        return ResponseEntity.ok("Market settlement processed.");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private AdminUser findFallbackAdmin() throws UserNotFoundException {
        for (User user : userRepository.findAll()) {
            if (user instanceof AdminUser) return (AdminUser) user;
        }
        throw new UserNotFoundException("Admin not found.");
    }
}
