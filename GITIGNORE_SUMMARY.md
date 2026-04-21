# 📋 .gitignore Summary - GamerDex

## ✅ Lo que se VERSIONARÁ (commitearás)

```
✅ app/src/main/java/                     # Código fuente
✅ app/src/test/                          # Tests
✅ app/src/androidTest/                   # Instrumented tests
✅ gradle/libs.versions.toml              # Version catalog
✅ app/build.gradle.kts                   # Config (SIN credenciales)
✅ build.gradle.kts                       # Root build
✅ gradle.properties                      # Propiedades (SIN tokens)
✅ settings.gradle.kts
✅ gradlew / gradlew.bat                  # Gradle wrapper
✅ gradle/wrapper/                        # Wrapper files
✅ .gitignore                             # Este archivo
✅ README.md                              # Documentación
✅ AGENTS.md
✅ SETUP_GUIDE.md
✅ VERTICAL_SLICE_SEARCH.md
✅ QUICK_REFERENCE.md
✅ CHECKLIST.md
✅ SECURITY_CREDENTIALS.md
✅ local.properties.example               # Plantilla SOLO
✅ .env.example                           # Plantilla SOLO
✅ AndroidManifest.xml
✅ proguard-rules.pro
✅ .gitattributes                         # Git config
```

---

## ❌ Lo que NUNCA se versionará (git ignore)

### 🔐 Credenciales (SEGURIDAD)
```
❌ local.properties                       # Token IGDB + Client ID
❌ .env                                   # Variables de entorno
❌ .env.local
❌ gradle.properties (si tiene secrets)
❌ keystore.properties
❌ *.jks / *.keystore                     # Certificados de firma
```

### 🔨 Build & Gradle
```
❌ .gradle/                               # Caché Gradle
❌ build/                                 # Output compilación
❌ app/build/                             # Build específico app
❌ *.apk / *.aab                          # Binarios compilados
```

### 💾 IDE & Editores
```
❌ .idea/                                 # Android Studio config
❌ .vscode/                               # Visual Studio Code
❌ *.iml                                  # IntelliJ modules
❌ *.sublime-project                      # Sublime Text
❌ .classpath / .project                  # Eclipse
```

### 🖥️ Sistema Operativo
```
❌ .DS_Store                              # macOS
❌ Thumbs.db / Desktop.ini                # Windows
❌ .directory / .Trash-*                  # Linux
```

### 📊 Tests & Coverage
```
❌ test-results/
❌ coverage/
❌ lint-baseline.xml
```

### 📄 Otros
```
❌ *.log                                  # Logs
❌ .tmp / .temp                           # Temporales
❌ *.orig / *.bak                         # Backups
```

---

## 📊 Impacto de .gitignore

### Tamaño del Repositorio

**Con archivo .gitignore optimizado**:
```
app/src/main/java/              ~500 KB  ✅ Versionado
build/                          ~200 MB  ❌ Ignorado
.gradle/                        ~100 MB  ❌ Ignorado
.idea/                          ~10 MB   ❌ Ignorado
Binarios (apk/aab)             ~50 MB   ❌ Ignorado
───────────────────────────────────────
Total a commitear:             ~500 KB  ✨ LIMPIO
```

**Sin .gitignore** (MALO):
```
Total a commitear:            ~360 MB  ❌ PESADO
```

---

## 🔍 Verificar que .gitignore Funciona

```bash
# Ver archivos que git IGNORARÍA
git status --porcelain --ignored

# Verificar específicamente
git check-ignore -v local.properties
git check-ignore -v .gradle/
git check-ignore -v build/

# Ver archivos tracked (versionados)
git ls-tree -r HEAD --name-only | wc -l
```

**Resultado esperado**:
```
✅ local.properties           → ignored (in .gitignore)
✅ .gradle/                   → ignored (in .gitignore)
✅ build/                     → ignored (in .gitignore)
```

---

## 🛠️ Si Necesitas Ignorar Algo Más

1. Abre `.gitignore`
2. Añade la línea:
   ```
   archivo_o_carpeta_a_ignorar/
   ```
3. Guarda
4. Verifica con: `git check-ignore -v nombre_archivo`

### Ejemplos Comunes

```bash
# Ignorar un archivo específico
*.orig

# Ignorar una carpeta
my_folder/

# Ignorar con patrón
**/*.log
app/**/build/

# Excepto un archivo
*.db
!important.db
```

---

## 📋 Checklist

- [ ] `.gitignore` creado/actualizado ✓
- [ ] `local.properties` excluido ✓
- [ ] `.env` excluido ✓
- [ ] `build/` excluido ✓
- [ ] `.gradle/` excluido ✓
- [ ] `.idea/` excluido ✓
- [ ] `local.properties.example` creado ✓
- [ ] `.env.example` opcional pero recomendado
- [ ] Verificado con `git check-ignore` ✓

---

## 🚀 Próximos Pasos

1. Crea `local.properties` basado en `local.properties.example`
2. Rellena con tus credenciales IGDB
3. Verifica: `git check-ignore -v local.properties` → debe aparecer
4. Haz tu primer commit

```bash
git add .gitignore local.properties.example
git add -A  # Todos los archivos permitidos
git commit -m "feat: Initial commit GamerDex - Clean Architecture"
git push origin main
```

---

## 📝 Para tu TFG

> "Se configuró un `.gitignore` comprehensivo que protege credenciales IGDB,
> excluye artefactos de build, y mantiene el repositorio limpio (~500KB).
> Se proporciona `local.properties.example` para facilitar setup en otros equipos."

---

**Tu repositorio está limpio y seguro** ✅

