package com.github.dimitryivaniuta.foundation.health;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.Instant;

/**
 * Represents the result of a health check, including an overall status
 * and optional diagnostic messages.
 * <p>
 * Use {@link #up()} to indicate a healthy system, or {@link #down(String...)}
 * to report failures. Additional messages provide context for the status.
 * </p>
 */
@Value
@Builder
public class HealthStatus {

    /**
     * Overall status of the health check.
     */
    Status status;

    /**
     * Optional diagnostic messages providing details about the status.
     */
    @Singular("message")
    java.util.List<String> messages;

    /**
     * Arbitrary key/value details for advanced diagnostics, such as individual
     * component latencies or version information.
     */
    @Singular("detail")
    java.util.Map<String, String> details;

    /**
     * Instant when the health check was performed.
     */
    Instant timestamp;

    /**
     * Creates a healthy {@code HealthStatus} with no diagnostic messages.
     *
     * @return a HealthStatus with {@link Status#UP}
     */
    public static HealthStatus up() {
        return HealthStatus.builder()
                .status(Status.UP)
                .timestamp(Instant.now())
                .build();
    }

    /**
     * Creates an unhealthy {@code HealthStatus} with one or more diagnostic messages.
     *
     * @param msgs one or more messages describing the failure
     * @return a HealthStatus with {@link Status#DOWN} and provided messages
     */
    public static HealthStatus down(String... msgs) {
        HealthStatusBuilder builder = HealthStatus.builder()
                .status(Status.DOWN)
                .timestamp(Instant.now());
        for (String msg : msgs) {
            builder.message(msg);
        }
        return builder.build();
    }
}
