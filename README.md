# Contacts CRUD — full stack demo

One simple table (`contacts`: `id, name, email, phone`) exposed through a REST API and
consumed by two mobile clients.

```
ContactsCrud/
├── backend/                # Spring Boot + PostgreSQL REST API (Dockerized)
├── flutter_contacts_app/   # Flutter client (http)
└── android_contacts_app/   # Native Android client (Kotlin, Retrofit, XML UI)
```

## 1. Start the backend (Docker)
```bash
cd backend
docker compose up --build
```
API → `http://localhost:8080/api/contacts`, Postgres → `localhost:5432`.
See `backend/README.md` for endpoints and curl examples.

## 2. Run a client
- **Flutter:** see `flutter_contacts_app/README.md` (needs the Flutter SDK installed —
  it was not present on this machine).
- **Android:** open `android_contacts_app/` in Android Studio and run on an emulator.
  See `android_contacts_app/README.md`.

## REST contract
| Method | Path                 | Body                  | Returns            |
|--------|----------------------|-----------------------|--------------------|
| GET    | `/api/contacts`      | —                     | `200` list         |
| GET    | `/api/contacts/{id}` | —                     | `200` one / `404`  |
| POST   | `/api/contacts`      | `{name,email,phone}`  | `201` created      |
| PUT    | `/api/contacts/{id}` | `{name,email,phone}`  | `200` / `404`      |
| DELETE | `/api/contacts/{id}` | —                     | `204` / `404`      |

## Base URLs by platform (local dev)
| Client                         | Base URL                        |
|--------------------------------|---------------------------------|
| Android emulator               | `http://10.0.2.2:8080/api/`     |
| Android physical (adb reverse) | `http://localhost:8080/api/`    |
| Flutter web / desktop / iOS    | `http://localhost:8080/api/`    |
| Physical device on Wi-Fi       | `http://<PC-LAN-IP>:8080/api/`  |

## Status
- **Backend:** built & verified via Docker — all 5 endpoints, plus 404/400 error cases,
  with data persisted in Postgres.
- **Android:** built to an APK and run on a real device against the live backend
  (list load + create persisted to Postgres confirmed).
- **Flutter:** source complete; run once the Flutter SDK is installed.
