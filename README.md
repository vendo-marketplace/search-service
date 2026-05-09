# Search Service

Service responsible for **fast querying and retrieval of search data** in the system.

The service provides an API for searching over a **pre-built read model stored in Elasticsearch**.  
It does not modify data and does not handle event processing.

---

# Purpose

The service is designed to:

- Execute full-text search queries
- Apply filters and sorting
- Support pagination
- Return optimized search results

---

# Tech Stack

- Java
- Spring Boot
- Elasticsearch
- Eureka
- MapStruct
- Zipkin
- REST API
- Docker

---

# Architecture

The service follows:

- **Hexagonal Architecture (Ports and Adapters)**

It is fully decoupled from write operations and focuses only on querying data.

---

## Layers

### domain

Contains core search models and value objects.

- Search result models
- Filtering and sorting abstractions
- Domain-level representations of search data

---

### application

Handles search use cases.

- Orchestrates search execution
- Applies business-level search logic
- Works with abstract query models

---

### port

Defines interfaces for communication.

- Input ports for search queries
- Output ports for search execution

---

### adapter

Handles external systems.

#### adapter.in

- REST controllers
- Request validation and mapping

#### adapter.out

- Elasticsearch integration
- Query construction and execution

---

# Project Structure

```
src
└── main
    └── java
        └── search-service
            ├── adapter
            │ ├── in
            │ └── out
            ├── application
            ├── domain
            ├── infrastructure
            └── port
```

---

# How It Works

1. Upstream services publish domain events
2. Events are consumed by the Indexer Service
3. Data is transformed into a search-optimized model
4. Documents are stored in Elasticsearch

This process ensures **eventual consistency** between write and read models.

---

# Responsibilities

- Build search projections
- Maintain index consistency
- Transform domain data into search-friendly format

---

# Non-Responsibilities

- Does NOT expose search APIs
- Does NOT handle user queries
- Does NOT act as a source of truth

---

# Role in System

Write Services → Events → Indexer Service → Elasticsearch

---

# Prerequisites

Required infrastructure:

- Configuration service
- Service registry
- Elasticsearch (search storage)

---

# Running the Service

Build and run:

```
mvn clean package
java -jar target/product-service.jar
```

---

# Environment Variables

| Variable          | Description             | Default        |
|-------------------|-------------------------|----------------|
| CONFIG_SERVER_URL | Config server endpoint  | localhost:8010 |
| APP_PROFILE       | Profile to fetch config | dev, prod      |

---

# Testing

Run unit tests:

```
mvn verify
```


---

# Code Principles

- Clean Architecture
- Separation of concerns
- Immutable data structures
- Constructor-based dependency injection
- Explicit boundaries between layers

---

# Summary

Indexer Service is a **dedicated projection builder** that transforms domain events into a search-optimized read model, enabling efficient querying in downstream services.
