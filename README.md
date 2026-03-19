# Robotest Auth Backend

Fully runnable Spring Boot 3.2 authentication backend.

## Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Gmail App Password (for email)

## Setup

### 1 — Create the database
```bash
createdb robotest_db
# Schema is auto-applied by spring.jpa.hibernate.ddl-auto=update on first run
```

### 2 — Configure application.properties
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/robotest_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_DB_PASSWORD

# Mail — get App Password at https://myaccount.google.com/apppasswords
spring.mail.username=your-email@gmail.com
spring.mail.password=xxxx-xxxx-xxxx-xxxx
```

### 3 — Run
```bash
mvn spring-boot:run
# Server starts at http://localhost:8080
# Admin account auto-created: admin@robotest.com / Admin@1234
```

### 4 — Run tests
```bash
mvn test
```

---

## API Reference

All responses follow: `{ success, message, data, timestamp }`

### Public Endpoints

| Method | URL | Body |
|--------|-----|------|
| POST | /api/auth/register | `{ fullName, username, email, password, confirmPassword }` |
| GET  | /api/auth/verify-email?token= | — |
| POST | /api/auth/resend-verification?email= | — |
| POST | /api/auth/login | `{ email, password }` |
| POST | /api/auth/refresh-token | `{ refreshToken }` |
| POST | /api/auth/forgot-password | `{ email }` |
| POST | /api/auth/reset-password | `{ token, newPassword, confirmPassword }` |
| GET  | /api/auth/validate-token?token= | — |

### Protected Endpoints (Bearer token required)

| Method | URL | Body |
|--------|-----|------|
| POST | /api/auth/logout | — |
| POST | /api/auth/change-password | `{ currentPassword, newPassword, confirmPassword }` |
| GET  | /api/auth/me | — |

---

## Example Requests

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Jane Doe","username":"janedoe","email":"jane@example.com","password":"Secret@123","confirmPassword":"Secret@123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com","password":"Secret@123"}'
```

### Use Protected Endpoint
```bash
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <accessToken>"
```

### Forgot Password
```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com"}'
```

### Reset Password
```bash
curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{"token":"<from-email>","newPassword":"NewSecret@123","confirmPassword":"NewSecret@123"}'
```

### Refresh Token
```bash
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refreshToken>"}'
```

### Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer <accessToken>"
```

---

## Token Strategy
| Token | Lifetime | Storage |
|-------|----------|---------|
| Access token | 15 min | Client memory |
| Refresh token | 7 days | BCrypt-hashed in DB |

- Refresh token is **rotated** on every `/refresh-token` call
- Logout **revokes** refresh token server-side
- Password reset/change **invalidates all sessions**
- Forgot password **never reveals** if email exists (anti-enumeration)

## Project Structure
```
src/main/java/com/robotest/
├── RobotestBackendApplication.java
├── config/
│   ├── SecurityConfig.java       ← Spring Security + JWT filter chain
│   └── DataInitializer.java      ← Seeds roles + admin on startup
├── controller/
│   └── AuthController.java       ← All auth endpoints
├── dto/
│   ├── request/                  ← RegisterRequest, LoginRequest, etc.
│   └── response/                 ← ApiResponse, AuthResponse, UserResponse
├── entity/
│   ├── User.java
│   └── Role.java
├── enums/
│   └── RoleName.java
├── exception/
│   ├── AppException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── UserRepository.java
│   └── RoleRepository.java
├── security/
│   ├── JwtService.java           ← Token generation + validation
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
└── service/
    ├── AuthService.java          ← All business logic
    └── EmailService.java         ← HTML email sending
```
