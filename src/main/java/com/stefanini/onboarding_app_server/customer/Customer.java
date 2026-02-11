package com.stefanini.onboarding_app_server.customer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity                       // Marca esta clase como entidad JPA (tabla)
@Table(name = "customers")    // Nombre de la tabla en la BD
public class Customer {

    @Id                                          // Clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Id autogenerado
    private Long id;

    private String documentType;    // CC / CE / PAS
    private String documentNumber;
    private String fullName;
    private String email;

    // Constructor vacío requerido por JPA
    public Customer() {
    }

    // Constructor útil para crear clientes en código (opcional)
    public Customer(String documentType, String documentNumber, String fullName, String email) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.fullName = fullName;
        this.email = email;
    }

    // Getters y setters (necesarios para que JPA y Jackson funcionen)

    public Long getId() {
        return id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}