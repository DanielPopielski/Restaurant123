# Restaurant API

REST API do zarządzania restauracją: użytkownicy (JWT), menu, stoliki i zamówienia,
plus powiadomienia kuchni w czasie rzeczywistym (Kafka + WebSocket/STOMP).
Stack: Java 17, Spring Boot 3.3, Spring Security 6, PostgreSQL, JPA/Hibernate, Apache Kafka.

## Wymagania

- Java 17+
- PostgreSQL (najprościej przez Dockera):

```bash
docker run -d --name restaurant-db -p 5432:5432 \
  -e POSTGRES_DB=restaurant -e POSTGRES_PASSWORD=postgres postgres:16
```

Kafka (powiadomienia dla kuchni):

```bash
docker run -d --name restaurant-kafka -p 9092:9092 apache/kafka:3.8.0
```

## Uruchomienie

```bash
./mvnw spring-boot:run
```

Konfiguracja przez zmienne środowiskowe (opcjonalnie): `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`.

**Uwaga:** przed wdrożeniem produkcyjnym ustaw własny `JWT_SECRET` (base64):
`openssl rand -base64 64`

## Frontend (React + Vite)

W folderze `frontend/`. Wymaga Node.js 18+ (https://nodejs.org).

```bash
cd frontend
npm install
npm run dev
```

Aplikacja: **http://localhost:5173** (żądania `/api` i `/ws` są proxowane do backendu na :8080).
Funkcje: logowanie/rejestracja, menu z koszykiem, moje zamówienia, panel admina
(dania, stoliki, statusy zamówień) i ekran kuchni na żywo (WebSocket).

## Dokumentacja API

Po uruchomieniu: http://localhost:8080/swagger-ui.html

## Endpointy

| Metoda | Ścieżka | Dostęp | Opis |
|---|---|---|---|
| POST | /api/v1/auth/register | publiczny | rejestracja (zwraca token) |
| POST | /api/v1/auth/authenticate | publiczny | logowanie (zwraca token) |
| GET | /api/v1/dishes | zalogowany | lista dań |
| POST/PUT/DELETE | /api/v1/dishes | ADMIN | zarządzanie daniami |
| GET | /api/v1/tables | zalogowany | lista stolików |
| POST/PUT/DELETE | /api/v1/tables | ADMIN | zarządzanie stolikami |
| POST | /api/v1/orders | zalogowany | złożenie zamówienia |
| GET | /api/v1/orders/my | zalogowany | moje zamówienia |
| GET | /api/v1/orders | ADMIN | wszystkie zamówienia |
| PATCH | /api/v1/orders/{id}/status | ADMIN | zmiana statusu |

## Przykłady (curl)

```bash
# Rejestracja
curl -X POST localhost:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"daniel","password":"password123"}'

# Logowanie
TOKEN=$(curl -s -X POST localhost:8080/api/v1/auth/authenticate \
  -H 'Content-Type: application/json' \
  -d '{"username":"daniel","password":"password123"}' | jq -r .token)

# Lista dań
curl localhost:8080/api/v1/dishes -H "Authorization: Bearer $TOKEN"

# Złożenie zamówienia
curl -X POST localhost:8080/api/v1/orders \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"tableId":1,"items":[{"dishId":1,"quantity":2}]}'
```

## Nadanie roli ADMIN

Pierwszego admina ustaw ręcznie w bazie:

```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'daniel';
```

## Ekran kuchni (Kafka + WebSocket)

Po złożeniu zamówienia (`POST /api/v1/orders`) aplikacja publikuje zdarzenie na topic Kafki
`kitchen-orders`. Konsument odbiera je i wypycha przez WebSocket (STOMP) na `/topic/kitchen`.

Podgląd na żywo: otwórz **http://localhost:8080/kitchen.html**, złóż zamówienie przez API —
powiadomienie pojawi się natychmiast, bez odświeżania.

Jeśli Kafka nie działa, aplikacja wystartuje normalnie (REST działa), a powiadomienia będą
logowane jako błąd producenta.

## Statusy zamówień

`NEW → IN_PROGRESS → READY → DELIVERED` (lub `CANCELLED`)
