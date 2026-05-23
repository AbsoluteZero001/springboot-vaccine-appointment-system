
<h1 align="center">疫苗预约系统 | Vaccine Appointment System</h1>

> A full-stack vaccine appointment management platform built with Vue 3 and Spring Boot 3 using a separated frontend-backend architecture. The system integrates Spring Security, JWT-based authentication, Redis caching, MySQL, Docker containerization, and GitHub Actions CI/CD. It supports online appointment scheduling, admin approval workflows, inventory management, and automated deployment pipelines.


<br/>

<!-- 语言切换按钮 -->
<p align="center">
  <a href="README.md">
    <img src="https://img.shields.io/badge/中文版本-点击查看-red?style=for-the-badge&logo=markdown&logoColor=white" />
  </a>

  <a href="README_EN.md">
    <img src="https://img.shields.io/badge/English-Version-blue?style=for-the-badge&logo=markdown&logoColor=white" />
  </a>
</p>

<br/>

<!-- 技术栈标签 -->
<p align="center">
  <img src="https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk" />
  <img src="https://img.shields.io/badge/SpringBoot-3.1.5-green?style=flat-square&logo=springboot" />
  <img src="https://img.shields.io/badge/Vue-3-brightgreen?style=flat-square&logo=vuedotjs" />
  <img src="https://img.shields.io/badge/MySQL-8-blue?style=flat-square&logo=mysql" />
  <img src="https://img.shields.io/badge/Redis-7-red?style=flat-square&logo=redis" />
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker" />
</p>



---

## Tech Stack

| Layer              | Technology                           | Version |
| ------------------ | ------------------------------------ | ------- |
| Backend Framework  | Spring Boot                          | 3.1.5   |
| Frontend Framework | Vue 3 (Composition API + TypeScript) | 3.4     |
| Security / Auth    | Spring Security + JWT (JJWT)         | 0.12.5  |
| ORM                | Spring Data JPA / Hibernate          | —       |
| Database           | MySQL                                | 8.0+    |
| Cache / Dist. Lock | Redis                                | 7+ (degradable) |
| API Docs           | SpringDoc OpenAPI                    | 2.2     |
| Build Tool         | Vite                                 | 5.x     |
| State Management   | Pinia                                | 2.x     |
| Router             | Vue Router                           | 4.x     |
| HTTP Client        | Axios                                | 1.x     |
| Project Build      | Maven                                | 3.9+    |
| Deployment         | Docker & Docker Compose              | —       |

---

## Features

### User Side

| Feature                  | Description                                                                                              |
| ------------------------ | -------------------------------------------------------------------------------------------------------- |
| Register / Login         | JWT token auth with brute-force protection (5 failed attempts trigger progressive lockout), auto-sync full user profile on login |
| Vaccine Browsing         | Filter by category, keyword search, 46 vaccines with brand, dosage, and process details                  |
| Vaccine Details          | Brand, manufacturer, doses, route of administration, age range, target diseases, immunization schedule   |
| Online Appointment       | Select date + time slot, auto-detects workday / Saturday half-day / Sunday off, requires real-name verification |
| My Appointments          | View / cancel / online payment, status flow: Booked → Completed / No-show / Cancelled, wider payment & status area for visibility |
| Vaccination Records      | View personal vaccination history                                                                        |
| Profile Settings         | Avatar upload & preview, nickname, gender / birthday / phone, notes                                      |
| Real-Name Verification   | ID number auto-derives gender, format validation, unlocks appointment after verification                 |
| Username Change          | Once per year, shows next available date, username is the unique account identifier                      |
| Phone Validation         | 11 digits, starts with 1 followed by 3-9, dual frontend + backend validation                             |
| Toast Feedback           | Global modal notifications (success / failure), auto-dismiss in 3 seconds, more prominent than inline alerts |

### Admin Side

| Feature              | Description                                                                          |
| -------------------- | ------------------------------------------------------------------------------------ |
| Appointment Review   | Filter by status (Booked / Completed / No-show / Cancelled), complete vaccination, late record, cancel |
| Vaccine Management   | Activate / deactivate, add, edit, delete, stock adjustment, image upload             |
| User Management      | User list, enable / disable, delete, view real-name verification info                |
| Admin Management     | Admin account CRUD                                                                    |
| Vaccination Records  | View all user vaccination records, administer vaccination                            |
| Data Overview        | Real-time appointment statistics by status                                           |

