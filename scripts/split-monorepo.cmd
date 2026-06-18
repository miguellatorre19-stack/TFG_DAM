@echo off
setlocal

set "FRONTEND_REPO_PATH=..\TFG_DAM_frontend"
set "BACKEND_REPO_PATH=..\TFG_DAM_backend"
set "FRONTEND_REMOTE_URL="
set "BACKEND_REMOTE_URL="

:parse_args
if "%~1"=="" goto args_done
if /I "%~1"=="-FrontendRepoPath" (
  set "FRONTEND_REPO_PATH=%~2"
  shift
  shift
  goto parse_args
)
if /I "%~1"=="-BackendRepoPath" (
  set "BACKEND_REPO_PATH=%~2"
  shift
  shift
  goto parse_args
)
if /I "%~1"=="-FrontendRemoteUrl" (
  set "FRONTEND_REMOTE_URL=%~2"
  shift
  shift
  goto parse_args
)
if /I "%~1"=="-BackendRemoteUrl" (
  set "BACKEND_REMOTE_URL=%~2"
  shift
  shift
  goto parse_args
)
echo Argumento no reconocido: %~1
exit /b 1

:args_done
for /f %%i in ('git status --porcelain') do (
  echo El worktree no esta limpio. Haz commit o stash antes de separar repos.
  exit /b 1
)

if exist "%FRONTEND_REPO_PATH%" (
  echo La carpeta "%FRONTEND_REPO_PATH%" ya existe.
  exit /b 1
)

if exist "%BACKEND_REPO_PATH%" (
  echo La carpeta "%BACKEND_REPO_PATH%" ya existe.
  exit /b 1
)

echo ^>^> git subtree split --prefix=frontend -b split/frontend
git subtree split --prefix=frontend -b split/frontend || exit /b 1

echo ^>^> git subtree split --prefix=backend -b split/backend
git subtree split --prefix=backend -b split/backend || exit /b 1

echo ^>^> git clone --single-branch --branch split/frontend . "%FRONTEND_REPO_PATH%"
git clone --single-branch --branch split/frontend . "%FRONTEND_REPO_PATH%" || exit /b 1

echo ^>^> git clone --single-branch --branch split/backend . "%BACKEND_REPO_PATH%"
git clone --single-branch --branch split/backend . "%BACKEND_REPO_PATH%" || exit /b 1

pushd "%FRONTEND_REPO_PATH%" || exit /b 1
echo ^>^> git branch -m main
git branch -m main || exit /b 1
echo ^>^> git remote remove origin
git remote remove origin || exit /b 1
if not "%FRONTEND_REMOTE_URL%"=="" (
  echo ^>^> git remote add origin "%FRONTEND_REMOTE_URL%"
  git remote add origin "%FRONTEND_REMOTE_URL%" || exit /b 1
)
popd

pushd "%BACKEND_REPO_PATH%" || exit /b 1
echo ^>^> git branch -m main
git branch -m main || exit /b 1
echo ^>^> git remote remove origin
git remote remove origin || exit /b 1
if not "%BACKEND_REMOTE_URL%"=="" (
  echo ^>^> git remote add origin "%BACKEND_REMOTE_URL%"
  git remote add origin "%BACKEND_REMOTE_URL%" || exit /b 1
)
popd

echo.
echo Separacion completada.
echo Frontend: %FRONTEND_REPO_PATH%
echo Backend:  %BACKEND_REPO_PATH%
echo.
echo Siguientes pasos:
echo 1. Revisar cada repo.
echo 2. Ejecutar build/test en cada uno.
echo 3. Hacer push a los remotos nuevos.

endlocal
