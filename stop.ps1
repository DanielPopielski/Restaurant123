# Zatrzymuje kontenery (backend i frontend zamknij zamykajac ich okna)
Set-Location $PSScriptRoot
docker compose stop
Write-Host "Kontenery zatrzymane." -ForegroundColor Green
