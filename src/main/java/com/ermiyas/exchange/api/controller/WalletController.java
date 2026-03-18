package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos;
import com.ermiyas.exchange.application.service.WalletService;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.Wallet;
import com.ermiyas.exchange.domain.vo.Money;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) { this.walletService = walletService; }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getBalance(@PathVariable Long userId) throws ExchangeException {
        Wallet wallet = walletService.getUserWallet(userId);
        return ResponseEntity.ok(Map.of(
                "totalBalance", wallet.getTotalBalance().value(),
                "availableBalance", wallet.availableBalance().value(),
                "reservedBalance", wallet.getReservedBalance().value()
        ));
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<String> deposit(@PathVariable Long userId, @RequestBody ExchangeDtos.WalletActionRequest request) throws ExchangeException {
        walletService.deposit(userId, Money.of(request.getAmount().toString()));
        return ResponseEntity.ok("Deposit successful.");
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable Long userId, @RequestBody ExchangeDtos.WalletActionRequest request) throws ExchangeException {
        walletService.withdraw(userId, Money.of(request.getAmount().toString()));
        return ResponseEntity.ok("Withdrawal successful.");
    }
}
