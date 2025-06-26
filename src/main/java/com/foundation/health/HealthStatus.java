package com.foundation.health;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

/**
 * Represents the health status of the application for liveness or readiness checks.
 * <p>
 * Contains an overall status, the timestamp of the check, and optional details
 * for specific subsystems or dependency checks.
 * </p>
 */
@Value
@Builder
public class HealthStatus {

    /**
     * Overall health status.
     * <p>
     * Typically {@link Status#UP} or {@link Status#DOWN}.
     * </p>
     */
    Status status;

    /**
     * Timestamp when the health check was performed.
     */
    Instant timestamp;

    /**
     * Optional details about individual components or dependencies.
     * <p>
     * Keys represent component names, values describe their state or error messages.
     * </p>
     */
    Map<String, String> details;
}
