# Onboarding App Server

Servicios backend para la kata **“Apertura de Cuentas para Clientes Nuevos”**.

---

## 1. ¿Qué hace este backend? (en palabras simples)

- Piensa en el backend como un **servidor** que habla con el frontend.
- Este servidor sabe hacer 2 cosas principales:
  - **Gestionar clientes**: guardar y listar clientes nuevos.
  - **Gestionar cuentas**: crear cuentas bancarias sencillas para esos clientes y listarlas.
- Toda la comunicación se hace a través de **APIs HTTP** que devuelven y reciben **JSON**.

Tecnologías que usamos (sin entrar a fondo):

- **Spring Boot**: un framework de Java que facilita crear APIs HTTP.
- **H2**: una **base de datos en memoria** (se guarda todo en RAM mientras la app está levantada). Es perfecta para una prueba técnica porque:
  - No hay que instalar nada extra.
  - Cada vez que reinicias el backend, la BD empieza limpia.

---

## 2. Cómo arrancar el backend

Requisitos:

- Java 21+ instalado.
- No necesitas instalar Maven globalmente: el proyecto trae scripts (`mvnw` / `mvnw.cmd`).

### 2.1. Comando en Linux / macOS / Git Bash / WSL

```bash
# Desde la carpeta onboarding-app-server
./mvnw spring-boot:run
```

### 2.2. Comando en Windows (PowerShell / CMD)

```powershell
# Desde la carpeta onboarding-app-server
mvnw.cmd spring-boot:run
```

Cuando veas en la consola algo como:

> Tomcat started on port 8080  
> Started OnboardingAppServerApplication

significa que el backend está listo en:

- Backend: `http://localhost:8080`
- Consola H2: `http://localhost:8080/h2-console`

### 2.3. ¿Qué es la consola H2?

Es una pequeña interfaz web que te permite ver el contenido de la base de datos en memoria.

Para entrar:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:onboardingdb`
- Usuario: `sa`
- Password: *(vacío)*

Al conectarte verás tablas como `CUSTOMERS` y `ACCOUNTS` cuando el backend haya creado datos.

---

## 3. Configuración principal (sin tecnicismos)

Archivo: `src/main/resources/application.properties`

Ahí se define, entre otras cosas:

```properties
spring.application.name=onboarding-app-server

