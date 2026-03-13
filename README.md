# Help Articles App

An Android app displaying help articles with offline support, built using Kotlin, Jetpack Compose, and Kotlin Multiplatform (KMP).

## Architecture

### Project Structure
```
├── app/                          # Android application module
│   └── src/main/java/com/eurail/app/
│       ├── data/                 # Data layer - Repository pattern
│       │   └── remote/           # Mock API service
│       │        ├── dto/         # Data transfer objects
│       │        └── interceptor/ # Interceptors for mock API
│       ├── domain/               # Domain layer - Business logic
│       ├── ui/                   # Presentation layer
│       │   ├── components/       # Reusable Compose components
│       │   ├── screens/          # List & Detail screens
│       │   └── theme/            # Material 3 theming
│       ├── util/                 # Connectivity monitoring
│       └── work/                 # WorkManager prefetch
└── shared/                       # KMP shared module
    └── src/commonMain/kotlin/    # Cache implementation
```

### Key Architecture Decisions

1. **Repository Pattern**: `ArticleRepository` acts as single source of truth, coordinating between network and cache
2. **Unidirectional Data Flow**: ViewModels expose `StateFlow<UiState>`, UI observes and reacts
3**KMP for Cache**: Shared cache module enables future iOS expansion

## Error Handling

### Network vs Backend Errors

The app distinguishes between two error types via `AppError` sealed class:

| Error Type | Detection | User Message |
|------------|-----------|--------------|
| **Network Errors** | `UnknownHostException`, `SocketTimeoutException`, HTTP 5xx | "No internet connection", "Request timed out", "Server unavailable" |
| **Backend Errors** | JSON payload with `errorCode`, `errorTitle`, `errorMessage` | Server-provided message displayed directly |

**Fallback Strategy**:
- Malformed JSON → `UnknownError` with generic message
- Any cached data available → Show cached content + offline banner
- No cache → Full-screen error with Retry button

## Offline Mode & Caching

### KMP Cache Module (`shared/`)

The `ArticleCache` interface with `InMemoryArticleCache` implementation provides:
- Thread-safe storage via `Mutex`
- Observable cache state via `Flow`
- Automatic detail caching when list is cached

### Staleness Rule

| Cache Age | Duration | Behavior |
|-----------|----------|----------|
| **FRESH** | < 5 minutes | Serve directly, no network call |
| **STALE** | 5 min - 24 hours | Serve cached, refresh in background |
| **EXPIRED** | > 24 hours | Must refresh, but can serve if offline |

**Rationale**: Help articles are reference content that changes infrequently. 5-minute freshness provides responsive UX, 24-hour expiry ensures content eventually updates while supporting extended offline use.

## Auto-Refresh

The app automatically fetches fresh data when:

1. **Connectivity Restored**: `ConnectivityMonitor` observes network state; when connection returns and cache is stale, triggers refresh
2. **App Resume**: ViewModel checks `shouldRefresh()` on initialization (implicit via `init` block)

## Background Prefetch

### WorkManager Implementation

```kotlin
PeriodicWorkRequest(interval = 24 hours)
  .setConstraints(
      requiredNetworkType = CONNECTED,
      requiresBatteryNotLow = true
  )
  .setInitialDelay(1 hour)
```

**Scheduling Rationale**:
- **24-hour interval**: Matches cache expiry; ensures fresh content daily
- **Network required**: Obvious requirement for fetching
- **Battery not low**: Respects user's device; help content isn't urgent
- **1-hour initial delay**: Avoids immediate work on app install
- **KEEP policy**: Prevents duplicate schedules if app reopens

## Testing

### Unit Tests (KMP Shared)
`shared/src/commonTest/kotlin/com/eurail/shared/cache/ArticleCacheTest.kt`
- Tests cache storage/retrieval
- Tests staleness calculation (fresh/stale/expired)
- Tests `shouldRefresh()` logic

### UI Tests (Compose)
`app/src/androidTest/java/com/eurail/app/ErrorScreenTest.kt`
- Tests error screen displays correct messages
- Tests Retry button interaction
- Tests accessibility (content descriptions)
