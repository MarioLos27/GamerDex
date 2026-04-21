# 🔐 Gestión de Credenciales - GamerDex

## ⚠️ IMPORTANTE: Protege tu Token IGDB

El archivo `.gitignore` está configurado para excluir archivos con credenciales.  
**NUNCA** commitees tu Client ID o Access Token a Git.

---

## 📋 Archivos Excluidos por Seguridad

El `.gitignore` actual protege:
```
local.properties          # Archivo local de configuración
gradle.properties         # Variables de Gradle
.env                      # Variables de entorno
.env.local                # Variables locales
secrets.xml               # Secrets de Android
keystore.properties       # Claves de firma
*.jks                     # Java Keystores
*.keystore                # Keystores
```

---

## ✅ Opción 1: Usar `local.properties` (RECOMENDADO)

### Paso 1: Crear `local.properties`

**Archivo**: `C:\Users\mario\AndroidStudioProjects\GamerDex\local.properties`

```properties
# ⚠️ ESTE ARCHIVO NO SE VERSIONARÁ EN GIT
# Credenciales IGDB - Solo local

igdb.client.id=tu_client_id_aqui
igdb.access.token=tu_access_token_aqui
```

### Paso 2: Leer en `DataModule.kt`

```kotlin
package com.mariolos27.gamerdex.data.di

import android.content.Context
import com.mariolos27.gamerdex.data.api.IgdbApi
import com.mariolos27.gamerdex.data.repository.GameRepositoryImpl
import com.mariolos27.gamerdex.domain.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    /**
     * Lee credenciales desde local.properties
     */
    private fun getIgdbCredentials(context: Context): Pair<String, String> {
        val propFile = File(context.filesDir.parentFile, "local.properties")
        val props = java.util.Properties()
        
        return if (propFile.exists()) {
            props.load(propFile.inputStream())
            val clientId = props.getProperty("igdb.client.id", "")
            val token = props.getProperty("igdb.access.token", "")
            Pair(clientId, token)
        } else {
            // Fallback: desde BuildConfig (para CI/CD)
            Pair(BuildConfig.IGDB_CLIENT_ID, BuildConfig.IGDB_ACCESS_TOKEN)
        }
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        val (clientId, accessToken) = getIgdbCredentials(context)
        
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Client-ID", clientId)
                .addHeader("Authorization", "Bearer $accessToken")
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
    
    // ...resto del código...
}
```

✅ **Ventaja**: Seguro, flexiblemente, local

---

## ✅ Opción 2: Usar `BuildConfig` (Para CI/CD)

### Paso 1: Configurar en `app/build.gradle.kts`

```kotlin
android {
    // ...existing code...
    
    defaultConfig {
        // ...existing code...
        
        buildConfigField("String", "IGDB_CLIENT_ID", "\"${System.getenv("IGDB_CLIENT_ID")}\"")
        buildConfigField("String", "IGDB_ACCESS_TOKEN", "\"${System.getenv("IGDB_ACCESS_TOKEN")}\"")
    }
}
```

### Paso 2: Variables de Entorno en Windows

```bash
# PowerShell
$env:IGDB_CLIENT_ID="tu_client_id"
$env:IGDB_ACCESS_TOKEN="tu_access_token"

# O permanentemente (Settings → Environment Variables)
```

### Paso 3: Usar en `DataModule.kt`

```kotlin
@Provides
@Singleton
fun provideRetrofit(): Retrofit {
    val CLIENT_ID = BuildConfig.IGDB_CLIENT_ID
    val ACCESS_TOKEN = BuildConfig.IGDB_ACCESS_TOKEN
    
    // ...rest of code...
}
```

✅ **Ventaja**: Ideal para GitHub Actions, CI/CD

---

## ✅ Opción 3: Archivo `.env` (Desarrollo Local)

### Paso 1: Crear `.env`

**Archivo**: `C:\Users\mario\AndroidStudioProjects\GamerDex\.env`

```
IGDB_CLIENT_ID=tu_client_id_aqui
IGDB_ACCESS_TOKEN=tu_access_token_aqui
```

