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
```
