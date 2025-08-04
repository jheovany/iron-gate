# Iron Gate

Iron Gate is a secure REST API built with Spring Boot and Kotlin that implements JWT authentication and authorization with role-based access control.

## Features

- JWT-based authentication with access and refresh tokens
- Role-based authorization (USER, MODERATOR, ADMIN)
- User registration and authentication
- Token validation and refresh
- Session management (logout, logout from all devices)
- PostgreSQL database integration

## Technologies

- **Framework**: Spring Boot
- **Language**: Kotlin
- **Database**: PostgreSQL
- **Security**: JWT (JSON Web Tokens)
- **Build Tool**: Gradle

## Getting Started

### Prerequisites

- JDK 17 or higher
- PostgreSQL database
- Gradle

### Environment Variables

The application requires the following environment variables:

```
DATASOURCE_URL=jdbc:postgresql://localhost:5432/irongate
DATASOURCE_USERNAME=postgres
DATASOURCE_PASSWORD=your_password
JWT_SECRET_KEY=your_secret_key_at_least_256_bits_long_base64_encoded
JWT_EXPIRATION=86400000 (optional, default: 24 hours in milliseconds)
JWT_REFRESH_EXPIRATION=604800000 (optional, default: 7 days in milliseconds)
```

### Building and Running

1. Clone the repository
2. Set up the required environment variables
3. Build the project:
   ```
   ./gradlew build
   ```
4. Run the application:
   ```
   ./gradlew bootRun
   ```

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Authenticate a user and get tokens
- `POST /api/auth/refresh-token` - Refresh an access token
- `POST /api/auth/logout` - Logout from current session
- `POST /api/auth/logout-all` - Logout from all sessions
- `GET /api/auth/me` - Get current user information
- `GET /api/auth/validate` - Validate a token

### User Management

The API includes role-based endpoints for different user types:

- User endpoints: `/api/users/**`
- Moderator endpoints: `/api/moderators/**`
- Admin endpoints: `/api/admins/**`

## Security

The application implements several security features:

- JWT authentication with signature verification
- Role-based access control
- Token expiration and refresh mechanism
- Secure password storage with encryption
- Protection against common web vulnerabilities

## License

This project is licensed under the terms of the license included in the repository.