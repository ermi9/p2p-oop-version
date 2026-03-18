package com.ermiyas.exchange.api.controller;

import com.ermiyas.exchange.api.dto.ExchangeDtos;
import com.ermiyas.exchange.application.service.UserService;
import com.ermiyas.exchange.domain.exception.ExchangeException;
import com.ermiyas.exchange.domain.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody ExchangeDtos.RegisterRequest request) throws ExchangeException {
        User user = userService.registerStandardUser(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok("User created with ID: " + user.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ExchangeDtos.LoginRequest request) throws ExchangeException {
        User user = userService.login(request.getUsername(), request.getPassword());
        String role = user.getRoleName().equals("EXCHANGE_ADMIN") ? "ADMIN" : "STANDARD";
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("role", role);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> request) throws ExchangeException {
        userService.updatePassword(id, request.get("password"));
        return ResponseEntity.ok("Password updated successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) throws ExchangeException {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(toUserMap(user));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> payload = new ArrayList<>();
        for (User user : userService.getAllUsers()) {
            payload.add(toUserMap(user));
        }
        return ResponseEntity.ok(payload);
    }

    private Map<String, Object> toUserMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("roleName", user.getRoleName());
        return map;
    }
}
