# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run all unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.samchendev.weathertest.viewModels.WeatherSearchViewModelTest"

# Run a single test method (use the exact method name, backtick names become dots)
./gradlew test --tests "com.samchendev.weathertest.viewModels.WeatherSearchViewModelTest.init loads weather for last saved city"

# Lint
./gradlew lint

# Install on connected device
./gradlew installDebug
```

## Architecture

Single-module Android app (`com.samchendev.weathertest`). Currently has one feature: weather search by city name or GPS coordinates.

**Layer flow:** `Screen` → `ViewModel` → `UseCases` → `Repo` / `CityManager` → `WeatherApi` (Retrofit) / `DataStore`

### Key patterns

**UiState holds callbacks** — `WeatherSearchUiState` bundles `onSearchClick` and `onGetMyCityWeatherTrigger` as lambdas alongside state. Composables receive only the `UiState` object, not separate callback parameters.

**Error messages are resource IDs** — `WeatherSearchViewModel._errorMessage` is a `SharedFlow<Int>` emitting string resource IDs (e.g. `R.string.city_not_found_message`), not raw strings.

**City text field lives on the ViewModel** — `WeatherSearchViewModel.cityState` is a Compose `TextFieldState`. The screen binds to it directly; no state hoisting through the UiState.

**Model mapping** — Network models in `data/remote/models/` are `@Serializable` data classes. `WeatherResponse.toWeatherInfo()` maps to the clean domain model in `domain/models/WeatherInfo`. No mapper class; the conversion method is on the network model itself.

**Use cases are thin wrappers** — `domain/userCases/` contains `GetWeatherInfoUseCase` (wraps repo), `GetLastCityUseCase` and `SaveLastCityUseCase` (wrap `CityManager`). They exist to decouple the ViewModel from concrete repo/manager types.

**GPS location via coroutine bridge** — `WeatherSearchViewModel.getCurrentLocation` wraps `FusedLocationProviderClient.getCurrentLocation` (Google Play Services) using `suspendCancellableCoroutine`, converting the callback API to a suspend function.

### Dependency Injection (Koin)

`di/Modules.kt` holds a single `appModule`. All bindings are `single` (singletons) except ViewModels which use `viewModel { }`. `WeatherTestApplication` starts Koin in `onCreate`.

`CityStorage` interface → `DataStoreCityStorage` (wraps `DataStore` from `util-kotlin`) → `CityManager` (domain-level wrapper that hardcodes the storage key) → `GetLastCityUseCase` / `SaveLastCityUseCase` → `WeatherSearchViewModel`.

### Navigation (Navigation3)

Uses `androidx.navigation3` (not Navigation 2). Nav destination keys are `@Serializable data object` types in `navigation/AppNav.kt`, each implementing `NavKey`. `NavDisplay.kt` wires the `NavDisplay` composable with slide animations. Each destination is registered as an extension function on `EntryProviderScope<NavKey>`.

### Networking

`OpenWeatherService` (object) builds the Retrofit instance. An OkHttp interceptor automatically appends the API key and `units=metric` to every request, so `WeatherApi` methods don't include those parameters. The API key is hardcoded in `utils/Constants.kt`.

### Testing approach

- JUnit 4 with `runTest` (coroutines-test)
- `MainDispatcherRule` swaps `Dispatchers.Main` for `UnconfinedTestDispatcher` — apply it with `@get:Rule`
- Tests use hand-written fakes (`FakeCityStorage`, `FakeWeatherApi`) rather than Mockito mocks; real `WeatherRepoImpl`, `CityManager`, and use cases are instantiated in tests. Fakes are private inner classes inside the test class, not separate files.
- `testOptions { unitTests.isReturnDefaultValues = true }` is set in `build.gradle.kts` to suppress Android framework crashes in unit tests

### External library: util-kotlin

`com.github.sam-chen-dev:util-kotlin` (JitPack) provides:
- `DataStore` — synchronous key/value persistence (wraps DataStore from `androidx.datastore:datastore-core`))
- `Text` — composable that takes a string resource `Int` instead of a `String`