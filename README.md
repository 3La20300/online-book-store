# Online Book Store Management System

Full Stack Graduation Project Specification and Implementation Guide.

---

## 1. Project Overview

The Online Book Store Management System is a full-stack web application developed with Spring Boot, Angular, and PostgreSQL. The system implements secure Role-Based Access Control (RBAC) supporting two distinct roles:
- **USER (Customer):** Can browse the public book catalog, view detailed book information, and manage their profile.
- **ADMIN (Administrator):** Has exclusive access to administrative operations, including full CRUD management of book inventory and administrator user account management.

---

## 2. Technology Stack

### Backend
- **Framework:** Java 21, Spring Boot 3.3.4
- **Security:** Spring Security, JSON Web Token (JJWT 0.12.6), BCrypt Password Hashing
- **Data Persistence:** Spring Data JPA, Hibernate, PostgreSQL Driver
- **Validation:** Jakarta Bean Validation
- **Documentation:** SpringDoc OpenAPI 3.1 / Swagger UI
- **Build Tool:** Apache Maven

### Frontend
- **Framework:** Angular 19 (Standalone Components, Reactive Forms)
- **State Management:** Angular Signals and RxJS
- **Routing & Security:** Angular Functional Route Guards (`authGuard`, `adminGuard`, `noAuthGuard`)
- **HTTP Client:** Angular `HttpClient` with Functional Interceptors (`authInterceptor`)
- **Styling:** Modular SCSS design system with CSS custom properties

### Infrastructure & Database
- **Database:** PostgreSQL 16 (Alpine)
- **Containerization:** Docker & Docker Compose

---

## 3. Team Responsibilities & Module Breakdown

This project is divided between two team members following a vertical domain feature architecture:

### Person 1: Security, Identity & Admin Management (Completed)
- **Backend:**
  - User JPA Entity and Role Enum (`USER`, `ADMIN`).
  - Spring Security configuration with stateless session management.
  - JWT generation, validation, and request filtering (`JwtService`, `JwtAuthenticationFilter`).
  - Authentication Controller & Service (`POST /api/auth/register`, `POST /api/auth/login`).
  - Current User Profile Controller (`GET /api/users/me`).
  - Administrator Management Controller & Service (`GET`, `POST`, `DELETE /api/admins`).
  - Centralized Exception Handling (`@RestControllerAdvice`) mapping standard HTTP status codes.
- **Frontend:**
  - Core TypeScript models (`User`, `AuthResponse`, `LoginRequest`, `RegisterRequest`).
  - Reactive `AuthService` and `AdminService`.
  - HTTP `authInterceptor` for automatic Bearer token injection.
  - Route Guards: `authGuard`, `adminGuard`, and `noAuthGuard`.
  - Authentication views: `LoginComponent` (with role-based routing) and `SignupComponent`.
  - Administration views: `AdminLayoutComponent` (sidebar layout) and `ManageAdminsComponent`.

### Person 2: Book Catalog, Storefront & Inventory CRUD (In Progress)
- **Backend:**
  - Book JPA Entity and Book Repository.
  - Book Service and Controller (`GET`, `POST`, `PUT`, `DELETE /api/books`).
  - Database Seeder (`CommandLineRunner`) for initial default admin and sample book records.
- **Frontend:**
  - Storefront UI: `NavbarComponent`, `BookCardComponent`, `HomeComponent` (Catalog & Hero).
  - Details view: `BookDetailsComponent` (`/books/:id`).
  - Book inventory management: `ManageBooksComponent` (`/admin/books` with Add/Edit/Delete modals).

---

## 4. System Architecture

```
[ Angular SPA (Port 4200) ]
        |
        | HTTP Requests + [Authorization: Bearer <JWT>]
        v
[ Spring Boot REST API (Port 8082) ]
        |
        +---> [ JwtAuthenticationFilter ] ---> [ SecurityContext ]
        |
        +---> [ REST Controllers (Auth, User, Admin, Book) ]
        |
        +---> [ Service Layer ]
        |
        +---> [ JPA Repositories ]
        |
        v
[ PostgreSQL Database (Port 5432) ]
```

---

## 5. Prerequisites

Ensure you have the following installed on your local environment:
- **Java Development Kit (JDK):** Version 17 or 21
- **Node.js:** Version 18.x or 20.x
- **Angular CLI:** `npm install -g @angular/cli`
- **Docker & Docker Compose**

---

## 6. Getting Started

