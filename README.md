

# Custom OAuth2 RBAC Authentication Service

An authentication and authorization service implementing **RBAC (Role-Based Access Control)** with a **custom OAuth2 flow**, **JWT access tokens**, and **Redis-backed refresh tokens**.  
Supports **multi-provider login** including **Google, GitHub, and Facebook**.

---

## Features

- Custom OAuth2 authentication flow (not Spring default auto-config)
- Role-Based Access Control (RBAC)
- JWT access tokens (stateless)
- Refresh tokens stored and managed in Redis
- Multi-provider OAuth2 login:
  - Google
  - GitHub
  - Facebook
- Secure token rotation & logout support
- Built with Spring Boot & Spring Security

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring Security
- OAuth2
- JWT
- Redis
- Maven
- Docker & Docker Compose

---

## Architecture Overview

- **Access Token**:  
  - JWT  
  - Short-lived  
  - Used for API authorization  

- **Refresh Token**:  
  - Stored in Redis  
  - Can be revoked  
  - Used to issue new access tokens  

- **RBAC**:  
  - Users → Roles → Permissions  
  - Fine-grained authorization at API level  

---

## OAuth2 Providers

The service supports multiple OAuth2 providers with a unified internal flow:

- Google
- GitHub
- Facebook

Each provider is mapped to a common user identity model in the system.

---

## Application-secret.yml

Create a `application-secret.yml` file:

```
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=900

REDIS_HOST=localhost
REDIS_PORT=6379

GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=

GITHUB_CLIENT_ID=
GITHUB_CLIENT_SECRET=

FACEBOOK_CLIENT_ID=
FACEBOOK_CLIENT_SECRET=
````

Sensitive configs are excluded via `.gitignore`.

---

## Running Locally

### Using Docker Compose

```bash
docker-compose up -d
```

### Using Maven

```bash
mvn clean install
mvn spring-boot:run
```

---

## API Flow (Simplified)

1. User authenticates via OAuth2 provider
2. Server validates provider token
3. JWT access token is issued
4. Refresh token is stored in Redis
5. Client uses access token for secured APIs
6. Refresh token is used to renew access token when expired

---

## Project Status

This project is intended as:

* A reference implementation
* A backend authentication service template
* A showcase of custom OAuth2 + RBAC design

---
