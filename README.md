# Foundation SDK

Shared dependency‑injection, configuration, and utility modules for microservices.

---

## Table of Contents

* [Overview](#overview)
* [Features](#features)
* [Project Structure](#project-structure)
* [Getting Started](#getting-started)

    * [Prerequisites](#prerequisites)
    * [Clone & Build](#clone--build)
* [Using the SDK](#using-the-sdk)

    * [Configuration (`ConfigModule`)](#configuration-configmodule)
    * [Kafka Streams (`KafkaStreamsModule`)](#kafka-streams-kafkastreamsmodule)
    * [gRPC Clients (`GrpcClientModule`)](#grpc-clients-grpcclientmodule)
    * [Persistence (`PersistenceModule`)](#persistence-persistencemodule)
    * [Health Checks (`HealthModule`)](#health-checks-healthmodule)
* [Running Tests](#running-tests)
* [CI/CD](#cicd)
* [Contributing](#contributing)
* [License](#license)

---

## Overview

The **Foundation SDK** provides a set of reusable Dagger 2 modules and utility classes to accelerate microservice development at. It includes:

* **Configuration**: loading and validating environment variables into an immutable `Config` object.
* **Kafka Streams**: wiring of `KafkaStreams` instances (KRaft‑ready).
* **gRPC Clients**: provisioning of Google Cloud Vision API clients with proper credentials.
* **Persistence**: interfaces and default implementations for writing documents to disk and publishing errors to Kafka.
* **Health Checks**: liveness/readiness probes with composite support.

## Features

* **Compile‑time DI** using Dagger 2 (no reflection at runtime)
* **Immutable configuration** via Lombok `@Value` + `@Builder`
* **Modern Java 21** support
* **Kafka 4.0.0** clients & streams (ZooKeeper‑free KRaft)
* **Google Cloud Vision** integration with scoped credentials
* **Disk & Kafka sinks** for documents and error envelopes
* **Health check framework** with multibinding and composite aggregation

## Project Structure

```plaintext
foundation-sdk/
├── src/
│   ├── main/java/com/foundation/
│   │   ├── config/               # ConfigModel, EnvConfigProvider, ConfigModule
│   │   ├── streams/              # KafkaStreamsModule, KafkaStreamsProvider
│   │   ├── grpc/                 # GrpcClientModule, VisionClientProvider
│   │   ├── persistence/          # DocumentSink, DiskDocumentSink, ErrorSink, KafkaErrorSink, PersistenceModule
│   │   └── health/               # HealthChecker, DefaultHealthChecker, CompositeHealthChecker, HealthModule, HealthComponent, HealthStatus, Status
│   └── test/java/com/foundation/  # Unit tests for each module
├── .github/workflows/ci.yml      # GitHub Actions CI
├── mvnw / mvnw.cmd / .mvn/       # Maven Wrapper
├── pom.xml                       # Project POM with dependency management
├── README.md                     # This file
└── LICENSE                       # Apache‑2.0
```

## Getting Started

### Prerequisites

* Java 21 JDK
* Maven 3.9+
* Git

### Clone & Build

```bash
# Clone the repo
git clone git@github.com:your-org/foundation-sdk.git
cd foundation-sdk

# Build with Maven Wrapper
./mvnw clean verify
```

## Using the SDK

In your microservice, add a dependency in your `pom.xml`:

```xml
<dependency>
    <groupId>com.foundation</groupId>
    <artifactId>foundation-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

Then in your `Main` application (e.g., in package `com.vendorinvoice`):

```java
import com.foundation.config.Config;
import com.foundation.config.ConfigModule;
import com.foundation.grpc.GrpcClientModule;
import com.foundation.persistence.PersistenceModule;
import com.foundation.streams.KafkaStreamsModule;
import com.foundation.health.HealthModule;
import dagger.Component;

@Singleton
@Component(modules = {
        ConfigModule.class,
        KafkaStreamsModule.class,
        GrpcClientModule.class,
        PersistenceModule.class,
        HealthModule.class,
        YourBusinessModule.class  // bind your service logic here
})
public interface AppComponent {
    KafkaStreams kafkaStreams();
    DocumentSink documentSink();
    ErrorSink errorSink();
    HealthChecker healthChecker();
}

public class Main {
    public static void main(String[] args) {
        AppComponent component = DaggerAppComponent.create();
        component.kafkaStreams().start();
        // Expose healthChecker() via HTTP or k8s probe
    }
}
```

### Configuration (`ConfigModule`)

Loads required environment variables (with defaults) into a `Config` object.

| Env Var                          | Description                     | Default                 |
| -------------------------------- | ------------------------------- | ----------------------- |
| `KAFKA_BOOTSTRAP_SERVERS`        | Kafka bootstrap servers         | `localhost:9092`        |
| `SCHEMA_REGISTRY_URL`            | Schema Registry URL             | `http://localhost:8081` |
| `APPLICATION_ID`                 | Kafka Streams application ID    | *required*              |
| `INPUT_TOPIC`, `OUTPUT_TOPIC`    | Kafka topics for your stream    | *required*              |
| `GOOGLE_APPLICATION_CREDENTIALS` | Path to Vision API credentials  | *required*              |
| `DOCUMENT_SINK_PATH`             | Base dir for DiskDocumentSink   | *required*              |
| `ERROR_TOPIC`                    | Kafka topic for error envelopes | *required*              |

### Kafka Streams (`KafkaStreamsModule`)

Provides a singleton `KafkaStreams` instance configured from `Config`. Use in your Dagger component to start/stop streams.

### gRPC Clients (`GrpcClientModule`)

Provides a singleton `ImageAnnotatorClient` for Google Cloud Vision, loading credentials and scoping to Cloud Platform.

### Persistence (`PersistenceModule`)

* **`DocumentSink`**: e.g. `DiskDocumentSink` writes documents to disk.
* **`ErrorSink`**: e.g. `KafkaErrorSink` publishes error envelopes to Kafka.

### Health Checks (`HealthModule`)

* **`HealthChecker`**: perform liveness and readiness.
* **`DefaultHealthChecker`**: basic up-status.
* **`CompositeHealthChecker`**: aggregates all checkers via multibinding.

## Running Tests

```bash
./mvnw test
```

Unit tests cover each module and ensure configuration and bindings behave as expected.

## CI/CD

GitHub Actions workflow at `.github/workflows/ci.yml` runs `mvn clean verify` on push and PR.

## Contributing

1. Fork the repo.
2. Create a feature branch.
3. Add tests for new functionality.
4. Submit a pull request for review.

## License

This project is licensed under the Apache License 2.0. See [LICENSE](LICENSE) for details.
