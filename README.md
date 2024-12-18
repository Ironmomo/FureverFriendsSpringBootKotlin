# Social Media API

This repository contains a **Spring Boot-based Social Media API** designed for a scalable, secure, and RESTful social media network. Below is a comprehensive technical overview of the API and its key components.

---

## Key-Features

### 1. **Preventing the N+1 Problem**
- Optimized fetching of entities using **Entity Graphs** and **fetch joins** to prevent excessive database queries and improve performance.

### 2. **Type-Safe Notification Service**
- Notifications for events like likes and follow requests are dispatched using a **type-safe notification service**.

### 3. **Type-Safe Actions in DTOs**
- DTOs include a **type-safe `action` property**, listing possible actions for resources with their respective URLs, adhering to RESTful API best practices.

### 4. **Comprehensive API Documentation**
- **Swagger UI** is integrated for user-friendly API exploration and testing.

### 5. **Secure Authentication and Authorization**
- JWT-based security using Spring Security and custom implemented:
  - `JwtAuthenticationFilter` (extends `OncePerRequestFilter`)
  - `JwtAuthentication` (implements `Authentication`)
  - `JwtAuthenticationProvider` (implements `AuthenticationProvider`)
- Role based Endpoint Authorization

### 6. **Persistence and Database**
- Uses **Spring Data JPA** with an **H2 in-memory database** for development and testing.

### 7. **Exception Handling**
- Centralized error handling using `@ControllerAdvice` for consistent and global exception management in the API layer.

### 8. **Business Logic Layer**
- Employs custom **AOP (Aspect-Oriented Programming)** to enforce business rules, such as checking if a user follows another user before allowing specific actions.

---

## Installation

### Prerequisites
1. **Java 17+**
2. **Gradle 8.10+**

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/Ironmomo/FureverFriendsSpringBootKotlin.git
   cd FureverFriendsSpringBootKotlin
   ```
2. Build Project
   ```bash
   ./gradlew clean build
   ```
3. Run the applicaion
      ```bash
   ./gradlew bootRun
   ```
4. Access the API documentation
- Swagger UI: http://localhost:8080/swagger-ui/index.html#/
