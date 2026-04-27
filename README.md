# Vaccine Appointment System | 疫苗预约系统

> 守护健康，疫苗先行 — 基于 Spring Boot 3 的在线疫苗预约管理平台  
> Protecting Health, Vaccination First — An online vaccine appointment management platform built with Spring Boot 3

---

## 📋 Table of Contents | 目录

- [Tech Stack | 技术栈](#tech-stack--技术栈)
- [Features | 功能特性](#features--功能特性)
- [Getting Started | 快速开始](#getting-started--快速开始)
- [API Overview | 接口概览](#api-overview--接口概览)
- [Project Structure | 项目结构](#project-structure--项目结构)
- [Database | 数据库](#database--数据库)
- [License | 许可证](#license--许可证)
- [Contact | 联系我](#-contact-me--联系我)
- [Author | 作者信息](#-author--作者信息)
- [Copyright | 著作权声明](#-copyright--著作权声明)
- [Project Notes | 项目说明](#-项目说明补充)

---

## Tech Stack | 技术栈

| Layer | Technology |
|-------|-----------|
| Backend 后端 | Spring Boot 3.1.5, Java 17 |
| Security 安全 | Spring Security, JWT (jjwt 0.12.5) |
| Database 数据库 | MySQL 8.0 (JPA / Hibernate) |
| Cache 缓存 | Redis 7, Spring Cache |
| API Docs 接口文档 | SpringDoc OpenAPI 2.2 (Swagger UI) |
| Build 构建 | Maven |
| Deployment 部署 | Docker & Docker Compose |
| Frontend 前端 | Static HTML / CSS / JS (served by Spring Boot) |

---

## Features | 功能特性

### Authentication & Authorization | 认证与授权

- JWT-based stateless authentication | 基于 JWT 的无状态认证
- Dual role system: **User** (`ROLE_USER`) and **Admin** (`ROLE_ADMIN`) | 双角色系统：用户和管理员
- User registration & login | 用户注册与登录
- Admin login | 管理员登录
- Password encryption via BCrypt | 密码 BCrypt 加密

### Vaccine Management | 疫苗管理

- Browse all vaccines with search by name | 浏览全部疫苗并支持按名称搜索
- Filter available vaccines | 筛选可用疫苗
- Admin CRUD: create, update, delete vaccines | 管理员增删改查疫苗
- Stock quantity management | 库存数量管理
- Availability toggle | 上架/下架切换
- Vaccine image upload support | 疫苗图片上传
- Rich vaccine metadata: brand, manufacturer, category, dosage, technique, age range, target disease, schedule info | 丰富的疫苗元数据：品牌、生产商、分类、剂型、技术、年龄范围、目标疾病、接种安排

### Appointment System | 预约系统

- Create appointments (user selects vaccine + time slot) | 创建预约（选择疫苗与时间段）
- View appointments by user or vaccine | 按用户或疫苗查看预约
- Cancel appointments (user) or force-cancel (admin) | 取消预约（用户）或强制取消（管理员）
- Confirm pending appointments | 确认待处理预约
- Complete appointments | 完成预约
- Status lifecycle: pending → confirmed → completed / cancelled | 状态流转：待处理 → 已确认 → 已完成 / 已取消
- **Redis distributed locks** to prevent duplicate appointments | **Redis 分布式锁**防止重复预约

### Vaccination Records | 接种记录

- Create records from completed appointments | 从完成的预约生成接种记录
- Track vaccination time, status, and notes | 记录接种时间、状态与备注
- Mark records as administered | 标记为已接种
- Filter by user, vaccine, or status | 按用户、疫苗或状态筛选
- Status: scheduled → administered | 状态：待接种 → 已接种

### Admin Dashboard | 管理后台

- Admin login | 管理员登录
- Manage vaccines (CRUD, stock, availability, images) | 管理疫苗（增删改查、库存、上下架、图片）
- View all users | 查看所有用户
- Cancel any appointment | 取消任意预约
- Upload vaccine images | 上传疫苗图片

### Frontend Pages | 前端页面

- **Homepage** — vaccine info, statistics, appointment guide | 首页 — 疫苗资讯、统计数据、预约指南
- **User Dashboard** — browse vaccines, book appointments, view records | 用户面板 — 浏览疫苗、预约、查看记录
- **User Profile** — edit personal info | 个人中心 — 编辑个人信息
- **Admin Dashboard** — system overview and management | 管理面板 — 系统概览与管理
- **Admin Vaccine Management** — full vaccine CRUD | 疫苗管理 — 完整疫苗管理界面
- **Admin Users** — user list management | 用户管理 — 用户列表管理

### Infrastructure | 基础设施

- CORS configured for local development | 配置 CORS 支持本地前端开发
- Redis cache TTL: 10 minutes | Redis 缓存时间：10 分钟
- File upload for vaccine images (stored in `./uploads`) | 疫苗图片上传（存储于 `./uploads`）
- Swagger UI at `/swagger-ui.html` | Swagger 接口文档
- OpenAPI docs at `/api-docs` | OpenAPI 文档

---

## Getting Started | 快速开始

### Prerequisites | 环境要求

- Java 17+
- Maven 3.9+
- MySQL 8.0
- Redis 7

### Configuration | 配置

编辑 `src/main/resources/application.yml` 或创建 `application-local.yml`（不纳入 Git 跟踪）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/vaccine_appointment_db
    username: root
    password: your-password
  data:
    redis:
      host: localhost
      port: 6379
```

### Run with Maven | 使用 Maven 启动

```bash
# 先启动 MySQL 和 Redis，然后：
./mvnw spring-boot:run

# 或使用本地配置：
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Run with Docker | 使用 Docker 启动

```bash
docker-compose up -d
```

启动三个容器：

| Container 容器 | Service 服务 | Description 说明 |
|---------------|-------------|-----------------|
| `vaccine-mysql` | MySQL 8.0 | Database 数据库 |
| `vaccine-redis` | Redis 7 | Cache 缓存 |
| `vaccine-app` | Spring Boot App | Application (port 8080) 应用 |

### Access | 访问地址

- Application 应用: http://localhost:8080
- Swagger UI 接口文档: http://localhost:8080/swagger-ui.html
- API docs 文档: http://localhost:8080/api-docs

---

## API Overview | 接口概览

### Public Endpoints | 公开接口

| Method | Path | Description 说明 |
|--------|------|-----------------|
| POST | `/api/auth/login` | Admin login 管理员登录 |
| POST | `/api/users/login` | User login 用户登录 |
| POST | `/api/users/register` | User registration 用户注册 |
| GET | `/api/vaccines` | List all vaccines 疫苗列表 |
| GET | `/api/vaccines/available` | Available vaccines 可用疫苗 |
| GET | `/api/vaccines/search?name=` | Search vaccines 搜索疫苗 |
| GET | `/api/vaccines/{id}` | Vaccine details 疫苗详情 |

### Authenticated Endpoints | 需认证接口 (User/Admin)

| Method | Path | Description 说明 |
|--------|------|-----------------|
| POST | `/api/appointments` | Create appointment 创建预约 |
| GET | `/api/appointments/user/{userId}` | User appointments 用户预约 |
| POST | `/api/appointments/{id}/cancel` | Cancel appointment 取消预约 |
| POST | `/api/appointments/{id}/confirm` | Confirm appointment 确认预约 |
| POST | `/api/appointments/{id}/complete` | Complete appointment 完成预约 |
| GET/PUT | `/api/users/{id}` | Get/update user 获取/更新用户 |
| POST | `/api/vaccination-records` | Create record 创建接种记录 |
| GET | `/api/vaccination-records/user/{userId}` | User records 用户接种记录 |

### Admin-Only Endpoints | 管理员接口

| Method | Path | Description 说明 |
|--------|------|-----------------|
| POST/PUT/DELETE | `/api/vaccines/**` | Manage vaccines 管理疫苗 |
| PATCH | `/api/vaccines/{id}/stock` | Update stock 更新库存 |
| PATCH | `/api/vaccines/{id}/availability` | Toggle availability 切换上架 |
| POST | `/api/vaccines/{id}/upload-image` | Upload vaccine image 上传图片 |
| GET | `/api/admins` | List all admins 管理员列表 |
| POST | `/api/appointments/{id}/cancel/admin` | Force-cancel appointment 强制取消 |

---

## Project Structure | 项目结构

```
vaccine-appointment-system/
├── src/main/java/com/springboot/vaccineappointmentsystem/
│   ├── config/              # Security, JWT, Redis, WebMVC configuration | 配置类
│   ├── controller/          # REST controllers | 控制器
│   ├── dto/                 # API response DTO | 响应对象
│   ├── entity/              # JPA entities | 实体类
│   ├── exception/           # Global exception handler | 全局异常处理
│   ├── repository/          # Spring Data JPA repositories | 数据访问层
│   ├── service/             # Business logic layer | 业务逻辑层
│   │   └── impl/            # Service implementations | 业务实现类
│   └── VaccineAppointmentSystemApplication.java
│
├── src/main/resources/
│   ├── static/              # Frontend (HTML, CSS, JS) | 前端静态资源
│   ├── application.yml      # Main configuration | 主配置
│   └── application-local.yml # Local overrides (gitignored) | 本地配置
│
├── docker-compose.yml       # Docker orchestration | Docker 编排
├── Dockerfile               # Docker image build | Docker 镜像构建
├── pom.xml                  # Maven dependencies | Maven 依赖
└── uploads/                 # Uploaded images directory | 上传图片目录
```

---

## Database | 数据库

Tables are auto-created by Hibernate (`ddl-auto: update`) | 数据表由 Hibernate 自动创建：

| Table 表名 | Description 说明 |
|-----------|-----------------|
| `vaccine` | Vaccine inventory and metadata | 疫苗库存与元数据 |
| `appointment` | Appointment records (0=pending, 1=confirmed, 2=completed, 3=cancelled) | 预约记录 |
| `user` | User accounts | 用户账户 |
| `admin` | Admin accounts | 管理员账户 |
| `vaccination_record` | Vaccination history | 接种记录 |

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

本项目由本人独立开发与维护，主要用于 Spring Boot + MySQL + Redis 学习与实践。  
This project is independently developed and maintained for learning purposes — Spring Boot + MySQL + Redis practice.

---

## ⚖️ Copyright | 著作权声明

© 2026 All Rights Reserved.

本项目为原创学习项目，仅用于学习交流与技术研究目的。  
未经授权禁止用于商业用途、二次发布或剽窃行为。

如有引用或使用需求，请提前联系作者获得授权。

This is an original learning project intended solely for educational exchange and technical research.  
Unauthorized commercial use, redistribution, or plagiarism is prohibited.  
Please contact the author in advance for permission if you wish to引用 or use this project.

---

## 📌 项目说明补充

- 本项目遵循开源学习与技术交流原则 | This project follows the principles of open-source learning and technical exchange
- 不涉及任何真实业务数据 | Does not involve any real business data
- 数据均为模拟测试数据 | All data is simulated test data
- 欢迎 Fork 与学习 | Feel free to fork and learn from this project

> **Note | 说明:** This README will be updated as the project evolves. | 本 README 将随项目完善持续更新。
