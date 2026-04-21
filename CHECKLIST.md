# ✅ Checklist Interactivo - GamerDex Setup & Testing

## 📋 FASE 1: Requisitos Previos

- [ ] **Android Studio instalado** con SDK 35
- [ ] **Emulador Android configurado** (o device físico)
- [ ] **Conexión a Internet** en el emulador/device
- [ ] **Gradle 8.8+** (verificar en `gradle/wrapper/gradle-wrapper.properties`)
- [ ] **Java 11+** instalado (`java -version`)

---

## 📋 FASE 2: Obtener Credenciales IGDB

### Crear Aplicación en Twitch

- [ ] Ir a [Twitch Developers Console](https://dev.twitch.tv/console/apps)
- [ ] Login con cuenta Twitch (crear si es necesario)
- [ ] Click "Create Application"
- [ ] Nombre: `GamerDex` (o similar)
- [ ] Category: `Application Integration`
- [ ] OAuth Redirect URL: `http://localhost:3000`
- [ ] Aceptar términos y crear
- [ ] Click "Manage" en la app creada

### Copiar Credenciales

- [ ] **Client ID**: 
  ```
  Copiar aquí: _________________________________
  ```
  
- [ ] **Generar Secret**: Click "New Secret"
  ```
  Client Secret: _____________________________
  ```

### Obtener Access Token

Opción A: **PowerShell** (Windows):
```powershell
$client_id = "PEGA_CLIENT_ID_AQUI"
$client_secret = "PEGA_CLIENT_SECRET_AQUI"

$response = Invoke-WebRequest `
  -Uri "https://id.twitch.tv/oauth2/token" `
  -Method POST `
  -Body @{
    client_id = $client_id
    client_secret = $client_secret
    grant_type = "client_credentials"
  }

$token = ($response.Content | ConvertFrom-Json).access_token
Write-Output "Access Token: $token"
```

- [ ] Token obtenido:
  ```
  Pegar aquí: ________________________________
  ```

---

## 📋 FASE 3: Configurar Proyecto

### 3.1 - Actualizar DataModule.kt

**Archivo**: `app/src/main/java/com/mariolos27/gamerdex/data/di/DataModule.kt`

Añadir OkHttp import al inicio:
```kotlin
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
```

Reemplazar función `provideRetrofit()`:
```kotlin
@Provides
@Singleton
fun provideRetrofit(): Retrofit {
    val CLIENT_ID = "TU_CLIENT_ID_AQUI"
    val ACCESS_TOKEN = "TU_ACCESS_TOKEN_AQUI"
    
    val authInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Client-ID", CLIENT_ID)
            .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
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

- [ ] **Client ID actualizado** en DataModule
- [ ] **Access Token actualizado** en DataModule
- [ ] Archivo guardado (Ctrl+S)

### 3.2 - Actualizar Dependencias

**Archivo**: `gradle/libs.versions.toml`

Bajo `[versions]` añadir:
```toml
okhttp = "4.12.0"
```

- [ ] Versión okhttp añadida

Bajo `[libraries]` añadir:
```toml
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
```

- [ ] Librería okhttp añadida

**Archivo**: `app/build.gradle.kts`

Bajo `dependencies`:
```kotlin
// OkHttp for auth interceptor
implementation(libs.okhttp)
```

- [ ] OkHttp importado en build.gradle.kts

---

## 📋 FASE 4: Build & Sync

### 4.1 - Limpiar Caché

```bash
cd C:\Users\mario\AndroidStudioProjects\GamerDex
./gradlew.bat clean
```

- [ ] Clean completado sin errores

### 4.2 - Sync Gradle

En Android Studio:
- [ ] File → Sync Now
- O manualmente:
```bash
./gradlew.bat build
```

Esperar a que termine...

- [ ] ✅ **Build exitoso** (sin errores rojos)
- [ ] Warnings de Hilt son normales (desaparecerán en compilación)

### 4.3 - Verificar Estructura

En Android Studio, expandir:
- [ ] `app/src/main/java/com/mariolos27/gamerdex/`
- [ ] Ver carpetas: `domain/`, `data/`, `presentation/`
- [ ] Ver: `GamerDexApplication.kt`

---

## 📋 FASE 5: Compilar APK

### 5.1 - Build Debug APK

```bash
./gradlew.bat assembleDebug
```

O desde Android Studio:
- [ ] Build → Build Bundle(s)/APK(s) → Build APK(s)

- [ ] Build completado
- [ ] Archivo: `app/build/outputs/apk/debug/app-debug.apk`

### 5.2 - Instalar en Emulador

```bash
./gradlew.bat installDebug
```

- [ ] App instalada en emulador

---

## 📋 FASE 6: Ejecutar y Probar

### 6.1 - Ejecutar App

**Opción 1**: Android Studio
- [ ] Run → Run 'app'
- [ ] Seleccionar emulador
- [ ] Click OK

**Opción 2**: Línea de comandos
```bash
adb shell am start -n com.mariolos27.gamerdex/.MainActivity
```

### 6.2 - Verificar UI Carga

- [ ] ✅ App abre sin crashear
- [ ] ✅ Ves: "🎮 Búsqueda de Juegos"
- [ ] ✅ TextField visible con placeholder "Busca un juego..."
- [ ] ✅ Icono 🔍 en SearchTextField

### 6.3 - Probar Búsqueda

Escribe: `"The Witcher"`

- [ ] Pulsa Enter o desliza teclado
- [ ] Espera 2-3 segundos

**Resultados Esperados**:
- [ ] ⏳ Loading spinner apareció
- [ ] ✅ Desapareció spinner
- [ ] ✅ Lista de juegos aparece
- [ ] ✅ Cada juego tiene portada
- [ ] ✅ Títulos visibles

**Si aparece error**:
- [ ] ❌ Mensaje rojo aparece
- [ ] Leer el mensaje de error
- [ ] Ver sección "Troubleshooting" abajo

### 6.4 - Probar Otros Juegos

Prueba búsquedas:
- [ ] `"Elden Ring"` → Deberías ver resultados
- [ ] `"xyzabc123"` → Deberías ver "No se encontraron resultados"
- [ ] Dejar vacío y pulsar Enter → Deberías ver "Busca tu juego favorito"

---

## 📋 FASE 7: Debugging

### 7.1 - Ver Logcat

Android Studio → Logcat tab (o Alt+6)

Filtrar por:
```
gamerdex
```

- [ ] Ves logs de la app

### 7.2 - Ver Requests HTTP

Editar `DataModule.kt`:

```kotlin
// Añadir import
import okhttp3.logging.HttpLoggingInterceptor

// En provideRetrofit()
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val httpClient = OkHttpClient.Builder()
    .addInterceptor(authInterceptor)
    .addInterceptor(loggingInterceptor)  // ← Añadir esta línea
    .build()
```

Necesitas la dependencia en `libs.versions.toml`:
```toml
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
```

Y en `build.gradle.kts`:
```kotlin
implementation(libs.okhttp.logging)
```

- [ ] Logging interceptor añadido
- [ ] Rebuild y rebuild
- [ ] Ahora en Logcat ves:
  ```
  --> GET /v4/games?search=The%20Witcher
  <-- 200 OK
  ```

### 7.3 - Breakpoints para Debugging

En `SearchViewModel.kt`, línea `fun searchGames()`:
- [ ] Click en el número de línea para poner breakpoint (punto rojo)
- [ ] Run → Debug 'app'
- [ ] Haz una búsqueda
- [ ] Ejecución se pausa en el breakpoint
- [ ] Variables tab: ve valores en vivo
- [ ] Step Over (F10): avanza línea a línea

---

## 📋 FASE 8: Verificación Final

### 8.1 - Funcionalidades OK

- [ ] ✅ Búsqueda funciona
- [ ] ✅ Imágenes cargan (Coil)
- [ ] ✅ Loading spinner aparece
- [ ] ✅ Estados cambiant: Idle → Loading → Success
- [ ] ✅ Error handling funciona
- [ ] ✅ TextField limpia con botón X
- [ ] ✅ Puede hacer búsquedas múltiples

### 8.2 - Sin Errores de Compilación

- [ ] ✅ `./gradlew.bat clean build` pasa
- [ ] ✅ 0 errores (warnings OK)
- [ ] ✅ No hay "Cannot add extension 'kotlin'"

### 8.3 - Código Limpio

- [ ] ✅ Sin imports no usados
- [ ] ✅ Sin variables no usadas
- [ ] ✅ Formato Kotlin oficial

---

## 🆘 Troubleshooting

### Error: "401 Unauthorized"
**Causa**: Credenciales inválidas o expiradas
**Fix**:
- [ ] Verificar Client ID es correcto
- [ ] Verificar Access Token es correcto
- [ ] Generar token nuevo en Twitch
- [ ] Actualizar en DataModule.kt

### Error: "Cannot resolve symbol 'okhttp3'"
**Causa**: OkHttp no está en dependencias
**Fix**:
- [ ] Verificar `libs.versions.toml` tiene `okhttp`
- [ ] Verificar `build.gradle.kts` tiene `implementation(libs.okhttp)`
- [ ] Sync Now

### Error: "Cannot add extension 'kotlin'"
**Causa**: Plugin `kotlin.android` duplicado
**Fix**:
- [ ] Abrir `app/build.gradle.kts`
- [ ] Verificar que `kotlin.android` aparece UNA sola vez en `plugins`

### Coil no carga imágenes
**Causa**: URL incorrecta
**Fix**:
- [ ] En Logcat buscar: `coverUrl`
- [ ] Verificar que URL empiece con `https:`
- [ ] En DataModule, loguear response: `println(response)`

### App se cuelga al buscar
**Causa**: Falta permiso INTERNET
**Fix**:
- [ ] Verificar `AndroidManifest.xml`:
  ```xml
  <uses-permission android:name="android.permission.INTERNET" />
  ```

---

## 📊 Testing (Opcional)

### Unit Test SearchViewModel

Crear: `app/src/test/java/com/mariolos27/gamerdex/presentation/screens/search/SearchViewModelTest.kt`

```kotlin
@Test
fun `searchGames with valid query updates state`() = runTest {
    val mockUseCase = mockk<SearchGamesUseCase>()
    coEvery { mockUseCase(any()) } returns Result.success(listOf(
        Game(1, "Test Game", null)
    ))
    
    val viewModel = SearchViewModel(mockUseCase)
    viewModel.searchGames()
    advanceUntilIdle()
    
    assertEquals(
        SearchUiState.Success(listOf(Game(1, "Test Game", null))),
        viewModel.uiState.value
    )
}
```

- [ ] Test corre: `./gradlew.bat test`
- [ ] Test pasa ✅

---

## ✨ ¡Completado!

Una vez que todos los checks estén ✅, tu **Vertical Slice de búsqueda está 100% funcional**.

### Próximos Pasos Sugeridos

1. **Documentar en TFG** la arquitectura implementada
2. **Crear otra vertical slice**: GameDetailScreen
3. **Añadir tests**: Unit + instrumented
4. **Implementar navegación**: Compose Navigation

---

**Última actualización**: 2026-04-21  
**Estado**: ✅ Implementación Completa

