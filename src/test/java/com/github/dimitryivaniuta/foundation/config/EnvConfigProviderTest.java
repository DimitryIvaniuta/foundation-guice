package com.github.dimitryivaniuta.foundation.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EnvConfigProvider} to verify loading of configuration
 * from environment variables or system properties, and enforcement of required fields.
 */
class EnvConfigProviderTest {

    /**
     * Backup of original system properties to restore after tests.
     */
    private String originalAppId;
    private String originalBootstrap;
    private String originalSchemaUrl;
    private String originalInputTopic;
    private String originalOutputTopic;
    private String originalGvThreshold;
    private String originalRunParallel;
    private String originalCredsPath;
    private String originalSecProtocol;
    private String originalTruststorePath;
    private String originalTruststorePass;
    private String originalKeystorePath;
    private String originalKeystorePass;
    private String originalTimeout;
    private String originalHealthInterval;

    /**
     * Preserve existing system properties before each test and clear them.
     */
    @BeforeEach
    void setUp() {
        originalAppId           = System.getProperty("APPLICATION_ID");
        originalBootstrap       = System.getProperty("KAFKA_BOOTSTRAP_SERVERS");
        originalSchemaUrl       = System.getProperty("SCHEMA_REGISTRY_URL");
        originalInputTopic      = System.getProperty("INPUT_TOPIC");
        originalOutputTopic     = System.getProperty("OUTPUT_TOPIC");
        originalGvThreshold     = System.getProperty("GV_THRESHOLD");
        originalRunParallel     = System.getProperty("RUN_GV_IN_PARALLEL");
        originalCredsPath       = System.getProperty("GOOGLE_CREDENTIALS_PATH");
        originalSecProtocol     = System.getProperty("KAFKA_SECURITY_PROTOCOL");
        originalTruststorePath  = System.getProperty("KAFKA_TRUSTSTORE_PATH");
        originalTruststorePass  = System.getProperty("KAFKA_TRUSTSTORE_PASSWORD");
        originalKeystorePath    = System.getProperty("KAFKA_KEYSTORE_PATH");
        originalKeystorePass    = System.getProperty("KAFKA_KEYSTORE_PASSWORD");
        originalTimeout         = System.getProperty("VISION_API_TIMEOUT_MS");
        originalHealthInterval  = System.getProperty("HEALTH_CHECK_INTERVAL_SEC");
        System.clearProperty("APPLICATION_ID");
        System.clearProperty("KAFKA_BOOTSTRAP_SERVERS");
        System.clearProperty("SCHEMA_REGISTRY_URL");
        System.clearProperty("INPUT_TOPIC");
        System.clearProperty("OUTPUT_TOPIC");
        System.clearProperty("GV_THRESHOLD");
        System.clearProperty("RUN_GV_IN_PARALLEL");
        System.clearProperty("GOOGLE_CREDENTIALS_PATH");
        System.clearProperty("KAFKA_SECURITY_PROTOCOL");
        System.clearProperty("KAFKA_TRUSTSTORE_PATH");
        System.clearProperty("KAFKA_TRUSTSTORE_PASSWORD");
        System.clearProperty("KAFKA_KEYSTORE_PATH");
        System.clearProperty("KAFKA_KEYSTORE_PASSWORD");
        System.clearProperty("VISION_API_TIMEOUT_MS");
        System.clearProperty("HEALTH_CHECK_INTERVAL_SEC");
    }

