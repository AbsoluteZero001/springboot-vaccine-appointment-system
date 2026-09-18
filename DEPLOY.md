# 生产部署说明

本目录同时包含两种部署产物：

- `vaccine-appointment-system-1.0.0.jar`：已内嵌前端页面的单体 JAR。
- `frontend-dist/`：构建后的前端静态文件，可由 Nginx 单独托管。

## 前置要求

- JDK 17 或更高版本
- MySQL 8.0
- Redis 7
- Docker 部署方式需要 Docker Engine 和 Docker Compose Plugin

## 方式一：Docker Compose

适用于 Linux 服务器，容器会启动 MySQL、Redis、后端和 Nginx 前端。

```bash
cp .env.example .env
chmod +x start.sh
./start.sh
```

上线前必须修改 `.env` 中的数据库密码和 `JWT_SECRET`。可使用以下命令生成 JWT 密钥：

```bash
openssl rand -base64 48
```

启动后默认访问：

- 前端：`http://服务器地址:80`
- 后端：`http://服务器地址:8080`
- 健康检查：`http://服务器地址:8080/actuator/health`

## 方式二：单体 JAR

适用于服务器已有 MySQL 和 Redis 的场景。

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=127.0.0.1
export DB_PORT=3306
export MYSQL_DATABASE=vaccine_appointment_db
export MYSQL_USER=vaccine_user
export MYSQL_PASSWORD='替换为强密码'
export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
export REDIS_PASSWORD='替换为强密码或留空'
export JWT_SECRET='替换为至少 64 位随机字符串'
export UPLOAD_DIR=/opt/vaccine-app/uploads

java -jar vaccine-appointment-system-1.0.0.jar
```

应用默认监听 `8080` 端口，可通过 `APP_PORT` 修改。前端由 JAR 内置的 Spring Boot 静态资源服务提供。

## 上线检查

1. 修改所有默认密码和 `JWT_SECRET`。
2. 只开放实际需要的端口，不要将 MySQL 或 Redis 端口暴露到公网。
3. 配置 HTTPS、域名和防火墙。
4. 首次登录后立即修改默认管理员密码。
5. 定期备份 MySQL 数据和 `UPLOAD_DIR` 目录。