# H2 en memoria
spring.datasource.url=jdbc:h2:mem:onboardingdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Consola H2 para ver tablas en el navegador
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Puerto HTTP
server.port=8080
```

En términos simples:

- **`spring.datasource.*`**: cómo conectarse a la base de datos H2 en memoria.
- **`spring.jpa.hibernate.ddl-auto=update`**: le dice a Spring que cree/actualice las tablas (`customers`, `accounts`) según las clases de Java.
- **`spring.jpa.show-sql=true`**: muestra en la consola los `INSERT`, `SELECT`, etc. Muy útil para la demo.
- **`spring.h2.console.*`**: activa la consola web de H2.
- **`server.port=8080`**: el backend escucha en el puerto 8080.

---

## 4. Arquitectura (cómo está organizado el código)

Paquete raíz de Java: `com.stefanini.onboarding_app_server`

Dentro hay dos bloques principales:

- `customer` → todo lo relacionado con **clientes**.
- `account` → todo lo relacionado con **cuentas**.

### 4.0. Tipo de arquitectura / patrones de diseño / buenas prácticas

De forma resumida, en este backend se aplican estas ideas de arquitectura y diseño:

- **Arquitectura en capas (layered architecture)**:
  - **Controladores (Controllers)**: exponen las URLs REST (`/api/customers`, `/api/accounts`).
  - **Servicios (Services)**: contienen la lógica de negocio (validaciones, reglas).
  - **Repositorios (Repositories)**: se encargan del acceso a la base de datos.
  - **Entidades (Entities)**: representan las tablas de la BD (`Customer`, `Account`).

- **Patrón Repositorio (Repository Pattern)**:
  - Clases como `CustomerRepository` y `AccountRepository` encapsulan todo el acceso a la BD.
  - El resto del código no sabe detalles de SQL ni de H2, solo llama a métodos como `save`, `findAll`, `findByCustomerId`.

- **Principios SOLID (aplicados de forma práctica)**:
  - **S – Single Responsibility Principle**: cada clase tiene una responsabilidad clara.
    - Controladores → manejar peticiones/respuestas HTTP.
    - Servicios → reglas de negocio.
    - Repositorios → acceso a datos.
  - **D – Dependency Inversion**: los servicios reciben los repositorios por constructor.
    - Ejemplo: `CustomerService(CustomerRepository customerRepository)`.
    - El código depende de interfaces/abstracciones, y Spring se encarga de inyectar la implementación.

- **Código limpio (Clean Code)**:
  - Nombres descriptivos: `CustomerService`, `AccountService`, `createCustomer`, `createAccount`, `getAllCustomers`, etc.
  - Métodos cortos y enfocados: por ejemplo, `createAccount` valida, comprueba el cliente, genera número de cuenta y guarda.
  - Manejo explícito de errores: las excepciones del servicio se traducen en el controlador a códigos HTTP claros (`400`, `404`) con mensajes JSON (`{ "message": "..." }`).
  - Documentación: este README explica decisiones y responsabilidades de cada capa.

### 4.1. Parte de clientes (`customer`)

Responsable de la gestión de clientes.

- **Customer**  
  Es la "plantilla" de un cliente en Java. Spring la usa para crear la tabla `customers`.

  Campos relevantes:
  - `id`: identificador interno (lo genera la BD).
  - `documentType`: tipo de documento (CC / CE / PAS).
  - `documentNumber`: número de documento.
  - `fullName`: nombre completo.
  - `email`: correo.

- **CustomerRepository**  
  Es la capa que habla con la base de datos para clientes. No escribimos SQL a mano; Spring genera las consultas.

  Lo más importante:
  - `save(customer)`: guarda o actualiza un cliente.
  - `findAll()`: devuelve todos los clientes.
  - `existsByDocumentNumber(documentNumber)`: comprueba si ya hay un cliente con ese documento.

- **CustomerService**  
  Aquí está la **lógica de negocio** de clientes. Es donde se aplican las reglas:

  - `createCustomer(customer)`:
    - Valida que haya `documentType`, `documentNumber` y `email`.
    - Verifica que el `documentNumber` no esté repetido.
    - Si algo falla, lanza una excepción con un mensaje entendible.
    - Si todo bien, llama a `CustomerRepository.save` y devuelve el cliente creado (con `id`).
  - `getAllCustomers()` devuelve la lista de todos los clientes.

- **CustomerController**  
  Es la "puerta de entrada" HTTP para clientes. Define las URLs:

  - `POST /api/customers` → crear cliente.
  - `GET /api/customers` → listar clientes.

  Recibe peticiones del frontend en JSON, llama a `CustomerService` y devuelve respuestas JSON. Si hay errores de validación devuelve `400` con un mensaje.

### 4.2. Parte de cuentas (`account`)

Responsable de crear y consultar cuentas asociadas a clientes.

- **Account**  
  Es la plantilla de una cuenta. Spring la usa para crear la tabla `accounts`.

  Campos relevantes:
  - `id`: identificador de la cuenta.
  - `customerId`: id del cliente dueño de la cuenta.
  - `accountNumber`: número de cuenta generado por el backend.
  - `status`: estado de la cuenta (por ahora `ACTIVE` / `INACTIVE`).

- **AccountRepository**  
  Capa que habla con la BD para cuentas.

  Lo más importante:
  - `save(account)`: guarda una cuenta.
  - `findAll()`: devuelve todas las cuentas.
  - `findByCustomerId(customerId)`: devuelve cuentas filtradas por cliente.

- **AccountService**  
  Lógica de negocio de cuentas:

  - `createAccount(customerId)`:
    - Valida que `customerId` venga informado.
    - Comprueba que el cliente existe (usando el repositorio de clientes).
    - Genera un `accountNumber` del estilo `ACC-{customerId}-{timestamp}`.
    - Crea la cuenta con estado `ACTIVE` y la guarda.
    - Si falta `customerId` o el cliente no existe, lanza excepciones que luego el controlador traduce a 400/404.

  - `getAccountsByCustomerId(customerId)`:
    - Si viene `customerId`, devuelve solo las cuentas de ese cliente.
    - Si no viene, devuelve todas las cuentas.

- **AccountController**  
  Puerta de entrada HTTP para cuentas. Define:

  - `POST /api/accounts` → crear cuenta para un cliente.
  - `GET /api/accounts?customerId=` → listar cuentas (filtradas por cliente o todas).

  Traduce los errores del servicio a códigos HTTP adecuados:
  - 400 cuando falta `customerId`.
  - 404 cuando el cliente no existe.

---

## 5. Contrato de API (lo que el front puede llamar)

Base URL general: `http://localhost:8080`

