# 疫苗预约管理系统

Vue 3 + Spring Boot 3 + MySQL + Redis 全栈疫苗预约管理平台 — 支持用户在线预约疫苗接种、管理员后台审核管理、接种记录追溯、审计日志追踪。

## 技术栈

| 层级   | 技术                                          | 版本                  |
|------|---------------------------------------------|---------------------|
| 前端   | Vue 3 + Vite + Pinia + Vue Router + Axios   | Node 18+            |
| 后端   | Spring Boot 3 + Spring Security + JPA + JWT | Java 17 / Maven 3.9 |
| 数据库  | MySQL                                       | 8.0                 |
| 缓存   | Redis                                       | 7.x                 |
| 容器化  | Docker + Docker Compose                     | —                   |
| 反向代理 | Nginx (生产模式)                                | Alpine              |

## 快速启动

### 前置要求

- Docker Desktop（或 Docker + Docker Compose）
- 或者本地安装：JDK 17、Maven 3.9、Node 18、MySQL 8.0、Redis 7

### 方式一：Docker Compose 一键启动（推荐）

```bash
# 1. 克隆项目
git clone <repo-url> && cd vaccine-appointment-system

# 2. 配置环境变量（可选，默认值可直接运行）
cp .env.example .env

# 3. 一键启动全部服务（MySQL + Redis + 后端 + 前端）
docker compose up -d

# 4. 访问
#    前端:  http://localhost
#    后端:  http://localhost:8080
#    Swagger: http://localhost:8080/swagger-ui/index.html
```

首次启动 MySQL 初始化需等待约 30 秒，后端会自动建表并导入测试数据。

### 方式二：本地开发模式

```bash
# ---- 1. 启动 MySQL & Redis ----
docker compose up -d mysql redis

# ---- 2. 启动后端 (新终端) ----
# 使用 dev profile：show-sql + debug logging
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# ---- 3. 启动前端 (新终端) ----
cd frontend
npm install
npm run dev

# 访问: http://localhost:5173（Vite 自动代理 /api 到 8080）
```

### 测试账号

| 角色   | 用户名      | 密码      |
|------|----------|---------|
| 管理员  | admin    | user123 |
| 普通用户 | testuser | user123 |

## 项目结构

```
vaccine-appointment-system/
├── frontend/                    # Vue 3 前端源码
│   ├── src/
│   │   ├── views/               # 页面组件
│   │   │   ├── HomeView.vue             # 首页（疫苗列表）
│   │   │   ├── UserDashboardView.vue    # 用户预约管理
│   │   │   ├── UserProfileView.vue      # 个人信息
│   │   │   ├── AdminLoginView.vue       # 管理员登录
│   │   │   ├── AdminDashboardView.vue   # 预约审核工作台
│   │   │   ├── AdminVaccineView.vue     # 疫苗库存管理
│   │   │   └── AdminUsersView.vue       # 用户管理
│   │   ├── components/          # 通用组件
│   │   ├── stores/              # Pinia 状态管理
│   │   ├── services/            # Axios API 封装
│   │   └── router/              # Vue Router 路由
│   ├── vite.config.ts           # Vite 构建配置
│   └── package.json
├── src/main/java/.../           # Spring Boot 后端源码
│   ├── entity/                  # JPA 实体
│   ├── enums/                   # 枚举 + JPA Converter
│   ├── repository/              # Spring Data JPA
│   ├── service/                 # 业务接口
│   │   └── impl/                # 业务实现
│   ├── controller/              # REST 控制器
│   ├── config/                  # Spring Security / JWT / WebMvc
│   └── dto/                     # 数据传输对象
├── src/main/resources/
│   ├── application.yml          # 基础配置（环境变量占位符）
│   ├── application-dev.yml      # 开发环境配置
│   └── application-prod.yml     # 生产环境配置
├── docker/
│   ├── Dockerfile.backend       # 后端多阶段构建
│   ├── Dockerfile.frontend      # 前端多阶段构建
│   ├── nginx/nginx.conf         # Nginx SPA + 反向代理
│   └── mysql/init.sql           # MySQL 初始化脚本
├── database/
│   └── init.sql                 # 完整数据库初始化（含测试数据）
├── docker-compose.yml           # 全栈一键编排
├── .env.example                 # 环境变量模板
└── pom.xml
```

## 环境变量

所有配置通过环境变量注入，无硬编码。复制 `.env.example` 为 `.env` 按需修改：

| 变量                       | 默认值                      | 说明                    |
|--------------------------|--------------------------|-----------------------|
| `MYSQL_ROOT_PASSWORD`    | `root`                   | MySQL root 密码         |
| `MYSQL_DATABASE`         | `vaccine_appointment_db` | 数据库名                  |
| `MYSQL_USER`             | `vaccine_user`           | 应用数据库用户               |
| `MYSQL_PASSWORD`         | `vaccine_pass`           | 应用数据库密码               |
| `DB_HOST`                | `mysql`                  | 后端连接数据库主机             |
| `DB_PORT`                | `3306`                   | 数据库端口                 |
| `REDIS_HOST`             | `redis`                  | Redis 主机              |
| `REDIS_PORT`             | `6379`                   | Redis 端口              |
| `REDIS_PASSWORD`         | (空)                      | Redis 密码              |
| `JWT_SECRET`             | (dev 默认值)                | JWT 签名密钥（生产必改）        |
| `SPRING_PROFILES_ACTIVE` | `prod`                   | Spring 配置文件（dev/prod） |
| `APP_PORT`               | `8080`                   | 后端端口                  |
| `UPLOAD_DIR`             | `/app/uploads`           | 文件上传目录                |
| `FRONTEND_PORT`          | `80`                     | 前端 Nginx 端口           |

