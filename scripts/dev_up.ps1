Set-Location $PSScriptRoot\..
.\scripts\dev-check.ps1
if ($LASTEXITCODE -ne 0) { exit 1 }

docker compose --env-file .env up -d db

do {
  Start-Sleep 2
  $health = docker inspect --format='{{.State.Health.Status}}' association-db
} while ($health -ne "healthy")

Remove-Item Env:\SPRING_PROFILES_ACTIVE -ErrorAction SilentlyContinue

Set-Location .\backend
mvn spring-boot:run

