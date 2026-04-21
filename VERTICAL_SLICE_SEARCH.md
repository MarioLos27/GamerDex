# 🎮 Vertical Slice: Búsqueda de Videojuegos - Documentación Completa

## 📚 Índice
1. [Resumen Ejecutivo](#resumen-ejecutivo)
2. [Flujo de Datos](#flujo-de-datos)
3. [Implementación por Capa](#implementación-por-capa)
4. [Cómo Usar](#cómo-usar)
5. [Testing](#testing)
6. [Decisiones Arquitectónicas](#decisiones-arquitectónicas)

---

## Resumen Ejecutivo

Se ha implementado la primera **Vertical Slice completa** del proyecto GamerDex: la búsqueda de videojuegos mediante la API de IGDB. 

✅ **Lo que hace**:
- El usuario escribe un nombre de juego en el SearchScreen
- La búsqueda se ejecuta asincronamente sin bloquear la UI
- Se muestran los resultados con portadas (lazy loading con Coil)
- Gestión de errores reactiva (Idle → Loading → Success/Error)

✅ **Cómo está construido**:
- **Domain**: Modelo `Game`, interfaz `GameRepository`, use case `SearchGamesUseCase`
- **Data**: DTO `GameDto`, API `IgdbApi`, implementación `GameRepositoryImpl`, módulo Hilt
- **Presentation**: Estados reactivos `SearchUiState`, ViewModel con `StateFlow`, Composables

✅ **Inyección de dependencias**: Hilt inyecta automáticamente repositorio → use case → ViewModel

---

## Flujo de Datos

### Secuencia de Ejecución

```
┌─────────────────────────────────────────────────────────────┐
│                      UI LAYER (Presentation)                │
│                                                              │
│  SearchScreen()                                              │
│  ├─ Observa: val uiState by vm.uiState.collectAsState()   │
│  ├─ TextField → onSearchQueryChanged(query)                 │
│  └─ Button "Buscar" → vm.searchGames()                      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                    ViewModel Layer                           │
│                                                              │
│  SearchViewModel (Hilt-injected)                            │
│  ├─ private val _uiState = MutableStateFlow(Idle)          │
│  ├─ fun searchGames():                                       │
│  │   ├─ _uiState.value = Loading                            │
│  │   ├─ result = searchGamesUseCase(query)                 │
│  │   └─ _uiState.value = Success(games) o Error(msg)      │
│  └─ corrutinas seguras con viewModelScope                  │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Domain Layer (Use Cases)                        │
│                                                              │
│  SearchGamesUseCase (Hilt-injected)                         │
│  ├─ suspend operator fun invoke(query: String)             │
│  ├─ Valida que query no esté vacío                         │
│  └─ Llama: gameRepository.searchGames(query)               │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Data Layer (Repositories)                       │
│                                                              │
│  GameRepositoryImpl (Hilt-injected)                          │
│  └─ override suspend fun searchGames():                      │
│      ├─ response = igdbApi.searchGames(query)              │
│      ├─ Mapea: response.map { it.toDomain() }             │
│      └─ return Result.success(games)                        │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              HTTP Layer (Retrofit)                           │
│                                                              │
│  IgdbApi.searchGames(@Query("search") query)               │
│  └─ GET https://api.igdb.com/v4/games?search=query        │
│      ↓                                                      │
│      Servidor IGDB                                          │
│      ↓                                                      │
│      JSON Response (GameDto list)                           │
└─────────────────────────────────────────────────────────────┘
```

---

## Implementación por Capa

### 1️⃣ DOMAIN Layer

#### `domain/model/Game.kt`
```kotlin
data class Game(
    val id: Long,
    val title: String,
    val coverUrl: String? = null
)
```
- Modelo puro, sin anotaciones Android
- Representa la entidad de negocio
- `coverUrl` es la URL de la portada del juego

#### `domain/repository/GameRepository.kt`
```kotlin
interface GameRepository {
    suspend fun searchGames(query: String): Result<List<Game>>
}
```
- Contrato que define QUÉ se puede hacer
- No define CÓMO (eso es responsabilidad de Data)
- Devuelve `Result<T>` para manejo tipo-seguro de errores

#### `domain/usecase/SearchGamesUseCase.kt`
```kotlin
class SearchGamesUseCase @Inject constructor(
    private val gameRepository: GameRepository
)
```
- Encapsula la lógica de negocio de búsqueda
- Valida que el query no esté vacío
- Inyectable automáticamente por Hilt
- `operator fun invoke()` permite usarlo como función: `useCase(query)`

---

### 2️⃣ DATA Layer

#### `data/api/dto/GameDto.kt`
```kotlin
data class GameDto(
    val id: Long,
    val name: String,
    val cover: CoverDto? = null
) {
    fun toDomain(): Game = Game(
        id = id,
        title = name,
        coverUrl = cover?.url?.let { "https:$it" }
    )
}
```
- Mapea la estructura JSON de IGDB
- `@SerializedName` para nombres diferentes entre JSON y Kotlin
- Método `toDomain()` convierte DTO → modelo de dominio
- IGDB devuelve URLs relativas, añadimos `https:` para que funcionen

#### `data/api/IgdbApi.kt`
```kotlin
interface IgdbApi {
    @GET("games")
    suspend fun searchGames(@Query("search") query: String): List<GameDto>
}
```
- Interfaz Retrofit que define endpoints
- URL base: `https://api.igdb.com/v4/`
- TODO: Implementar interceptor con Bearer token de Twitch

#### `data/repository/GameRepositoryImpl.kt`
```kotlin
class GameRepositoryImpl @Inject constructor(
    private val igdbApi: IgdbApi
) : GameRepository {
    override suspend fun searchGames(query: String): Result<List<Game>> {
        return try {
            val response = igdbApi.searchGames(query)
            val games = response.map { it.toDomain() }
            Result.success(games)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```
- Implementa la interfaz `GameRepository`
- Coordina API + mapeo
- Manejo de errores con `Result` (type-safe)

#### `data/di/DataModule.kt`
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideGameRepository(igdbApi: IgdbApi): GameRepository {
        return GameRepositoryImpl(igdbApi)
    }
}
```
- **Inyección de Dependencias**: Vincula interfaz con implementación
- `Singleton` = una sola instancia en toda la app
- Hilt automáticamente inyecta en constructores `@Inject`

---

### 3️⃣ PRESENTATION Layer

#### `presentation/screens/search/SearchUiState.kt`
```kotlin
sealed class SearchUiState {
    data object Idle : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(val games: List<Game>) : SearchUiState()
    data class Error(val message: String, val exception: Throwable? = null) : SearchUiState()
}
```
- **Sealed class** = lista exhaustiva de estados posibles
- Compilador obliga a manejar todos los casos con `when`
- **Tipo-seguro**: No hay strings mágicos o booleans confusos

#### `presentation/screens/search/SearchViewModel.kt`
```kotlin
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchGamesUseCase: SearchGamesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    fun searchGames() {
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            val result = searchGamesUseCase(query)
            result.onSuccess { games ->
                _uiState.value = SearchUiState.Success(games)
            }
        }
    }
}
```
- `@HiltViewModel` = Hilt lo instancia automáticamente
- `MutableStateFlow` privado = solo ViewModel puede escribir
- `StateFlow` público = UI puede leer reactivamente
- `viewModelScope.launch` = corrutina que sobrevive rotación de pantalla
- `onSuccess { }` = callback si Result es éxito

#### `presentation/screens/search/SearchScreen.kt`
```kotlin
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(...) {
        SearchTextField(...)
        
        Box { 
            when (uiState) {
                SearchUiState.Idle -> IdleContent()
                SearchUiState.Loading -> LoadingContent()
                is SearchUiState.Success -> SuccessContent(...)
                is SearchUiState.Error -> ErrorContent(...)
            }
        }
    }
}
```
- `hiltViewModel()` = inyección automática en Composable
- `collectAsState()` = convierte `StateFlow` en `State` para Compose
- **Renderización condicional**: Cada estado tiene su UI
- `AsyncImage` de Coil para lazy loading de portadas

---

## Cómo Usar

### Paso 1: Obtener Credenciales IGDB

1. Ir a [Twitch Developers](https://dev.twitch.tv/console/apps)
2. Crear una app
3. Obtener: **Client ID** y **Access Token**

### Paso 2: Configurar Autenticación (DataModule.kt)

```kotlin
@Provides
@Singleton
fun provideRetrofit(): Retrofit {
    val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Client-ID", "tu_client_id_aqui")
            .addHeader("Authorization", "Bearer tu_token_aqui")
            .build()
        chain.proceed(request)
    }
    
    val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()
    
    return Retrofit.Builder()
        .baseUrl("https://api.igdb.com/v4/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
```

### Paso 3: Compilar y Ejecutar

```bash
# Sync y build
./gradlew.bat build

# Ejecutar en emulador
# En Android Studio: Run → Run 'app'
```

### Paso 4: Probar en la App
1. Abre la app
2. Escribe "The Witcher" en el TextField
3. Presiona Enter o el botón Buscar
4. Deberías ver la lista de resultados con portadas

---

## Testing

### Unit Test Ejemplo (SearchGamesUseCase)

```kotlin
@Test
fun `searchGames with empty query returns empty list`() = runTest {
    // Given
    val useCase = SearchGamesUseCase(mockRepository)
    
    // When
    val result = useCase("")
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals(emptyList(), result.getOrNull())
}
```

### Instrumented Test (SearchViewModel)

```kotlin
@get:Rule
val instantExecutorRule = InstantTaskExecutorRule()

@Test
fun `loadGames updates state`() = runTest {
    val viewModel = SearchViewModel(mockUseCase)
    
    viewModel.searchGames()
    advanceUntilIdle()
    
    assertEquals(
        SearchUiState.Success(listOf(testGame)),
        viewModel.uiState.value
    )
}
```

---

## Decisiones Arquitectónicas

### ✅ Por qué Clean Architecture

| Decisión | Razón |
|----------|-------|
| 3 capas (Domain, Data, Presentation) | Separación de responsabilidades; fácil de testear |
| Domain sin AndroidX | Independencia; reutilizable en backend/desktop |
| Use Cases en Domain | Encapsulan lógica; reutilizables en múltiples pantallas |
| Sealed class para estados | Type-safe; compilador obliga a manejar todos los casos |
| StateFlow en ViewModel | Reactivo; sobrevive rotación de pantalla; corrutina-safe |
| Result<T> para errores | Type-safe vs exceptions; composable con flatMap, etc |
| Hilt para DI | Automático; generado en compile-time; zero-runtime cost |
| Coil para imágenes | Mejor que Picasso; suspendible; integrado con Compose |

### 🎯 Por qué cada tecnología

| Tech | Alternativa | Por Qué |
|------|-------------|--------|
| Retrofit | Ktor client | Retrofit: más maduro, mejor comunidad IGDB |
| Gson | kotlinx.serialization | Gson: compatible con IGDB; menos setup |
| Hilt | Koin | Hilt: generado en compile-time; 0 reflection |
| StateFlow | LiveData | StateFlow: mejor con corrutinas; multi-observer |
| Sealed class | Enums | Sealed: puede tener datos asociados (Success(list)) |
| Coil | Glide | Coil: Kotlin-first; suspend-friendly; más pequeño |

---

## Próximos Pasos

### Sprint 2: Detalles de Juego
- Nueva pantalla `GameDetailScreen`
- Nuevo endpoint en `IgdbApi` para fetchear detalles
- Stack navigation con Compose Navigation

### Sprint 3: Favoritos
- Room database entity `FavoritedGame`
- `FavoritesRepository` para listar favoritos
- FAB en búsqueda para marcar como favorito

### Sprint 4: Autenticación
- Supabase Auth integration
- Login screen con Sign Up
- Mantener sesión en local preferences

---

## Archivos Creados

```
✅ domain/
   ├── model/Game.kt
   ├── repository/GameRepository.kt
   └── usecase/SearchGamesUseCase.kt

✅ data/
   ├── api/
   │   ├── IgdbApi.kt
   │   └── dto/GameDto.kt
   ├── repository/GameRepositoryImpl.kt
   └── di/DataModule.kt

✅ presentation/
   └── screens/search/
       ├── SearchScreen.kt
       ├── SearchViewModel.kt
       └── SearchUiState.kt

✅ GamerDexApplication.kt (Hilt entry)
✅ MainActivity.kt (actualizado)
✅ AndroidManifest.xml (permisos + app class)
✅ gradle/libs.versions.toml (actualizado)
✅ app/build.gradle.kts (actualizado)
```

---

**Documentado para TFG** ✅  
Clean Architecture, MVVM, StateFlow, Hilt, Compose, Retrofit, sin XML.

