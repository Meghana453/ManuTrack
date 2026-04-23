# ManuTrack — Production, Inventory & Logistics Management System

A full-stack manufacturing ERP built with **Spring Boot 3** (backend) and **React 18** (frontend).

---

## 🏗 Architecture

```
manutrack/
├── backend/          ← Spring Boot 3.2 REST API (Java 21)
│   └── src/main/java/com/manutrack/
│       ├── module/
│       │   ├── iam/          (Identity & Access)
│       │   ├── production/   (Plans, Machines, WorkOrders)
│       │   ├── inventory/    (Items, MaterialRequests)
│       │   ├── procurement/  (Vendors, POs, Invoices)
│       │   ├── logistics/    (Carriers, Routes, Shipments)
│       │   ├── analytics/    (Reports, KPIs)
│       │   └── notification/ (Alerts)
│       ├── config/           (Security, Swagger, Seeder)
│       ├── exception/        (Global Exception Handler)
│       └── security/         (JWT Filter + Provider)
└── frontend/         ← React 18 + Tailwind CSS
    └── src/
        ├── pages/dashboard/  (7 role-specific dashboards)
        ├── components/       (Table, Modal, Badge, StatCard)
        ├── services/         (Axios API client)
        └── context/          (AuthContext + JWT)
```

---

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- Node.js 18+
- npm 9+

---

### Backend Setup

```bash
cd backend

# Run with embedded H2 (zero config, perfect for dev/Postman testing)
mvn spring-boot:run
```

**Backend runs at:** `http://localhost:7071/api`

**H2 Console:** `http://localhost:7071/api/h2-console`
- JDBC URL: `jdbc:h2:mem:manutrackdb`
- Username: `sa` / Password: `password`

**Swagger UI:** `http://localhost:7071/api/swagger-ui.html`

#### Switch to MySQL (production)

Edit `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/manutrack?useSSL=false&serverTimezone=UTC
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

---

### Frontend Setup

```bash
cd frontend
npm install
npm start
```

**Frontend runs at:** `http://localhost:3000`

---

## 🔐 Demo Credentials (auto-seeded)

| Role        | Email                         | Password        |
|-------------|-------------------------------|-----------------|
| Admin       | admin@manutrack.com           | admin123        |
| Planner     | planner@manutrack.com         | planner123      |
| Supervisor  | supervisor@manutrack.com      | supervisor123   |
| Inventory   | inventory@manutrack.com       | inventory123    |
| Procurement | procurement@manutrack.com     | procurement123  |
| Logistics   | logistics@manutrack.com       | logistics123    |

---

## 📮 Postman API Testing

### Step 1 — Login and get JWT token
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "admin@manutrack.com",
  "password": "admin123"
}
```
Copy the `token` from the response.

### Step 2 — Set Authorization header
All protected requests require:
```
Authorization: Bearer <your_token>
```

### Key API Endpoints

#### Auth
| Method | URL                       | Body / Notes           |
|--------|---------------------------|------------------------|
| POST   | /api/auth/register        | Register user          |
| POST   | /api/auth/login           | Login → returns token  |

#### Production
| Method | URL                                     |
|--------|-----------------------------------------|
| GET    | /api/production/plans                   |
| POST   | /api/production/plans                   |
| PUT    | /api/production/plans/{id}              |
| DELETE | /api/production/plans/{id}              |
| GET    | /api/production/machines                |
| POST   | /api/production/machines                |
| PUT    | /api/production/machines/{id}           |
| GET    | /api/production/work-orders             |
| POST   | /api/production/work-orders             |
| PUT    | /api/production/work-orders/{id}        |
| GET    | /api/production/plans/{id}/work-orders  |

#### Inventory
| Method | URL                                          |
|--------|----------------------------------------------|
| GET    | /api/inventory/items                         |
| POST   | /api/inventory/items                         |
| PATCH  | /api/inventory/items/{id}/adjust-stock       |
| GET    | /api/inventory/items/low-stock               |
| GET    | /api/inventory/items/out-of-stock            |
| GET    | /api/inventory/material-requests             |
| POST   | /api/inventory/material-requests             |
| PUT    | /api/inventory/material-requests/{id}        |

#### Procurement
| Method | URL                                |
|--------|------------------------------------|
| GET    | /api/procurement/vendors           |
| POST   | /api/procurement/vendors           |
| GET    | /api/procurement/purchase-orders   |
| POST   | /api/procurement/purchase-orders   |
| GET    | /api/procurement/invoices          |
| POST   | /api/procurement/invoices          |

#### Logistics
| Method | URL                                |
|--------|------------------------------------|
| GET    | /api/logistics/carriers            |
| POST   | /api/logistics/carriers            |
| GET    | /api/logistics/routes              |
| POST   | /api/logistics/routes              |
| GET    | /api/logistics/shipments           |
| POST   | /api/logistics/shipments           |
| GET    | /api/logistics/shipments/status/{status} |

#### Analytics
| Method | URL                                       |
|--------|-------------------------------------------|
| GET    | /api/analytics/dashboard                  |
| GET    | /api/analytics/production                 |
| GET    | /api/analytics/inventory                  |
| GET    | /api/analytics/procurement                |
| GET    | /api/analytics/logistics                  |
| POST   | /api/analytics/reports/generate?scope=FULL |

#### Users & Audit
| Method | URL                              |
|--------|----------------------------------|
| GET    | /api/users                       |
| GET    | /api/users/{id}                  |
| PUT    | /api/users/{id}                  |
| DELETE | /api/users/{id}                  |
| GET    | /api/audit-logs                  |
| GET    | /api/audit-logs/user/{userId}    |

#### Notifications
| Method | URL                                        |
|--------|--------------------------------------------|
| GET    | /api/notifications/user/{userId}           |
| GET    | /api/notifications/user/{userId}/unread    |
| PATCH  | /api/notifications/{id}/read               |
| PATCH  | /api/notifications/{id}/dismiss            |

---

## 🛡 Security

- JWT Bearer token authentication (24hr expiry)
- Role-Based Access Control (RBAC) on every endpoint via `@PreAuthorize`
- BCrypt password hashing
- CORS configured for localhost:3000 and localhost:5173
- Global exception handler returns structured JSON errors

## 📊 Tech Stack

| Layer      | Technology                              |
|------------|-----------------------------------------|
| Backend    | Spring Boot 3.2, Java 21                |
| Security   | Spring Security 6, JWT (JJWT 0.12)     |
| ORM        | Spring Data JPA, Hibernate              |
| Database   | H2 (dev) / MySQL (prod)                 |
| Mapping    | MapStruct 1.5                           |
| Boilerplate| Lombok                                  |
| API Docs   | SpringDoc OpenAPI (Swagger UI)          |
| Frontend   | React 18, React Router v6               |
| Styling    | Tailwind CSS 3                          |
| Charts     | Recharts                                |
| HTTP Client| Axios                                   |
| Toasts     | React Hot Toast                         |
