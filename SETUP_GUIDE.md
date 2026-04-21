# ⚙️ Guía de Configuración - GamerDex Search Feature

## 🔴 ANTES DE COMPILAR: Configuración Obligatoria

### 1. Obtener Credenciales de IGDB (vía Twitch OAuth)

**Pasos**:
1. Ir a [Twitch Developers Console](https://dev.twitch.tv/console/apps)
2. Login con tu cuenta de Twitch (crear una si no tienes)
3. **"Create Application"**
   - Name: `GamerDex`
   - Category: `Application Integration` (o similar)
   - OAuth Redirect URL: `http://localhost:3000` (placeholder)
   - Click "Create"
4. En la página de la app, Click "Manage"
5. Copiar:
   - **Client ID** (string alfanumérico)
   - Click "New Secret" → copiar **Client Secret**

### 2. Obtener Access Token

Para testing local, usar **Client Credentials Flow**:

```bash
# En PowerShell o terminal
$client_id = "tu_client_id_aqui"
$client_secret = "tu_client_secret_aqui"

$response = Invoke-WebRequest `
  -Uri "https://id.twitch.tv/oauth2/token" `
  -Method POST `
  -Body @{
    client_id = $client_id
    client_secret = $client_secret
    grant_type = "client_credentials"
  }

$token = ($response.Content | ConvertFrom-Json).access_token
Write-Output "Tu Access Token: $token"
```

Guardar el **access_token** (válido 60 días).

### 3. Configurar en DataModule.kt

**Archivo**: `app/src/main/java/com/mariolos27/gamerdex/data/di/DataModule.kt`

Reemplazar la función `provideRetrofit()`:

```kotlin
@Provides
@Singleton
fun provideRetrofit(): Retrofit {
    // TODO: Usar BuildConfig o local.properties para credenciales
    val CLIENT_ID = "tu_client_id_aqui"
    val ACCESS_TOKEN = "tu_access_token_aqui"
    
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

⚠️ **IMPORTANTE**: 
- Para producción, usar `BuildConfig` o `local.properties`
- **NUNCA** commitear credenciales al repo
- Para TFG: Documentar el proceso en la memoria

### 4. Añadir Dependencia de OkHttp

**Archivo**: `gradle/libs.versions.toml`

Añadir bajo `[versions]`:
```toml
okhttp = "4.12.0"
```

Añadir bajo `[libraries]`:
```toml
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
```

**Archivo**: `app/build.gradle.kts`

Añadir en `dependencies`:
```kotlin
// OkHttp for auth interceptor
implementation(libs.okhttp)
```

---

## 🟢 COMPILAR Y EJECUTAR

### Paso 1: Sync & Build

```bash
cd C:\Users\mario\AndroidStudioProjects\GamerDex

# Sync con Gradle
./gradlew.bat clean
./gradlew.bat build

# Si falla con Hilt:
# Build → Rebuild Project en Android Studio
```

### Paso 2: Ejecutar en Emulador

1. **Android Studio**: Run → Run 'app'
2. O desde terminal:
```bash
./gradlew.bat installDebug  # Instala en emulador conectado
adb shell am start -n com.mariolos27.gamerdex/.MainActivity
```

### Paso 3: Verificar Permisos

Si la app se cuelga sin conexión:
- Verificar que `AndroidManifest.xml` tiene: `<uses-permission android:name="android.permission.INTERNET" />`
- En emulador Android 6+: Requiere permiso runtime (la app lo pide automáticamente en Compose)

---

## 🧪 PROBAR LA FUNCIONALIDAD

### En la App

1. **SearchScreen** aparece con:
   - TextField con icono 🔍
   - Mensaje "Busca tu juego favorito"

2. **Escribe un juego**: `"The Witcher"`

3. **Pulsa Enter** o desliza teclado

4. **Verás**:
   - ⏳ Loading spinner
   - ✅ Lista de juegos con portadas
   - Si falla → ❌ Mensaje de error

### Resultados Esperados

Para `"The Witcher"`:
- The Witcher 3: Wild Hunt
- The Witcher 2: Assassins of Kings
- The Witcher: Enhanced Edition
- (Etc. según IGDB)

Cada resultado muestra:
- Portada del juego (imagen)
- Título
- ID del juego

---

## 🐛 DEBUGGING

### Ver Requests en Logcat

**Archivo**: `DataModule.kt`

Añadir logging:

```kotlin
@Provides
@Singleton
fun provideRetrofit(): Retrofit {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // Ver JSON completo
    }
    
    val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()
    
    // ...rest del código
}
```

Necesitas añadir en `libs.versions.toml`:
```toml
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
```

### Ver Errores en Logcat

1. Android Studio → Logcat tab
2. Filtrar por: `gamerdex` o `SearchViewModel`
3. Buscar líneas rojo (ERROR) o amarillo (WARNING)

Ejemplo:
```
E/SearchViewModel: Error en la búsqueda: 401 Unauthorized
   → Credenciales incorrectas o expiradas
```

### Breakpoints

1. Click en el número de línea (ej: 45 en SearchViewModel.kt)
2. Run → Debug 'app'
3. La app se pausará en ese punto
4. Variables tab: ver valores en vivo

---

## ✅ CHECKLIST DE CONFIGURACIÓN

- [ ] Client ID de Twitch copiado
- [ ] Access Token obtenido y válido
- [ ] Credenciales añadidas a `DataModule.kt`
- [ ] `OkHttp` añadido a `libs.versions.toml` y `build.gradle.kts`
- [ ] Permiso `INTERNET` en `AndroidManifest.xml`
- [ ] `GamerDexApplication` con `@HiltAndroidApp`
- [ ] `MainActivity` con `@AndroidEntryPoint`
- [ ] `./gradlew.bat clean build` sin errores
- [ ] App abre sin crashear
- [ ] Búsqueda devuelve resultados (o error legible si no)

---

## 📝 Para la Memoria del TFG

### Sección: "Configuración de IGDB API"

> La integración con IGDB requiere autenticación mediante Twitch OAuth 2.0.
> Se implementó un interceptor HTTP (OkHttp) que añade automáticamente
> los headers `Client-ID` y `Authorization: Bearer {token}` a cada request.
> El flujo de credenciales utiliza el Client Credentials Grant,
> adecuado para aplicaciones backend/backend-for-frontend sin usuario.
> Las credenciales se configuran en `DataModule.kt` (capa Data).
> Para producción se recomienda usar BuildConfig o variables de entorno
> en lugar de hardcodear valores en el código fuente.

---

## 🆘 Problemas Comunes

| Error | Solución |
|-------|----------|
| `401 Unauthorized` | Token expirado; obtener uno nuevo |
| `404 Not Found` | URL base incorrecta o endpoint typo |
| `INTERNET` permission denied | Verificar `AndroidManifest.xml` |
| Hilt "Cannot find scoped component" | Verificar `@HiltAndroidApp` en Application |
| Retrofit devuelve lista vacía | Query correcto; validar formato JSON |
| Coil no carga imágenes | URL incorrecta; loguear `coverUrl` en Logcat |

---

## 🚀 Próximo Paso

Una vez funcione la búsqueda, puedes:

1. **Mejorar UX**:
   - Agregar paginación (offset, limit)
   - Filtros por género/año
   - Historial de búsquedas

2. **Persistencia**:
   - Guardar favoritos en Room
   - Cachear resultados

3. **Testing**:
   - Mock de IgdbApi para tests
   - Test de SearchViewModel sin hacer requests reales

4. **Navegación**:
   - Ir a GameDetailScreen desde un resultado
   - Mostrar detalles completos del juego

---

**Configurado para TFG** ✅

