# 💊 FEFO/FIFO Demo — Pharmacy Inventory Management

A **Spring Boot** REST API that implements **FEFO (First Expired, First Out)** and **FIFO (First In, First Out)** inventory rotation strategies for pharmaceutical stock management. This project demonstrates best practices for medication shelf-life control, helping pharmacies minimize waste and ensure patient safety by always dispensing the earliest-expiring or earliest-received stock first.

---

## 📋 Table of Contents

- [About](#about)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Variables](#environment-variables)
  - [Running with Docker](#running-with-docker)
  - [Running Locally](#running-locally)
- [API Overview](#api-overview)
- [FEFO vs FIFO](#fefo-vs-fifo)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

---

## About

Managing pharmaceutical inventory requires strict compliance with stock rotation policies. Dispensing an expired or near-expiry medication can have serious health consequences. This demo project models two of the most widely used strategies in healthcare logistics:

- **FEFO** — prioritized by expiration date (required by EU GDP Guidelines and WHO Good Storage Practices)
- **FIFO** — prioritized by entry date into stock

The API allows pharmacies to register batches of medications and automatically retrieve them in the correct dispensing order.

---

## Features

- ✅ Register medication batches with expiration dates and entry timestamps
- ✅ Automatic FEFO ordering (earliest expiry dispatched first)
- ✅ Automatic FIFO ordering (oldest received dispatched first)
- ✅ RESTful endpoints for batch management
- ✅ Persistent storage via relational database
- ✅ Containerized with Docker & Docker Compose
- ✅ Environment-variable–based configuration

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.0 |
| Build Tool | Gradle |
| Database | PostgreSQL (via Docker) |
| Containerization | Docker / Docker Compose |
| ORM | Spring Data JPA / Hibernate |

---

## Getting Started

### Prerequisites

- [Java 25](https://adoptium.net/)
- [Docker](https://www.docker.com/) & Docker Compose
- [Git](https://git-scm.com/)

### Environment Variables

Copy the example file and fill in your values:

```bash
cp .env-example .env
```

Edit `.env` with your database credentials and any other required settings before running the application.

---

### Running with Docker

The easiest way to get up and running:

```bash
# Clone the repository
git clone https://github.com/Doug16Yanc/fefo-fifo-demo.git
cd fefo-fifo-demo

# Copy and configure environment
cp .env-example .env

# Start all services (app + database)
docker-compose up --build
```

The API will be available at `http://localhost:8080`.

---

### Running Locally

If you prefer to run without Docker (requires a local PostgreSQL instance):

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

Make sure your database is running and your `.env` (or `application.properties`) is configured correctly.

---

## API Overview

> Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/batches` | Register a new medication batch |
| `GET` | `/batches` | List all registered batches |
| `GET` | `/batches/fefo` | Retrieve batches ordered by FEFO |
| `GET` | `/batches/fifo` | Retrieve batches ordered by FIFO |

> ⚠️ Endpoints may vary. Refer to the source code or a running Swagger UI (if enabled) for the full contract.

---

## FEFO vs FIFO

| Strategy | Full Name | Dispatch Priority | Best Used When |
|---|---|---|---|
| **FEFO** | First Expired, First Out | Earliest expiration date | Medications / perishable goods |
| **FIFO** | First In, First Out | Earliest entry into stock | Non-perishable / general inventory |

In pharmaceutical contexts, **FEFO is the recommended approach** per EU GDP Guidelines (2013/C 343/01) and WHO Technical Report Series 1025 (2020), as it directly prevents the dispensing of expired medications regardless of when they entered the system.

---

## Project Structure

```
fefo-fifo-demo/
├── src/
│   └── main/
│       ├── java/          # Application source code
│       └── resources/     # Configuration files
├── Dockerfile
├── docker-compose.yml
├── build.gradle
├── .env-example
└── README.md
```

---

## Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## License

This project is open source. See the repository for license details.

---

> Built with ❤️ by [Doug16Yanc](https://github.com/Doug16Yanc)
