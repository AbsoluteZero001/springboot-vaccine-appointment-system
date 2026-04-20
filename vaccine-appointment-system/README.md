# Vaccine Appointment System

A full-featured vaccine appointment management system backend with a static frontend for testing. This project is designed as a graduation project demonstration.

## Features

- **User Module**: Registration, login, profile management
- **Admin Module**: Admin login, user management
- **Vaccine Management**: CRUD operations, stock management, availability toggling
- **Appointment Module**: Schedule, cancel, query appointments with duplicate prevention mechanism
- **Vaccination Records**: Track vaccination status and completion

## Technology Stack

- **Backend**: Spring Boot 3.1.5, Spring Data JPA, Spring Security, Spring Data Redis
- **Database**: MySQL 8.0+
- **Cache & Lock**: Redis (for distributed locking)
- **API Documentation**: OpenAPI 3 (Swagger UI)
- **Frontend**: Static HTML/CSS/JavaScript with Fetch API

## Database Setup

1. Install MySQL 8.0+ and create a database:
   ```sql
   CREATE DATABASE vaccine_appointment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. Configure database connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/vaccine_appointment_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. The application will automatically create tables using JPA `ddl-auto=update`. Alternatively, you can run the SQL scripts:
   - `src/main/resources/schema.sql` - Table definitions
   - `src/main/resources/data.sql` - Initial data (admin account, sample vaccines)

## Redis Setup

1. Install Redis and start the Redis server on default port (6379).

2. Configure Redis connection in `application.properties` if needed.

## Running the Application

### Prerequisites
- Java 17
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### Steps
1. Clone the repository
2. Configure database and Redis settings in `application.properties`
3. Build the project:
   ```bash
   mvn clean package
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   Or run the generated JAR:
   ```bash
   java -jar target/vaccine-appointment-system-0.0.1-SNAPSHOT.jar
   ```

5. The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, access the Swagger UI at:
- `http://localhost:8080/swagger-ui.html`

## Frontend Testing

The static frontend pages are located in `src/main/resources/static/`. Open `index.html` in a browser (served by the backend at `http://localhost:8080/`).

### Default Accounts

**Admin:**
- Username: `admin`
- Password: `admin123`

**Test User:**
- Username: `testuser`
- Password: `user123`

### User Flow
1. **Login/Register** (`index.html`)
2. **User Dashboard** (`user-dashboard.html`) - View available vaccines and schedule appointments
3. **My Appointments** (`user-profile.html`) - View and cancel appointments, see vaccination records

### Admin Flow
1. **Admin Login** (`index.html`)
2. **Admin Dashboard** (`admin-dashboard.html`) - System overview and pending appointments
3. **Manage Vaccines** (`admin-vaccine.html`) - Add, edit, delete vaccines
4. **Manage Users** (`admin-users.html`) - Activate/deactivate, delete users

## Project Structure

```
src/main/java/com/springboot/vaccineappointmentsystem/
├── config/           # Configuration classes (Security, Redis)
├── controller/       # REST API controllers
├── entity/          # JPA entities
├── repository/      # Spring Data JPA repositories
├── service/         # Service interfaces and implementations
└── VaccineAppointmentSystemApplication.java

src/main/resources/
├── static/          # Frontend HTML/CSS/JS files
├── templates/
├── application.properties
├── schema.sql       # Database schema
└── data.sql         # Initial data
```

## Key Implementation Details

### Duplicate Appointment Prevention
The system prevents users from having multiple pending appointments for the same vaccine through:
1. **Application-level check**: `AppointmentService.hasPendingAppointment()` verifies no existing pending/confirmed appointments for the user+vaccine combination.
2. **Database constraint**: Unique index on `appointment(user_id, vaccine_id, status)` for pending status (MySQL doesn't support conditional unique indexes, so application logic handles this).
3. **Redis distributed lock** (planned): To handle concurrent requests in clustered environments.

### Security
- Password encryption using BCrypt
- Basic Spring Security configuration with public endpoints for login/registration
- Role-based access control (planned for future enhancement)

### Error Handling
- Global exception handling with consistent error responses
- Validation using Spring Boot Starter Validation

## Testing

Run tests with:
```bash
mvn test
```

## Deployment

1. Update `application.properties` for production database and Redis
2. Set proper security configurations (CORS, CSRF, etc.)
3. Build with production profile:
   ```bash
   mvn clean package -Pprod
   ```
4. Deploy the generated JAR file

## Future Enhancements

1. Email notifications for appointment reminders
2. SMS integration
3. Advanced reporting and analytics
4. Mobile application
5. OAuth2 authentication
6. Microservices architecture

## License

This project is for educational purposes.

## Contact

For questions or support, please contact the project maintainer.