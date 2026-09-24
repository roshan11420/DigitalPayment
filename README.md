# 💳 Digital Wallet & Payment Processing System

A simple **fintech web application** built with Java and Spring Boot that allows users to create accounts, manage wallet balances, transfer money, and track transaction history.

The project focuses on demonstrating important backend concepts such as **REST APIs, database transactions, authentication, wallet management, and transaction processing**.

## 🚀 Features

### 👤 User Management
- Create a new user account
- Login using password and OTP verification
- View account and wallet details
- Unique account number for each wallet

### 💰 Wallet Management
- Check wallet balance
- Credit money to wallet
- Debit money from wallet
- Wallet status management

### 💸 Money Transfer
- Transfer money using account number
- Transfer money using mobile number
- Generate and scan QR code for wallet identification
- Validate insufficient balance
- Prevent invalid transactions

### 📜 Transaction History
- View all wallet transactions
- Track credit, debit, and transfer transactions
- Transaction reference number
- Transaction status
- Transaction date and time

## 🏗️ Architecture

```text
                    Web Frontend
                 HTML + CSS + JavaScript
                          │
                          ↓
                    REST APIs
                          │
                          ↓
                    Spring Boot
                          │
        ┌─────────────────┼─────────────────┐
        ↓                 ↓                 ↓
   Auth Service     Wallet Service    Transaction Service
        │                 │                 │
        └─────────────────┼─────────────────┘
                          ↓
                      PostgreSQL
```

## 🛠️ Tech Stack

### Backend
- Java
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate

### Database
- PostgreSQL

### Security
- Spring Security
- JWT
- Password hashing
- OTP-based verification

### Frontend
- HTML
- CSS
- JavaScript

### Testing & Tools
- JUnit
- Mockito
- Postman
- Swagger / OpenAPI
- Maven
- Git & GitHub

### QR Code
- ZXing

## 🗄️ Database Design

The application uses three main entities:

```text
User
 │
 │ 1 : 1
 ↓
Wallet
 │
 │
 ↓
Transaction
 ↙       ↘
Sender   Receiver
```

### User

```text
id
name
mobile
email
password
status
created_at
```

### Wallet

```text
id
user_id
account_number
balance
status
created_at
```

### Transaction

```text
id
reference_number
sender_wallet_id
receiver_wallet_id
amount
type
status
description
created_at
```

## 🔗 Main REST APIs

### Authentication

```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/verify-otp
```

### Wallet

```http
GET  /api/wallet
GET  /api/wallet/balance
POST /api/wallet/credit
POST /api/wallet/debit
```

### Money Transfer

```http
POST /api/transactions/transfer
POST /api/transactions/transfer/mobile
POST /api/transactions/transfer/account
```

### Transaction History

```http
GET /api/transactions/history
GET /api/transactions/{referenceNumber}
```

### QR Code

```http
GET /api/wallet/qr
```

## 🔄 Money Transfer Flow

```text
User initiates transfer
          ↓
Authenticate user
          ↓
Find receiver
          ↓
Validate amount
          ↓
Check sender balance
          ↓
Debit sender wallet
          ↓
Credit receiver wallet
          ↓
Create transaction record
          ↓
Return transaction reference
```

The transfer operation uses a database transaction so that the debit and credit operations are handled together.

If an operation fails, the transaction can be rolled back to prevent an incomplete transfer.

## 🔐 Authentication Flow

```text
Register
   ↓
Login with Password
   ↓
Verify OTP
   ↓
Generate JWT
   ↓
Access Protected APIs
```

Passwords are stored using hashing rather than plain text.

## 📱 QR Payment Flow

Each wallet can have a QR code containing the wallet/account identifier.

```text
Generate QR
     ↓
User scans QR
     ↓
Receiver wallet identified
     ↓
Enter amount
     ↓
Confirm transfer
     ↓
Transaction completed
```

## 📊 Transaction Types

```text
CREDIT
DEBIT
TRANSFER_SENT
TRANSFER_RECEIVED
```

Transaction statuses:

```text
PENDING
SUCCESS
FAILED
```

## 🧠 Key Backend Concepts Demonstrated

This project is designed to demonstrate practical backend concepts:

- REST API development
- Object-Oriented Programming
- Spring Boot architecture
- JPA entity relationships
- PostgreSQL database design
- Database transactions
- `@Transactional`
- Authentication and authorization
- JWT
- Password hashing
- Input validation
- Exception handling
- Transaction history
- QR-based wallet identification

## 📁 Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com.digitalwallet/
    │       ├── controller/
    │       ├── service/
    │       ├── repository/
    │       ├── entity/
    │       ├── dto/
    │       ├── security/
    │       └── exception/
    │
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html
            ├── login.html
            ├── register.html
            ├── dashboard.html
            ├── transfer.html
            └── history.html
```

## ⚙️ Setup & Installation

### 1. Clone the repository

```bash
git clone https://github.com/your-username/digital-wallet.git
cd digital-wallet
```

### 2. Create PostgreSQL database

```sql
CREATE DATABASE digital_wallet;
```

### 3. Configure database

Update `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/digital_wallet
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

The application will run on:

```text
http://localhost:8080
```

## 🧪 API Testing

The REST APIs can be tested using **Postman**.

Example transfer request:

```http
POST /api/transactions/transfer
```

```json
{
  "receiver": "9876543210",
  "amount": 500,
  "description": "Wallet transfer"
}
```

Example response:

```json
{
  "referenceNumber": "TXN-8F72A1",
  "amount": 500,
  "status": "SUCCESS",
  "message": "Transfer completed successfully"
}
```

## 🔮 Future Improvements

Possible future enhancements:

- Idempotency keys for duplicate payment prevention
- Optimistic/pessimistic locking for concurrent transactions
- Email/SMS notifications
- Spending limits
- Admin dashboard
- Audit logs
- Docker deployment
- Redis caching
- Automated CI/CD
- Cloud deployment

## ⚠️ Disclaimer

This is an **educational fintech project** designed to demonstrate software engineering and backend development concepts. It is not intended for processing real-world financial transactions.

## 👨‍💻 Author

**Roshan Sahu**

Built as a learning project to explore:

**Java • Spring Boot • PostgreSQL • REST APIs • Spring Security • Fintech Backend Development**
