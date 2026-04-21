# 🔧 Comandos Git - GamerDex

## 🚀 Primeros Pasos con Git

### Inicializar Repositorio (Si no está inicializado)

```bash
cd C:\Users\mario\AndroidStudioProjects\GamerDex

# Inicializar repo local
git init

# Conectar con repositorio remoto
git remote add origin https://github.com/tu_usuario/GamerDex.git

# Verificar remoto
git remote -v
```

---

## 📝 Workflow Normal (Día a Día)

### Ver Estado

```bash
# Ver archivos modificados/nuevos
git status

# Ver cambios específicos
git diff                    # Todos los cambios
git diff app/src/main/     # Solo carpeta específica
git diff --cached          # Cambios staged
```

### Añadir Cambios

```bash
# Añadir todos los archivos (respetando .gitignore)
git add .

# Añadir solo archivo específico
git add app/src/main/java/com/mariolos27/gamerdex/MainActivity.kt

# Añadir carpeta
git add app/src/main/java/

# Ver qué se añadiría
git add -n .               # Dry run
```

### Verificar que .gitignore Funciona

```bash
# Verificar que credentials NO se añaden
git check-ignore -v local.properties
# Debe aparecer: local.properties (in .gitignore)

git check-ignore -v build/
# Debe aparecer: build/ (in .gitignore)
```

### Hacer Commit

```bash
# Commit con mensaje descriptivo
git commit -m "feat: Implement search functionality"

# Commit interactivo (elige qué añadir)
git commit -i

# Modificar último commit
git commit --amend
git commit --amend --no-edit   # Sin cambiar mensaje
```

---

## 📤 Pushear a Remoto

```bash
# Push a rama main
git push origin main

# Push a rama específica
git push origin develop

# Push con seguimiento de rama
git push -u origin main        # Próximos push: solo "git push"

# Force push (¡CUIDADO!)
git push -f origin main        # Solo si estás seguro
```

---

## 📥 Pullear del Remoto

```bash
# Traer últimos cambios
git pull                       # Equivale a fetch + merge
git pull origin main

# Fetch sin mergear
git fetch                      # Ver cambios sin aplicar
```

---

## 🌿 Gestión de Ramas

### Crear y Cambiar Ramas

```bash
# Crear rama nueva
git branch feature/new-feature

# Crear y cambiar a nueva rama
git checkout -b feature/new-feature
# O (Git 2.23+):
git switch -c feature/new-feature

# Cambiar a rama existente
git checkout main
# O:
git switch main

# Ver ramas locales
git branch

# Ver todas las ramas (local + remoto)
git branch -a
```

### Mergear Ramas

```bash
# Cambiar a rama destino
git checkout main

# Mergear rama feature
git merge feature/new-feature

# Mergear con historia lineal
git merge --rebase feature/new-feature

# Cancelar merge en conflicto
git merge --abort
```

### Eliminar Ramas

```bash
# Eliminar rama local
git branch -d feature/new-feature

# Forzar eliminación (si no está mergeada)
git branch -D feature/new-feature

# Eliminar rama remota
git push origin --delete feature/new-feature
```

---

## 🔍 Ver Historial

```bash
# Ver commits
git log

# Ver commits en una línea
git log --oneline

# Ver últimos N commits
git log -n 5

# Ver gráfico de ramas
git log --graph --oneline --all

# Ver commits de un archivo
git log --oneline app/src/main/java/MainActivity.kt

# Ver cambios en commits
git log -p                     # Todos los cambios
git log -p -n 3                # Últimos 3 commits

# Ver commit específico
git show 1a2b3c4d              # Por hash
git show HEAD~2                # Hace 2 commits
```

---

## 🔄 Deshacer Cambios

### Antes de Hacer Commit

```bash
# Descartar cambios en archivo específico
git checkout app/src/main/java/MainActivity.kt
# O (Git 2.23+):
git restore app/src/main/java/MainActivity.kt

# Descartar todos los cambios no commiteados
git checkout .
# O:
git restore .

# Descartar cambios staged (unstage)
git reset HEAD app/src/main/java/MainActivity.kt
# O:
git restore --staged app/src/main/java/MainActivity.kt
```

### Después de Hacer Commit

```bash
# Deshacer último commit (mantener cambios)
git reset --soft HEAD~1

# Deshacer últimos 2 commits (mantener cambios)
git reset --soft HEAD~2

# Deshacer commit sin mantener cambios
git reset --hard HEAD~1

# Crear nuevo commit que deshace cambios
git revert 1a2b3c4d

# Cambiar mensaje del último commit
git commit --amend -m "nuevo mensaje"
```

---

## 🚨 Conflictos de Merge

```bash
# Ver conflictos
git status

# Ver contenido de conflicto
git diff

# Resolver manualmente:
# 1. Abre archivos con <<<<<<, ======, >>>>>>>
# 2. Elige qué código mantener
# 3. Elimina marcadores

# Marcar como resuelto
git add archivo_resuelto

# Completar merge
git commit

# Cancelar merge
git merge --abort
```

---

## 🔐 Protección de Credenciales

### Verificar que local.properties NO está tracked

```bash
# Ver archivos tracked
git ls-tree -r HEAD --name-only | grep local.properties

# Resultado esperado: NADA (no debe aparecer)

# Verificar .gitignore
git check-ignore -v local.properties
# Resultado: local.properties (in .gitignore) ✅
```

