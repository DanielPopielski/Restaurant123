# Uruchamia caly projekt: baze, Kafke, backend i frontend
Set-Location $PSScriptRoot

Write-Host "[1/3] Startuje PostgreSQL i Kafke (Docker)..." -ForegroundColor Cyan
docker compose up -d

Write-Host "[2/3] Startuje backend (nowe okno)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot'; .\mvnw.cmd spring-boot:run"

Write-Host "[3/3] Startuje frontend (nowe okno)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot\frontend'; npm run dev"

Write-Host "Czekam 25 s na start backendu, potem otwieram przegladarke..." -ForegroundColor Yellow
Start-Sleep -Seconds 25
Start-Process "http://localhost:5173"
Write-Host "Gotowe. Zamkniecie tego okna niczego nie zatrzymuje." -ForegroundColor Green
