package com.foundation.health;

import dagger.multibindings.IntoSet;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

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
     * Constructs the DefaultHealthChecker.
     * <p>
     * Dependencies for extended checks can be injected here if needed.
     * </p>
     */
    @Inject
    public DefaultHealthChecker() {
        // No-op constructor for dependency injection
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
