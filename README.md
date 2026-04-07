# Finance Dashboard Backend

A simple, clean Finance Dashboard REST API built with **Java 21**, **Spring Boot 3.x**, and **Maven**.

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Language     | Java 21                             |
| Framework    | Spring Boot 3.2                     |
| Security     | Spring Security + JWT (jjwt 0.11.5) |
| Database     | H2 (in-memory)                      |
| ORM          | Spring Data JPA / Hibernate         |
| Build        | Maven                               |
| Utilities    | Lombok                              |

**Why H2?** It requires zero setup — the database is created fresh every time the app starts.

---

## How to Run

### Prerequisites
- Java 21+
- Maven 3.8+

### Start the app
```bash
cd finance-dashboard
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

H2 Console (browser): `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:financedb`
- Username: `sa`
- Password:

---

## Seeded Users

These users are created automatically on first startup:



Roles added automatically before production as we need to test the roles. to ease it added user and password directly.

| Role    | Email                  | Password     |
|---------|------------------------|--------------|
| ADMIN   | admin@finance.com      | admin123     |
| ANALYST | analyst@finance.com    | analyst123   |
| VIEWER  | viewer@finance.com     | viewer123    |

10 sample transactions are also seeded (Jan–Mar 2024).

---

## Authentication Flow

### Step 1 — Login to get a token
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@finance.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@finance.com",
  "role": "ADMIN"
}
```

### Step 2 — Use the token in all subsequent requests
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## API Endpoints

### Auth (Public — no token needed)

| Method | Endpoint             | Description                           |
|--------|----------------------|---------------------------------------|
| POST   | /api/auth/register   | Register new user (gets VIEWER role)  |
| POST   | /api/auth/login      | Login and receive JWT token           |

---

### Users (ADMIN only)

| Method | Endpoint               | Description              |
|--------|------------------------|--------------------------|
| GET    | /api/users             | List all users (paginated)|
| PUT    | /api/users/{id}/role   | Change a user's role     |
| PUT    | /api/users/{id}/status | Activate/deactivate user |

---

### Transactions

| Method | Endpoint                | Access         | Description                        |
|--------|-------------------------|----------------|------------------------------------|
| POST   | /api/transactions       | ADMIN          | Create a transaction               |
| GET    | /api/transactions       | ALL roles      | List transactions (filtered + paged)|
| GET    | /api/transactions/{id}  | ALL roles      | Get single transaction             |
| PUT    | /api/transactions/{id}  | ADMIN          | Update a transaction               |
| DELETE | /api/transactions/{id}  | ADMIN          | Soft delete a transaction          |

**Filter query params for GET /api/transactions:**
```
?type=EXPENSE
?category=Food
?startDate=2024-01-01&endDate=2024-03-31
?page=0&size=5
```

---

### Dashboard

| Method | Endpoint                      | Access            | Description                     |
|--------|-------------------------------|-------------------|---------------------------------|
| GET    | /api/dashboard/summary        | ANALYST, ADMIN    | Total income, expenses, balance |
| GET    | /api/dashboard/by-category    | ANALYST, ADMIN    | Totals grouped by category      |
| GET    | /api/dashboard/monthly-trend  | ANALYST, ADMIN    | Monthly income vs expense       |
| GET    | /api/dashboard/recent         | ALL roles         | Last 10 transactions            |

---

## Access Control Summary

| Action                      | VIEWER | ANALYST | ADMIN |
|-----------------------------|--------|---------|-------|
| Login / Register            | ✅     | ✅      | ✅    |
| View transactions           | ✅     | ✅      | ✅    |
| View recent activity        | ✅     | ✅      | ✅    |
| Create/Update/Delete txns   | ❌     | ❌      | ✅    |
| View dashboard summary      | ❌     | ✅      | ✅    |
| View category breakdown     | ❌     | ✅      | ✅    |
| View monthly trend          | ❌     | ✅      | ✅    |
| Manage users                | ❌     | ❌      | ✅    |

---

## Project Structure

```
src/main/java/com/finance/dashboard/
├── config/
│   ├── SecurityConfig.java       # Spring Security + JWT filter setup
│   └── DataSeeder.java           # Seeds users and transactions on startup
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── TransactionController.java
│   └── DashboardController.java
├── dto/
│   ├── request/                  # Incoming request bodies with validation
│   └── response/                 # Outgoing response shapes
├── entity/
│   ├── User.java
│   └── Transaction.java
├── enums/
│   ├── Role.java                 # VIEWER, ANALYST, ADMIN
│   └── TransactionType.java      # INCOME, EXPENSE
├── exception/
│   └── GlobalExceptionHandler.java  # Consistent error responses
├── repository/
│   ├── UserRepository.java
│   └── TransactionRepository.java   # JPQL dashboard queries
├── security/
│   ├── JwtUtil.java              # Token generation and validation
│   └── JwtAuthFilter.java        # Reads Bearer token on each request
└── service/
    ├── AuthService.java
    ├── UserService.java
    ├── TransactionService.java
    └── DashboardService.java
```

---

## Sample Request Examples

### Create a transaction (ADMIN)
```http
POST /api/transactions
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "amount": 5000.00,
  "type": "EXPENSE",
  "category": "Travel",
  "date": "2024-04-10",
  "notes": "Conference travel"
}
```

### Get dashboard summary (ANALYST or ADMIN)
```http
GET /api/dashboard/summary
Authorization: Bearer <analyst-token>
```

**Response:**
```json
{
  "totalIncome": 260000.00,
  "totalExpenses": 39800.00,
  "netBalance": 220200.00
}
```

### Paginated + filtered transactions
```http
GET /api/transactions?type=EXPENSE&category=Food&page=0&size=5
Authorization: Bearer <viewer-token>
```

---



---
Finance dashboard can be integrated into the banking project I have created.

GitHub link: https://github.com/PrayagKad/Distributed-Payment-Engine
this simulates a real world bank system, that handles transactions from multiple users. Each user can create two type of accounts 
1.savings account
2.current account
these transactions made by multiple users can be used as transactions data required for dashboard.


---

## Design Decisions & Tradeoffs

1. **H2 in-memory DB** — Data resets on restart. Easy for testing; swap with PostgreSQL by changing `application.properties` and adding the driver dependency.

2. **Simple JWT** — Token is signed with HMAC-SHA256. Role is embedded in the token so no DB lookup is needed on each request.

3. **Soft delete** — Deleted transactions are marked `deleted=true` and hidden from all queries. Raw data stays in the DB.

4. **No refresh tokens** — The token lasts 24 hours. Not suitable for production but appropriate here.

5. **Self-registration always gives VIEWER role** — An admin must manually promote users via `PUT /api/users/{id}/role`.

6. **Dashboard queries use JPQL aggregation** — No loading all rows into memory and summing in Java.
