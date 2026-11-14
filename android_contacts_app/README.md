# Android Contacts CRUD app (native Kotlin, XML UI)

A native Android client for the Spring Boot `contacts` API. Architecture mirrors the
team's house style: **Retrofit + coroutines + a `Result` wrapper + Repository + ViewModel**,
with a classic **XML / RecyclerView** UI (ViewBinding, no Compose).

## Prerequisites
- Android Studio (this project was built with the Gradle wrapper + the JBR bundled in
  Android Studio — **Java 21**, so the wrapper uses **Gradle 8.7**).
- The backend running (see `../backend` → `docker compose up --build`).

## Open & run
1. Open `android_contacts_app/` in Android Studio (it creates `local.properties` with your
   SDK path automatically — the committed one points at this machine's SDK).
2. Run on an **emulator** — the app talks to `http://10.0.2.2:8080` (the emulator's alias
   for the host's `localhost`).

### Running on a physical device
`10.0.2.2` only works on the emulator. For a real phone, either:
- **USB tunnel (no Wi-Fi needed):** `adb reverse tcp:8080 tcp:8080` and change `BASE_URL`
  in `data/api/RetrofitClient.kt` to `http://localhost:8080/api/`, **or**
- **Same Wi-Fi:** set `BASE_URL` to your PC's LAN IP, e.g. `http://192.168.1.42:8080/api/`.

Cleartext HTTP to `10.0.2.2`/`localhost` is already allowed via
`res/xml/network_security_config.xml` (dev only).

## Build from the command line
```bash
# JAVA_HOME must point at a JDK 17+ (e.g. Android Studio's jbr)
./gradlew assembleDebug
# APK -> app/build/outputs/apk/debug/app-debug.apk
```

## Structure
- `data/Result.kt` — status/data/message wrapper
- `data/api/BaseRemoteDataSource.kt` — turns a Retrofit `Response` into a `Result`
- `data/api/ContactApi.kt` — Retrofit interface (5 CRUD endpoints)
- `data/api/RetrofitClient.kt` — Retrofit + Gson + OkHttp logging, base URL
- `data/ContactRepository.kt` — repository over the API
- `ui/ContactViewModel.kt` — `LiveData<Result<…>>` + CRUD actions (viewModelScope)
- `ui/MainActivity.kt` — list (RecyclerView + SwipeRefresh), FAB, delete dialog
- `ui/ContactFormActivity.kt` — add/edit form with validation
- `ui/ContactAdapter.kt` — `ListAdapter` + `DiffUtil`
- `res/layout/` — `activity_main`, `activity_contact_form`, `item_contact`

## Verified
Built to an APK and run on a connected device against the live backend: the list loaded
existing contacts and creating a new one persisted it to Postgres (confirmed via the API).