⚠️ **Verificar que `.gitignore` incluye `.env`** (ya lo hace)

### Paso 2: Añadir Plugin en `app/build.gradle.kts`

```kotlin
plugins {
    // ... existing plugins ...
    id("com.microsoft.gradle.dotenv") version "1.0"
}

android {
    defaultConfig {
        buildConfigField("String", "IGDB_CLIENT_ID", "\"${System.getenv("IGDB_CLIENT_ID")}\"")
        buildConfigField("String", "IGDB_ACCESS_TOKEN", "\"${System.getenv("IGDB_ACCESS_TOKEN")}\"")
    }
}
```

✅ **Ventaja**: Limpio, estándar en desarrollo web

---

## 🔒 Mejores Prácticas de Seguridad

### Para Desarrollo Local
✅ Usar **`local.properties`** o **`.env`**  
✅ Regenerar tokens cada 60 días  
✅ Tokens diferentes por environment (dev/staging/prod)

### Para GitHub / Repositorio
✅ NUNCA hardcodear tokens  
✅ Usar **GitHub Secrets** para CI/CD  
✅ Excluir con `.gitignore`

### Para Producción
✅ Usar **variables de entorno** del servidor  
✅ Usar **secretos del contenedor** (Docker)  
✅ Usar **keystores separadas** por firma

---

## 🛠️ Estructura Recomendada

```
GamerDex/
├── .gitignore                      # Protege credenciales
├── local.properties                # ⚠️ LOCAL, NO EN GIT
├── .env                            # ⚠️ LOCAL, NO EN GIT
│
├── app/
│   ├── build.gradle.kts            # Lee variables
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   └── local.properties            # ⚠️ LOCAL
│
└── docs/
    └── CREDENCIALES.md             # Este archivo
```

---

## ✅ Checklist de Seguridad

- [ ] `.gitignore` actualizado con `local.properties`
- [ ] `.gitignore` actualizado con `.env`
- [ ] `local.properties` creado con credenciales
- [ ] `local.properties` añadido a `.gitignore` ✓
- [ ] Token IGDB válido (generado hace menos de 60 días)
- [ ] Token NO hardcodeado en código fuente
- [ ] `DataModule.kt` lee desde `local.properties` o `BuildConfig`
- [ ] Git verifica: `git status local.properties` → no lista
- [ ] Git verifica: `git status .env` → no lista

---

## 🧪 Verificar que Git Ignora

```bash
# Verificar que git ignora correctamente
git check-ignore -v local.properties
git check-ignore -v .env

# Si aparecen: están en .gitignore ✅
# Si NO aparecen: necesitan ser añadidas

# Listar archivos ignorados
git check-ignore -v *
```

---

## 🚨 Si Accidentalmente Commitiste Credenciales

⚠️ **Los tokens están comprometidos. Debes regenerarlos:**

```bash
# 1. Cambiar token en Twitch Developers Console
# 2. Eliminar del historio Git
git filter-branch --force --index-filter \
  'git rm --cached --ignore-unmatch local.properties' \
  --prune-empty --tag-name-filter cat -- --all

# 3. Force push (CUIDADO: reescribe historia)
git push origin --force --all
```

**Mejor**: Usa **git-filter-repo** (más seguro)

```bash
# Instalar
pip install git-filter-repo

# Usar
git filter-repo --path local.properties --invert-paths

# Push
git push origin --force --all
```

---

## 📝 Para tu TFG

**Puedes escribir**:

> "Se implementó un sistema de gestión seguro de credenciales IGDB.
> Las claves se almacenan en `local.properties` (no versionado),
> evitando exposición en el repositorio Git.
> Para CI/CD se usan variables de entorno y secrets de GitHub.
> El `.gitignore` está configurado para excluir archivos sensibles."

---

## 🔗 Referencias

- [Android Security Best Practices](https://developer.android.com/privacy-and-security)
- [Git .gitignore Documentation](https://git-scm.com/docs/gitignore)
- [Twitch OAuth Security](https://dev.twitch.tv/docs/authentication)
- [12 Factor App - Config](https://12factor.net/config)

---

**Tu proyecto está 🔒 protegido**

