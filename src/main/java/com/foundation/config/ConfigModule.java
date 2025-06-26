package com.foundation.config;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;

/**
 * Dagger module responsible for providing application-wide configuration.
 * <p>
 * This module uses {@link EnvConfigProvider} to load environment-driven
 * settings into an immutable {@link Config} object. The provided
 * Config instance is a singleton within the Dagger component scope.
 * </p>
 *
 * @see EnvConfigProvider
 * @see Config
 */
@Module
public class ConfigModule {

    /**
     * Provides the immutable application {@link Config} object.
     * <p>
     * Loads all required settings (Kafka servers, topics, timeouts, credentials, etc.)
     * from environment variables or system properties, applying any necessary
     * defaults or validations.
     * </p>
     *
     * @return a singleton Config instance populated with environment settings
     */
    @Provides
    @Singleton
    public Config provideConfig() {
        return EnvConfigProvider.loadConfig();
    }
}