### Infrastructure

| Feature                  | Description                                          |
| ------------------------ | ---------------------------------------------------- |
| JWT Stateless Auth       | `Authorization: Bearer <token>` on every request     |
| BCrypt Password Hashing  | All passwords stored as BCrypt hashes                |
| Brute-Force Protection   | 5 failed logins trigger progressive lockout 30s → 60s |
| Redis Distributed Lock   | Prevents overselling on appointments, auto-degrades to local lock when Redis unavailable |
| Optimistic Locking       | Vaccine stock uses @Version for concurrency control  |
| CORS Protection          | Only configured origins allowed                      |
| Role-Based Access        | ROLE_USER / ROLE_ADMIN two-tier isolation            |

---

## Quick Start

### Prerequisites

- **Docker & Docker Compose** (recommended, one-click deploy)
- Or install manually: **JDK 17+** + **MySQL 8.0+** + **Node.js 18+** + **Redis 7+** (optional)

### Option 1: One-Click Docker Deploy (Recommended)

```bash
# Linux / macOS
bash start.sh

# Windows
start.bat
```

The script will automatically:

1. Check for `.env` environment variable file (copies from `.env.example` on first run)
2. Detect Docker environment
3. Build images and start all services
4. Wait for MySQL → Redis → Backend health checks to pass
5. Print access URLs

Starts 4 containers:

| Container          | Service             | Port  |
| ------------------ | ------------------- | ----- |
| `vaccine-mysql`    | MySQL 8.0           | 3306  |
| `vaccine-redis`    | Redis 7             | 6379  |
| `vaccine-backend`  | Spring Boot App     | 8080  |
| `vaccine-frontend` | Nginx + Vue 3 SPA   | 80    |

On first start, the database auto-executes `database/init.sql`, creating 5 tables and seeding 46 vaccines + 1 admin account.

### Option 2: Development Mode (Separate Frontend & Backend)

```bash
# Terminal 1: Start backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 2: Start frontend (hot reload)
cd frontend && npm install && npm run dev
```

| Service              | URL                                  |
| -------------------- | ------------------------------------ |
| Frontend (Vite Dev)  | http://localhost:5173                |
| Backend API          | http://localhost:8080                |
| Swagger UI           | http://localhost:8080/swagger-ui.html |

The Vite dev server automatically proxies `/api` and `/uploads` requests to the backend at `localhost:8080`.

### Option 3: Single JAR Deployment

```bash
# Build frontend and package as single JAR
mvn package -Pfrontend -DskipTests

# Run
java -jar target/vaccine-appointment-system-0.0.1-SNAPSHOT.jar
```

Visit `http://localhost:8080` — both frontend and backend are served by Spring Boot.

---

### Default Admin Account (Initialization)

The system auto-creates an admin account via DataInitializer on startup (only when no admin data exists in the database):

- Default role: ROLE_ADMIN
- Account source: System init script / DataInitializer
- Password policy: BCrypt hashed storage (override via environment variables in production)

Regular users can self-register via the registration endpoint.

---

## Configuration

### Environment Variables

All sensitive configuration is injected via environment variables. See `.env.example`:

```bash
# MySQL
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=vaccine_appointment_db
MYSQL_USER=vaccine_user
MYSQL_PASSWORD=vaccine_pass

# Redis (leave empty for no password)
REDIS_PASSWORD=

# JWT (change in production)
JWT_SECRET=change-me-to-a-random-64-character-string-in-production

# Ports
APP_PORT=8080
FRONTEND_PORT=80
```

### Spring Profile

| Profile       | Description                                             |
| ------------- | ------------------------------------------------------- |
| `dev` (default) | Development: SQL logging + DEBUG level               |
| `prod`        | Production: Redis cache + Gzip compression + WARN level |

---

## API Overview

### Public Endpoints

| Method | Path                  | Description                                                     |
| ------ | --------------------- | --------------------------------------------------------------- |
| POST   | `/api/auth/login`     | Unified login, returns full user profile (avatar, real-name, phone, etc.) |
| GET    | `/api/auth/verify`    | Verify token and sync full user profile                         |
| POST   | `/api/users/register` | User registration                                               |

