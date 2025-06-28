package com.github.dimitryivaniuta.foundation.health;

/**
 * Defines the contract for performing health checks on application components.
 * <p>
 * Implementations should provide both liveness and readiness checks.
 * Liveness indicates whether the application is running, while readiness
 * indicates whether the application is able to serve requests (all dependent
 * services are available and initialized).
 * </p>
 */
public interface HealthChecker {

    /**
     * Performs a liveness check on the implementing component.
     * <p>
     * Liveness checks verify that the application process is alive and
     * running without fatal errors. This check should be fast and not
     * depend on external resources.
     * </p>
     *
     * @return a {@link HealthStatus} object indicating the liveness state
     */
    HealthStatus checkLiveness();

    /**
     * Performs a readiness check on the implementing component.
     * <p>
     * Readiness checks verify that the application is fully initialized
     * and all required dependencies (e.g., Kafka, databases, external APIs)
     * are available. Services should not receive traffic if not ready.
     * </p>
     *
     * @return a {@link HealthStatus} object indicating the readiness state
     */
    HealthStatus checkReadiness();
}