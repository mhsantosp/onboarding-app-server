package com.stefanini.onboarding_app_server.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación simple por ahora: solo el id del cliente
    private Long customerId;

    // Se generará automáticamente en el servicio
    private String accountNumber;

    // ACTIVE / INACTIVE
    private String status;

    public Account() {
    }

    public Account(Long customerId, String accountNumber, String status) {
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}