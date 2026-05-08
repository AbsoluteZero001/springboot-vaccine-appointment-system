@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

set PROJECT_ROOT=%~dp0
set FRONTEND_DIR=%PROJECT_ROOT%frontend
set BACKEND_PORT=8080
set FRONTEND_PORT=5173
set LOG_DIR=%PROJECT_ROOT%logs
set PID_DIR=%PROJECT_ROOT%logs

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

:menu
cls
echo ============================================================
echo   疫苗预约系统 - 全栈一键启动 VACCINE APPOINTMENT SYSTEM
echo ============================================================
echo.
echo  【开发模式】前后端分离，支持热重载
echo    1. 全栈启动 - 后端(:8080) + 前端 Vite(:5173)
echo    2. 仅启动前端 Vite (:5173) - 后端需单独启动
echo.
echo  【单服务部署】前端 build 到后端 static，只启动一个端口
echo    3. 构建前端并启动单服务 (:8080)
echo    4. 仅构建前端（npm run build）
echo.
echo  【停止服务】
echo    5. 停止全部服务
echo.
echo  【退出】
echo    0. 退出
echo.
echo ============================================================

set /p choice="请输入选项 (0-5): "

if "%choice%"=="1" goto start-all
if "%choice%"=="2" goto start-frontend-only
if "%choice%"=="3" goto start-single-service
if "%choice%"=="4" goto build-only
if "%choice%"=="5" goto stop-all
if "%choice%"=="0" goto exit-all
echo 无效选项，请重新选择...
pause
goto menu

:: ============================================================
:: 1. 全栈启动 (前后端分离，两个端口)
:: ============================================================
:start-all
echo.
echo [1/2] 启动后端 SpringBoot (:8080) ...
cd /d "%PROJECT_ROOT%"
start "SpringBoot-Backend" cmd /c "mvn spring-boot:run > "%LOG_DIR%\backend.log" 2>&1"
echo       后端启动中，等待端口 %BACKEND_PORT% 就绪...

:: Wait for backend to be ready
call :wait-port %BACKEND_PORT% 60
if !errorlevel! neq 0 (
    echo       [FAIL] 后端启动超时，请检查 logs/backend.log
    pause
    goto menu
)
echo       [OK] 后端已就绪 (http://localhost:%BACKEND_PORT%)

echo.
echo [2/2] 启动前端 Vite (:5173) ...
cd /d "%FRONTEND_DIR%"
start "Vite-Frontend" cmd /c "npm run dev > "%LOG_DIR%\frontend.log" 2>&1"
echo       前端启动中...

call :wait-port %FRONTEND_PORT% 30
if !errorlevel! neq 0 (
    echo       [WARN] 前端启动超时，可能还在安装依赖
)
echo       [OK] 前端已就绪 (http://localhost:%FRONTEND_PORT%)

echo.
echo ============================================================
echo   全栈启动完成！
echo   前端: http://localhost:%FRONTEND_PORT%   (Vite 热重载)
echo   后端: http://localhost:%BACKEND_PORT%   (Spring Boot)
echo   API:  http://localhost:%FRONTEND_PORT%/api/xxx  (自动代理到后端)
echo   停止: 运行本脚本选择选项 5，或关闭弹出的两个终端窗口
echo ============================================================
echo.
echo 提示: 前端通过 Vite proxy 调用后端 /api，无需 CORS 处理
echo.
pause
goto menu

:: ============================================================
:: 2. 仅启动前端
:: ============================================================
:start-frontend-only
echo.
echo 启动前端 Vite 开发服务器 (:5173) ...
echo 注意: 需要后端已在 :8080 运行（手动启动或 MongoDB/Redis 已就绪）
cd /d "%FRONTEND_DIR%"
start "Vite-Frontend" cmd /c "npm run dev"
echo.
echo 前端已启动: http://localhost:%FRONTEND_PORT%
pause
goto menu

:: ============================================================
:: 3. 单服务部署 (构建前端 + 只启动后端)
:: ============================================================
:start-single-service
echo.
echo [1/2] 构建前端并输出到 src/main/resources/static ...
cd /d "%FRONTEND_DIR%"
call npm run build
if %errorlevel% neq 0 (
    echo [FAIL] 前端构建失败
    pause
    goto menu
)
echo       [OK] 前端构建完成，已输出到后端 static 目录

echo.
echo [2/2] 启动单服务 (:8080) ...
cd /d "%PROJECT_ROOT%"
start "SpringBoot-Single" cmd /c "mvn spring-boot:run"
echo       后端启动中...

call :wait-port %BACKEND_PORT% 60
if !errorlevel! neq 0 (
    echo       [FAIL] 后端启动超时
    pause
    goto menu
)
echo       [OK] 服务已启动！

echo.
echo ============================================================
echo   单服务部署完成！
echo   访问: http://localhost:%BACKEND_PORT%
echo   前后端合并在同一端口，无需分开部署
echo ============================================================
echo.
pause
goto menu

:: ============================================================
:: 4. 仅构建前端
:: ============================================================
:build-only
echo.
echo 构建前端 (npm run build) ...
cd /d "%FRONTEND_DIR%"
call npm run build
if %errorlevel% equ 0 (
    echo [OK] 构建成功！文件输出到 src/main/resources/static/
) else (
    echo [FAIL] 构建失败
)
echo.
pause
goto menu

:: ============================================================
:: 5. 停止全部服务
:: ============================================================
:stop-all
echo.
echo 正在停止所有服务...

:: Kill Spring Boot (Java process on port 8080)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%BACKEND_PORT%" ^| findstr "LISTENING"') do (
    echo   停止 Java 进程 (PID: %%a, 端口: %BACKEND_PORT%)
    taskkill /PID %%a /F >nul 2>&1
)

:: Kill Vite (Node process on port 5173)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%FRONTEND_PORT%" ^| findstr "LISTENING"') do (
    echo   停止 Node 进程 (PID: %%a, 端口: %FRONTEND_PORT%)
    taskkill /PID %%a /F >nul 2>&1
)

:: Also try stopping by window title
taskkill /FI "WINDOWTITLE eq SpringBoot-Backend*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq Vite-Frontend*" /F >nul 2>&1
taskkill /FI "WINDOWTITLE eq SpringBoot-Single*" /F >nul 2>&1

echo [OK] 所有服务已停止
echo.
pause
goto menu

:: ============================================================
:: 0. 退出
:: ============================================================
:exit-all
echo 再见！
exit /b 0

:: ============================================================
:: Helper: wait for port to be ready
:: ============================================================
:wait-port
set port=%1
set timeout=%2
set count=0
:wait-loop
timeout /t 2 /nobreak >nul
set /a count+=1
netstat -ano | findstr ":%port%" | findstr "LISTENING" >nul
if %errorlevel% equ 0 exit /b 0
if %count% geq %timeout% exit /b 1
goto wait-loop
