package com.github.dimitryivaniuta.foundation.health;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import javax.inject.Singleton;

import java.util.Set;

/**
 * Dagger module for health check components.
 * <p>
 * This module uses Dagger multibindings to register multiple
 * {@link HealthChecker} implementations into a set, and
 * provides a composite health checker that aggregates their statuses.
 * </p>
 *
 * @see HealthChecker
 * @see DefaultHealthChecker
 * @see CompositeHealthChecker
 */
@Module
public abstract class HealthModule {

    /**
     * Contributes the default health checker to the set of HealthChecker implementations.
     * <p>
     * By using {@link IntoSet}, multiple health checkers can be bound
     * and will be run by the composite checker.
     * </p>
     *
     * @param defaultHealthChecker the default health checker implementation
     * @return the bound HealthChecker instance
     */
    @Binds
    @IntoSet
    public abstract HealthChecker bindDefaultHealthChecker(DefaultHealthChecker defaultHealthChecker);

    /**
     * Provides a composite {@link HealthChecker} that aggregates all
     * bound HealthChecker implementations into a single unified health report.
     * <p>
     * The composite runs each individual checker's liveness and readiness,
     * returning DOWN if any checker reports DOWN, otherwise UP. Detailed
     * statuses from each checker are included in the returned {@link HealthStatus}.
     * </p>
     *
     * @param checkers the set of all registered HealthChecker instances
     * @return a singleton CompositeHealthChecker instance
     */
    @Provides
    @Singleton
    public static HealthChecker provideCompositeHealthChecker(Set<HealthChecker> checkers) {
        return new CompositeHealthChecker(checkers);
    }
}
