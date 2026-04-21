# 🎮 GamerDex - Aplicación TFG

> **Búsqueda de Videojuegos** con Clean Architecture, MVVM, Jetpack Compose, Hilt y IGDB API.

## 📖 Documentación Principal

Sigue estos documentos EN ORDEN:

### 1️⃣ **[CHECKLIST.md](./CHECKLIST.md)** ← EMPIEZA AQUÍ
   - ✅ Paso a paso para configurar y ejecutar
   - 📝 Checkboxes interactivos
   - 🆘 Troubleshooting incluido

### 2️⃣ **[SETUP_GUIDE.md](./SETUP_GUIDE.md)**
   - 🔑 Cómo obtener credenciales IGDB
   - ⚙️ Configuración detallada
   - 🐛 Debugging tips

### 3️⃣ **[SECURITY_CREDENTIALS.md](./SECURITY_CREDENTIALS.md)** 🔐
   - 🔐 Gestión segura de credenciales
   - 📋 Protección de tokens IGDB
   - ✅ Mejores prácticas de seguridad

### 4️⃣ **[VERTICAL_SLICE_SEARCH.md](./VERTICAL_SLICE_SEARCH.md)**
   - 📚 Documentación arquitectónica completa
   - 🏗️ Explicación de cada capa
   - 💡 Decisiones de diseño

### 5️⃣ **[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)**
   - 🚀 Referencia rápida para desarrollo
   - 📋 Patrones y ejemplos de código
   - ⚡ Comandos esenciales

### 6️⃣ **[AGENTS.md](./AGENTS.md)**
   - 🤖 Guía para IA/Copilot
   - 📦 Convenciones del proyecto
   - 🔌 Puntos de integración

### 📌 **Archivos de Git**
   - **[.gitignore](./.gitignore)** - Excluye credenciales y build artifacts
   - **[GITIGNORE_SUMMARY.md](./GITIGNORE_SUMMARY.md)** - Explicación de qué se ignora
   - **[local.properties.example](./local.properties.example)** - Plantilla de configuración

---

## 📂 Estructura del Proyecto

```
GamerDex/
├── 📚 Documentación
│   ├── CHECKLIST.md                  # ← INICIA AQUÍ
│   ├── SETUP_GUIDE.md
│   ├── VERTICAL_SLICE_SEARCH.md
│   ├── QUICK_REFERENCE.md
│   ├── IMPLEMENTATION_SUMMARY.md
│   └── AGENTS.md
│
├── app/src/main/
│   ├── java/com/mariolos27/gamerdex/
│   │   ├── GamerDexApplication.kt              # Hilt entry @HiltAndroidApp
│   │   ├── MainActivity.kt                     # App entry
│   │   │
│   │   ├── domain/                            # Lógica de negocio pura
│   │   │   ├── model/Game.kt
│   │   │   ├── repository/GameRepository.kt
│   │   │   └── usecase/SearchGamesUseCase.kt
│   │   │
│   │   ├── data/                              # Integración datos
│   │   │   ├── api/
│   │   │   │   ├── IgdbApi.kt                 # Retrofit endpoints
│   │   │   │   └── dto/GameDto.kt             # Mapeo JSON
│   │   │   ├── repository/GameRepositoryImpl.kt
│   │   │   └── di/DataModule.kt               # Hilt DI
│   │   │
│   │   └── presentation/                      # UI Reactiva
│   │       └── screens/search/
│   │           ├── SearchScreen.kt            # Composable principal
│   │           ├── SearchViewModel.kt         # State management
│   │           └── SearchUiState.kt           # Estados
│   │
│   └── AndroidManifest.xml                    # Permisos + Application
│
├── gradle/
│   └── libs.versions.toml                     # Version catalog
│
├── app/build.gradle.kts                       # Configuración app
├── build.gradle.kts                           # Root build
└── gradle.properties                          # Propiedades Gradle
```

---

## 🚀 Quickstart (5 minutos)

### Para los Impacientes

```bash
# 1. Obtener token IGDB (ver SETUP_GUIDE.md)
# Client ID: xxxxxx
# Access Token: xxxxxx

# 2. Editar DataModule.kt
#    Reemplazar CLIENT_ID y ACCESS_TOKEN

# 3. Build
./gradlew.bat clean build

# 4. Ejecutar
# Android Studio → Run → Run 'app'

# 5. Probar en la app
# Escribe "The Witcher" y pulsa Enter
```

---

## 🏗️ Arquitectura en Un Vistazo

```
┌─────────────────────────────────────────┐
│    PRESENTATION (SearchScreen)          │
│  StateFlow → Composables → UI           │
└─────────┬───────────────────────────────┘
          │ SearchViewModel
          │ @HiltViewModel + viewModelScope
          ↓
┌─────────────────────────────────────────┐
│    DOMAIN (SearchGamesUseCase)          │
│  Lógica de negocio pura + suspend       │
└─────────┬───────────────────────────────┘
          │ GameRepository.searchGames()
          ↓
┌─────────────────────────────────────────┐
│    DATA (GameRepositoryImpl)             │
│  IgdbApi → Mapeo DTO → Result<Game>    │
└─────────┬───────────────────────────────┘
          │ Retrofit + Hilt DI
          ↓
┌─────────────────────────────────────────┐
│    IGDB API (https://api.igdb.com)      │
│  HTTP GET /v4/games?search=query       │
└─────────────────────────────────────────┘
```

