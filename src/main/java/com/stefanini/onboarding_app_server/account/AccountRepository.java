package com.stefanini.onboarding_app_server.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Para listar cuentas por cliente
    List<Account> findByCustomerId(Long customerId);
}