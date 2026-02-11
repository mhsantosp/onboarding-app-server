package com.stefanini.onboarding_app_server.account;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // POST /api/accounts
    // Body esperado: { "customerId": 1 }
    @PostMapping
    public ResponseEntity<Object> createAccount(@RequestBody Map<String, Long> body) {
        try {
            Long customerId = body.get("customerId");
            Account created = accountService.createAccount(customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            // Falta customerId → 400
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalStateException e) {
            // Cliente no encontrado → 404
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // GET /api/accounts?customerId=1
    @GetMapping
    public List<Account> getAccounts(@RequestParam(required = false) Long customerId) {
        return accountService.getAccountsByCustomerId(customerId);
    }
}