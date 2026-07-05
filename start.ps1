Set-Location $PSScriptRoot

docker compose up -d

Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot'; .\mvnw.cmd spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "Set-Location '$PSScriptRoot\frontend'; npm run dev"

Start-Sleep -Seconds 25
Start-Process "http://localhost:5173"