### Step 1: Clone the Repository
```bash
git clone https://github.com/3La20300/online-book-store.git
cd online-book-store
```

### Step 2: Start PostgreSQL Database via Docker
Start the PostgreSQL container:
```bash
docker compose up -d
```
Verify that the database container is healthy and listening on port `5432` (or your mapped port).

### Step 3: Start the Backend Service
Navigate to the `backend` directory and run:
```bash
cd backend
./mvnw clean spring-boot:run
```
*(On Windows PowerShell, use `.\mvnw.cmd clean spring-boot:run`)*

The backend server will start on `http://localhost:8082`.
- Swagger UI Documentation: `http://localhost:8082/swagger-ui.html`
- OpenAPI Specification: `http://localhost:8082/v3/api-docs`

### Step 4: Start the Frontend Application
In a separate terminal, navigate to the `frontend` directory:
```bash
cd frontend
npm install
npm start
```
The Angular development server will run at `http://localhost:4200`.

---

## 7. REST API Endpoints

### Authentication & Profile (`/api/auth`, `/api/users`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Registers a new user with default role `USER`. |
| `POST` | `/api/auth/login` | Public | Authenticates credentials and returns a JWT token. |
| `GET` | `/api/users/me` | Authenticated | Retrieves current authenticated user profile. |

### Administrator Management (`/api/admins`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admins` | ADMIN | Retrieves list of all administrator accounts. |
| `POST` | `/api/admins` | ADMIN | Registers a new administrator account (`role = ADMIN`). |
| `DELETE` | `/api/admins/{id}` | ADMIN | Removes an administrator account by ID. |

### Book Catalog & Management (`/api/books`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/books` | Public / USER / ADMIN | Retrieves list of all books. |
| `GET` | `/api/books/{id}` | Public / USER / ADMIN | Retrieves detailed information for a single book. |
| `POST` | `/api/books` | ADMIN | Adds a new book to the inventory. |
| `PUT` | `/api/books/{id}` | ADMIN | Updates an existing book record. |
| `DELETE` | `/api/books/{id}` | ADMIN | Permanently deletes a book from inventory. |

---

## 8. Error Handling & HTTP Status Codes

The backend implements centralized exception handling via `@RestControllerAdvice` returning standard JSON payloads:

```json
{
  "timestamp": "2026-09-29T21:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "path": "/api/auth/register",
  "validationErrors": {
    "email": "Email must be a valid email address",
    "password": "Password must be at least 6 characters long"
  }
}
```

### Standard Status Codes:
- **200 OK:** Request succeeded.
- **201 Created:** Resource successfully created.
- **204 No Content:** Resource successfully deleted.
- **400 Bad Request:** Form validation failure or password mismatch.
- **401 Unauthorized:** Invalid or missing authentication credentials.
- **403 Forbidden:** Authenticated user lacks permission for the requested resource.
- **404 Not Found:** Requested entity does not exist.
- **409 Conflict:** Resource already exists (e.g. duplicate email registration).
- **500 Internal Server Error:** Unexpected server-side exception.

---

## 9. Project Directory Layout

```
online-book-store/
|-- docker-compose.yml
|-- README.md
|
|-- backend/
|   |-- pom.xml
|   `-- src/main/java/com/bookstore/
|       |-- BookStoreApplication.java
|       |-- config/              (SecurityConfig, CorsConfig, OpenApiConfig)
|       |-- controller/          (AuthController, UserController, AdminController, BookController)
|       |-- dto/                 (request/ and response/ DTOs)
|       |-- entity/              (User, Book, Role)
|       |-- exception/           (GlobalExceptionHandler, Custom Exceptions)
|       |-- repository/          (UserRepository, BookRepository)
|       |-- security/            (JwtService, JwtAuthenticationFilter, CustomUserDetailsService)
|       `-- service/             (AuthService, AdminService, BookService)
|
`-- frontend/
    |-- package.json
    |-- angular.json
    `-- src/app/
        |-- app.config.ts
        |-- app.routes.ts
        |-- core/
        |   |-- guards/          (auth.guard, admin.guard, no-auth.guard)
        |   |-- interceptors/    (auth.interceptor)
        |   |-- models/          (user.model, auth.model, book.model)
        |   `-- services/        (auth.service, admin.service, book.service)
        |-- features/
        |   |-- auth/            (login, signup)
        |   |-- admin/           (admin-layout, manage-admins, manage-books)
        |   `-- customer/        (home, book-details)
        `-- shared/              (navbar, footer, book-card)
```
