# EECS 4413: Forward Auction E-Commerce System
**Team Atlas** 

A full-stack forward auction system. Sellers list items with a starting price and duration. Buyers browse, select, and bid. The highest bidder at expiry wins and pays through a mock payment flow.

---

## Quick Start with Docker (Deliverable 3)

The entire application runs in a single Docker container — no local Java or SQLite install needed.

**Prerequisites:** Docker Desktop (https://www.docker.com/products/docker-desktop/)

```bash
# 1. Build and start
docker compose up --build

# 2. Open the app
#    http://localhost:8080

# 3. Stop
docker compose down

# 4. Full reset (wipes the database volume)
docker compose down -v
docker compose up --build
```

The database is automatically created and seeded with sample data on first boot.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | HTML / CSS / JavaScript |
| Backend | Java 17, Spring Boot 3.2.2 |
| Database | SQLite (`sqlite-jdbc 3.45.1.0`) |
| ORM / Data Access | Spring Data JPA (User entity) + JdbcTemplate (all other tables) |
| Validation | Jakarta Bean Validation (`@NotBlank`, `@Positive`, `@Email`, etc.) |
| Testing | Postman |

---

## Prerequisites

Install the following before proceeding:

- **Java 17+** — https://adoptium.net/
- **Maven 3.8+** — https://maven.apache.org/download.cgi
- **SQLite3 CLI** — https://www.sqlite.org/download.html
- **Eclipse IDE** (recommended) or any IDE with Maven/Spring Boot support
- **Postman** — https://www.postman.com/downloads/

Verify your installs:
```bash
java -version      # must be 17+
mvn -version       # must be 3.8+
sqlite3 --version
```

---

## Project Structure

```
Ecommerce-System/
├── backend/
│   ├── src/main/java/com/yorku/auction/
│   │   ├── AuctionSystemApplication.java   ← entry point
│   │   ├── controller/   ← AuthController, BidController, CatalogueController,
│   │   │                    PaymentController, SellerController,
│   │   │                    SessionSelectionController, UserController
│   │   ├── service/      ← UserService, BidService, CatalogueService,
│   │   │                    AuctionService, PaymentService, SessionSelectionService
│   │   ├── dto/          ← SignupRequest, LoginRequest, PlaceBidRequest,
│   │   │                    CreateAuctionRequest, PayNowRequest,
│   │   │                    SelectionRequest, CatalogueItemResponse
│   │   ├── model/        ← User.java (@Entity)
│   │   └── repository/   ← UserRepository (JpaRepository)
│   ├── src/main/resources/
│   │   ├── application.properties   ← server port, SQLite config
│   │   ├── schema.sql               ← creates all 6 tables
│   │   └── data.sql                 ← seeds sample + test data
│   └── pom.xml
├── frontend/
│   ├── auth.html    ← Sign-up / Login page
│   └── index.html   ← Main app: browse, bid, sell, pay
├── testing/         ← Postman collection (see Testing section)
├── docs/
│   └── EECS 4413 Project Deliverable 1.pdf
└── database/        ← auction.db lives here after setup
```

---

## Installation and Setup

### 1. Clone the repository

```bash
git clone https://github.com/nurjahan-shiah/Ecommerce-System
cd Ecommerce-System
```

### 2. Set up and seed the database

**The application must NOT be running while doing this.**

Navigate to the `backend` folder, then run:

**Windows (Command Prompt):**
```cmd
cd backend
del auction.db
sqlite3 auction.db < src/main/resources/schema.sql
sqlite3 auction.db < src/main/resources/data.sql
```

**macOS / Linux:**
```bash
cd backend
rm -f auction.db
sqlite3 auction.db < src/main/resources/schema.sql
sqlite3 auction.db < src/main/resources/data.sql
```

This creates `auction.db` with:
- 4 sample users (2 buyers, 1 seller, 1 test winner)
- 3 active auction items (laptop, phone, headphones)
- 1 ENDED auction (auction_id=5, winner=testuser4) — needed for TC07 payment test
- 1 COMPLETED auction (auction_id=6) — needed for duplicate payment rejection test

> **Always re-run these commands to reset to a clean state before running the Postman test suite.**

---

## Running the Application

### Option A: Eclipse (recommended)

1. Open Eclipse → **File → Import → Existing Maven Projects**
2. Select the `backend/` folder → click **Finish**
3. Wait for Maven to download dependencies
4. In the **Project Explorer**, navigate to `com.yorku.auction`
5. Right-click `AuctionSystemApplication.java` → **Run As → Java Application**

### Option B: Maven CLI

```bash
cd backend
mvn spring-boot:run
```

### Option C: Build JAR and run

```bash
cd backend
mvn clean package -DskipTests
java -jar target/auction-system-1.0.0-SNAPSHOT.jar
```

**The server starts on: http://localhost:8080**

You should see this in the console:
```
  Auction System Started Successfully
  Access at: http://localhost:8080
```

---

## Running the Frontend

No build step required, just open the HTML files directly in your browser.

1. Open `frontend/auth.html` to sign up or log in
2. After authenticating, you'll be redirected to `frontend/index.html`

> The frontend calls `http://localhost:8080`, make sure the backend is running first.

---

## API Reference

Base URL: `http://localhost:8080`

| Method | Endpoint | Required Headers | Description |
|---|---|---|---|
| POST | `/api/auth/signup` | — | Register a new user |
| POST | `/api/auth/login` | — | Login; returns `sessionId` + `userId` |
| GET | `/api/catalogue/items/active` | — | List all active auctions |
| GET | `/api/catalogue/items?keyword=X` | — | Search active auctions by keyword |
| POST | `/api/session/selection` | `X-Session-Id`, `X-User-Id` | Select an auction (one per session) |
| GET | `/api/session/selection` | `X-Session-Id` | Get current session selection |
| DELETE | `/api/session/selection` | `X-Session-Id` | Clear session selection |
| POST | `/api/bids` | `X-Session-Id`, `X-User-Id` | Place bid on selected auction |
| GET | `/api/bids/{auctionId}` | — | Get bid history for an auction |
| POST | `/api/seller/auctions` | `X-User-Id` | Seller creates a new auction listing |
| POST | `/api/payments/pay` | `X-Session-Id`, `X-User-Id` | Process payment for won auction |
| GET | `/api/users/{id}` | — | Get user profile by ID |

**How session headers work:** `/api/auth/login` returns `sessionId` and `userId` in the JSON response body. Pass these as `X-Session-Id` and `X-User-Id` headers on all authenticated requests.

---

## Testing

### Import the Postman collection

1. Open Postman
2. Click **Import** → select the collection file from the `testing/` folder

### Pre-seeded test accounts

| Username | Password | Role | Notes |
|---|---|---|---|
| `testuser1` | `password123` | BUYER | General buyer |
| `testuser2` | `password123` | BUYER | General buyer |
| `seller1` | `password123` | SELLER | Pre-seeded seller for item uploads |
| `testuser4` | `password123` | BUYER | Winner of auction #5 — use for TC07 payment test |

### Test case order

Test cases are stateful — each one builds on the previous. **Run them in this exact order:**

```
TC01 → TC02 → TC03 → TC14 → TC15 → TC16 → TC09 → TC08 → TC06 → TC05 → TC04 → TC07
```

### Running manually

Open each test case folder in Postman and run the requests inside in order.

### Running automatically (recommended)

1. Right-click the **Test Cases** collection folder in Postman
2. Click **Run**
3. Click the orange **Run** button on the right

All test cases run in sequence with results shown inline.

### Test case summary

| ID | Category | Expected Outcome |
|---|---|---|
| TC01 | Sign-up — successful registration | 201, user record created |
| TC02 | Login — successful | 200, returns `sessionId` + `userId` |
| TC03 | Login — wrong password | 401, error message |
| TC04 | Bidding — valid higher bid | 200, price updated |
| TC05 | Bidding — non-integer bid (25.5) | 400, bid rejected |
| TC06 | Bidding — bid not higher than current | 400, bid rejected |
| TC07 | Payment — successful completion | 200, auction marked COMPLETED |
| TC08 | Session — only one item selected per session | Latest selection replaces previous |
| TC09 | Browse — keyword search | Returns matching active auctions |
| TC10 | Architecture — service communication failure | 400, graceful error message |
| TC11 | Architecture — DB layer isolation | Controller → Service → Repository chain enforced |
| TC12 | Architecture — SOA interface contract | Services communicate via defined interfaces |
| TC13 | Receipt — successful display | Receipt with total amount + shipping days |
| TC14 | Seller — successful item upload | Auction created, item appears in catalogue |
| TC15 | Seller — missing required fields | 400, item not created |
| TC16 | Seller — invalid numeric values (0 or negative) | 400, item not created |

---

## Resetting to Clean State

Run this any time you want to start fresh (app must be **stopped** first):

**Windows:**
```cmd
cd backend
del auction.db
sqlite3 auction.db < src/main/resources/schema.sql
sqlite3 auction.db < src/main/resources/data.sql
```

**macOS / Linux:**
```bash
cd backend
rm -f auction.db
sqlite3 auction.db < src/main/resources/schema.sql
sqlite3 auction.db < src/main/resources/data.sql
```

---

## Team

| Name | Student ID |
|---|---|
| Nurjahan Ahmed Shiah | 218802348 |
| Karishma Maharjan | 218731109 |
| Kennie Oraka | 219163104 |
| Andrew Tissi | 218724179 |

© 2026 Team Atlas, York University. All rights reserved.
