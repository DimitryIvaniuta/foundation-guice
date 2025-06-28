package com.foundation.config;

import com.github.dimitryivaniuta.foundation.config.Config;
import com.github.dimitryivaniuta.foundation.config.ConfigModule;
import dagger.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConfigModule} to verify that the Dagger binding
 * correctly provides a singleton {@link Config} instance with expected values.
 */
class ConfigModuleTest {

    /**
     * Test component used to validate ConfigModule bindings.
     */
    @Singleton
    @Component(modules = ConfigModule.class)
    interface TestComponent {
        /**
         * Exposes the {@link Config} instance from the graph.
         *
         * @return the provided Config
         */
        Config config();
    }

    private TestComponent component;

    /**
     * Builds a fresh Dagger component before each test.
     */
    @BeforeEach
    void setUp() {
        component = DaggerConfigModuleTest_TestComponent.create();
    }

    /**
     * Verifies that {@code provideConfig()} returns a non-null Config instance.
     */
    @Test
    void testConfigIsProvided() {
        Config cfg = component.config();
        assertNotNull(cfg, "Config should not be null");
    }

    /**
     * Verifies that ConfigModule provides a singleton within the same component.
     */
    @Test
    void testConfigSingletonWithinComponent() {
        Config first = component.config();
        Config second = component.config();
        assertSame(first, second, "Config should be the same instance within a component");
    }

    /**
     * Verifies that separate components produce distinct Config instances.
     */
    @Test
    void testConfigNotSharedAcrossComponents() {
        TestComponent other = DaggerConfigModuleTest_TestComponent.create();
        assertNotSame(component.config(), other.config(),
                "Config should not be shared across different components");
    }

    /**
     * Verifies that mandatory config fields have default or environment-driven values.
     * In absence of environment variables, defaults declared in EnvConfigProvider should apply.
     */
    @Test
    void testMandatoryConfigValuesNotNullOrEmpty() {
        Config cfg = component.config();
        assertNotNull(cfg.getKafkaBootstrapServers(), "Kafka bootstrap servers should be set");
        assertFalse(cfg.getKafkaBootstrapServers().isEmpty(), "Kafka bootstrap servers should not be empty");
        assertNotNull(cfg.getApplicationId(), "Application ID should be set");
        assertFalse(cfg.getApplicationId().isEmpty(), "Application ID should not be empty");
    }
}