### User Endpoints

| Method | Path                         | Description                                  |
| ------ | ---------------------------- | -------------------------------------------- |
| GET    | `/api/users/{id}`            | Get user info (full profile)                 |
| GET    | `/api/users/username/{name}` | Find user by username                        |
| PUT    | `/api/users/{id}/profile`    | Update profile (includes phone validation)   |
| PUT    | `/api/users/{id}/username`   | Change username (once per year)              |
| POST   | `/api/users/{id}/avatar`     | Upload avatar                                |
| POST   | `/api/users/{id}/verify`     | Real-name verification (auto-derives gender) |

### Vaccine Endpoints

| Method | Path                         | Description                             |
| ------ | ---------------------------- | --------------------------------------- |
| GET    | `/api/vaccines`              | Vaccine list                            |
| GET    | `/api/vaccines/available`    | Available vaccines (active + in stock)  |
| GET    | `/api/vaccines/search?name=` | Search vaccines                         |
| GET    | `/api/vaccines/{id}`         | Vaccine details                         |

### Appointment Endpoints

| Method | Path                              | Description           |
| ------ | --------------------------------- | --------------------- |
| POST   | `/api/appointments`               | Create appointment    |
| GET    | `/api/appointments/user/{userId}` | User appointment list |
| GET    | `/api/appointments/{id}`          | Appointment details   |
| POST   | `/api/appointments/{id}/cancel`   | Cancel appointment    |
| POST   | `/api/appointments/{id}/pay`      | Pay for appointment   |

### Vaccination Record Endpoints

| Method | Path                                       | Description            |
| ------ | ------------------------------------------ | ---------------------- |
| GET    | `/api/vaccination-records/user/{userId}`   | User vaccination records |
| GET    | `/api/vaccination-records/status/{status}` | Query records by status |

### Admin Endpoints (ROLE_ADMIN)

#### User Management

| Method | Path              | Description                     |
| ------ | ----------------- | ------------------------------- |
| GET    | `/api/users`      | User list                       |
| PUT    | `/api/users/{id}` | Update user (status toggle)     |
| DELETE | `/api/users/{id}` | Delete user                     |

#### Admin Account Management

| Method | Path               | Description      |
| ------ | ------------------ | ---------------- |
| GET    | `/api/admins`      | Admin list       |
| GET    | `/api/admins/{id}` | Admin details    |
| POST   | `/api/admins`      | Create admin     |
| PUT    | `/api/admins/{id}` | Update admin     |
| DELETE | `/api/admins/{id}` | Delete admin     |

#### Vaccine Management

| Method | Path                              | Description          |
| ------ | --------------------------------- | -------------------- |
| POST   | `/api/vaccines`                   | Add vaccine          |
| PUT    | `/api/vaccines/{id}`              | Update vaccine       |
| DELETE | `/api/vaccines/{id}`              | Delete vaccine       |
| PATCH  | `/api/vaccines/{id}/availability` | Activate / deactivate |
| PATCH  | `/api/vaccines/{id}/stock`        | Adjust stock         |
| POST   | `/api/vaccines/{id}/upload-image` | Upload vaccine image |

#### Appointment Management

| Method | Path                                  | Description              |
| ------ | ------------------------------------- | ------------------------ |
| GET    | `/api/appointments`                   | All appointments         |
| GET    | `/api/appointments/pending`           | Pending appointments     |
| GET    | `/api/appointments/status/{status}`   | Query by status          |
| GET    | `/api/appointments/{id}/logs`         | Appointment audit log    |
| POST   | `/api/appointments/{id}/complete`     | Complete vaccination     |
| POST   | `/api/appointments/{id}/late-record`  | Late record vaccination  |
| POST   | `/api/appointments/{id}/cancel/admin` | Admin cancel appointment |

#### Vaccination Record Management

| Method | Path                                           | Description               |
| ------ | ---------------------------------------------- | ------------------------- |
| GET    | `/api/vaccination-records/{id}`                | Record details            |
| GET    | `/api/vaccination-records/vaccine/{vaccineId}` | Query by vaccine          |
| PUT    | `/api/vaccination-records/{id}`                | Update record             |
| POST   | `/api/vaccination-records/{id}/administer`     | Administer vaccination    |

