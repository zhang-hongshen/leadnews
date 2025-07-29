# LeadNews: A Comprehensive News Media Platform

## Overview

LeadNews is a modern, feature-rich news and media platform built on a microservices architecture. It provides a complete solution for content creation, distribution, and consumption, with separate interfaces for end-users, content creators (WeMedia), and administrators. The project is developed using Java, Spring Boot, and Spring Cloud, and it leverages a wide range of technologies to deliver a scalable and robust system.

## Architecture

The platform is designed using a decoupled, microservices-based approach, orchestrated with Docker Compose. This ensures high availability, scalability, and maintainability.

The core components include:
- **API Gateways**: Separate gateways for the user-facing app, the WeMedia portal, and the admin dashboard.
- **Backend Services**: Individual microservices for handling users, articles, search, user behavior, scheduling, and more.
- **Data Persistence**: A polyglot persistence approach using MySQL for relational data and MongoDB for non-relational data.
- **Asynchronous Communication**: Kafka is used as a message broker for handling asynchronous tasks and event-driven communication between services.
- **Infrastructure**: Nacos for service discovery and configuration, Elasticsearch for full-text search, Minio for object storage, and Redis for caching.

## Technology Stack

- **Backend**: Java 8+, Spring Boot, Spring Cloud Alibaba
- **Databases**: MySQL, MongoDB
- **Search Engine**: Elasticsearch
- **Message Broker**: Kafka
- **Caching**: Redis
- **Object Storage**: Minio
- **Service Discovery & Configuration**: Nacos
- **Containerization**: Docker, Docker Compose
- **Build Tool**: Maven

## Modules

The project is organized into a multi-module Maven project:

- `leadnews-common`: Contains shared utility classes, constants, and common configuration.
- `leadnews-model`: Defines the data models (POJOs, DTOs) used across different services.
- `leadnews-feign-api`: Holds the Feign client interfaces for type-safe, declarative REST calls between services.
- `leadnews-gateway`: Includes API gateways for routing requests to the appropriate microservices.
  - `leadnews-app-gateway`: For the main user application.
  - `leadnews-wemedia-gateway`: For the WeMedia (self-publishing) portal.
  - `leadnews-admin-gateway`: For the administration backend.
- `leadnews-service`: The core of the application, containing the business logic for each microservice.
  - `leadnews-user`: Manages user accounts, profiles, and authentication.
  - `leadnews-article`: Handles article creation, storage, and retrieval.
  - `leadnews-wemedia`: Powers the WeMedia portal for content creators.
  - `leadnews-search`: Provides advanced search capabilities using Elasticsearch.
  - `leadnews-behavior`: Tracks and analyzes user behavior.
  - `leadnews-schedule`: Manages scheduled and delayed tasks.
- `leadnews-basic`: Provides basic components, such as the Minio starter.
- `leadnews-utils`: A collection of utility modules.
- `docker`: Contains all the necessary Docker configurations, including `docker-compose.yaml`, to run the entire platform.

## Features

- **User Portal**: A user-facing application for browsing, reading, and searching for news articles.
- **WeMedia Portal**: A dedicated portal for content creators to write, manage, and publish their articles.
- **Admin Dashboard**: A powerful backend system for administrators to manage users, moderate content, and monitor the platform.
- **Full-Text Search**: Fast and relevant article search powered by Elasticsearch.
- **Asynchronous Task Handling**: Efficiently manages tasks like article indexing and notifications using Kafka.
- **Centralized Configuration**: All microservice configurations are managed in Nacos.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Maven
- Docker and Docker Compose

### Installation & Deployment

1.  **Clone the repository:**
    ```bash
    git clone git@github.com:zhang-hongshen/leadnews.git
    cd leadnews
    ```

2.  **Build the project:**
    Compile and package all the Java modules using Maven.
    ```bash
    mvn clean install -DskipTests
    ```

3.  **Start the platform:**
    All services are containerized and can be started easily with Docker Compose.
    ```bash
    cd docker
    docker-compose up -d
    ```
    This command will start all the microservices and their dependencies (databases, Nacos, Kafka, etc.).

### Accessing the Applications

Once all services are running, you can access the different parts of the platform:

- **Nacos Console**: `http://localhost:8848/nacos`
- **Kibana (for Elasticsearch)**: `http://localhost:5601`
- **User App**: `http://localhost:8080` (proxied by Nginx)
- **WeMedia Portal**: `http://localhost:8081` (proxied by Nginx)
- **Admin Dashboard**: `http://localhost:8082` (proxied by Nginx)

*Note: The exact ports for the frontends are configured in the Nginx service and may vary.*

## Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue.

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
