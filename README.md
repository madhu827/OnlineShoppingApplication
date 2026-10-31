# 🛒 Online Shopping Application

A full-stack web application built with **Spring Boot 3.5**, **React 18**, **MongoDB** and **Apache Cassandra**.

---

## 🏗️ Technology Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.5, Maven |
| Authentication | Spring Security + JWT (JJWT 0.12) |
| User Database | MongoDB Atlas (cloud) |
| Product Database | Apache Cassandra (local / Docker) |
| Frontend | React 18 + Vite, Axios, React Router v6 |
| API Docs | Springdoc OpenAPI 3 (Swagger UI) |

---

## ⚡ Quick Start

### Prerequisites
- Java 21+ (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Node.js 18+ (`node -v`)
- Docker Desktop (for Cassandra) OR a running Cassandra instance

---

### 1. Start Cassandra (Docker)

```bash
docker compose up -d cassandra
# Wait ~60 seconds for Cassandra to initialise
docker compose logs -f cassandra   # watch progress
```

---

### 2. Configure Environment Variables

Your `.env` file (already created) contains:

```env
MONGODB_URI=mongodb+srv://...@onlineshopping.gsdj3is.mongodb.net/
CASSANDRA_HOST=127.0.0.1
CASSANDRA_PORT=9042
CASSANDRA_KEYSPACE=online_shopping
```

Load them in PowerShell before starting the backend:

```powershell
$env:MONGODB_URI      = "mongodb+srv://srinulakkakula68_db_user:YU77KZAepMD8mgT8@onlineshopping.gsdj3is.mongodb.net/"
$env:CASSANDRA_HOST   = "127.0.0.1"
$env:CASSANDRA_PORT   = "9042"
$env:CASSANDRA_KEYSPACE = "online_shopping"
```

---

### 3. Start Backend

```powershell
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

Backend starts on → **http://localhost:8080**

On first run, `DataInitializer` automatically seeds:
- **MongoDB**: `admin` user + `testuser`
- **Cassandra**: 20 sample products across 5 categories

---

### 4. Start Frontend

```powershell
cd frontend
npm install      # first time only
npm run dev
```

Frontend starts on → **http://localhost:3000**

---

## 🔐 Default Credentials

| Role | Username | Password |
|------|----------|----------|
| ADMIN | `admin` | `Admin@1234` |
| USER | `testuser` | `Test@1234` |

> ⚠️ Change these immediately in production!

---

## 🌐 URLs

| URL | Description |
|-----|-------------|
| http://localhost:3000 | React Frontend |
| http://localhost:8080/swagger-ui.html | Swagger API Docs |
| http://localhost:8080/v3/api-docs | OpenAPI JSON |

---

## 📡 API Endpoints

### Auth (`/api/auth`)
```
POST /api/auth/register    – Register new account
POST /api/auth/login       – Login (returns JWT + refresh token)
POST /api/auth/refresh     – Refresh access token
POST /api/auth/logout      – Logout (invalidates refresh tokens)
```

### Products (`/api/products`)
```
GET  /api/products/active                         – All active products (public)
GET  /api/products/category/{category}            – By category (public)
GET  /api/products/category/{category}/{id}       – Single product (public)
POST /api/products                                – Create (ADMIN)
PUT  /api/products/category/{category}/{id}       – Update (ADMIN)
DELETE /api/products/category/{category}/{id}     – Delete (ADMIN)
```

### Users (`/api/users`)
```
GET    /api/users/me        – My profile (JWT required)
GET    /api/users           – All users (ADMIN)
DELETE /api/users/{id}      – Delete user (ADMIN)
```

### Admin (`/api/admin`)
```
GET  /api/admin/users                       – List all users
POST /api/admin/users/{username}/promote    – Grant ADMIN role
POST /api/admin/users/{username}/demote     – Remove ADMIN role
```

---

## 🗄️ Database Design

### MongoDB — `online_shopping` database

| Collection | Purpose |
|---|---|
| `users` | User accounts, credentials (BCrypt), roles |
| `refresh_tokens` | JWT refresh tokens (TTL auto-expire) |

### Apache Cassandra — `online_shopping` keyspace

| Table | Primary Key | Purpose |
|---|---|---|
| `products` | `(category, id DESC)` | Product catalog |

> **Why this partition key?** Products are almost always queried by category. Partitioning by `category` ensures all products in a category live on the same Cassandra node, making category queries extremely fast without `ALLOW FILTERING`.

---

## 📁 Project Structure

```
d:\Online SHopping\
├── .env                         ← Environment variables (not committed)
├── .gitignore
├── docker-compose.yml           ← Cassandra + optional Cassandra Web UI
├── cassandra-init/
│   └── init.cql                 ← Keyspace + table DDL (auto-run in Docker)
├── backend/                     ← Spring Boot (Maven)
│   ├── pom.xml
│   └── src/main/java/com/onlineshopping/
│       ├── config/              ← Mongo, Cassandra, Security, OpenAPI, DataInitializer
│       ├── entity/              ← mongo/ and cassandra/ (kept separate)
│       ├── repository/          ← mongo/ and cassandra/ (kept separate)
│       ├── security/            ← JWT filter, service, properties, UserDetailsService
│       ├── dto/                 ← request/ and response/ DTOs
│       ├── service/             ← Business logic (impl/ subpackage)
│       ├── controller/          ← REST controllers
│       └── exception/           ← Custom exceptions + global handler
└── frontend/                    ← React 18 + Vite
    ├── vite.config.js           ← Proxy /api → :8080
    └── src/
        ├── api/                 ← axiosInstance (JWT auto-inject), authApi, productApi
        ├── context/             ← AuthContext (global auth state)
        ├── components/          ← common/, auth/, product/
        ├── pages/               ← Public + protected + admin pages
        └── styles/              ← Complete CSS design system
```

---

## 🔒 Security Architecture

```
Request → JwtAuthFilter → SecurityContextHolder → Controller
              ↓
         Extract JWT
              ↓
         Load User from MongoDB
              ↓
         Validate signature + expiry
              ↓
         Set Authentication (with roles)
```

Role-based access rules (defined in `SecurityConfig`):
- `GET /api/products/**` → **Public**
- `POST/PUT/DELETE /api/products/**` → **ROLE_ADMIN**
- `GET /api/users/me` → **Any authenticated user**
- `GET /api/users`, `/api/admin/**` → **ROLE_ADMIN**

---

## 🚀 Making Yourself Admin

Option A — Use the seeded admin account (`admin` / `Admin@1234`).

Option B — Promote any registered user via API:
```bash
curl -X POST http://localhost:8080/api/admin/users/{username}/promote \
  -H "Authorization: Bearer <admin-jwt-token>"
```

Option C — MongoDB Atlas UI (direct):
```js
db.users.updateOne(
  { username: "yourUsername" },
  { $set: { roles: ["ROLE_ADMIN", "ROLE_USER"] } }
)
```

---

## 🧪 Testing the API with Swagger

1. Open http://localhost:8080/swagger-ui.html
2. Call `POST /api/auth/login` with `admin` / `Admin@1234`
3. Copy the `accessToken` from the response
4. Click **Authorize** → paste `Bearer <token>`
5. Now you can call any protected endpoint

---

## 🛠️ Development Tips

| Task | Command |
|------|---------|
| Backend compile check | `mvn compile` |
| Backend tests | `mvn test` |
| Backend package | `mvn clean package -DskipTests` |
| Frontend lint | `npm run lint` |
| Frontend build | `npm run build` |
| Cassandra CQL shell | `docker exec -it cassandra-shopping cqlsh` |
| View products table | `SELECT * FROM online_shopping.products LIMIT 10;` |
| Cassandra Web UI | `docker compose --profile tools up -d` → http://localhost:3001 |

