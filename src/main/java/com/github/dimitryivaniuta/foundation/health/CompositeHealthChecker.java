package com.github.dimitryivaniuta.foundation.health;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * CompositeHealthChecker aggregates multiple {@link HealthChecker} implementations
 * to provide a unified health report for both liveness and readiness.
 * <p>
 * It executes each registered checker and combines their statuses:
 * <ul>
 *   <li>If any checker reports {@link Status#DOWN}, the composite status is DOWN.</li>
 *   <li>Otherwise, the composite status is UP.</li>
 * </ul>
 * Detailed results from each checker are included in the returned {@link HealthStatus}.
 * </p>
 *
 * @see HealthChecker
 * @see HealthStatus
 * @see Status
 */
@Singleton
public class CompositeHealthChecker implements HealthChecker {

    private final Set<HealthChecker> checkers;

    /**
     * Constructs a CompositeHealthChecker.
     *
     * @param checkers the set of HealthChecker implementations to aggregate
     */
    @Inject
    public CompositeHealthChecker(Set<HealthChecker> checkers) {
        if (checkers == null || checkers.isEmpty()) {
            throw new IllegalArgumentException("At least one HealthChecker must be provided");
        }
        this.checkers = checkers;
    }

    /**
     * Aggregates liveness checks across all registered HealthCheckers.
     *
     * @return a {@link HealthStatus} representing the combined liveness
     */
    @Override
    public HealthStatus checkLiveness() {
        boolean anyDown = false;
        Map<String, String> details = new HashMap<>();

        for (HealthChecker checker : checkers) {
            HealthStatus status = checker.checkLiveness();
            details.put(checker.getClass().getSimpleName(), status.getStatus().name());
            if (status.getStatus() == Status.DOWN) {
                anyDown = true;
            }
        }

        return HealthStatus.builder()
                .status(anyDown ? Status.DOWN : Status.UP)
                .timestamp(Instant.now())
                .details(details)
                .build();
    }

    /**
     * Aggregates readiness checks across all registered HealthCheckers.
     *
     * @return a {@link HealthStatus} representing the combined readiness
     */
    @Override
    public HealthStatus checkReadiness() {
        boolean anyDown = false;
        Map<String, String> details = new HashMap<>();

        for (HealthChecker checker : checkers) {
            HealthStatus status = checker.checkReadiness();
            details.put(checker.getClass().getSimpleName(), status.getStatus().name());
            if (status.getStatus() == Status.DOWN) {
                anyDown = true;
            }
        }

        return HealthStatus.builder()
                .status(anyDown ? Status.DOWN : Status.UP)
                .timestamp(Instant.now())
                .details(details)
                .build();
    }
}
