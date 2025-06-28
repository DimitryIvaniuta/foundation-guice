package com.github.dimitryivaniuta.foundation.health;

import com.github.dimitryivaniuta.foundation.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.Collections;

/**
 * Default implementation of {@link HealthChecker}.
 * <p>
 * Provides basic liveness and readiness checks for the application.
 * This implementation always returns healthy for both liveness and readiness,
 * but can be extended to include dependency checks (e.g., Kafka, gRPC clients).
 * </p>
 *
 * @see HealthChecker
 */
@Singleton
public class DefaultHealthChecker implements HealthChecker {

    /**
     * Application configuration, used to parameterize health checks.
     */
    private final Config config;

    /**
     * Constructs a DefaultHealthChecker with the provided configuration.
     *
     * @param config the application configuration instance
     */
    @Inject
    public DefaultHealthChecker(final Config config) {
        this.config = config;
    }

    /**
     * Performs a liveness check.
     * <p>
     * This always returns a healthy status. Override this method
     * to include JVM or thread-health checks.
     * </p>
     *
     * @return a {@link HealthStatus} indicating the liveness state
     */
    @Override
    public HealthStatus checkLiveness() {
        return HealthStatus.builder()
                .status(Status.UP)
                .timestamp(Instant.now())
                .details(Collections.emptyMap())
                .build();
    }

    /**
     * Performs a readiness check.
     * <p>
     * This always returns a healthy status. Override this method
     * to include dependency availability checks (e.g., Kafka, DB connections).
     * </p>
     *
     * @return a {@link HealthStatus} indicating the readiness state
     */
    @Override
    public HealthStatus checkReadiness() {
        return HealthStatus.builder()
                .status(Status.UP)
                .timestamp(Instant.now())
                .details(Collections.emptyMap())
                .build();
    }
}
