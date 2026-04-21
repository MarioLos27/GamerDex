# GamerDex AI Agent Guidelines

## 🏗️ Architecture Overview

**Clean Architecture** con tres capas bien definidas. La aplicación es 100% Kotlin + Jetpack Compose (sin XML).

### Capa Domain (Lógica de Negocio)
- **Ubicación**: `domain/model/` y `domain/usecase/` y `domain/repository/`
- **Modelos**: Data classes independientes de Android (ej: `Game.kt`)
- **Use Cases**: Lógica de negocio encapsulada (ej: `SearchGamesUseCase.kt` - operador `invoke()`)
- **Interfaces**: Contratos sin implementación (ej: `GameRepository` - define qué hace sin cómo)
- **Detalles**: Usa `Result<T>` para manejo de errores; funciones `suspend` para operaciones async

### Capa Data (Fuentes de Datos)
- **Ubicación**: `data/api/`, `data/repository/`, `data/di/`
- **DTOs**: Mapean JSON de IGDB (`GameDto.kt` con `@SerializedName`)
- **API**: Retrofit interface (`IgdbApi.kt`) - define endpoints
- **Repositorio**: Implementa la interfaz de Domain, mapea DTOs → modelos (`GameRepositoryImpl.kt`)
- **DI Module**: Configura Hilt providers (`DataModule.kt`) - vincula interfaces con implementaciones
- **TODO**: El interceptor con Bearer token de Twitch va en `provideRetrofit()` más adelante

### Capa Presentation (UI Reactiva)
- **Ubicación**: `presentation/screens/`, `presentation/components/`, `presentation/theme/`
- **States**: Sealed class `SearchUiState` con 4 estados (Idle, Loading, Success, Error)
- **ViewModel**: Usa `StateFlow` y corrutinas (`viewModelScope`) para reactividad
- **Composables**: Funciones puras que observan `StateFlow` con `collectAsState()`
- **Inyección**: `@HiltViewModel` automático, `hiltViewModel()` en Compose
- **Imágenes**: Librería **Coil** con `AsyncImage()` para lazy loading

### Flujo de Datos: Búsqueda (Ejemplo Real)
```
UI (SearchScreen) 
  → ViewModel.searchGames()
    → SearchGamesUseCase(query)
      → GameRepository.searchGames() [interfaz]
        → GameRepositoryImpl.searchGames() [implementación]
          → IgdbApi.searchGames() [Retrofit]
            → mapea GameDto → Game
              → devuelve Result<List<Game>>
```

## 🛠️ Developer Workflows

**Build & Sync**:
- Sync: `./gradlew.bat assemble` o click "Sync Now" en Android Studio
- Errores Hilt: Si falla, hacer `Build → Rebuild Project`

**Unit tests** (lógica sin Android):
```bash
./gradlew.bat test  # Corre tests en app/src/test/
```

**Instrumented tests** (con emulador/device):
```bash
./gradlew.bat connectedAndroidTest  # Corre tests en app/src/androidTest/
```

**Debug**: 
- Breakpoints en AndroidStudio
- Logcat en Logcat tab
- Edge-to-edge habilitado en `MainActivity.onCreate()`

## 🎯 Project Conventions

| Aspecto | Regla |
|--------|-------|
| **Namespaces** | `com.mariolos27.gamerdex.*` en todos los archivos |
| **Dependencies** | SIEMPRE via `libs.*` en `gradle/libs.versions.toml` |
| **Kotlin style** | Official (check: `gradle.properties`: `kotlin.code.style=official`) |
| **Compose** | Envuelve todo en `GamerDexTheme {}` |
| **SDKs** | Min 26, Target 35, Compile 35 |
| **XML** | ❌ PROHIBIDO - Solo Kotlin + Compose |
| **StateFlow** | Preferencia para state reactivo sobre LiveData |
| **Corrutinas** | `viewModelScope.launch {}` en ViewModels |

