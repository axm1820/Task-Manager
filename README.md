# Task Manager API

A Spring Boot REST API for managing projects and tasks, with JWT authentication and
role-based access control. Built as the foundation for a cloud + AI portfolio project
(see "Roadmap" below).

## Stack

- Java 17, Spring Boot 3.3
- Spring Web, Spring Data JPA, Spring Security
- PostgreSQL
- JWT (jjwt)
- Docker / Docker Compose
- Lombok

## Architecture

```
Client
  │
  ▼
JwtAuthFilter ──► SecurityContext
  │
  ▼
Controller (Auth / Project / Task)
  │
  ▼
Service (business rules + ownership checks)
  │
  ▼
Repository (Spring Data JPA)
  │
  ▼
PostgreSQL
```

- **Auth**: stateless JWT. `/api/auth/register` and `/api/auth/login` are public;
  every other endpoint requires a `Bearer` token.
- **Authorization**: project ownership is enforced in `ProjectService.findOwnedProject()`,
  which every task operation also goes through, so access control lives in one place.
- **Errors**: `GlobalExceptionHandler` converts exceptions into a consistent JSON shape
  with timestamp, status, error, message, and field-level validation errors.

## Running locally

```bash
docker compose up --build
```

This starts PostgreSQL and the app together. The API will be available at
`http://localhost:8080`.

To run without Docker (needs a local Postgres instance):

```bash
export DB_URL=jdbc:postgresql://localhost:5432/taskmanager
export DB_USERNAME=taskmanager
export DB_PASSWORD=taskmanager
export JWT_SECRET=some-long-random-string-at-least-256-bits
./mvnw spring-boot:run
```

## API overview

| Method | Endpoint                                  | Auth        | Description                  |
|--------|--------------------------------------------|-------------|-------------------------------|
| POST   | `/api/auth/register`                       | Public      | Create an account             |
| POST   | `/api/auth/login`                          | Public      | Get a JWT                     |
| POST   | `/api/projects`                            | User        | Create a project              |
| GET    | `/api/projects`                            | User        | List your projects (paged)    |
| GET    | `/api/projects/{id}`                       | Owner/Admin | Get a project                 |
| DELETE | `/api/projects/{id}`                       | Owner/Admin | Delete a project              |
| POST   | `/api/projects/{id}/tasks`                 | Owner/Admin | Create a task                 |
| GET    | `/api/projects/{id}/tasks`                 | Owner/Admin | List tasks (paged)            |
| PATCH  | `/api/projects/{id}/tasks/{taskId}`        | Owner/Admin | Update task details and priority |
| PATCH  | `/api/projects/{id}/tasks/{taskId}/status` | Owner/Admin | Update task status            |
| PATCH  | `/api/projects/{id}/tasks/{taskId}/assignee` | Owner/Admin | Assign or unassign a task |
| DELETE | `/api/projects/{id}/tasks/{taskId}`        | Owner/Admin | Delete a task                 |

### Example: register + create a project

```bash
curl -X POST localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Akshat","email":"akshat@example.com","password":"password123"}'

# copy the "token" from the response, then:

curl -X POST localhost:8080/api/projects \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Portfolio Site","description":"Personal site rebuild"}'
```

Tasks support `TODO`, `IN_PROGRESS`, and `DONE` statuses, plus `LOW`, `MEDIUM`, and
`HIGH` priorities and optional due dates. Filter tasks with query parameters such as
`?status=TODO&priority=HIGH&dueBefore=2026-09-10`; pagination and Spring's usual
`sort` parameters remain supported.

Assign a task to a registered user:

```bash
curl -X PATCH localhost:8080/api/projects/1/tasks/10/assignee \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"assigneeId":2}'
```

Send `{"assigneeId":null}` to unassign it. Assigning an unknown user returns `404`.

## Roadmap

This project is being built up in stages:

1. ✅ Core REST API with JWT auth and RBAC (this repo)
2. ⬜ Docker + AWS deployment (ECS/Fargate, RDS, S3, CloudWatch)
3. ⬜ CI/CD with GitHub Actions
4. ⬜ Spring AI-powered project assistant (agentic tool-calling over these endpoints)
5. ⬜ RAG-based document Q&A using pgvector

## Notes on design decisions

- **`ddl-auto: update`** is used for local/dev convenience. In a real deployment this
  should be replaced with Flyway or Liquibase migrations.
- **Ownership checks live in `ProjectService`**, not in controllers or security config,
  so that both `Project` and `Task` operations share one authorization path instead of
  duplicating the "is this yours?" logic.
- **JWT secret and DB credentials are environment-driven** (`application.yml` reads
  from env vars with local defaults), so the same image can run locally and in ECS
  without code changes — just different environment variables.
# Task-Manager
