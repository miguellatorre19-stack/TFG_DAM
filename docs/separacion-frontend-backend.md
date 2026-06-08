# Separacion del Monorepo

## Objetivo

Separar `frontend/` y `backend/` en dos repos distintos sin destruir el monorepo original y preservando el historial relevante de cada carpeta.

## Enfoque

Se utiliza `git subtree split`, que genera una rama nueva con el historial filtrado de una subcarpeta.

Ventajas:

- No modifica el monorepo actual.
- Preserva historial por carpeta.
- Permite crear dos repos limpios y luego publicarlos por separado.

## Requisito importante

El worktree debe estar limpio antes de ejecutar la separacion.

```powershell
git status --short
```

Si hay cambios, haz commit o stash primero.

## Script preparado

Desde la raiz del proyecto:

```powershell
.\scripts\split-monorepo.ps1
```

Esto crea por defecto dos carpetas hermanas:

- `..\TFG_DAM_frontend`
- `..\TFG_DAM_backend`

## Con remotos nuevos

Si ya has creado dos repos vacios en GitHub:

```powershell
.\scripts\split-monorepo.ps1 `
  -FrontendRemoteUrl "https://github.com/USUARIO/TFG_DAM_frontend.git" `
  -BackendRemoteUrl "https://github.com/USUARIO/TFG_DAM_backend.git"
```

## Que hace el script

1. Comprueba que el monorepo esta limpio.
2. Genera la rama `split/frontend`.
3. Genera la rama `split/backend`.
4. Clona cada rama filtrada en una carpeta nueva.
5. Renombra la rama actual a `main` en cada repo nuevo.
6. Elimina el remoto original para evitar pushes accidentales al monorepo.
7. Añade el remoto nuevo si se ha indicado una URL.

## Despues de separar

En cada repo nuevo:

```powershell
git status
git remote -v
```

Frontend:

```powershell
cd ..\TFG_DAM_frontend
npm install
npm run lint
npm run build
```

Backend:

```powershell
cd ..\TFG_DAM_backend
mvn test
```

## Nota sobre el backend

Se ha añadido un `.gitignore` propio y un `README.md` dentro de `backend/` para que al separarlo ya tenga metadatos basicos de repo independiente.
