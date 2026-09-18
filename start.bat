@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul

cd /d "%~dp0"

title 疫苗预约系统 - 企业级启动器

echo.
echo ============================================
echo   疫苗预约系统 - 企业级启动器
echo ============================================
echo.

REM ==================================================
REM 1. 检查环境变量文件
REM 如果 .env 不存在，则从 .env.example 自动复制
REM ==================================================
if not exist ".env" (
    echo [WARN] 未检测到 .env 文件，正在创建...

    if exist ".env.example" (
        copy /y ".env.example" ".env" >nul
        echo [INFO] 已从 .env.example 创建 .env
    ) else (
        echo [ERROR] 缺少 .env.example，无法创建配置文件
        pause
        exit /b 1
    )
)

echo [INFO] .env OK


REM ==================================================
REM 2. 检查 Docker 是否安装
REM ==================================================
where docker >nul 2>&1
if errorlevel 1 (
    echo [ERROR] 未检测到 Docker，请先安装 Docker Desktop
    pause
    exit /b 1
)

docker compose version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Docker Compose 不可用
    pause
    exit /b 1
)

echo [INFO] Docker OK


REM ==================================================
REM 3. 启动 Docker Compose
REM --build：重新构建镜像
REM -d：后台运行
REM ==================================================
echo [STEP] 启动 Docker Compose...

docker compose up -d --build

if errorlevel 1 (
    echo [ERROR] Docker Compose 启动失败
    pause
    exit /b 1
)

echo [INFO] Docker Compose 启动完成


REM ==================================================
REM 4. 等待容器初始启动
REM 【修复】：使用 ping 替代 timeout，避免 Input redirection 报错
REM ==================================================
echo [STEP] 等待容器初始化...
ping 127.0.0.1 -n 6 >nul


REM ==================================================
REM 5. 检查后端健康状态
REM 【修复】：PowerShell 添加 NonInteractive 和 InputFormat None
REM ==================================================
echo [STEP] 检查后端健康状态...

set RETRY=0

:CHECK_BACKEND
set /a RETRY+=1

echo   Backend 检测中 (!RETRY!/30)

powershell -NoProfile -NonInteractive -InputFormat None -Command ^
"try { ^
  $r = Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -UseBasicParsing -TimeoutSec 3; ^
  if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } ^
} catch { exit 1 }"

if !errorlevel! == 0 (
    echo [INFO] Backend Ready
    goto CHECK_FRONTEND
)

if !RETRY! GEQ 30 (
    echo [WARN] Backend 启动超时，可能仍在初始化
    goto SHOW_RESULT
)

ping 127.0.0.1 -n 3 >nul
goto CHECK_BACKEND


REM ==================================================
REM 6. 检查前端健康状态
REM ==================================================
:CHECK_FRONTEND
echo [STEP] 检查前端健康状态...

set FRONT_RETRY=0

:FRONT_LOOP
set /a FRONT_RETRY+=1

echo   Frontend 检测中 (!FRONT_RETRY!/20)

powershell -NoProfile -NonInteractive -InputFormat None -Command ^
"try { ^
  $r = Invoke-WebRequest -Uri 'http://localhost' -UseBasicParsing -TimeoutSec 3; ^
  if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 } ^
} catch { exit 1 }"

if !errorlevel! == 0 (
    echo [INFO] Frontend Ready
    goto SHOW_RESULT
)

if !FRONT_RETRY! GEQ 20 (
    echo [WARN] Frontend 启动超时
    goto SHOW_RESULT
)

ping 127.0.0.1 -n 3 >nul
goto FRONT_LOOP


REM ==================================================
REM 7. 输出结果信息
REM ==================================================
:SHOW_RESULT
echo.
echo ============================================
echo   启动完成（v8 Enterprise Stable）
echo ============================================
echo.
echo 🌐 前端地址: http://localhost
echo 🔧 后端地址: http://localhost:8080
echo.

echo ============================================
echo   默认管理员账号
echo ============================================
echo 用户名: admin
echo 密码  : admin123
echo.

echo ============================================
echo   常用运维命令
echo ============================================
echo 查看日志: docker compose logs -f
echo 停止服务: docker compose down
echo 重启服务: docker compose restart
echo.

start http://localhost

pause
exit /b 0