package com.stefanini.onboarding_app_server.account;

import com.stefanini.onboarding_app_server.customer.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    public Account createAccount(Long customerId) {
        // 1) Validar que se envía customerId
        if (customerId == null) {
            throw new IllegalArgumentException("customerId es obligatorio");
        }

        // 2) Verificar que el cliente existe
        boolean exists = customerRepository.existsById(customerId);
        if (!exists) {
            // luego esto lo traduciremos a 404 en el controlador
            throw new IllegalStateException("Cliente no encontrado");
        }

        // 3) Generar accountNumber simple (puedes mejorarlo si quieres)
        String accountNumber = generateAccountNumber(customerId);

        // 4) Crear la entidad Account
        Account account = new Account();
        account.setCustomerId(customerId);
        account.setAccountNumber(accountNumber);
        account.setStatus("ACTIVE"); // por defecto activa

        // 5) Guardar en la BD
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByCustomerId(Long customerId) {
        if (customerId == null) {
            // si no hay filtro, devolvemos todas (te puede servir en la kata)
            return accountRepository.findAll();
        }
        return accountRepository.findByCustomerId(customerId);
    }

    // Implementación muy simple, suficiente para la kata
    private String generateAccountNumber(Long customerId) {
        // ejemplo: "ACC-{customerId}-{timestamp}"
        long timestamp = Instant.now().toEpochMilli();
        return "ACC-" + customerId + "-" + timestamp;
    }
}