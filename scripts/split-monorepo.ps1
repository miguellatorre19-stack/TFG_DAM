param(
    [string]$FrontendRepoPath = "..\\TFG_DAM_frontend",
    [string]$BackendRepoPath = "..\\TFG_DAM_backend",
    [string]$FrontendRemoteUrl = "",
    [string]$BackendRemoteUrl = ""
)

$ErrorActionPreference = "Stop"

function Require-CleanWorktree {
    $status = git status --porcelain
    if ($status) {
        throw "El worktree no esta limpio. Haz commit o stash antes de separar repos."
    }
}

function Require-AvailablePath([string]$path) {
    if (Test-Path $path) {
        throw "La carpeta '$path' ya existe. Elige otra ruta o borra la existente."
    }
}

function Run([string]$command, [string]$workdir = $PWD.Path) {
    Write-Host ">> $command"
    Push-Location $workdir
    try {
        Invoke-Expression $command
        if ($LASTEXITCODE -ne 0) {
            throw "Fallo ejecutando: $command"
        }
    } finally {
        Pop-Location
    }
}

Require-CleanWorktree
Require-AvailablePath $FrontendRepoPath
Require-AvailablePath $BackendRepoPath

Run "git subtree split --prefix=frontend -b split/frontend"
Run "git subtree split --prefix=backend -b split/backend"

Run "git clone --single-branch --branch split/frontend . `"$FrontendRepoPath`""
Run "git clone --single-branch --branch split/backend . `"$BackendRepoPath`""

Run "git branch -m main" $FrontendRepoPath
Run "git branch -m main" $BackendRepoPath

Run "git remote remove origin" $FrontendRepoPath
Run "git remote remove origin" $BackendRepoPath

if ($FrontendRemoteUrl) {
    Run "git remote add origin `"$FrontendRemoteUrl`"" $FrontendRepoPath
}

if ($BackendRemoteUrl) {
    Run "git remote add origin `"$BackendRemoteUrl`"" $BackendRepoPath
}

Write-Host ""
Write-Host "Separacion completada."
Write-Host "Frontend: $FrontendRepoPath"
Write-Host "Backend:  $BackendRepoPath"
Write-Host ""
Write-Host "Siguientes pasos recomendados:"
Write-Host "1. Revisar cada repo."
Write-Host "2. Ejecutar tests/build en cada uno."
Write-Host "3. Hacer el primer push al remoto nuevo si has configurado origin."
