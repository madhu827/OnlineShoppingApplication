# ============================================================
# run-backend.ps1
# Starts the Spring Boot backend
# ============================================================

Set-Location $PSScriptRoot

# 1. Environment variables (Local MongoDB - No SSL issues)
[System.Environment]::SetEnvironmentVariable('MONGODB_URI', 'mongodb://localhost:27017/online_shopping', 'Process')
[System.Environment]::SetEnvironmentVariable('CASSANDRA_HOST', '127.0.0.1', 'Process')
[System.Environment]::SetEnvironmentVariable('CASSANDRA_PORT', '9042', 'Process')
[System.Environment]::SetEnvironmentVariable('CASSANDRA_KEYSPACE', 'online_shopping', 'Process')

# 2. Check Java
$javaCheck = java -version 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host 'Java is not installed! Please install Java 21.' -ForegroundColor Red
    exit 1
}

# 3. Locate Maven
$mvnExe = 'mvn'

if (Test-Path 'C:\maven\bin\mvn.cmd') {
    $mvnExe = 'C:\maven\bin\mvn.cmd'
    $env:PATH = 'C:\maven\bin;' + $env:PATH
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    $mvnExe = 'mvn'
}

Write-Host "Using Maven: $mvnExe" -ForegroundColor Green
Write-Host 'Starting Spring Boot backend on http://localhost:8080 ...' -ForegroundColor Cyan

# 4. Run Spring Boot
& $mvnExe clean spring-boot:run
