# Cognivex - RAG AI Application

A Spring Boot-based Retrieval-Augmented Generation (RAG) system using Spring AI and Ollama.

## Features

- Chat with AI using context from ingested documents
- Document ingestion for RAG
- Reactive WebFlux API
- Security with basic authentication
- OpenAPI documentation
- Docker support

## Prerequisites

- Java 21
- Maven 3.6+
- Ollama (for local AI models)

## Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd cognivex
   ```

2. **Start Ollama and pull models**
   ```bash
   ollama serve
   ollama pull qwen3.5:0.8b
   ollama pull nomic-embed-text
   ```

3. **Run with Maven**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Or use Docker Compose**
   ```bash
   docker-compose up --build
   ```

## API Endpoints

- `GET /v1/chat?question=<query>` - Chat with AI (requires auth)
- `GET /v1/ingest` - Ingest documents (requires auth)
- `GET /actuator/health` - Health check
- `GET /v3/api-docs` - OpenAPI spec
- `GET /swagger-ui.html` - Swagger UI

## Authentication

Use basic auth with:
- Username: `user` / Password: `password` (or set via env vars)

## Configuration

Set environment variables:
- `OLLAMA_BASE_URL` - Ollama server URL
- `OLLAMA_CHAT_MODEL` - Chat model name
- `OLLAMA_EMBEDDING_MODEL` - Embedding model name
- `APP_USER` - Basic auth username
- `APP_PASSWORD` - Basic auth password

## Testing

Run tests:
```bash
./mvnw test
```

## Deployment

Build and run:
```bash
./mvnw clean package
java -jar target/cognivex-0.0.1-SNAPSHOT.jar
```

Or use Docker:
```bash
docker build -t cognivex .
docker run -p 8080:8080 cognivex
```

## Architecture

- **Controllers**: Handle HTTP requests
- **Services**: Business logic for RAG and ingestion
- **Config**: Vector store and security configuration
- **Resources**: Application configuration and sample data