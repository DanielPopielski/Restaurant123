# Restaurant API

REST API for restaurant management: users (JWT), menu, tables and orders,
plus real-time kitchen notifications (Kafka + WebSocket/STOMP).
Stack: Java 17, Spring Boot 3.3, Spring Security 6, PostgreSQL, JPA/Hibernate, Apache Kafka.

## Quick start (Windows)

With Docker Desktop and Node.js installed, run everything with one command:

```powershell
.\start.ps1
```

This starts PostgreSQL and Kafka via `docker compose`, launches the backend and the
frontend in separate windows and opens the app in your browser. Stop the containers
with `.\stop.ps1`.

## Requirements

- Java 17+
- PostgreSQL (easiest via Docker):

```bash
docker run -d --name restaurant-db -p 5432:5432 \
  -e POSTGRES_DB=restaurant -e POSTGRES_PASSWORD=postgres postgres:16
```

Kafka (kitchen notifications):

```bash
docker run -d --name restaurant-kafka -p 9092:9092 apache/kafka:3.8.0
```

## Running the app

```bash
./mvnw spring-boot:run
```

Configuration via environment variables (optional): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`.

**Note:** before deploying to production, set your own `JWT_SECRET` (base64):
`openssl rand -base64 64`

## Frontend (React + Vite)

Located in the `frontend/` folder. Requires Node.js 18+ (https://nodejs.org).

```bash
cd frontend
npm install
npm run dev
```

App: **http://localhost:5173** (`/api` and `/ws` requests are proxied to the backend on :8080).
Features: login/registration, menu with a cart, order history, admin panel
(dishes, tables, order statuses) and a live kitchen screen (WebSocket).

## API documentation

Once running: http://localhost:8080/swagger-ui.html

To call protected endpoints from Swagger UI: authenticate via
`POST /api/v1/auth/authenticate`, copy the token from the response,
click the **Authorize** button and paste it (without the `Bearer` prefix).

## Endpoints

| Method | Path | Access | Description |
|---|---|---|---|
| POST | /api/v1/auth/register | public | registration (returns a token) |
| POST | /api/v1/auth/authenticate | public | login (returns a token) |
| GET | /api/v1/dishes | authenticated | list dishes |
| POST/PUT/DELETE | /api/v1/dishes | ADMIN | manage dishes |
| GET | /api/v1/tables | authenticated | list tables |
| POST/PUT/DELETE | /api/v1/tables | ADMIN | manage tables |
| POST | /api/v1/orders | authenticated | place an order |
| GET | /api/v1/orders/my | authenticated | my orders |
| GET | /api/v1/orders | ADMIN | all orders |
| PATCH | /api/v1/orders/{id}/status | ADMIN | update order status |

## Examples (curl)

```bash
# Registration
curl -X POST localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"daniel","password":"password123"}'

# Login
TOKEN=$(curl -s -X POST localhost:8080/api/v1/auth/authenticate \
  -H 'Content-Type: application/json' \
  -d '{"username":"daniel","password":"password123"}' | jq -r .token)

# List dishes
curl localhost:8080/api/v1/dishes -H "Authorization: Bearer $TOKEN"

# Place an order
curl -X POST localhost:8080/api/v1/orders \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"tableId":1,"items":[{"dishId":1,"quantity":2}]}'
```

## Granting the ADMIN role

Set up the first admin manually in the database:

```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'daniel';
```

## Kitchen screen (Kafka + WebSocket)

After an order is placed (`POST /api/v1/orders`), the application publishes an event
to the `kitchen-orders` Kafka topic. A consumer picks it up and pushes it over
WebSocket (STOMP) to `/topic/kitchen`.

Live preview: open **http://localhost:8080/kitchen.html**, place an order through the
API — the notification appears instantly, no refresh needed.

If Kafka is down, the application still starts normally (REST keeps working) and
notifications are logged as producer errors.

## Order statuses

`NEW → IN_PROGRESS → READY → DELIVERED` (or `CANCELLED`)