    /**
     * Restore original system properties after each test.
     */
    @AfterEach
    void tearDown() {
        setOrClear("APPLICATION_ID", originalAppId);
        setOrClear("KAFKA_BOOTSTRAP_SERVERS", originalBootstrap);
        setOrClear("SCHEMA_REGISTRY_URL", originalSchemaUrl);
        setOrClear("INPUT_TOPIC", originalInputTopic);
        setOrClear("OUTPUT_TOPIC", originalOutputTopic);
        setOrClear("GV_THRESHOLD", originalGvThreshold);
        setOrClear("RUN_GV_IN_PARALLEL", originalRunParallel);
        setOrClear("GOOGLE_CREDENTIALS_PATH", originalCredsPath);
        setOrClear("KAFKA_SECURITY_PROTOCOL", originalSecProtocol);
        setOrClear("KAFKA_TRUSTSTORE_PATH", originalTruststorePath);
        setOrClear("KAFKA_TRUSTSTORE_PASSWORD", originalTruststorePass);
        setOrClear("KAFKA_KEYSTORE_PATH", originalKeystorePath);
        setOrClear("KAFKA_KEYSTORE_PASSWORD", originalKeystorePass);
        setOrClear("VISION_API_TIMEOUT_MS", originalTimeout);
        setOrClear("HEALTH_CHECK_INTERVAL_SEC", originalHealthInterval);
    }

    /**
     * Helper to restore or clear a system property.
     */
    private void setOrClear(String key, String value) {
        if (value != null) System.setProperty(key, value);
        else System.clearProperty(key);
    }

    /**
     * Verifies that loadConfig() successfully reads all required properties
     * and populates a Config object with the correct values.
     */
    @Test
    void testLoadConfigWithAllProperties() {
        System.setProperty("APPLICATION_ID", "app-1");
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "host1:9092,host2:9092");
        System.setProperty("SCHEMA_REGISTRY_URL", "http://schema:8081");
        System.setProperty("INPUT_TOPIC", "in-topic");
        System.setProperty("OUTPUT_TOPIC", "out-topic");
        System.setProperty("GV_THRESHOLD", "10");
        System.setProperty("RUN_GV_IN_PARALLEL", "true");
        System.setProperty("GOOGLE_CREDENTIALS_PATH", "/tmp/key.json");
        System.setProperty("KAFKA_SECURITY_PROTOCOL", "SSL");
        System.setProperty("KAFKA_TRUSTSTORE_PATH", "/tmp/trust.jks");
        System.setProperty("KAFKA_TRUSTSTORE_PASSWORD", "trustpwd");
        System.setProperty("KAFKA_KEYSTORE_PATH", "/tmp/key.jks");
        System.setProperty("KAFKA_KEYSTORE_PASSWORD", "keypwd");
        System.setProperty("VISION_API_TIMEOUT_MS", "15000");
        System.setProperty("HEALTH_CHECK_INTERVAL_SEC", "60");

        Config cfg = EnvConfigProvider.loadConfig();
        assertEquals("app-1", cfg.getApplicationId());
        assertEquals("host1:9092,host2:9092", cfg.getKafkaBootstrapServers());
        assertEquals("http://schema:8081", cfg.getSchemaRegistryUrl());
        assertEquals("in-topic", cfg.getInputTopic());
        assertEquals("out-topic", cfg.getOutputTopic());
        assertEquals(10, cfg.getGvThreshold());
        assertTrue(cfg.isRunGVInParallel());
//        assertEquals("/tmp/key.json", cfg.getGoogleCredentialsPath());
        assertEquals("SSL", cfg.getKafkaSecurityProtocol());
        assertEquals("/tmp/trust.jks", cfg.getKafkaTruststorePath());
        assertEquals("trustpwd", cfg.getKafkaTruststorePassword());
        assertEquals("/tmp/key.jks", cfg.getKafkaKeystorePath());
        assertEquals("keypwd", cfg.getKafkaKeystorePassword());
        assertEquals(15000L, cfg.getVisionApiTimeoutMs());
        assertEquals(60, cfg.getHealthCheckIntervalSec());
    }

    /**
     * Verifies that missing required properties cause a descriptive exception.
     */
    @Test
    void testMissingRequiredPropertyThrows() {
        // Only set some properties, omit APPLICATION_ID
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                EnvConfigProvider::loadConfig
        );
        assertTrue(
                ex.getMessage().contains("APPLICATION_ID"),
                "Exception message should mention missing APPLICATION_ID"
        );
    }
}
