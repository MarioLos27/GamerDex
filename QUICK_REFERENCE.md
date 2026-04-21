~~~~# 🚀 Quick Reference - GamerDex Developer Guide

## Estructura del Proyecto en Un Vistazo

```
app/src/main/java/com/mariolos27/gamerdex/
├── 📦 domain/
│   ├── model/Game.kt                    # Modelo puro
│   ├── repository/GameRepository.kt     # Interfaz contrato
│   └── usecase/SearchGamesUseCase.kt   # Lógica de negocio
│
├── 📦 data/
│   ├── api/
│   │   ├── IgdbApi.kt                   # Retrofit interface
│   │   └── dto/GameDto.kt               # Mapeo JSON
│   ├── repository/GameRepositoryImpl.kt  # Implementación
│   └── di/DataModule.kt                 # Inyección (Hilt)
│
├── 📦 presentation/
│   └── screens/search/
│       ├── SearchScreen.kt              # UI Composable
│       ├── SearchViewModel.kt           # State management
│       └── SearchUiState.kt             # Estados reactivos
│
├── GamerDexApplication.kt               # Hilt entry @HiltAndroidApp
└── MainActivity.kt                      # App entry @AndroidEntryPoint
```

---

## Flujo de Datos en 4 Pasos

```
1. Usuario → TextField (SearchScreen)
   ↓
2. ViewModel.searchGames() → SearchGamesUseCase(query)
   ↓
3. UseCase → GameRepository.searchGames() → IgdbApi.searchGames()
   ↓
4. API devuelve List<GameDto> → Mapea a List<Game> → SearchUiState.Success
```

---

## Comandos Esenciales

```bash
# Build completo
./gradlew.bat clean build

# Solo ejecutar tests
./gradlew.bat test

# Instalar en emulador
./gradlew.bat installDebug

# Ejecutar con logs detallados
./gradlew.bat build --info

# Limpiar caché Gradle
./gradlew.bat clean

# Rebuild Project (si Hilt falla)
# Android Studio → Build → Rebuild Project
```

---

## Patrones Clave

### 1. Crear Nuevo Use Case

```kotlin
// domain/usecase/GetPopularGamesUseCase.kt
class GetPopularGamesUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(): Result<List<Game>> {
        return gameRepository.getPopularGames()
    }
}
```

### 2. Añadir Nuevo ViewModel

```kotlin
// presentation/screens/popular/PopularViewModel.kt
@HiltViewModel
class PopularViewModel @Inject constructor(
    private val getPopularGamesUseCase: GetPopularGamesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<PopularUiState>(PopularUiState.Idle)
    val uiState: StateFlow<PopularUiState> = _uiState.asStateFlow()
    
    fun loadPopular() {
        viewModelScope.launch {
            _uiState.value = PopularUiState.Loading
            val result = getPopularGamesUseCase()
            result.onSuccess { games ->
                _uiState.value = PopularUiState.Success(games)
            }
        }
    }
}
```

### 3. Crear Nueva Screen

```kotlin
// presentation/screens/popular/PopularScreen.kt
@Composable
fun PopularScreen(
    viewModel: PopularViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LazyColumn {
        items(uiState.games) { game ->
            GameCard(game)
        }
    }
}
```

### 4. Añadir Nuevo Endpoint API

```kotlin
// En data/api/IgdbApi.kt
@GET("games")
suspend fun getPopularGames(
    @Query("order") order: String = "popularity"
): List<GameDto>

// En data/repository/GameRepositoryImpl.kt
override suspend fun getPopularGames(): Result<List<Game>> {
    return try {
        val response = igdbApi.getPopularGames()
        Result.success(response.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

---

## Estado Reactivo (StateFlow)

### Observar en Composable

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    // Convertir StateFlow a State para Compose
    val state by viewModel.uiState.collectAsState()
    
    // Renderizar según el estado
    when (state) {
        is MyUiState.Idle -> IdleUI()
        is MyUiState.Loading -> LoadingUI()
        is MyUiState.Success -> SuccessUI((state as MyUiState.Success).data)
        is MyUiState.Error -> ErrorUI((state as MyUiState.Error).message)
    }
}
```

### Patrón StateFlow Seguro en ViewModel

```kotlin
// Privado: Solo el ViewModel puede escribir
private val _uiState = MutableStateFlow<UiState>(UiState.Idle)

// Público: Solo lectura desde UI
val uiState: StateFlow<UiState> = _uiState.asStateFlow()

// Método para actualizar
fun updateState(newState: UiState) {
    _uiState.value = newState  // Recomposición automática
}
```

