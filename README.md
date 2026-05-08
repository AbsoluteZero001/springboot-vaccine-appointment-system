# Vaccine Appointment System | 疫苗预约系统

> 守护健康，疫苗先行 — 基于 Spring Boot 3 + Vue 3 的全栈疫苗预约管理平台
> Protecting Health, Vaccination First — A full-stack online vaccine appointment management platform built with Spring Boot 3 + Vue 3

---

## Table of Contents | 目录

- [Tech Stack | 技术栈](#tech-stack--技术栈)
- [Features | 功能特性](#features--功能特性)
- [Getting Started | 快速开始](#getting-started--快速开始)
- [API Overview | 接口概览](#api-overview--接口概览)
- [Project Structure | 项目结构](#project-structure--项目结构)
- [Database | 数据库](#database--数据库)
- [Build & Deploy | 构建与部署](#build--deploy--构建与部署)
- [License | 许可证](#license--许可证)
- [Contact | 联系我](#-contact-me--联系我)
- [Author | 作者信息](#-author--作者信息)
- [Copyright | 著作权声明](#-copyright--著作权声明)
- [Project Notes | 项目说明](#-项目说明补充)

---

## Tech Stack | 技术栈

| Layer 层级 | Technology 技术 | Version 版本 |
|-----------|----------------|-------------|
| Backend 后端框架 | Spring Boot | 3.1.5 |
| Security 安全认证 | Spring Security + JWT (JJWT) | 0.12.5 |
| ORM | Spring Data JPA / Hibernate | — |
| Database 数据库 | MySQL | 8.0+ |
| Cache 缓存 / 分布式锁 | Redis | 7+ (可选 / optional) |
| API Docs 接口文档 | SpringDoc OpenAPI (Swagger UI) | 2.2 |
| Frontend 前端框架 | Vue 3 (Composition API + TypeScript) | 3.4 |
| Build Tool 构建工具 | Vite | 5.x |
| State Management 状态管理 | Pinia | 2.x |
| Router 路由 | Vue Router | 4.x |
| HTTP Client | Axios | 1.x |
| Build 构建 | Maven | 3.9+ |
| Deployment 部署 | Docker & Docker Compose | — |

---

## Features | 功能特性

### User-Facing | 用户端

| Feature 功能 | Description 说明 |
|-------------|-----------------|
| Register / Login 注册登录 | JWT 令牌认证，支持防暴力破解（5 次失败触发逐级冻结 30s → 60s） |
| Vaccine Browsing 疫苗浏览 | 按分类筛选、关键词搜索，46 种疫苗含品牌、剂量、工艺、适用人群 |
| Vaccine Details 疫苗详情 | 品牌、制造商、剂次、接种途径、年龄范围、目标疾病、免疫程序 |
| Online Booking 在线预约 | 选择日期 + 时段（自动区分工作日/周六半天/周日休息） |
| My Appointments 我的预约 | 查看 / 取消预约，状态流转：待确认 → 已确认 → 已完成 / 已取消 |
| Vaccination Records 接种记录 | 查看个人历史接种记录 |
| News Carousel 资讯轮播 | 首页 5 屏疫苗热点资讯轮播 |

### Admin-Facing | 管理端

| Feature 功能 | Description 说明 |
|-------------|-----------------|
| Appointment Management 预约管理 | 按状态筛选（待确认/已确认/已完成/已取消），支持确认、取消、完成操作 |
| Vaccine CRUD 疫苗管理 | 上架/下架、添加、编辑、删除、库存调整、图片上传 |
| User Management 用户管理 | 用户列表、启用/禁用、删除 |
| Vaccination Records 接种记录 | 创建记录、查看、标记为已接种 |
| Dashboard 数据概览 | 各状态预约数量统计 |

### Infrastructure | 基础设施

| Feature 功能 | Description 说明 |
|-------------|-----------------|
| JWT Stateless Auth 无状态认证 | 每次请求携带 `Authorization: Bearer <token>` |
| BCrypt Encryption 密码加密 | 所有密码 BCrypt 哈希存储 |
| Brute-Force Protection 防暴力破解 | 登录失败 5 次触发逐级冻结 |
| Redis Distributed Lock 分布式锁 | 预约操作防并发超卖，Redis 不可用时自动降级为本地锁 |
| CORS Protection 跨域防护 | 仅允许配置的源（localhost:5173 / 3000 / 8080） |
| Role-Based Access 角色权限 | USER / ADMIN / SUPER_ADMIN 三级隔离 |
| File Upload 文件上传 | 疫苗图片上传至 `./uploads/` |

---

## Getting Started | 快速开始

### Prerequisites | 环境要求

- **JDK 17+**
- **MySQL 8.0+**
- **Node.js 18+**（仅开发模式需要 / dev mode only）
- **Redis 7+**（可选 / optional，无 Redis 时自动降级为本地锁）

### Database Setup | 数据库初始化

```sql
CREATE DATABASE vaccine_appointment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

应用首次启动时会自动执行 `schema.sql` 和 `data.sql` 创建表结构并插入种子数据。
Tables are auto-created on first boot via `schema.sql` and `data.sql` with seed data.

---

### Method 1: IDEA One-Click | 方式一：IDEA 一键启动（推荐 / Recommended）

1. 用 IntelliJ IDEA 打开项目根目录
2. 右上角运行配置下拉菜单选择 **"Full-Stack App"**
3. 点击运行按钮

> 后端 `:8080` 和前端 `:5173` 同时启动，前端通过 Vite 代理自动转发 `/api` 请求到后端。

---

### Method 2: Startup Script | 方式二：终端启动脚本

**Windows：**
```batch
start-all.bat
```
**Linux / Mac：**
```bash
chmod +x start-all.sh && ./start-all.sh
```

Interactive menu options | 交互菜单选项：

| Option 选项 | Description 说明 |
|------------|-----------------|
| 1 | Full-stack dev — Backend `:8080` + Vite `:5173` (hot reload) 全栈开发模式 |
| 2 | Frontend only — Vite `:5173` (requires backend running) 仅启动前端 |
| 3 | Single service — Build frontend + start backend on `:8080` 构建前端并单端口部署 |
| 4 | Build only — `npm run build` output to `static/` 仅构建前端 |
| 5 | Stop all services 停止全部服务 |

---

### Method 3: Docker Compose | 方式三：Docker Compose

```bash
docker-compose up -d
```

启动三个容器 / Starts three containers：

| Container 容器 | Service 服务 | Description 说明 |
|---------------|-------------|-----------------|
| `vaccine-mysql` | MySQL 8.0 | Database 数据库 |
| `vaccine-redis` | Redis 7 | Cache 缓存 |
| `vaccine-app` | Spring Boot App | Application (port 8080) 应用 |

---

### Method 4: Single JAR | 方式四：单体 JAR 部署

```bash
# Build single JAR with frontend included
# 构建含前端的单体 JAR
mvn package -Pfrontend -DskipTests

# Run 运行
java -jar target/vaccine-appointment-system-0.0.1-SNAPSHOT.jar
```

访问 `http://localhost:8080`，前后端均由 Spring Boot 提供服务。
Frontend and backend served together from a single port.

---

### Configuration | 配置

编辑 `src/main/resources/application.yml` 或创建 `application-local.yml`（不纳入 Git 跟踪）：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vaccine_appointment_db
    username: root
    password: your-password
  redis:
    host: localhost
    port: 6379

app:
  jwt:
    secret: vaccine-appointment-system-secret-key-2024
    expiration: 86400000   # 24 hours / 小时
```

### Access | 访问地址

| Service 服务 | URL |
|-------------|-----|
| Application 应用 | http://localhost:8080 |
| Dev Frontend 开发前端 (Vite) | http://localhost:5173 |
| Swagger UI 接口文档 | http://localhost:8080/swagger-ui.html |
| API Docs 文档 | http://localhost:8080/api-docs |

### Test Accounts | 测试账号

| Role 角色 | Username 用户名 | Password 密码 |
|-----------|---------------|--------------|
| User 普通用户 | testuser | user123 |
| User 普通用户 | john_doe | user123 |
| User 普通用户 | lisa_wang | user123 |
| Admin 管理员 | admin | user123 |

> 种子数据包含 11 个用户、2 个管理员、46 种疫苗。
> Seed data includes 11 users, 2 admins, 46 vaccines.

---

## API Overview | 接口概览

### Public Endpoints | 公开接口

| Method | Path | Description 说明 |
|--------|------|-----------------|
| POST | `/api/auth/login` | Unified login (user + admin) 统一登录入口 |
| POST | `/api/users/register` | User registration 用户注册 |
| GET | `/api/vaccines` | List all vaccines 疫苗列表 |
| GET | `/api/vaccines/available` | Available vaccines 可用疫苗 |
| GET | `/api/vaccines/search?name=` | Search vaccines 搜索疫苗 |
| GET | `/api/vaccines/{id}` | Vaccine details 疫苗详情 |

### Authenticated Endpoints | 需认证接口 (User / Admin)

| Method | Path | Description 说明 |
|--------|------|-----------------|
| POST | `/api/appointments` | Create appointment 创建预约 |
| GET | `/api/appointments/user/{userId}` | User appointments 用户预约 |
| GET | `/api/appointments/{id}` | Appointment details 预约详情 |
| POST | `/api/appointments/{id}/cancel` | Cancel appointment 取消预约 |
| POST | `/api/appointments/{id}/confirm` | Confirm appointment 确认预约 |
| POST | `/api/appointments/{id}/complete` | Complete appointment 完成预约 |
| GET / PUT | `/api/users/{id}` | Get / Update user 获取 / 更新用户 |
| POST | `/api/vaccination-records` | Create record 创建接种记录 |
| GET | `/api/vaccination-records/user/{userId}` | User records 用户接种记录 |
| GET | `/api/vaccination-records/status/{status}` | Records by status 按状态查询 |

### Admin-Only Endpoints | 管理员接口

| Method | Path | Description 说明 |
|--------|------|-----------------|
| POST / PUT / DELETE | `/api/vaccines/**` | Manage vaccines 管理疫苗 |
| PATCH | `/api/vaccines/{id}/stock` | Update stock 更新库存 |
| PATCH | `/api/vaccines/{id}/availability` | Toggle availability 切换上架 |
| POST | `/api/vaccines/{id}/upload-image` | Upload vaccine image 上传图片 |
| GET | `/api/admins` | List all admins 管理员列表 |
| GET / PUT / DELETE | `/api/admins/{id}` | Manage admin 管理管理员 |
| POST | `/api/appointments/{id}/cancel/admin` | Force-cancel appointment 强制取消预约 |

---

## Project Structure | 项目结构

```
vaccine-appointment-system/
├── frontend/                         # Vue 3 前端工程
│   ├── src/
│   │   ├── views/                    # 7 个页面视图
│   │   │   ├── HomeView.vue          #   首页（登录/注册/轮播）
│   │   │   ├── AdminLoginView.vue    #   管理员登录
│   │   │   ├── UserDashboardView.vue #   用户疫苗浏览与预约
│   │   │   ├── UserProfileView.vue   #   我的预约与接种记录
│   │   │   ├── AdminDashboardView.vue#   预约管理控制台
│   │   │   ├── AdminVaccineView.vue  #   疫苗 CRUD 管理
│   │   │   └── AdminUsersView.vue    #   用户管理
│   │   ├── components/               # 9 个可复用组件
│   │   │   ├── SiteHeader.vue        #   全局导航栏
│   │   │   ├── SiteFooter.vue        #   页脚
│   │   │   ├── AlertMessage.vue      #   消息提示
│   │   │   ├── LoadingOverlay.vue    #   加载遮罩
│   │   │   ├── NewsCarousel.vue      #   疫苗资讯轮播
│   │   │   ├── LoginMessage.vue      #   登录防暴力破解提示
│   │   │   ├── VaccineCard.vue       #   疫苗卡片
│   │   │   ├── AppointmentModal.vue  #   预约日期时段选择
│   │   │   └── VaccineEditModal.vue  #   疫苗编辑表单
│   │   ├── router/index.ts           # 路由配置 + 导航守卫
│   │   ├── stores/auth.ts            # Pinia 认证状态
│   │   ├── services/api.ts           # Axios API 封装
│   │   └── styles/global.css         # 全局样式
│   ├── vite.config.ts                # Vite 配置（代理 + 别名 + 构建）
│   ├── tsconfig.json
│   └── package.json
├── src/main/
│   ├── java/com/springboot/vaccineappointmentsystem/
│   │   ├── config/                   # SecurityConfig, JwtTokenProvider, RedisConfig, WebMvcConfig ...
│   │   ├── controller/               # AuthController, UserController, VaccineController ...
│   │   ├── service/                  # 业务接口 + impl 实现
│   │   ├── repository/               # JPA 数据访问层
│   │   ├── entity/                   # User, Admin, Vaccine, Appointment, VaccinationRecord
│   │   ├── dto/                      # ApiResponse 统一响应体
│   │   └── exception/               # GlobalExceptionHandler 全局异常处理
│   └── resources/
│       ├── application.yml           # 主配置（MySQL / Redis / JWT）
│       ├── schema.sql                # 建表 DDL
│       ├── data.sql                  # 46 种疫苗 + 测试用户种子数据
│       └── static/                   # Vue build 输出（开发模式不使用）
├── database/
│   └── init.sql                      # 独立数据库初始化脚本
├── .idea/runConfigurations/          # IDEA 一键启动配置
│   ├── SpringBoot_Backend.xml        #   后端 Maven 启动配置
│   ├── Vue_Frontend.xml              #   前端 npm dev 配置
│   └── Full-Stack_App.xml            #   复合配置（同时启动前后端）
├── start-all.bat / start-all.sh      # 全栈一键启动脚本
├── docker-compose.yml                # Docker 编排（MySQL + Redis + App）
├── Dockerfile                        # 多阶段构建
└── pom.xml                           # Maven（含 frontend profile）
```

---

## Vite Proxy | 开发代理

Vite 开发服务器自动将以下请求代理到后端 `http://localhost:8080`：

| Prefix 前缀 | Target 目标 | Purpose 用途 |
|------------|------------|-------------|
| `/api` | `http://localhost:8080` | REST API 请求 |
| `/uploads` | `http://localhost:8080` | 疫苗图片资源 |

> 开发时前端 `:5173` 直接发起 `/api/xxx` 请求，无需处理 CORS，无需配置后端地址。

---

## Database | 数据库

Tables are auto-created by Hibernate (`ddl-auto: update`) on first boot.
数据表由 Hibernate 自动创建（`ddl-auto: update`）：

| Table 表名 | Description 说明 |
|-----------|-----------------|
| `vaccine` | Vaccine inventory and metadata | 疫苗库存与元数据 |
| `appointment` | Appointment records (0=pending, 1=confirmed, 2=completed, 3=cancelled) | 预约记录 |
| `user` | User accounts (implements UserDetails) | 用户账户 |
| `admin` | Admin accounts (implements UserDetails) | 管理员账户 |
| `vaccination_record` | Vaccination history | 接种记录 |

### Vaccine Seed Data | 疫苗种子数据（46 种）

| Category 分类 | Count 数量 | Examples 示例 |
|-------------|-----------|--------------|
| Hepatitis B 乙肝疫苗 | 7 | 重组乙型肝炎（CHO/汉逊酵母/酿酒酵母），10μg/20μg/60μg |
| HPV 疫苗 | 3 | 九价 Gardasil 9、四价 Gardasil、二价 馨可宁 |
| Influenza 流感疫苗 | 3 | 四价流感（裂解）、三价流感（裂解/亚单位） |
| Pneumonia 肺炎疫苗 | 3 | 23 价多糖、13 价结合 |
| Others 其他 | 30 | 带状疱疹、新冠加强、狂犬、水痘、甲肝、百白破、乙脑、流脑、卡介苗、脊灰、麻腮风、Hib、轮状病毒、EV71、出血热、钩端螺旋体、霍乱、黄热病、登革热、腮腺炎、风疹 |

---

## Build & Deploy | 构建与部署

```bash
# ============================================
# Development Mode 开发模式（前后端分离）
# ============================================

# Terminal 1: Backend 后端
mvn spring-boot:run                              # Backend :8080

# Terminal 2: Frontend 前端
cd frontend && npm run dev                       # Frontend :5173 (hot reload)

# Or use the unified script 或使用统一脚本：
./start-all.sh                                   # 菜单选择 1

# ============================================
# Single Service 单体部署
# ============================================

# Build frontend + package into single JAR
# 构建前端并打包为单体 JAR
mvn package -Pfrontend -DskipTests

# Run 运行
java -jar target/vaccine-appointment-system-0.0.1-SNAPSHOT.jar

# ============================================
# Docker 容器化
# ============================================

# Docker Compose (MySQL + Redis + App)
docker-compose up -d

# Standalone Docker build
docker build -t vaccine-appointment-system .
```

---

## License | 许可证

MIT

---

## 📬 Contact Me | 联系我

如有问题、建议或合作交流，欢迎通过以下方式联系我：

[![Email Me](https://img.shields.io/badge/Email-absolutezero.cold200@simplelogin.com-blue?style=for-the-badge)](mailto:absolutezero.cold200@simplelogin.com)

---

## 👤 Author | 作者信息

[https://github.com/AbsoluteZero001](https://github.com/AbsoluteZero001)

本项目由本人独立开发与维护，主要用于 Spring Boot + Vue 3 + MySQL + Redis 全栈学习与实践。
This project is independently developed and maintained for full-stack learning purposes — Spring Boot + Vue 3 + MySQL + Redis practice.

---

## ⚖️ Copyright | 著作权声明

© 2026 All Rights Reserved.

本项目为原创学习项目，仅用于学习交流与技术研究目的。
未经授权禁止用于商业用途、二次发布或剽窃行为。

如有引用或使用需求，请提前联系作者获得授权。

This is an original learning project intended solely for educational exchange and technical research.
Unauthorized commercial use, redistribution, or plagiarism is prohibited.
Please contact the author in advance for permission if you wish to reference or use this project.

---

## 📌 项目说明补充

- 本项目遵循开源学习与技术交流原则 | This project follows the principles of open-source learning and technical exchange
- 不涉及任何真实业务数据 | Does not involve any real business data
- 数据均为模拟测试数据 | All data is simulated test data
- 前端同时提供 Vue 3 SPA（`frontend/`）和传统 HTML/CSS/JS（`src/main/resources/static/`）两套实现
- 欢迎 Fork 与学习 | Feel free to fork and learn from this project

> **Note | 说明:** This README will be updated as the project evolves. | 本 README 将随项目完善持续更新。
