@echo off
setlocal

set "REPO_PATH=%~1"
if "%REPO_PATH%"=="" set "REPO_PATH=."

set "TARGET_NAME=miguellatorre19-stack"
set "TARGET_EMAIL=miguellatorre19@gmail.com"

pushd "%REPO_PATH%" >nul 2>&1 || (
  echo No se puede acceder a "%REPO_PATH%".
  exit /b 1
)

git rev-parse --is-inside-work-tree >nul 2>&1 || (
  echo "%CD%" no es un repo Git.
  popd
  exit /b 1
)

for /f %%i in ('git status --porcelain') do (
  echo El repo "%CD%" no esta limpio. Haz commit o stash antes de reescribir historial.
  popd
  exit /b 1
)

echo.
echo Reescribiendo historial en:
echo %CD%
echo Autor final: %TARGET_NAME% ^<%TARGET_EMAIL%^>
echo.

git filter-branch -f --env-filter "GIT_AUTHOR_NAME='%TARGET_NAME%'; GIT_AUTHOR_EMAIL='%TARGET_EMAIL%'; GIT_COMMITTER_NAME='%TARGET_NAME%'; GIT_COMMITTER_EMAIL='%TARGET_EMAIL%'; export GIT_AUTHOR_NAME GIT_AUTHOR_EMAIL GIT_COMMITTER_NAME GIT_COMMITTER_EMAIL" --tag-name-filter cat -- --all || (
  echo Fallo en git filter-branch.
  popd
  exit /b 1
)

git for-each-ref --format="delete %%(refname)" refs/original | git update-ref --stdin
git reflog expire --expire=now --all
git gc --prune=now --aggressive

echo.
echo Historial reescrito en %CD%
echo Revisa ahora:
echo   git shortlog -sne --all
echo Y si esta bien:
echo   git push origin --force --all
echo   git push origin --force --tags
echo.

popd
endlocal
