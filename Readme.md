
# 📚 Course Search API

A modular Spring Boot + Elasticsearch project for full-text course discovery, filtering, autocomplete, and fuzzy search.

---

## 🧱 Tech Stack

- Java 17
- Spring Boot 3.4.7
- Spring Data Elasticsearch
- Elasticsearch 7.17.x
- Maven
- Docker + Docker Compose

---

## 🚀 Getting Started

### 1. Clone the Repo

```bash
git clone https://github.com/ArjunTomar1402/coursesearch.git
cd coursesearch
```

### 2. Launch Elasticsearch (via Docker)

Make sure you have Docker and Docker Compose installed.

```bash
docker-compose up -d
```

This will start Elasticsearch on port `9200`.

To verify it's running:

```bash
curl http://localhost:9200
```

### 3. Build and Run the Spring Boot App

```bash
./mvnw clean package
./mvnw spring-boot:run
```

The API will run at:

```bash
http://localhost:8080
```

---

## 📦 Sample Data Indexing

Before querying, populate Elasticsearch with sample course documents:

```bash
curl -X POST http://localhost:8080/api/index
```

---

## 🔍 Search API Usage

### Search by Keyword

```bash
curl "http://localhost:8080/api/search?keyword=science"
```

### Search by Age Range

```bash
curl "http://localhost:8080/api/search?minAge=5&maxAge=6"
```

### Search by Category

```bash
curl "http://localhost:8080/api/search?category=math"
```

### Combine Filters

```bash
curl "http://localhost:8080/api/search?keyword=english&minAge=7&maxAge=10&category=language"
```

---

## ✨ Autocomplete and Fuzzy Search

### Autocomplete

```bash
curl "http://localhost:8080/api/autocomplete?prefix=phy"
```

### Fuzzy Autocomplete

```bash
curl "http://localhost:8080/api/fuzzy-autocomplete?prefix=scince"
```

---

## 🧪 Run All Tests

To verify the logic and search filters with unit + integration tests:

```bash
./mvnw test
```

---

## 🧹 Stop Elasticsearch

When you're done:

```bash
docker-compose down
```

---

## ✅ Features

- 🔍 Full-text search with keyword scoring
- 🎯 Filter by minAge, maxAge, and category
- ⚡ Fast autocomplete
- 🧠 Fuzzy matching (e.g., `scince` → `science`)
- 🔬 Tested: Integration tests included
- 🧱 Modular codebase with clear responsibilities

---

## 📁 Project Structure

```
coursesearch/
├── src/
│   ├── main/
│   │   ├── java/com/example/coursesearch/
│   │   │   ├── controller/
│   │   │   ├── document/
│   │   │   ├── search/
            ├── service/
            ├── repository/
│   │   │   └── config/
│   └── test/
│       └── java/com/example/coursesearch/
│           └── search/

├── docker-compose.yml
├── pom.xml
└── README.md
```
## 📘 API Documentation

Once the app is running, you can view and test all API endpoints here:

```
Swagger UI:  
[http://localhost:8080/swagger-ui.html]
OpenAPI JSON:  
[http://localhost:8080/v3/api-docs]
```

---

## 📬 Contact

Feel free to reach out:

**Arjun Tomar**  
📧 arjun.tomar@iic.ac.in  
 

