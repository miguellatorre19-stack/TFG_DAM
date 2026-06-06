Set-Location $PSScriptRoot\..
.\scripts\dev-check.ps1
if ($LASTEXITCODE -ne 0) { exit 1 }

docker compose --env-file .env -f docker-compose-dev.yaml up -d db

do {
  Start-Sleep 2
  $health = docker inspect --format='{{.State.Health.Status}}' association-dev-db
} while ($health -ne "healthy")

Set-Location .\backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev-local

