@echo off
echo =========================================
echo Vaccine Appointment System
echo =========================================
echo.

REM Check if Docker Compose is available
where docker-compose >nul 2>nul
if %errorlevel% equ 0 (
    echo Docker Compose detected
    echo.
    echo Available options:
    echo 1. Start with Docker Compose (MySQL + Redis + App)
    echo 2. Start Spring Boot app only (requires external MySQL/Redis)
    echo 3. Exit
    echo.
    set /p choice="Enter your choice (1-3): "

    if "%choice%"=="1" (
        echo Starting with Docker Compose...
        docker-compose up --build
    ) else if "%choice%"=="2" (
        echo Starting Spring Boot app only...
        echo Make sure MySQL and Redis are running
        mvn spring-boot:run
    ) else if "%choice%"=="3" (
        echo Exiting...
        exit /b 0
    ) else (
        echo Invalid choice. Exiting...
        exit /b 1
    )
) else (
    echo Docker Compose not found. Starting Spring Boot app only...
    echo Note: You need to have MySQL and Redis running separately
    mvn spring-boot:run
)