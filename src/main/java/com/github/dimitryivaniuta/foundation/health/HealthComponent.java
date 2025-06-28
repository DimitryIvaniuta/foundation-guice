package com.github.dimitryivaniuta.foundation.health;

import dagger.Component;
import jakarta.inject.Singleton;

/**
 * HealthComponent is the Dagger component that assembles all health‐check
 * bindings (from {@link HealthModule}) into a single injectable graph.
 * <p>
 * Use {@code DaggerHealthComponent.create().getHealthChecker()} to obtain
 * your fully‐configured {@link HealthChecker}, which will in turn execute
 * each registered checker (e.g. {@link DefaultHealthChecker}) and aggregate
 * them in the {@link CompositeHealthChecker}.
 * </p>
 */
@Singleton
@Component(modules = HealthModule.class)
public interface HealthComponent {

    /**
     * Returns the composite {@link HealthChecker} instance.
     * <p>
     * This checker runs all bound {@link HealthChecker} implementations
     * (via {@code @IntoSet} in {@link HealthModule}) and aggregates their
     * liveness/readiness into a single {@link HealthStatus}.
     * </p>
     *
     * @return the application’s singleton HealthChecker
     */
    HealthChecker getHealthChecker();
}