### Si Accidentalmente Commiteaste local.properties

```bash
# OPCIÓN 1: Remover del índice (recomendado)
git rm --cached local.properties

# Añadir a .gitignore (debe estar ya)
echo "local.properties" >> .gitignore

# Commit
git commit -m "chore: Remove local.properties from git tracking"

# OPCIÓN 2: Reescribir historio (más seguro)
# Ver SECURITY_CREDENTIALS.md
```

---

## 📋 Convenciones de Commits

### Formato

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Tipos

```
feat:     Nueva feature
fix:      Bug fix
docs:     Documentación
style:    Cambios de formato (no código)
refactor: Refactorización
perf:     Mejora de performance
test:     Tests
chore:    Tasks (dependencies, etc)
ci:       CI/CD
```

### Ejemplos

```bash
# Feature
git commit -m "feat(search): Add IGDB API integration"

# Bug fix
git commit -m "fix(ui): Fix button alignment issue"

# Documentación
git commit -m "docs: Update README with setup guide"

# Configuración
git commit -m "chore(deps): Update Hilt to 2.51"

# Multiple líneas
git commit -m "feat(search): Implement game search

- Add SearchViewModel
- Add SearchUiState with 4 states
- Integrate with IGDB API
- Add loading spinner"
```

---

## 🔀 Stash (Guardar Cambios Temporales)

```bash
# Guardar cambios sin commitear
git stash

# Ver stash guardados
git stash list

# Aplicar último stash
git stash pop

# Aplicar stash específico
git stash apply stash@{0}

# Ver contenido de stash
git stash show stash@{0}

# Eliminar stash
git stash drop stash@{0}
```

---

## 🔍 Buscar en Historial

```bash
# Buscar commits por mensaje
git log --grep="search"

# Buscar commits por autor
git log --author="Mario"

# Buscar commits que tocaron archivo específico
git log -- app/src/main/java/MainActivity.kt

# Buscar dónde se agregó/eliminó una línea
git log -S "searchGames" --oneline
```

---

## 🏷️ Tags (Versiones)

```bash
# Crear tag
git tag v1.0.0

# Crear tag con descripción
git tag -a v1.0.0 -m "Release 1.0.0 - Search feature"

# Ver tags
git tag -l

# Push tags
git push origin v1.0.0
git push origin --tags        # Todos los tags

# Eliminar tag local
git tag -d v1.0.0

# Eliminar tag remoto
git push origin -d v1.0.0
```

---

## 🚀 GitHub Específico

### Clonar Repositorio

```bash
git clone https://github.com/tu_usuario/GamerDex.git
cd GamerDex
```

### Crear Pull Request (PR)

```bash
# 1. Crear rama feature
git checkout -b feature/search-improvements

# 2. Hacer cambios y commitear
git add .
git commit -m "feat(search): Add filters"

# 3. Push
git push -u origin feature/search-improvements

# 4. Ir a GitHub y crear PR manualmente
# O usar GitHub CLI:
gh pr create --title "Add search filters" --body "Adds genre and year filters"
```

### Review de PR Local

```bash
# Traer rama del PR
git fetch origin pull/123/head:pr-123

# Cambiar a la rama
git checkout pr-123

# Testear cambios
# ...

# Fusionar en main
git checkout main
git merge pr-123
```

---

## 📊 Estadísticas

```bash
# Número de commits
git rev-list --count HEAD

# Líneas de código
git ls-files | xargs wc -l

# Cambios por archivo
git diff --stat

# Contribuciones por autor
git shortlog -sn
```

---

## ⚡ Alias Útiles

```bash
# Crear alias (en ~/.gitconfig o global)
git config --global alias.st status
git config --global alias.co checkout
git config --global alias.br branch
git config --global alias.ci commit
git config --global alias.l "log --oneline"
git config --global alias.lg "log --graph --oneline --all"

# Usar
git st                        # Equivale a git status
git co main                   # Equivale a git checkout main
git lg                        # Ver gráfico bonito
```

---

## 🆘 Situaciones de Emergencia

### "Perdí mis cambios!"

```bash
# Ver todo lo que has hecho (incluso commits eliminados)
git reflog

# Recuperar commits antiguos
git reset --hard abc1234     # abc1234 es el hash del reflog
```

### "Hice commit en rama equivocada"

```bash
# En rama equivocada
git reset --soft HEAD~1       # Deshacer commit, mantener cambios

# Cambiar a rama correcta
git checkout -b feature/correct

# Commitear en rama correcta
git commit -m "feat: ..."
```

### "Necesito reescribir historial"

```bash
# Rebase interactivo (úsalo con CUIDADO)
git rebase -i HEAD~3          # Últimos 3 commits

# Force push (¡SOLO si es rama personal!)
git push -f origin feature/myfeature
```

---

## 📚 Recursos

- [Git Documentation](https://git-scm.com/doc)
- [GitHub Docs](https://docs.github.com)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [Git Flight Rules](https://github.com/k88hudson/git-flight-rules)

---

## ✅ Checklist Antes de Push

- [ ] `git status` - Sin archivos no deseados
- [ ] `git diff` - Cambios esperados
- [ ] `git check-ignore -v local.properties` - Protegido
- [ ] `git log --oneline -n 5` - Commits con mensajes claros
- [ ] Tests pasan (si aplica)
- [ ] Código sin errores de compilación
- [ ] `.gitignore` actualizado

---

**¡Usa Git con confianza!** 🚀