---

## 📦 Tecnologías

| Stack | Versión | Uso |
|-------|---------|-----|
| **Kotlin** | 2.0.21 | Lenguaje |
| **Jetpack Compose** | 2026.02.01 | UI |
| **Hilt** | 2.51 | Dependency Injection |
| **Retrofit** | 2.9.0 | HTTP Client |
| **Coil** | 2.6.0 | Image Loading |
| **StateFlow** | Built-in | Reactive State |
| **Room** | 2.6.1 | Local DB (futuro) |
| **KSP** | 2.0.21-1.0.25 | Annotation Processor |

---

## ✅ Checklist Rápida Previa

- [ ] Android Studio 2024.2.1+
- [ ] SDK 35 instalado
- [ ] Emulador o device conectado
- [ ] Gradle 8.8+
- [ ] Java 11+
- [ ] Cuenta Twitch

---

## 🆘 Ayuda Rápida

### "No funciona nada"
→ Ver **[CHECKLIST.md](./CHECKLIST.md)** sección Troubleshooting

### "¿Cómo añado una nueva feature?"
→ Ver **[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)** sección "Patrones Clave"

### "¿Cómo funciona esto?"
→ Ver **[VERTICAL_SLICE_SEARCH.md](./VERTICAL_SLICE_SEARCH.md)** sección correspondiente

### "¿Comandos para IA?"
→ Ver **[AGENTS.md](./AGENTS.md)**

---

## 📝 Notas Importantes

⚠️ **Credenciales** 🔐
- NUNCA commitear tokens en Git
- Crear `local.properties` basado en `local.properties.example`
- Ver **[SECURITY_CREDENTIALS.md](./SECURITY_CREDENTIALS.md)** para detalles
- El `.gitignore` protege automáticamente `local.properties`

⚠️ **Kotlin Style**
- Official Kotlin style (`kotlin.code.style=official` en gradle.properties)
- Sin XML: 100% Compose
- Imports limpios

⚠️ **Hilt**
- Si falla: `Build → Rebuild Project` en Android Studio
- Necesita `@HiltAndroidApp` en Application
- Necesita `@AndroidEntryPoint` en Activities

⚠️ **Git & .gitignore**
- `.gitignore` está configurado para excluir:
  - `local.properties` (credenciales)
  - `build/`, `.gradle/`, `.idea/` (build artifacts)
  - Archivos de sistema (`Thumbs.db`, `.DS_Store`)
- Ver **[GITIGNORE_SUMMARY.md](./GITIGNORE_SUMMARY.md)** para detalles

---

## 🎓 Para la Memoria del TFG

### Palabras Clave
- Clean Architecture
- MVVM (Model-View-ViewModel)
- StateFlow (estado reactivo)
- Hilt (inyección de dependencias)
- Jetpack Compose (UI declarativa)
- Retrofit (cliente HTTP)
- IGDB API
- Result<T> (manejo de errores)

### Secciones Sugeridas
1. **Introducción**: Propósito y objetivos
2. **Tecnologías**: Stack técnico elegido
3. **Arquitectura**: Clean Architecture en tres capas
4. **Implementación**: Vertical Slice de búsqueda
5. **Testing**: Estrategia de pruebas
6. **Conclusiones**: Aprendizajes y futuros pasos

### Diagramas Recomendados
- Arquitectura por capas
- Flujo de datos
- Diagrama de clases
- Tabla de tecnologías

---

## 🚀 Roadmap de Features

### Sprint 1 ✅ COMPLETADO
- [x] Búsqueda de videojuegos
- [x] Visualización de resultados
- [x] Manejo de estados reactivos

### Sprint 2 🔄 EN PROGRESO
- [ ] Detalles de juego (GameDetailScreen)
- [ ] Navegación entre pantallas
- [ ] Más información del juego (descripción, rating, etc)

### Sprint 3 📋 PLANEADO
- [ ] Favoritos (Room database)
- [ ] Historial de búsquedas
- [ ] Compartir juego

### Sprint 4 🔐 PLANEADO
- [ ] Autenticación con Supabase
- [ ] Sincronización en la nube
- [ ] Perfil de usuario

---

## 📞 Soporte

- Dudas sobre **código**: Ver QUICK_REFERENCE.md
- Dudas sobre **arquitectura**: Ver VERTICAL_SLICE_SEARCH.md  
- Dudas sobre **configuración**: Ver SETUP_GUIDE.md
- Dudas sobre **setup**: Ver CHECKLIST.md

---

## 📄 Licencia

Proyecto académico para Trabajo de Fin de Grado (TFG)

---

## 👨‍💻 Desarrollador

**Mario**  
TFG: GamerDex - Aplicación Android de búsqueda de videojuegos  
Stack: Kotlin + Jetpack Compose + Clean Architecture

---

**¡Feliz desarrollo! 🚀**

**Próximo paso**: Abre **[CHECKLIST.md](./CHECKLIST.md)** y sigue los pasos.



