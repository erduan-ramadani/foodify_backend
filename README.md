# Foodify Backend

Backend service for **Foodify**, built with Java and Spring Boot.

Foodify is a nutrition tracking application. This backend provides authentication, user management and food entry management through a secured REST API.

## Tech Stack

* Java 21
* Spring Boot 4
* Spring MVC
* Spring Security
* Spring Data JPA
* PostgreSQL
* JWT Authentication
* Bean Validation
* Gradle
* JUnit 5
* Mockito
* MockMvc
* H2 for testing

## Features

### Authentication

* User registration
* User login
* JWT-based authentication
* BCrypt password hashing
* Stateless authentication with Spring Security
* Protected API endpoints

### User Management

* Create users
* Retrieve users
* Update user information
* Delete users
* Pagination for user lists
* Email uniqueness validation

### Food Entries

Authenticated users can manage their own food entries.

Each food entry contains:

* Name
* Calories
* Protein
* Carbohydrates
* Fat
* Creation date

Supported operations include:

* Create food entries
* Retrieve food entries
* Update food entries
* Delete food entries
* Filter entries by date
* Paginated food entry retrieval

Food entries are associated with the authenticated user so that users can only modify their own entries.

## Architecture

The application follows a layered backend architecture with clear separation of responsibilities.

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
PostgreSQL
```

Authentication requests additionally pass through the Spring Security filter chain:

```text
Client
  |
  | Authorization: Bearer <JWT>
  v
Spring Security
  |
  v
JwtAuthenticationFilter
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
PostgreSQL
```

### Project Structure

```text
controller
    REST API endpoints

service
    Business logic

repository
    Database access with Spring Data JPA

entity
    JPA persistence models

dto
    Request and response models

security
    JWT authentication filter

config
    Spring Security configuration

exception
    Custom exceptions and centralized error handling
```

## REST API

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

Registration creates a new user with a BCrypt-hashed password.

Successful login returns a JWT access token.

### Users

```text
GET    /api/users/{id}
GET    /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}
```

User lists support Spring Data pagination.

### Food Entries

```text
POST   /api/food-entries
GET    /api/food-entries
GET    /api/food-entries/date?date=YYYY-MM-DD
PUT    /api/food-entries/{foodEntryId}
DELETE /api/food-entries/{foodEntryId}
```

Food entry endpoints require authentication.

## Security

The backend uses stateless JWT authentication.

After a successful login, the server generates a signed JWT containing the user's email.

Clients send the token with subsequent requests:

```text
Authorization: Bearer <token>
```

A custom `JwtAuthenticationFilter`:

1. Reads the Bearer token
2. Validates and parses the JWT
3. Extracts the user's email
4. Loads the user from the database
5. Adds the authenticated user to the Spring Security context

Passwords are never stored directly and are hashed using BCrypt.

## Validation

Incoming requests are validated using Jakarta Bean Validation.

Examples include:

* Valid email addresses
* Required fields
* Minimum password length
* Non-negative calorie values

Invalid requests return structured HTTP `400 Bad Request` responses.

## Error Handling

A centralized `@RestControllerAdvice` handles application errors consistently.

Examples:

```text
400 Bad Request
401 Unauthorized
404 Not Found
409 Conflict
500 Internal Server Error
```

Error responses contain:

```text
status
message
timestamp
```

## Testing

The project contains both unit and integration tests.

### Unit Tests

Service and security components are tested using JUnit 5 and Mockito.

Examples include:

* Successful and failed user creation
* Duplicate email handling
* Login with valid credentials
* Invalid login credentials
* Food entry creation
* Food entry ownership checks
* Updating and deleting food entries
* JWT generation and parsing
* Invalid or manipulated JWTs
* Expired JWTs
* JWT authentication filter behavior

### Controller Tests

MockMvc is used to verify:

* HTTP status codes
* Request validation
* Pagination
* Successful responses
* Error responses

### Security Integration Tests

Integration tests verify the complete Spring Security flow, including:

* Requests without JWT
* Requests with valid JWT
* Invalid JWTs
* Expired JWTs
* Public authentication endpoints
* Protected endpoints

H2 is used as the test database.

## Key Learnings

This project is part of my transition from client-side development toward Java backend development.

### Spring Boot Architecture

I practiced structuring a backend into controllers, services, repositories, DTOs and persistence entities while keeping business logic separated from HTTP and database concerns.

### REST API Development

I implemented REST endpoints for authentication, users and nutrition data, including CRUD operations, pagination and filtering.

### Spring Data JPA

I gained practical experience with:

* JPA entities
* Entity relationships
* Repository interfaces
* Derived queries
* Pagination
* Transaction management

### Authentication and Security

I implemented JWT-based stateless authentication with Spring Security and learned how authentication filters, the Security Context and password hashing work together.

### Testing

A major focus of the project is automated testing.

I practiced:

* Unit testing with JUnit
* Dependency mocking with Mockito
* Controller testing with MockMvc
* Security integration testing
* Testing authentication success and failure paths

### Error Handling and Validation

I implemented centralized exception handling and request validation to create consistent API behavior and meaningful HTTP responses.

## Current Development

The backend is actively being expanded alongside the Foodify application.

Current development focuses on building a solid foundation for:

* Nutrition tracking
* User-specific data
* Secure authentication
* Backend-driven Foodify features
* Further API and database functionality

## Purpose

Foodify Backend is a personal project for applying and deepening practical knowledge of Java backend development with Spring Boot.

The project focuses on writing maintainable backend code, understanding server-side architecture and building a secure REST API that can be integrated with the Foodify client application.
