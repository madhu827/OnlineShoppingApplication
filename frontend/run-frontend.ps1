# ============================================================
# run-frontend.ps1
# One-click script to start the React frontend on :3000
# Run from D:\Online SHopping\frontend\ directory.
# ============================================================

Set-Location $PSScriptRoot

# Check Node.js
if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    Write-Host "[ERROR] Node.js not found! Install from https://nodejs.org/" -ForegroundColor Red
    exit 1
}
Write-Host "[INFO] Node: $(node -v)   npm: $(npm -v)" -ForegroundColor Green

# Install dependencies if node_modules is missing
if (-not (Test-Path "$PSScriptRoot\node_modules")) {
    Write-Host "[INFO] Installing dependencies (first run)..." -ForegroundColor Cyan
    npm install
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] npm install failed." -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Starting React Frontend on http://localhost:3000" -ForegroundColor Cyan
Write-Host "  API proxied to  http://localhost:8080" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

npm run dev

