# Contacts CRUD API — Spring Boot + Postgres

A minimal REST API over a single `contacts` table (`id, name, email, phone`).

## Run with Docker (recommended — no JDK/Maven/Postgres needed locally)

```bash
cd backend
docker compose up --build
```

This starts:
- **Postgres** on `localhost:5432` (db `contactsdb`, user/pass `contacts`/`contacts`)
- **API** on `http://localhost:8080`

Stop with `Ctrl+C`; remove everything (including DB data) with `docker compose down -v`.

## REST endpoints

| Method | Path                 | Body              | Returns             |
|--------|----------------------|-------------------|---------------------|
| GET    | `/api/contacts`      | —                 | `200` list          |
| GET    | `/api/contacts/{id}` | —                 | `200` one / `404`   |
| POST   | `/api/contacts`      | `{name,email,phone}` | `201` created    |
| PUT    | `/api/contacts/{id}` | `{name,email,phone}` | `200` / `404`    |
| DELETE | `/api/contacts/{id}` | —                 | `204` / `404`       |

Validation: `name` and `email` are required, `email` must be a valid address
(otherwise `400` with per-field messages).

## Quick test

```bash
curl -X POST http://localhost:8080/api/contacts \
  -H "Content-Type: application/json" \
  -d '{"name":"Ada Lovelace","email":"ada@example.com","phone":"+1-555-0100"}'

curl http://localhost:8080/api/contacts
```

## Client base URLs
- Android emulator: `http://10.0.2.2:8080/api/`
- iOS simulator / Flutter web / desktop: `http://localhost:8080/api/`
- Physical device on same Wi-Fi: `http://<your-PC-LAN-IP>:8080/api/`

## Run without Docker
Needs JDK 17 + a local Postgres with db `contactsdb`. Then:
```bash
./mvnw spring-boot:run   # or: mvn spring-boot:run
```
