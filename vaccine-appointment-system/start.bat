@echo off
REM =============================================================
REM 疫苗预约系统 - 一键启动脚本 (Windows)
REM 用法: 双击 start.bat 或在命令行运行 start.bat
REM =============================================================
setlocal enabledelayedexpansion

cd /d "%~dp0"

echo.
echo ============================================
echo   疫苗预约系统 - 一键启动
echo ============================================
echo.

REM ---- 1. 环境文件检查 ----
echo [STEP] 1. 检查 .env 环境变量文件...
if not exist .env (
    echo [WARN] .env 文件不存在，正从 .env.example 复制...
    copy /y .env.example .env >nul
    echo [INFO] 已创建 .env，请编辑设置密码后重新运行。
    echo.
    echo   使用记事本编辑: notepad .env
    echo.
    pause
    exit /b 0
)
echo [INFO] .env 文件已就绪

REM ---- 2. Docker 环境检查 ----
echo [STEP] 2. 检查 Docker 环境...
where docker >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] 未检测到 Docker，请先安装 Docker Desktop: https://docs.docker.com/get-docker/
    pause
    exit /b 1
)

REM 检测 compose 命令
set COMPOSE_CMD=docker compose
docker compose version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    where docker-compose >nul 2>&1
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] 未检测到 Docker Compose，请安装 Docker Desktop。
        pause
        exit /b 1
    )
    set COMPOSE_CMD=docker-compose
)
echo [INFO] Docker 环境正常

REM ---- 3. 拉取并启动服务 ----
echo [STEP] 3. 构建镜像并启动所有服务...
%COMPOSE_CMD% up -d --build

REM ---- 4. 等待服务就绪 ----
echo [STEP] 4. 等待服务健康检查通过...

echo     等待 MySQL 就绪...
set MYSQL_OK=0
for /L %%i in (1,1,30) do (
    for /f "tokens=*" %%s in ('docker inspect -f "{{.State.Health.Status}}" vaccine-mysql 2^>nul') do set MYSQL_STATUS=%%s
    if "!MYSQL_STATUS!"=="healthy" (
        set MYSQL_OK=1
        echo     MySQL 就绪
        goto :mysql_ok
    )
    timeout /t 3 /nobreak >nul
    echo|set /p="."
)
echo     超时
:mysql_ok

echo     等待 Redis 就绪...
set REDIS_OK=0
for /L %%i in (1,1,15) do (
    for /f "tokens=*" %%s in ('docker inspect -f "{{.State.Health.Status}}" vaccine-redis 2^>nul') do set REDIS_STATUS=%%s
    if "!REDIS_STATUS!"=="healthy" (
        set REDIS_OK=1
        echo     Redis 就绪
        goto :redis_ok
    )
    timeout /t 2 /nobreak >nul
    echo|set /p="."
)
echo     超时
:redis_ok

echo     等待后端服务就绪...
for /L %%i in (1,1,20) do (
    for /f "tokens=*" %%s in ('docker inspect -f "{{.State.Health.Status}}" vaccine-backend 2^>nul') do set BACKEND_STATUS=%%s
    if "!BACKEND_STATUS!"=="healthy" (
        echo     后端就绪
        goto :backend_ok
    )
    timeout /t 5 /nobreak >nul
    echo|set /p="."
)
echo     超时（容器可能仍在初始化中）
:backend_ok

REM ---- 5. 完成提示 ----
echo.
echo ============================================
echo   所有服务已启动！
echo ============================================
echo.
echo   前端页面:    http://localhost
echo   后端 API:    http://localhost:8080
echo.
echo   管理员账号:  admin
echo   管理员密码:  admin123
echo.
echo   常用命令:
echo     查看日志:  %COMPOSE_CMD% logs -f
echo     停止服务:  %COMPOSE_CMD% down
echo     重启服务:  %COMPOSE_CMD% restart
echo.
echo [INFO] 开箱即用，祝使用愉快！
echo.
pause