### 5.1. Clientes

#### POST `/api/customers`

Crear un nuevo cliente.

**Body (JSON):**

```json
{
  "documentType": "CC",
  "documentNumber": "123456",
  "fullName": "Nombre Apellido",
  "email": "email@ejemplo.com"
}
```

**Respuestas típicas:**

- `201 Created` – cliente creado correctamente

```json
{
  "id": 1,
  "documentType": "CC",
  "documentNumber": "123456",
  "fullName": "Nombre Apellido",
  "email": "email@ejemplo.com"
}
```

- `400 Bad Request` – datos faltantes o inválidos

```json
{
  "message": "documentNumber es obligatorio"
}
```

#### GET `/api/customers`

Lista todos los clientes registrados.

**Respuesta:**

- `200 OK`

```json
[
  {
    "id": 1,
    "documentType": "CC",
    "documentNumber": "123456",
    "fullName": "Nombre Apellido",
    "email": "email@ejemplo.com"
  }
]
```

### 5.2. Cuentas

#### POST `/api/accounts`

Crea una cuenta asociada a un cliente ya existente.

**Body (JSON):**

```json
{
  "customerId": 1
}
```

**Respuestas típicas:**

- `201 Created` – cuenta creada

```json
{
  "id": 1,
  "customerId": 1,
  "accountNumber": "ACC-1-1707600000000",
  "status": "ACTIVE"
}
```

- `400 Bad Request` – falta `customerId`

```json
{
  "message": "customerId es obligatorio"
}
```

- `404 Not Found` – el cliente con ese `customerId` no existe

```json
{
  "message": "Cliente no encontrado"
}
```

#### GET `/api/accounts?customerId={id}`

Lista cuentas.

- Si `customerId` viene informado → cuentas solo de ese cliente.
- Si no se pasa `customerId` → devuelve todas las cuentas.

**Ejemplo:**

`GET /api/accounts?customerId=1`

```json
[
  {
    "id": 1,
    "customerId": 1,
    "accountNumber": "ACC-1-1707600000000",
    "status": "ACTIVE"
  }
]
```

---

## 6. Resumen

1. **Contexto**: tenemos un frontend en Angular y este backend en Spring Boot. El backend es el que realmente guarda los clientes y las cuentas en una BD en memoria (H2).
2. **Clientes**:
   - El front llama a `POST /api/customers` con los datos del cliente.
   - El backend valida la información y guarda el cliente en la tabla `customers`.
   - `GET /api/customers` devuelve la lista de clientes para mostrarla en la UI.
3. **Cuentas**:
   - El front llama a `POST /api/accounts` pasando el `customerId`.
   - El backend verifica que el cliente existe, genera un `accountNumber` y guarda la cuenta como `ACTIVE` en la tabla `accounts`.
   - `GET /api/accounts?customerId=...` devuelve las cuentas de ese cliente.
4. **Tecnología**: no hace falta entrar en detalle de Java; basta con decir que se usa Spring Boot + H2 en memoria para tener un backend ligero y fácil de ejecutar en local.
