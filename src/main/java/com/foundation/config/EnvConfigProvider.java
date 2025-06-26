package com.foundation.config;

import java.util.Map;

/**
 * EnvConfigProvider is responsible for loading and validating
 * environment variables and system properties into a {@link Config}
 * instance. All configurations are sourced from the environment
 * or JVM properties, with sensible defaults applied where appropriate.
 * <p>
 * If a required configuration is missing or invalid, this provider
 * throws an {@link IllegalStateException} to fail fast on startup.
 * </p>
 */
public final class EnvConfigProvider {
    private static final Map<String, String> ENV = System.getenv();

    // Default values for optional settings
    private static final String DEFAULT_BOOTSTRAP = "localhost:9092";
    private static final String DEFAULT_SCHEMA_REGISTRY = "http://localhost:8081";
    private static final int    DEFAULT_GV_THRESHOLD = 15;
    private static final boolean DEFAULT_RUN_PARALLEL = false;
    private static final String DEFAULT_SECURITY_PROTOCOL = "PLAINTEXT";
    private static final long   DEFAULT_VISION_TIMEOUT = 30_000L;
    private static final int    DEFAULT_HEALTH_INTERVAL = 60;

    // Prevent instantiation
    private EnvConfigProvider() {}

    /**
     * Loads all environment-driven settings into a {@link Config} instance.
     *
     * @return a fully populated Config
     * @throws IllegalStateException if any required environment variable is missing or invalid
     */
    public static Config loadConfig() {
        String kafkaServers    = getEnvOrDefault("KAFKA_BOOTSTRAP_SERVERS", DEFAULT_BOOTSTRAP);
        String schemaRegistry  = getEnvOrDefault("SCHEMA_REGISTRY_URL", DEFAULT_SCHEMA_REGISTRY);

        String appId           = getEnvOrThrow("APPLICATION_ID");
        String inputTopic      = getEnvOrThrow("INPUT_TOPIC");
        String outputTopic     = getEnvOrThrow("OUTPUT_TOPIC");

        int gvThreshold        = parseInt(getEnvOrDefault("GV_THRESHOLD", Integer.toString(DEFAULT_GV_THRESHOLD)), "GV_THRESHOLD");
        boolean runParallel    = parseBoolean(getEnvOrDefault("RUN_GV_IN_PARALLEL", Boolean.toString(DEFAULT_RUN_PARALLEL)), "RUN_GV_IN_PARALLEL");

        String credentialsPath = getEnvOrThrow("GOOGLE_APPLICATION_CREDENTIALS");

        String securityProtocol     = getEnvOrDefault("KAFKA_SECURITY_PROTOCOL", DEFAULT_SECURITY_PROTOCOL);
        String truststorePath       = getEnvOrDefault("KAFKA_TRUSTSTORE_PATH", "");
        String truststorePassword   = getEnvOrDefault("KAFKA_TRUSTSTORE_PASSWORD", "");
        String keystorePath         = getEnvOrDefault("KAFKA_KEYSTORE_PATH", "");
        String keystorePassword     = getEnvOrDefault("KAFKA_KEYSTORE_PASSWORD", "");

        long visionTimeout          = parseLong(getEnvOrDefault("VISION_API_TIMEOUT_MS", Long.toString(DEFAULT_VISION_TIMEOUT)), "VISION_API_TIMEOUT_MS");
        int healthInterval          = parseInt(getEnvOrDefault("HEALTH_CHECK_INTERVAL_SEC", Integer.toString(DEFAULT_HEALTH_INTERVAL)), "HEALTH_CHECK_INTERVAL_SEC");

        return Config.builder()
                .kafkaBootstrapServers(kafkaServers)
                .schemaRegistryUrl(schemaRegistry)
                .applicationId(appId)
                .inputTopic(inputTopic)
                .outputTopic(outputTopic)
                .gvThreshold(gvThreshold)
                .runGVInParallel(runParallel)
                .googleCredentialsPath(credentialsPath)
                .kafkaSecurityProtocol(securityProtocol)
                .kafkaTruststorePath(truststorePath)
                .kafkaTruststorePassword(truststorePassword)
                .kafkaKeystorePath(keystorePath)
                .kafkaKeystorePassword(keystorePassword)
                .visionApiTimeoutMs(visionTimeout)
                .healthCheckIntervalSec(healthInterval)
                .build();
    }

    /**
     * Retrieves an environment variable or system property,
     * returning a default if not present or blank.
     *
     * @param name the variable name
     * @param def  the default value
     * @return the environment value or the default
     */
    private static String getEnvOrDefault(String name, String def) {
        String val = ENV.getOrDefault(name, System.getProperty(name));
        return (val == null || val.isBlank()) ? def : val;
    }

    /**
     * Retrieves a required environment variable or system property,
     * throwing if missing or blank.
     *
     * @param name the variable name
     * @return the environment value
     * @throws IllegalStateException if missing or blank
     */
    private static String getEnvOrThrow(String name) {
        String val = ENV.getOrDefault(name, System.getProperty(name));
        if (val == null || val.isBlank()) {
            throw new IllegalStateException(
                    String.format("Required environment variable '%s' is not set", name)
            );
        }
        return val;
    }

    /**
     * Parses an integer from a string, failing with context on error.
     *
     * @param value the string value
     * @param name  the variable name (for error messaging)
     * @return the parsed integer
     * @throws IllegalStateException if parse fails
     */
    private static int parseInt(String value, String name) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    String.format("Environment variable '%s' must be an integer, but was '%s'", name, value),
                    e
            );
        }
    }

    /**
     * Parses a long from a string, failing with context on error.
     *
     * @param value the string value
     * @param name  the variable name (for error messaging)
     * @return the parsed long
     * @throws IllegalStateException if parse fails
     */
    private static long parseLong(String value, String name) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    String.format("Environment variable '%s' must be a long, but was '%s'", name, value),
                    e
            );
        }
    }

    /**
     * Parses a boolean from a string, failing if not 'true' or 'false'.
     *
     * @param value the string value
     * @param name  the variable name (for error messaging)
     * @return the parsed boolean
     * @throws IllegalStateException if value is not 'true' or 'false'
     */
    private static boolean parseBoolean(String value, String name) {
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        throw new IllegalStateException(
                String.format("Environment variable '%s' must be 'true' or 'false', but was '%s'", name, value)
        );
    }
}
