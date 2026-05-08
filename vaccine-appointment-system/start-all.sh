#!/usr/bin/env bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
FRONTEND_DIR="$SCRIPT_DIR/frontend"
LOG_DIR="$SCRIPT_DIR/logs"
BACKEND_PORT=8080
FRONTEND_PORT=5173

mkdir -p "$LOG_DIR"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

# Cleanup on exit
cleanup() {
    echo -e "\n${YELLOW}正在停止所有服务...${NC}"
    if [ -n "$BACKEND_PID" ]; then kill "$BACKEND_PID" 2>/dev/null; fi
    if [ -n "$FRONTEND_PID" ]; then kill "$FRONTEND_PID" 2>/dev/null; fi
    # Kill by port
    lsof -ti:$BACKEND_PORT 2>/dev/null | xargs kill -9 2>/dev/null || true
    lsof -ti:$FRONTEND_PORT 2>/dev/null | xargs kill -9 2>/dev/null || true
    echo -e "${GREEN}[OK] 所有服务已停止${NC}"
}
trap cleanup EXIT INT TERM

# Wait for port
wait_port() {
    local port=$1
    local timeout=${2:-60}
    local count=0
    while [ $count -lt $timeout ]; do
        if lsof -ti:"$port" >/dev/null 2>&1; then
            return 0
        fi
        sleep 2
        count=$((count + 1))
    done
    return 1
}

# Menu
show_menu() {
    clear
    echo "============================================================"
    echo "  疫苗预约系统 - 全栈一键启动 VACCINE APPOINTMENT SYSTEM"
    echo "============================================================"
    echo ""
    echo " 【开发模式】前后端分离，支持热重载"
    echo "   1. 全栈启动 - 后端(:8080) + 前端 Vite(:5173)"
    echo "   2. 仅启动前端 Vite (:5173) - 后端需单独启动"
    echo ""
    echo " 【单服务部署】前端 build 到后端 static，只启动一个端口"
    echo "   3. 构建前端并启动单服务 (:8080)"
    echo "   4. 仅构建前端（npm run build）"
    echo ""
    echo " 【停止服务】"
    echo "   5. 停止全部服务"
    echo ""
    echo " 【退出】"
    echo "   0. 退出"
    echo ""
    echo "============================================================"
}

while true; do
    show_menu
    read -rp "请输入选项 (0-5): " choice

    case $choice in
        1)
            echo -e "\n${CYAN}[1/2] 启动后端 SpringBoot (:8080) ...${NC}"
            cd "$SCRIPT_DIR"
            mvn spring-boot:run > "$LOG_DIR/backend.log" 2>&1 &
            BACKEND_PID=$!
            echo "   后端 PID: $BACKEND_PID"
            echo "   等待端口 $BACKEND_PORT 就绪..."
            if wait_port $BACKEND_PORT 60; then
                echo -e "   ${GREEN}[OK] 后端已就绪 (http://localhost:$BACKEND_PORT)${NC}"
            else
                echo -e "   ${RED}[FAIL] 后端启动超时，请检查 logs/backend.log${NC}"
            fi

            echo -e "\n${CYAN}[2/2] 启动前端 Vite (:5173) ...${NC}"
            cd "$FRONTEND_DIR"
            npm run dev > "$LOG_DIR/frontend.log" 2>&1 &
            FRONTEND_PID=$!
            echo "   前端 PID: $FRONTEND_PID"
            if wait_port $FRONTEND_PORT 30; then
                echo -e "   ${GREEN}[OK] 前端已就绪 (http://localhost:$FRONTEND_PORT)${NC}"
            else
                echo -e "   ${YELLOW}[WARN] 前端启动超时，可能还在安装依赖${NC}"
            fi

            echo ""
            echo "============================================================"
            echo "  全栈启动完成！"
            echo "  前端: http://localhost:$FRONTEND_PORT   (Vite 热重载)"
            echo "  后端: http://localhost:$BACKEND_PORT   (Spring Boot)"
            echo "  API:  http://localhost:$FRONTEND_PORT/api/xxx (自动代理)"
            echo "  停止: Ctrl+C 终止本脚本"
            echo "============================================================"
            echo ""
            read -rp "按 Enter 返回菜单..."
            ;;

        2)
            echo -e "\n${CYAN}启动前端 Vite 开发服务器 (:5173) ...${NC}"
            echo -e "${YELLOW}注意: 需要后端已在 :8080 运行${NC}"
            cd "$FRONTEND_DIR"
            npm run dev
            ;;

        3)
            echo -e "\n${CYAN}[1/2] 构建前端并输出到 src/main/resources/static ...${NC}"
            cd "$FRONTEND_DIR"
            if npm run build; then
                echo -e "   ${GREEN}[OK] 前端构建完成${NC}"
            else
                echo -e "   ${RED}[FAIL] 前端构建失败${NC}"
                exit 1
            fi

            echo -e "\n${CYAN}[2/2] 启动单服务 (:8080) ...${NC}"
            cd "$SCRIPT_DIR"
            mvn spring-boot:run > "$LOG_DIR/backend.log" 2>&1 &
            BACKEND_PID=$!
            if wait_port $BACKEND_PORT 60; then
                echo -e "   ${GREEN}[OK] 服务已启动！${NC}"
                echo -e "   访问: ${GREEN}http://localhost:$BACKEND_PORT${NC}"
            else
                echo -e "   ${RED}[FAIL] 后端启动超时${NC}"
            fi

            echo ""
            read -rp "按 Enter 返回菜单..."
            ;;

        4)
            echo -e "\n${CYAN}构建前端 (npm run build) ...${NC}"
            cd "$FRONTEND_DIR"
            npm run build && echo -e "${GREEN}[OK] 构建成功！${NC}" || echo -e "${RED}[FAIL] 构建失败${NC}"
            echo ""
            read -rp "按 Enter 返回菜单..."
            ;;

        5)
            cleanup
            echo ""
            read -rp "按 Enter 返回菜单..."
            ;;

        0)
            echo "再见！"
            exit 0
            ;;

        *)
            echo "无效选项"
            sleep 1
            ;;
    esac
done
