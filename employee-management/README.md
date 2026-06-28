# Employee Management System

A production-grade Spring Boot REST API featuring JWT authentication, role-based access control, pagination, filtering, Swagger UI, Docker support, and JUnit tests.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 |
| Security | Spring Security + JWT (jjwt 0.11) |
| Database | MySQL 8.0 + Spring Data JPA |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Containerisation | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Testing | JUnit 5 + Mockito + H2 |
| Build | Maven |

---

## Project Structure

```
src/main/java/com/ems/
├── config/             # Security, Swagger, DataInitializer
├── controller/         # AuthController, EmployeeController, DepartmentController
├── dto/
│   ├── request/        # AuthRequest, EmployeeRequest, DepartmentRequest
│   └── response/       # AuthResponse, EmployeeResponse, DepartmentResponse, ApiResponse
├── entity/             # User, Employee, Department, Role, EmployeeStatus
├── exception/          # GlobalExceptionHandler, EmsExceptions
├── repository/         # UserRepository, EmployeeRepository, DepartmentRepository
├── security/           # JwtUtils, JwtAuthFilter, UserDetailsServiceImpl
└── service/
    └── impl/           # AuthServiceImpl, EmployeeServiceImpl, DepartmentServiceImpl
```

---

## Quick Start with Docker

```bash
# 1. Clone the repo
git clone https://github.com/your-username/employee-management.git
cd employee-management

# 2. Set up environment variables
cp .env.example .env
# Edit .env with your values

# 3. Run with Docker Compose
docker-compose up --build -d

# App runs at: http://localhost:8080
# Swagger UI:  http://localhost:8080/swagger-ui.html
```

---

## Running Locally (without Docker)

```bash
# Prerequisites: Java 17, Maven, MySQL running locally

# 1. Create database
mysql -u root -p -e "CREATE DATABASE employee_db;"

# 2. Update application.properties (change mysql host to localhost)
# spring.datasource.url=jdbc:mysql://localhost:3306/employee_db...

# 3. Build and run
mvn spring-boot:run
```

---

## Default Users (auto-seeded on startup)

| Username | Password | Role |
|---|---|---|
| admin | admin123 | ROLE_ADMIN |
| manager | manager123 | ROLE_MANAGER |

---

## API Endpoints

### Auth
```
POST /api/auth/login      — Get JWT token
POST /api/auth/register   — Register a new user
```

### Employees
```
POST   /api/employees                          — Create (ADMIN, MANAGER)
GET    /api/employees                          — List all (paginated)
GET    /api/employees/{id}                     — Get by ID
GET    /api/employees/search?name=&status=...  — Filter/search
GET    /api/employees/department/{deptId}      — By department
PUT    /api/employees/{id}                     — Update (ADMIN, MANAGER)
PATCH  /api/employees/{id}/status?status=      — Update status only
DELETE /api/employees/{id}                     — Delete (ADMIN only)
```

### Departments
```
POST   /api/departments        — Create (ADMIN, MANAGER)
GET    /api/departments        — List all (paginated)
GET    /api/departments/{id}   — Get by ID
PUT    /api/departments/{id}   — Update (ADMIN, MANAGER)
DELETE /api/departments/{id}   — Delete (ADMIN only)
```

---

## Using the API

### Step 1 — Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Step 2 — Use the JWT token
```bash
curl http://localhost:8080/api/employees \
  -H "Authorization: Bearer <your-token-here>"
```

### Step 3 — Swagger UI
Open `http://localhost:8080/swagger-ui.html`, click **Authorize**, and paste your Bearer token.

---

## Filtering / Search Example

```
GET /api/employees/search?name=muthu&status=ACTIVE&minSalary=50000&page=0&size=10&sortBy=firstName&direction=asc
```

All filter parameters are optional and can be combined freely.

---

## Running Tests

```bash
# All tests (uses H2 in-memory DB)
mvn test

# Specific test class
mvn test -Dtest=EmployeeServiceTest

# With coverage report
mvn test jacoco:report
```

---

## Role-Based Access

| Operation | EMPLOYEE | MANAGER | ADMIN |
|---|---|---|---|
| View employees/departments | ✅ | ✅ | ✅ |
| Create employee/department | ❌ | ✅ | ✅ |
| Update employee/department | ❌ | ✅ | ✅ |
| Delete employee | ❌ | ❌ | ✅ |
| Delete department | ❌ | ❌ | ✅ |

---

## GitHub Secrets (for CI/CD)

Add these in your GitHub repo → Settings → Secrets → Actions:

| Secret | Value |
|---|---|
| DOCKER_USERNAME | Your Docker Hub username |
| DOCKER_PASSWORD | Your Docker Hub password |

---

## Author

Muthu Gopala Krishnan M  
[linkedin.com/in/muthugk](https://linkedin.com/in/muthugk)  
muthugk12@gmail.com
