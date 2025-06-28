package com.github.dimitryivaniuta.foundation.config;

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
        // Provide required environment settings via system properties for tests
        System.setProperty("APPLICATION_ID", "test-app");
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        System.setProperty("SCHEMA_REGISTRY_URL", "http://localhost:8081");
        System.setProperty("INPUT_TOPIC", "test-input-topic");
        System.setProperty("OUTPUT_TOPIC", "test-output-topic");
        System.setProperty("GV_THRESHOLD", "5");
        System.setProperty("RUN_GV_IN_PARALLEL", "false");
        System.setProperty("GOOGLE_CREDENTIALS_PATH", "/tmp/creds.json");
        System.setProperty("KAFKA_SECURITY_PROTOCOL", "PLAINTEXT");
        System.setProperty("KAFKA_TRUSTSTORE_PATH", "/tmp/truststore.jks");
        System.setProperty("KAFKA_TRUSTSTORE_PASSWORD", "trustpass");
        System.setProperty("KAFKA_KEYSTORE_PATH", "/tmp/keystore.jks");
        System.setProperty("KAFKA_KEYSTORE_PASSWORD", "keypass");
        System.setProperty("VISION_API_TIMEOUT_MS", "10000");
        System.setProperty("HEALTH_CHECK_INTERVAL_SEC", "30");

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
