@echo off
title Online Shopping Backend

cd /d "%~dp0"

echo [1/3] Setting Environment Variables...
:: Using local MongoDB (Zero SSL drops, No IP blocking)
set MONGODB_URI=mongodb://localhost:27017/online_shopping
set CASSANDRA_HOST=127.0.0.1
set CASSANDRA_PORT=9042
set CASSANDRA_KEYSPACE=online_shopping

echo [2/3] Adding Maven to PATH...
set PATH=C:\maven\bin;%PATH%

echo [3/3] Starting Spring Boot Backend on http://localhost:8080 ...
mvn clean spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Backend stopped with an error. Press any key to exit...
    pause
)