## 📁 Estructura de Carpetas (Vertical Slice)

```
domain/
  ├── model/          # Modelos puros (Game.kt, User.kt, etc)
  ├── repository/     # Interfaces (GameRepository.kt)
  └── usecase/        # Casos de uso (SearchGamesUseCase.kt)

data/
  ├── api/            # Retrofit & DTOs (IgdbApi.kt, GameDto.kt)
  ├── repository/     # Implementaciones (GameRepositoryImpl.kt)
  └── di/             # Módulos Hilt (DataModule.kt)

presentation/
  ├── screens/        # Pantallas (SearchScreen.kt)
  ├── components/     # Componentes reutilizables
  ├── theme/          # Material3 theme
  └── view models/    # ViewModels (SearchViewModel.kt)
```

## 🔑 Key Files & Patterns

| Archivo | Propósito | Patrón |
|---------|-----------|--------|
| `MainActivity.kt` | Entry point, setup Compose | `@AndroidEntryPoint`, `setContent { GamerDexTheme { } }` |
| `GamerDexApplication.kt` | App class para Hilt | `@HiltAndroidApp` |
| `*ViewModel.kt` | State management | `@HiltViewModel`, `StateFlow`, `MutableStateFlow.asStateFlow()` |
| `*UiState.kt` | Estados de pantalla | `sealed class` con casos (Idle, Loading, Success, Error) |
| `*Screen.kt` | UI Composables | `collectAsState()`, condiciones por estado |
| `DataModule.kt` | DI de repositories | `@Module @InstallIn(SingletonComponent::class)` |
| `IgdbApi.kt` | API endpoints | Retrofit interface con `@GET`, `@POST` |
| `*Repository.kt` (impl) | Mapeo DTO→Domain | `Result<T>` para errores |

## 🔌 Dependency Injection (Hilt)

**Flujo de Setup**:
1. Clase Application marcada con `@HiltAndroidApp` → `GamerDexApplication.kt`
2. Activity/Fragment marcada con `@AndroidEntryPoint` → `MainActivity.kt`
3. ViewModel marcada con `@HiltViewModel` → `SearchViewModel.kt`
4. Módulos definen providers → `DataModule.kt`
5. `Inject constructor` para dependencias → `GameRepositoryImpl @Inject constructor(...)`

**Binding**: `DataModule.kt` vincula:
- `GameRepository` (interfaz) → `GameRepositoryImpl` (implementación)
- `IgdbApi` (interfaz Retrofit) ← `Retrofit` builder

## 📦 Dependencies Clave

| Librería | Versión | Uso |
|----------|---------|-----|
| Kotlin | 2.0.21 | Lenguaje |
| Compose BOM | 2026.02.01 | UI framework |
| Retrofit | 2.9.0 | HTTP client (IGDB API) |
| Gson | Retrofit | JSON parsing |
| Hilt | 2.51 | Dependency injection |
| Coil | 2.6.0 | Image loading |
| Room | 2.6.1 | Local DB (futuro) |
| KSP | 2.0.21-1.0.25 | Annotation processor |

## 🚀 Next Verticales Slice (Roadmap)

1. **Autenticación Twitch**: Supabase Auth en `AuthUseCase`
2. **Detalles de Juego**: Nueva pantalla + API endpoint
3. **Favoritos**: Room database + `FavoritesRepository`
4. **Navegación**: Compose Navigation entre screens

## ⚠️ Common Issues & Fixes

| Error | Causa | Fix |
|-------|-------|-----|
| "Cannot add extension 'kotlin'" | Plugin duplicado | Revisar que `kotlin.android` aparece UNA sola vez |
| Hilt: "Cannot find scoped component" | ViewModel scope mal | Usar `@HiltViewModel` + `hiltViewModel()` |
| Retrofit 404 en IGDB | Faltan headers Auth | Implementar interceptor en `DataModule` |
| Coil image no carga | URL incorrecta | Debug: imprimir `coverUrl` en Logcat |
