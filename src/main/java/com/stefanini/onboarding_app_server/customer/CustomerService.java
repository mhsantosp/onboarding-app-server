package com.stefanini.onboarding_app_server.customer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Spring inyecta el repositorio aquí
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {
        // Validaciones simples en el back (además de las del front)

        if (customer.getDocumentType() == null || customer.getDocumentType().isBlank()) {
            throw new IllegalArgumentException("documentType es obligatorio");
        }

        if (customer.getDocumentNumber() == null || customer.getDocumentNumber().isBlank()) {
            throw new IllegalArgumentException("documentNumber es obligatorio");
        }

        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            throw new IllegalArgumentException("email es obligatorio");
        }

        // Ejemplo: evitar documentos duplicados (opcional, pero útil)
        if (customerRepository.existsByDocumentNumber(customer.getDocumentNumber())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese documentNumber");
        }

        // Guarda en la BD y devuelve el cliente con id generado
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}