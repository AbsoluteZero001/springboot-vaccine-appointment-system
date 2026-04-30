# 疫苗预约系统 (Vaccine Appointment System)

基于 **Spring Boot 3 + JPA + MySQL + Redis** 的疫苗预约管理系统，开箱即用，内置22种真实疫苗测试数据。

## 快速开始

### 前置条件

- JDK 17+
- MySQL 8.0+
- Redis 7+（可选，仅用作缓存）

### 方式一：Docker Compose（推荐）

```bash
docker-compose up -d
```

访问 http://localhost:8080

### 方式二：手动启动

1. 创建数据库

```sql
CREATE DATABASE vaccine_appointment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 启动应用

```bash
# Windows
start.bat

# Linux/Mac
chmod +x start.sh && ./start.sh

# 或 Maven 直接启动
mvn spring-boot:run -DskipTests
```

3. 访问 http://localhost:8080

---

## 测试账号

| 角色   | 用户名       | 密码      |
|------|-----------|---------|
| 普通用户 | testuser  | user123 |
| 普通用户 | john_doe  | user123 |
| 普通用户 | lisa_wang | user123 |
| 管理员  | admin     | user123 |

> 管理员账号在前台首页登录后，访问 `admin-vaccine.html` 进入管理后台。

---

## 功能特性

### 用户端

- 首页：疫苗热点资讯轮播 + 登录/注册
- 疫苗列表：22种真实疫苗，按分类筛选、搜索
- 疫苗卡片：品牌、剂量、制作工艺、适用人群、库存状态
- 在线预约：选择日期 + 时段（区分工作日/周末/节假日）
- 我的预约：查看/取消预约记录

### 管理端

- 疫苗管理：上架、下架、添加、编辑、删除
- 完整的 CRUD 支持，可修改疫苗所有参数
- 用户管理

### 疫苗数据（22种测试数据）

| 分类    | 疫苗示例                         | 品牌                  | 剂量             | 工艺              |
|-------|------------------------------|---------------------|----------------|-----------------|
| 乙肝疫苗  | 重组乙型肝炎疫苗（CHO/汉逊酵母/酿酒酵母）      | 华北金坦/大连汉信/Engerix-B | 10μg/20μg/60μg | CHO细胞/汉逊酵母/酿酒酵母 |
| HPV疫苗 | 九价/四价/二价 HPV疫苗               | Gardasil 9/馨可宁      | 0.5ml          | 重组蛋白            |
| 流感疫苗  | 四价/三价流感疫苗                    | 华兰生物/长春生物           | 0.5ml          | 裂解灭活            |
| 肺炎疫苗  | 23价/13价肺炎疫苗                  | Pneumovax 23/沃安欣    | 0.5ml          | 多糖/结合           |
| 其他    | 带状疱疹/新冠加强/狂犬/水痘/甲肝/百白破/乙脑/流脑 | Shingrix/成大速达等      | —              | —               |

---

## 项目结构

```
├── database/
│   └── init.sql              # 完整数据库初始化脚本（含测试数据）
├── src/main/
│   ├── java/.../             # 后端源码
│   │   ├── config/           # JWT、安全配置
│   │   ├── controller/       # REST API
│   │   ├── entity/           # 实体类
│   │   ├── repository/       # JPA 数据访问
│   │   └── service/          # 业务逻辑
│   └── resources/
│       ├── static/           # 前端静态页面
│       │   ├── index.html              # 首页（登录/注册）
│       │   ├── user-dashboard.html     # 用户中心（疫苗列表）
│       │   ├── user-profile.html       # 我的预约
│       │   ├── admin-dashboard.html    # 管理控制台
│       │   ├── admin-vaccine.html      # 疫苗管理
│       │   ├── admin-users.html        # 用户管理
│       │   ├── common.css              # 全局样式
│       │   └── app.js                  # 前端业务逻辑
│       ├── data.sql          # Spring Boot 自动初始化数据
│       ├── schema.sql        # 数据库建表语句
│       └── application.yml   # 主配置
├── docker-compose.yml        # Docker 编排
└── pom.xml                   # Maven 依赖
```

## 技术栈

- **后端**: Spring Boot 3.1.5, Spring Data JPA, Spring Security, JWT
- **数据库**: MySQL 8.0, Redis 7
- **前端**: 纯 HTML/CSS/JS（无前端框架依赖）
- **接口文档**: SpringDoc OpenAPI (http://localhost:8080/swagger-ui.html)
