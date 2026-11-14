# Flutter Contacts CRUD app

A simple Flutter client for the Spring Boot `contacts` API (list / add / edit / delete).

## Prerequisites
- Flutter SDK installed (`flutter --version` works). On this machine Flutter was **not**
  found — install it from https://docs.flutter.dev/get-started/install/windows and add
  `flutter\bin` to your PATH.
- The backend running (see `../backend` → `docker compose up --build`).

## First-time setup
This folder ships only `pubspec.yaml` + `lib/`. Generate the platform scaffolding
(android/, ios/, web/, etc.) and fetch packages:

```bash
cd flutter_contacts_app
flutter create .          # generates android/, ios/, web/ ... keeps existing lib/
flutter pub get
```

### Allow cleartext HTTP on Android (dev only)
The API is plain `http://` during development. After `flutter create .`, open
`android/app/src/main/AndroidManifest.xml` and add to the `<application>` tag:

```xml
<application
    android:usesCleartextTraffic="true"
    ... >
```

## Run
```bash
flutter run
```

- **Android emulator** automatically uses `http://10.0.2.2:8080` (host loopback).
- **Web/desktop/iOS simulator** use `http://localhost:8080`.
- **Physical phone:** edit `_lanHost` in `lib/services/contact_api.dart` to your PC's
  LAN IP (e.g. `192.168.1.42`) and make sure the phone is on the same network.

## Architecture — ChangeNotifier + Provider (MVVM)
Mirrors the native Android app's ViewModel architecture:

| Layer | Flutter | Android equivalent |
|-------|---------|--------------------|
| State holder | `ContactViewModel extends ChangeNotifier` | `ContactViewModel : ViewModel()` |
| Notify UI | `notifyListeners()` | `LiveData` value update |
| Observe in UI | `context.watch<ContactViewModel>()` | `viewModel.contacts.observe(...)` |
| One-off action | `context.read<ContactViewModel>()` | direct `viewModel` call |
| Data source | `ContactApi` (http) | `ContactRepository` (Retrofit) |

The `ChangeNotifierProvider` is created in `main.dart` above `MaterialApp`, so the
list and form screens share the **same single** ViewModel instance.

## Structure
- `lib/models/contact.dart` — data model + JSON mapping
- `lib/services/contact_api.dart` — REST calls (`http` package)
- `lib/viewmodels/contact_viewmodel.dart` — **ChangeNotifier** state + CRUD actions
- `lib/screens/contact_list_screen.dart` — observes the ViewModel (`context.watch`)
- `lib/screens/contact_form_screen.dart` — add/edit form; saves via `context.read`
- `lib/main.dart` — app entry + `ChangeNotifierProvider`

> After pulling this change, run `flutter pub get` to fetch the `provider` package.
