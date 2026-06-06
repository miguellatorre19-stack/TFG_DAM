Set-Location $PSScriptRoot\..

docker compose --env-file .env -f docker-compose-dev.yaml ps