## 核心业务设计

### 预约状态机

```
已预约 ──管理员完成接种──▶ 已完成（终态）
已预约 ──超时未到场──▶ 未到场（可补录接种记录，状态不变）
已预约 ──用户/管理员取消──▶ 已取消（终态，恢复库存）
```

### 审计日志

`appointment_log` 表记录每一次状态变更，包含操作人、旧状态、新状态、变更原因、时间戳。支持按预约 ID 查询完整操作历史。

### 定时任务

`@Scheduled(cron = "0 */5 * * * *")` 每 5 分钟检测已过期且无接种记录的预约，自动标记为"未到场"。

### 统计数据

`GET /api/statistics`（管理员）返回接种率、预约成功率等指标，用于管理仪表盘。

## 开发模式 vs 生产模式

| 维度     | 开发模式                                | 生产模式                     |
|--------|-------------------------------------|--------------------------|
| 前端运行   | `npm run dev` (Vite HMR)            | Nginx 静态服务               |
| 后端运行   | `mvn spring-boot:run` (dev profile) | JRE Docker 容器            |
| SQL 日志 | 开启 `show-sql`                       | 关闭                       |
| 缓存     | 本地 Caffeine                         | Redis                    |
| Gzip   | 关闭                                  | Nginx + Spring Boot 双重压缩 |
| 前端端口   | 5173 (Vite 代理 /api)                 | 80 (Nginx 反向代理)          |
| 热重载    | 前后端均支持                              | 无                        |

## API 概览

| 模块     | 端点                                        | 权限     |
|--------|-------------------------------------------|--------|
| 认证     | `POST /api/auth/login`                    | 公开     |
| 疫苗     | `GET/POST/PUT/DELETE /api/vaccines`       | 读写分离   |
| 预约     | `GET/POST /api/appointments`              | 用户     |
| 预约     | `POST /api/appointments/{id}/cancel`      | 用户/管理员 |
| 预约     | `POST /api/appointments/{id}/complete`    | 管理员    |
| 预约     | `POST /api/appointments/{id}/late-record` | 管理员    |
| 预约     | `GET /api/appointments/{id}/logs`         | 管理员    |
| 接种记录   | `GET/POST/PUT /api/vaccination-records`   | 管理员    |
| 统计     | `GET /api/statistics`                     | 管理员    |
| API 文档 | `/swagger-ui/index.html`                  | 开发模式   |

## 毕业设计 / 面试展示要点

### 工程化亮点

- **Docker Compose 一键编排** — 评审老师/面试官可直接 `docker compose up -d` 跑起全栈
- **多阶段 Docker 构建** — 构建阶段与运行阶段分离，镜像体积最小化
- **环境变量统一管理** — `.env.example` 模板，无硬编码，符合 12-Factor App
- **Spring Profile 分离** — dev/prod 配置独立，开发调试友好，生产安全
- **JPA AttributeConverter** — 数据库 INTEGER 存储，Java 强类型枚举，状态机约束
- **审计日志** — `appointment_log` 表完整追踪状态变更，企业级数据追溯
- **Spring Security + JWT** — 无状态认证，角色权限控制（USER / ADMIN）
- **Redis 缓存 + 分布式锁** — 高并发下库存扣减安全
- **Git 提交规范** — Conventional Commits（feat/fix/style/docs/chore）

### 答辩/面试可讲的改进方向

1. 为什么 INTEGER 存状态而不是 VARCHAR？— 存储效率 + 索引性能 + JPA Converter 解耦
2. 为什么用 JWT 而不是 Session？— 无状态、水平扩展友好、适合前后端分离
3. 为什么多阶段 Docker 构建？— 减小镜像体积（Maven 构建产物不进入运行时镜像）
4. 为什么定时任务用 @Scheduled 而不是 Quartz？— 场景简单，Spring 内置足够，避免过度设计
5. 审计日志为什么单独建表而不是在 appointment 加字段？— 单一职责，支持多次变更历史追溯

## 故障排查

### Docker 启动后前端 502

后端首次启动需要约 30-60 秒（Maven 依赖下载 + Spring Boot 启动 + 数据库建表）。等待后刷新即可。

### 数据库连接失败

检查 `.env` 中 `DB_HOST`：Docker 内为 `mysql`，本地开发为 `localhost`。

### 端口冲突

修改 `.env` 中的 `FRONTEND_PORT` / `APP_PORT` / `MYSQL_PORT` / `REDIS_PORT`。

## License

MIT