#### Statistics

| Method | Path              | Description   |
| ------ | ----------------- | ------------- |
| GET    | `/api/statistics` | Data overview |

---

## Project Structure

```
vaccine-appointment-system/
├── frontend/                          # Vue 3 Frontend
│   ├── src/
│   │   ├── views/                     # Page views
│   │   │   ├── HomeView.vue           #   Home (login / register / carousel)
│   │   │   ├── UserDashboardView.vue  #   Vaccine browsing & appointment
│   │   │   ├── UserProfileView.vue    #   My appointments & vaccination records
│   │   │   ├── UserSettingsView.vue   #   Settings (profile / avatar / real-name)
│   │   │   ├── AdminDashboardView.vue #   Appointment management console
│   │   │   ├── AdminVaccineView.vue   #   Vaccine CRUD management
│   │   │   └── AdminUsersView.vue     #   User management
│   │   ├── components/                # Reusable components
│   │   │   ├── SiteHeader.vue         #   Global navigation bar
│   │   │   ├── SiteFooter.vue         #   Footer
│   │   │   ├── ModalMessage.vue       #   Global toast notification (success/failure)
│   │   │   ├── LoadingOverlay.vue     #   Loading overlay
│   │   │   ├── NewsCarousel.vue       #   Vaccine news carousel
│   │   │   ├── MedicalIllustration.vue#   Medical illustration decoration
│   │   │   ├── LoginMessage.vue       #   Login brute-force protection notice
│   │   │   ├── VaccineCard.vue        #   Vaccine card
│   │   │   ├── AppointmentModal.vue   #   Appointment date/time slot picker
│   │   │   └── VaccineEditModal.vue   #   Vaccine edit form
│   │   ├── router/index.ts            # Router config + navigation guards
│   │   ├── stores/auth.ts             # Pinia auth state management
│   │   ├── services/api.ts            # Axios request wrapper
│   │   └── styles/global.css          # Global styles
│   ├── vite.config.ts                 # Vite config
│   ├── tsconfig.json
│   └── package.json
├── src/main/
│   ├── java/com/springboot/vaccineappointmentsystem/
│   │   ├── config/                    # SecurityConfig, JwtTokenProvider, RedisConfig, DataInitializer ...
│   │   ├── controller/                # AuthController, VaccineController, AppointmentController, AdminController ...
│   │   ├── service/                   # Service interfaces + impl
│   │   ├── repository/                # JPA data access layer
│   │   ├── entity/                    # SysUser, Vaccine, Appointment, VaccinationRecord, AppointmentLog
│   │   ├── enums/                     # AppointmentStatus, VaccinationRecordStatus enums & JPA converters
│   │   ├── dto/                       # ApiResponse, AppointmentStatistics
│   │   └── exception/                 # GlobalExceptionHandler
│   └── resources/
│       ├── application.yml            # Main config
│       ├── application-dev.yml        # Dev profile
│       ├── application-prod.yml       # Prod profile
│       └── static/                    # Vite build output (not used in dev, .gitignore'd)
├── database/
│   └── init.sql                       # DB init script (DDL + 46 vaccines + 1 admin)
├── docker/
│   ├── Dockerfile.backend             # Spring Boot multi-stage build
│   ├── Dockerfile.frontend            # Vue 3 + Nginx multi-stage build
│   ├── mysql/
│   │   └── init.sql                   # MySQL container init script
│   └── nginx/
│       └── nginx.conf                 # Nginx reverse proxy + SPA config
├── .github/workflows/
│   └── ci-cd.yml                      # CI/CD pipeline
├── docker-compose.yml                 # Docker orchestration
├── start.sh                           # Linux / macOS one-click startup
├── start.bat                          # Windows one-click startup
├── .env.example                       # Environment variable template
└── pom.xml                            # Maven config (includes frontend profile)
```

---

## Database

5 tables are auto-maintained by Hibernate `ddl-auto: update`. Docker deployments initialize via `database/init.sql`.

