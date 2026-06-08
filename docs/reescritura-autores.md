# Reescritura de autores

## Objetivo

Unificar todo el historial para que todos los commits queden atribuidos a:

- `miguellatorre19-stack <miguellatorre19@gmail.com>`

## Importante

Esto reescribe historial Git.

Consecuencias:

1. Cambian los hashes de todos los commits.
2. Necesitaras `force-push`.
3. Cualquier clon antiguo quedara desincronizado.
4. Si el otro usuario sigue dado de alta como colaborador en GitHub, seguira apareciendo en acceso aunque ya no figure como autor de commits.

## Diferencia importante

Hay dos cosas distintas:

1. **Autores/contributors por commits**
   Esto se corrige reescribiendo historial.

2. **Colaborador con acceso al repo**
   Esto se quita en GitHub, en:
   `Settings > Collaborators`

## Script para un repo

Desde `cmd`:

```bat
scripts\rewrite-authors.cmd .
```

O para un repo hermano:

```bat
scripts\rewrite-authors.cmd ..\TFG_DAM_frontend
scripts\rewrite-authors.cmd ..\TFG_DAM_backend
```

## Script para los tres repos

Desde la raiz del monorepo:

```bat
scripts\rewrite-authors-all.cmd
```

## Verificacion

En cada repo:

```bat
git shortlog -sne --all
```

Debe aparecer solo:

```txt
miguellatorre19-stack <miguellatorre19@gmail.com>
```

## Push final

En cada repo:

```bat
git push origin --force --all
git push origin --force --tags
```

## Nota sobre GitHub

GitHub puede tardar un rato en actualizar la grafica de contributors.
