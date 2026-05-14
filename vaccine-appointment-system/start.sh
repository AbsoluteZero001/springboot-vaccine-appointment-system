#!/usr/bin/env bash
# =============================================================
# 疫苗预约系统 - 一键启动脚本 (Linux / macOS)
# 用法: bash start.sh
# =============================================================
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "${CYAN}[STEP]${NC}  $1"; }

echo ""
echo "============================================"
echo "  疫苗预约系统 - 一键启动"
echo "============================================"
echo ""

# ---- 1. 环境文件检查 ----
log_step "1. 检查 .env 环境变量文件..."
if [ ! -f .env ]; then
  log_warn ".env 文件不存在，正从 .env.example 复制..."
  cp .env.example .env
  log_info "已创建 .env，请编辑设置密码后再重新运行。"
  echo ""
  echo "  vim .env   # 或使用任意文本编辑器"
  echo ""
  exit 0
fi
log_info ".env 文件已就绪"

# ---- 2. Docker 环境检查 ----
log_step "2. 检查 Docker 环境..."
if ! command -v docker &> /dev/null; then
  log_error "未检测到 Docker，请先安装 Docker: https://docs.docker.com/get-docker/"
  exit 1
fi

if ! docker compose version &> /dev/null 2>&1 && ! docker-compose version &> /dev/null 2>&1; then
  log_error "未检测到 Docker Compose 插件，请安装 docker compose 或 docker-compose。"
  exit 1
fi

# 自动选择 compose 命令
COMPOSE="docker compose"
if ! docker compose version &> /dev/null 2>&1; then
  COMPOSE="docker-compose"
fi
log_info "Docker 环境正常"

# ---- 3. 拉取并启动服务 ----
log_step "3. 构建镜像并启动所有服务..."
$COMPOSE up -d --build

# ---- 4. 等待服务就绪 ----
log_step "4. 等待服务健康检查通过..."

echo -n "  等待 MySQL 就绪..."
for i in $(seq 1 60); do
  if docker inspect -f '{{.State.Health.Status}}' vaccine-mysql 2>/dev/null | grep -q healthy; then
    echo " OK"
    break
  fi
  [ "$i" -eq 60 ] && { echo " 超时"; log_error "MySQL 启动超时，请运行 'docker compose logs mysql' 排查。"; exit 1; }
  sleep 2
  echo -n "."
done

echo -n "  等待 Redis 就绪..."
for i in $(seq 1 30); do
  if docker inspect -f '{{.State.Health.Status}}' vaccine-redis 2>/dev/null | grep -q healthy; then
    echo " OK"
    break
  fi
  [ "$i" -eq 30 ] && { echo " 超时"; log_error "Redis 启动超时"; exit 1; }
  sleep 1
  echo -n "."
done

echo -n "  等待后端服务就绪..."
for i in $(seq 1 60); do
  if docker inspect -f '{{.State.Health.Status}}' vaccine-backend 2>/dev/null | grep -q healthy; then
    echo " OK"
    break
  fi
  [ "$i" -eq 60 ] && log_warn "后端健康检查未通过，但容器可能仍在初始化中，可稍后检查日志。"
  sleep 3
  echo -n "."
done

# ---- 5. 完成提示 ----
source .env 2>/dev/null || true
FRONTEND_PORT="${FRONTEND_PORT:-80}"
APP_PORT="${APP_PORT:-8080}"

echo ""
echo "============================================"
echo "  所有服务已启动！"
echo "============================================"
echo ""
echo "  前端页面:    http://localhost:${FRONTEND_PORT}"
echo "  后端 API:    http://localhost:${APP_PORT}"
echo ""
echo "  管理员账号:  admin"
echo "  管理员密码:  admin123"
echo ""
echo "  常用命令:"
echo "    查看日志:  $COMPOSE logs -f"
echo "    停止服务:  $COMPOSE down"
echo "    重启服务:  $COMPOSE restart"
echo ""
log_info "开箱即用，祝使用愉快！"