---

## Inyección de Dependencias (Hilt)

### Constructor Injection (Preferido)

```kotlin
class MyViewModel @Inject constructor(
    private val useCase: MyUseCase,
    private val repository: MyRepository
) : ViewModel() {
    // Los parámetros se inyectan automáticamente
}
```

### En Composable

```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()  // Hilt lo instancia
) {
    // ...
}
```

### En Activity

```kotlin
@AndroidEntryPoint  // Habilita inyección en esta Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // ...
    }
}
```

### Binding en DataModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideRepository(api: MyApi): MyRepository {
        return MyRepositoryImpl(api)  // Interfaz → Implementación
    }
}
```

---

## Composables Reutilizables

### Loading Spinner

```kotlin
@Composable
fun LoadingSpinner() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
```

### Error State

```kotlin
@Composable
fun ErrorState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("❌", style = MaterialTheme.typography.displayLarge)
            Text(message)
        }
    }
}
```

### Game Card

```kotlin
@Composable
fun GameCard(game: Game) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = game.coverUrl,
                contentDescription = game.title,
                modifier = Modifier.size(100.dp, 100.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(game.title, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
```

---

## Manejo de Errores

### Result<T> Pattern

```kotlin
val result = useCase(query)

result.onSuccess { data ->
    println("✅ Éxito: $data")
}.onFailure { error ->
    println("❌ Error: ${error.message}")
}

// O extraer valor
val data: List<Game>? = result.getOrNull()
val error: Throwable? = result.exceptionOrNull()
```

### Try-Catch en Repositorio

```kotlin
override suspend fun searchGames(query: String): Result<List<Game>> {
    return try {
        val response = igdbApi.searchGames(query)
        Result.success(response.map { it.toDomain() })
    } catch (e: SocketTimeoutException) {
        Result.failure(Exception("Timeout: Sin conexión"))
    } catch (e: HttpException) {
        Result.failure(Exception("Error HTTP: ${e.code()}"))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

---

## Testing (Ejemplos)

### Mock ViewModel

```kotlin
@Test
fun `searchGames loads and shows results`() = runTest {
    val mockUseCase = mockk<SearchGamesUseCase>()
    coEvery { mockUseCase(any()) } returns Result.success(listOf(testGame))
    
    val viewModel = SearchViewModel(mockUseCase)
    viewModel.searchGames()
    advanceUntilIdle()
    
    assertEquals(
        SearchUiState.Success(listOf(testGame)),
        viewModel.uiState.value
    )
}
```

### Mock Repository

```kotlin
@Test
fun `searchGames with error returns failure`() = runTest {
    val mockApi = mockk<IgdbApi>()
    coEvery { mockApi.searchGames(any()) } throws Exception("Network error")
    
    val repository = GameRepositoryImpl(mockApi)
    val result = repository.searchGames("test")
    
    assertTrue(result.isFailure)
}
```

---

## Dependencias Importantes

| Librería | Uso | Import |
|----------|-----|--------|
| Hilt | DI | `@HiltViewModel`, `@Inject`, `hiltViewModel()` |
| StateFlow | Reactive state | `MutableStateFlow`, `asStateFlow()`, `collectAsState()` |
| Coil | Image loading | `AsyncImage` |
| Retrofit | HTTP client | `@GET`, `@Query`, `@Body` |
| Compose | UI | `Column`, `Row`, `Box`, `LazyColumn` |
| Material3 | Design | `Card`, `Button`, `TextField`, `MaterialTheme` |

---

## Environment Setup

### local.properties (Git-ignored)
```properties
sdk.dir=C:\\Android\\sdk
IGDB_CLIENT_ID=your_client_id
IGDB_ACCESS_TOKEN=your_access_token
```

### BuildConfig (Acceso a variables)
```kotlin
// buildTypes o productFlavors
buildConfigField("String", "IGDB_CLIENT_ID", "\"${System.getenv("IGDB_CLIENT_ID")}\"")
```

---

## Checklist Antes de Push

- [ ] `./gradlew.bat clean build` sin errores
- [ ] Tests pasan: `./gradlew.bat test`
- [ ] No hay warnings de Hilt
- [ ] Credenciales **nunca** en código (BuildConfig o env vars)
- [ ] Code style: `official` en `gradle.properties`
- [ ] Imports limpios (no unused)
- [ ] Commits con mensajes claros

---

**Memoriza este documento para desarrollo rápido** 🚀

