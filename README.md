# Online Book Store Management System

Full Stack Web Application Specification and Implementation Guide.

---

## 1. Project Description

The Online Book Store Management System is an enterprise-grade full-stack web application designed for online book catalog browsing and administration. The application is built with a decoupled architecture utilizing a Spring Boot RESTful backend, an Angular Single Page Application (SPA) frontend, and a containerized PostgreSQL relational database.

The application enforces secure Role-Based Access Control (RBAC) across two distinct user roles:
- **USER (Customer):** Can register an account, browse the book catalog, view detailed book specifications, and retrieve their profile information.
- **ADMIN (Administrator):** Has exclusive access to the administration dashboard, allowing complete CRUD (Create, Read, Update, Delete) operations over book inventory and management of administrator accounts.

---

## 2. Key Features

### Authentication & Authorization
- Customer self-registration with automatic default `USER` role assignment.
- Secure credential authentication utilizing BCrypt password hashing and JSON Web Tokens (JWT).
- Stateless request authorization through custom Spring Security filter chains.
- Role-based route protection on both frontend (Angular Route Guards) and backend (`@PreAuthorize`).
- Protected administrative APIs preventing privilege escalation.

### Customer Storefront
- Dynamic landing page with store branding and book catalog showcase.
- Responsive book grid displaying cover images, titles, categories, and formatted pricing.
- Detailed book view presenting complete descriptions, authors, and metadata.

### Administrative Management
- Dedicated admin portal accessible only to authenticated administrators.
- Comprehensive book inventory management (Add, Edit, View, and Delete books).
- Administrator user management (View all administrators, register new administrators, and delete admin accounts).

---

## 3. Technology Stack

### Backend
- **Language & Runtime:** Java 21
- **Framework:** Spring Boot 3.3.4
- **Security:** Spring Security, JSON Web Token (JJWT 0.12.6), BCrypt
- **Data Persistence:** Spring Data JPA, Hibernate, PostgreSQL Driver
- **Validation:** Jakarta Bean Validation
- **Documentation:** SpringDoc OpenAPI 3.1 / Swagger UI
- **Build Tool:** Apache Maven

### Frontend
- **Framework:** Angular 19 (Standalone Components, Reactive Forms)
- **State Management:** Angular Signals and RxJS
- **Routing & Guards:** Functional CanActivate Guards (`authGuard`, `adminGuard`, `noAuthGuard`)
- **HTTP Client:** Angular `HttpClient` with Functional Interceptors (`authInterceptor`)
- **Styling:** Modular SCSS design system with CSS custom properties

### Infrastructure & Database
- **Database:** PostgreSQL 16 (Alpine)
- **Containerization:** Docker & Docker Compose

---

## 4. System Architecture

```
[ Angular Single Page Application (Port 4200) ]
                      |
                      | HTTP Requests + [Authorization: Bearer <JWT>]
                      v
       [ Spring Boot REST API (Port 8082) ]
                      |
        +-------------+-------------+
        |                           |
        v                           v
[ JwtAuthenticationFilter ]  [ GlobalExceptionHandler ]
        |                           |
        v                           v
[ SecurityContext / RBAC ]   [ Standard JSON Errors ]
        |
        +---> [ REST Controllers (Auth, User, Admin, Book) ]
        |
        +---> [ Business Service Layer ]
        |
        +---> [ Spring Data JPA Repositories ]
        |
        v
[ PostgreSQL Relational Database (Port 5432) ]
```

---

## 5. Prerequisites

Ensure the following tools are installed on your environment:
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
Start the containerized PostgreSQL service:
```bash
docker compose up -d
```
Verify that the container is running and healthy on port `5432`.

### Step 3: Launch the Backend Service
Navigate to the `backend` directory and start the Spring Boot application:
```bash
cd backend
./mvnw clean spring-boot:run
```
*(On Windows PowerShell, execute: `.\mvnw.cmd clean spring-boot:run`)*

The backend server starts on `http://localhost:8082`.
- Swagger UI Documentation: `http://localhost:8082/swagger-ui.html`
- OpenAPI Specification: `http://localhost:8082/v3/api-docs`

### Step 4: Launch the Frontend Application
In a separate terminal, navigate to the `frontend` directory:
```bash
cd frontend
npm install
npm start
```
The Angular development server will start on `http://localhost:4200`.

---

## 7. REST API Endpoints

### Authentication & Profile (`/api/auth`, `/api/users`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Public | Registers a new user with default role `USER`. |
| `POST` | `/api/auth/login` | Public | Authenticates credentials and returns a signed JWT token. |
| `GET` | `/api/users/me` | Authenticated | Retrieves the authenticated user profile from SecurityContext. |

### Administrator Management (`/api/admins`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admins` | ADMIN | Retrieves a list of all administrator accounts. |
| `POST` | `/api/admins` | ADMIN | Creates a new administrator account (`role = ADMIN`). |
| `DELETE` | `/api/admins/{id}` | ADMIN | Removes an administrator account by ID. |

### Book Catalog & Inventory (`/api/books`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/books` | Public / USER / ADMIN | Retrieves list of all books. |
| `GET` | `/api/books/{id}` | Public / USER / ADMIN | Retrieves full details for a single book. |
| `POST` | `/api/books` | ADMIN | Adds a new book to inventory. |
| `PUT` | `/api/books/{id}` | ADMIN | Updates an existing book record. |
| `DELETE` | `/api/books/{id}` | ADMIN | Deletes a book record from the inventory. |

---

## 8. Exception Handling & Error Responses

The backend utilizes centralized exception handling with `@RestControllerAdvice` to ensure all error payloads maintain a consistent structure:

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

### Standard HTTP Status Codes:
- **200 OK:** Request completed successfully.
- **201 Created:** New resource created successfully.
- **204 No Content:** Resource deleted successfully.
- **400 Bad Request:** Request validation failure or mismatched inputs.
- **401 Unauthorized:** Invalid or missing authentication token.
- **403 Forbidden:** Authenticated user lacks required administrative permissions.
- **404 Not Found:** Requested resource does not exist.
- **409 Conflict:** Resource constraint violation (e.g. duplicate email registration).
- **500 Internal Server Error:** Unhandled server exception.

---

## 9. Project Directory Structure

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
