# SmartERP — Billing, Inventory & Accounting Management System

A full-stack ERP web application built for small retail businesses to manage ledgers, inventory, sales/purchase vouchers, PDF invoicing, and AI-powered financial reports.

**Live Demo:** [https://smarterp-flax.vercel.app](https://smarterp-flax.vercel.app)

---

## Features

- **Authentication** — JWT-based signup/login with BCrypt password hashing and Spring Security
- **Ledger Management** — Create and manage Customer, Supplier, Expense, and Income ledgers with balance tracking
- **Inventory Management** — Track stock items with purchase/selling prices, quantities, units, and low stock alerts
- **Sales Vouchers** — Record sales transactions; automatically deducts stock and updates customer ledger balance
- **Purchase Vouchers** — Record purchase transactions; automatically increases stock and updates supplier ledger balance
- **PDF Invoice Generation** — Generate downloadable A4 PDF invoices for any voucher using iText
- **AI Business Reports** — Feed live ledger and invoice data to Groq LLM (Llama 3.3) for plain-English business insights and recommendations
- **Dashboard** — Real-time summary of ledgers, stock items, invoices, and low stock alerts

---

## Tech Stack

**Backend**
- Java 21, Spring Boot 3.5
- Spring Security + JWT (JJWT 0.12.6)
- Spring Data JPA + Hibernate
- PostgreSQL (Supabase)
- iText 5.5 (PDF generation)
- Groq API — Llama 3.3 70B (AI reports)

**Frontend**
- React 18 + Vite
- Tailwind CSS
- Axios
- React Router DOM

**Deployment**
- Backend: Render (Docker)
- Frontend: Vercel
- Database: Supabase (PostgreSQL)

---

## Project Structure

```
smarterp/
├── src/                        # Spring Boot backend
│   └── main/java/com/smarterp/
│       ├── controllers/        # REST API endpoints
│       ├── services/           # Business logic
│       ├── entities/           # JPA entities
│       ├── repositories/       # Spring Data repositories
│       ├── dto/                # Request/Response DTOs
│       ├── security/           # JWT filter, config
│       ├── config/             # CORS config
│       └── exceptions/         # Global exception handler
├── frontend/                   # React frontend
│   └── src/
│       ├── pages/              # Login, Signup, Dashboard, Ledgers, StockItems, Vouchers, Reports
│       ├── components/         # Sidebar, ProtectedRoute
│       ├── api/                # Axios config
│       └── context/            # Auth context
├── Dockerfile                  # Docker build for Render
└── pom.xml
```

---

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Ledgers
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/ledgers` | Get all ledgers |
| GET | `/api/ledgers/type/{type}` | Get ledgers by type |
| POST | `/api/ledgers` | Create ledger |
| PUT | `/api/ledgers/{id}` | Update ledger |
| DELETE | `/api/ledgers/{id}` | Delete ledger |

### Stock Items
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/stock-items` | Get all stock items |
| GET | `/api/stock-items/{id}` | Get stock item by ID |
| GET | `/api/stock-items/low-stock` | Get low stock items |
| POST | `/api/stock-items` | Create stock item |
| PUT | `/api/stock-items/{id}` | Update stock item |
| DELETE | `/api/stock-items/{id}` | Delete stock item |

### Invoices (Vouchers)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/invoices` | Get all invoices |
| GET | `/api/invoices/{id}` | Get invoice by ID |
| GET | `/api/invoices/type/{type}` | Get invoices by type |
| GET | `/api/invoices/{id}/pdf` | Download PDF invoice |
| POST | `/api/invoices` | Create invoice (auto-updates stock + ledger) |

### Reports
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports` | Get business report with AI summary |

---

## Local Setup

### Prerequisites
- Java 21
- Maven
- Node.js 18+
- Supabase account (PostgreSQL)
- Groq API key ([console.groq.com](https://console.groq.com))

### Backend

1. Clone the repo:
```bash
git clone https://github.com/AnuvabPol7/smarterp.git
cd smarterp
```

2. Set environment variables (Windows):
```
DB_PASSWORD=your-supabase-db-password
GROQ_API_KEY=your-groq-api-key
```

3. Update `src/main/resources/application.yml` with your Supabase connection URL

4. Run the backend:
```bash
mvn spring-boot:run
```

Backend runs at `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at `http://localhost:5173`

---

## How It Works

### Business Flow
```
Add Stock Items (products)
        ↓
Create Purchase Voucher (buy from supplier)
→ Stock quantity increases automatically
→ Supplier ledger balance updates
        ↓
Create Sales Voucher (sell to customer)
→ Stock quantity decreases automatically
→ Customer ledger balance updates
        ↓
Generate PDF Invoice for customer
        ↓
View AI-powered business report
```

### AI Reports
Business data (total sales, purchases, profit/loss, stock counts, low stock alerts) is sent to Groq's Llama 3.3 70B model which returns plain-English business insights and actionable recommendations.

---

## Deployment

### Backend (Render)
- Dockerfile included at project root
- Set environment variables in Render dashboard: `DB_PASSWORD`, `GROQ_API_KEY`
- Auto-deploys on push to `main`

### Frontend (Vercel)
- Root directory set to `frontend/`
- Framework preset: Vite
- Auto-deploys on push to `main`

---

## Screenshots

| Dashboard | Ledgers | Vouchers |
|-----------|---------|----------|
| Live stats overview | Customer/Supplier accounts | Sales & Purchase records |

| Reports | PDF Invoice |
|---------|-------------|
| AI-powered insights | Downloadable A4 invoice |