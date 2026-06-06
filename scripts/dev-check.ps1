param([string]$EnvFile = ".env")

function Load-Env($path) {
  Get-Content $path | ForEach-Object {
    if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
    $k,$v = $_ -split '=',2
    [Environment]::SetEnvironmentVariable($k.Trim(), $v.Trim(), "Process")
  }
}

function Check-Port($port, $name) {
  $c = Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue
  if ($c) {
    $p = Get-Process -Id $c[0].OwningProcess -ErrorAction SilentlyContinue
    Write-Host "[ERROR] $name puerto $port ocupado por PID $($c[0].OwningProcess) $($p.ProcessName)"
    return $false
  }
  Write-Host "[OK] $name puerto $port libre"
  return $true
}

Load-Env $EnvFile

$serverPort = if ($env:SERVER_PORT) { $env:SERVER_PORT } else { "8080" }
$dbHostPort = if ($env:DB_PORT) { $env:DB_PORT } else { "3308" }

$ok = $true
$ok = (Check-Port $serverPort "API") -and $ok
$ok = (Check-Port $dbHostPort "DB") -and $ok

if (-not $ok) { exit 1 }

