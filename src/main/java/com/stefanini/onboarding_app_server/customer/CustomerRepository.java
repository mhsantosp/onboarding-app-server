package com.stefanini.onboarding_app_server.customer;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<Entidad, TipoDeId>
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Ejemplo de método extra útil si luego lo necesitas:
    boolean existsByDocumentNumber(String documentNumber);
}