| Table                | Description                                                                                  |
| -------------------- | -------------------------------------------------------------------------------------------- |
| `sys_user`           | Users (including admins, distinguished by role), supports avatar, real-name verification, username change cooldown |
| `vaccine`            | Vaccine inventory & metadata, @Version optimistic locking for concurrency                    |
| `appointment`        | Appointment records (0=Booked, 1=Completed, 2=No-show, 3=Cancelled), online payment (0=Unpaid, 1=Paid) |
| `vaccination_record` | Vaccination records, linked to appointment, user, vaccine, doctor                            |
| `appointment_log`    | Appointment audit log                                                                        |

### Vaccine Seed Data (46 Vaccines)

| Category          | Count | Examples                                                                                                                        |
| ----------------- | ----- | ------------------------------------------------------------------------------------------------------------------------------- |
| Hepatitis B       | 7     | Recombinant Hepatitis B (CHO / Hansenula yeast / Saccharomyces), 10μg / 20μg / 60μg                                            |
| HPV               | 3     | 9-valent Gardasil 9, 4-valent Gardasil, 2-valent Cecolin                                                                       |
| Influenza         | 3     | Quadrivalent influenza (split), Trivalent influenza (split / subunit)                                                           |
| Pneumococcal      | 3     | 23-valent polysaccharide, 13-valent conjugate                                                                                   |
| Others            | 30    | Herpes zoster, COVID-19, Rabies, Varicella, Hepatitis A, DTaP, Japanese encephalitis, Meningococcal, BCG, Polio, MMR, Hib, Rotavirus, EV71, HFRS, Leptospirosis, Cholera, Yellow fever, Dengue, Mumps, Rubella |

> Vaccine images default to placeholders. Upload actual images through the admin panel.

---

## CI/CD

The project includes a GitHub Actions pipeline (`.github/workflows/ci-cd.yml`):

| Job                    | Description                       |
| ---------------------- | --------------------------------- |
| Fast Check             | Maven compile, fail fast          |
| Secret Scan            | Gitleaks secret scanning          |
| OWASP Dependency Check | Dependency vulnerability check    |
| CodeQL Analysis        | Code security analysis            |
| Build & Test           | Maven test + package + artifact upload |
| Docker Build           | Docker image build verification   |

---

## Docker Deployment Architecture

```
                   ┌──────────────────┐
                   │   Nginx (:80)    │
                   │  Vue 3 SPA +     │
                   │  API Reverse Proxy│
                   └────────┬─────────┘
                            │ /api/* → backend:8080
                   ┌────────▼─────────┐
                   │  Spring Boot     │
                   │  (:8080)         │
                   └──┬──────────┬────┘
                      │          │
              ┌───────▼──┐  ┌───▼──────┐
              │  MySQL   │  │  Redis   │
              │  (:3306) │  │  (:6379) │
              └──────────┘  └──────────┘
```

## Screenshots

---

## Home Page

![Home Page](vaccine-appointment-system/docs/images/home.png)

---

## Password Brute-Force Protection

## ![Password Brute-Force Protection](vaccine-appointment-system/docs/images/Password-brute-force-protection.png)

## User Side

### User Vaccine List

![User Vaccine List](vaccine-appointment-system/docs/images/user-vaccine-list.png)

---

### User Appointment Page

![User Appointment Page](vaccine-appointment-system/docs/images/user-appointment.png)

### User Profile Update

![User Profile Update](vaccine-appointment-system/docs/images/User-Profile-Update.png)

---

## Admin Side

### Vaccine Management List

![Admin Vaccine Management List](vaccine-appointment-system/docs/images/admin-vaccine-list.png)

---

### Vaccine Edit

![Admin Vaccine Edit](vaccine-appointment-system/docs/images/admin-vaccine-edit.png)

---

### Vaccine Management Module

![Admin Vaccine Management](vaccine-appointment-system/docs/images/admin-vaccine-management.png)

---

### Appointment Review List

![Admin Appointment Review List](vaccine-appointment-system/docs/images/admin-appointment-review-list.png)

---

### User Management

![Admin User Management](vaccine-appointment-system/docs/images/User-Management-Admin.png)

## License

MIT

---

## Author

[https://github.com/AbsoluteZero001](https://github.com/AbsoluteZero001)

This project is a Spring Boot + Vue 3 full-stack learning project. It does not involve any real business data — all data is simulated for testing purposes. Forks and learning exchanges are welcome.

(c) 2026 All Rights Reserved.
