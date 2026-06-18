@echo off
setlocal

call "%~dp0rewrite-authors.cmd" "." || exit /b 1
call "%~dp0rewrite-authors.cmd" "..\TFG_DAM_frontend" || exit /b 1
call "%~dp0rewrite-authors.cmd" "..\TFG_DAM_backend" || exit /b 1

echo.
echo Reescritura completada en los tres repos.
echo.
echo Siguiente paso en cada repo:
echo   git shortlog -sne --all
echo   git push origin --force --all
echo   git push origin --force --tags
echo.

endlocal
