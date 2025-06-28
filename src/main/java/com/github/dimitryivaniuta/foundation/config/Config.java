package com.github.dimitryivaniuta.foundation.config;

import lombok.Builder;
import lombok.Value;

/**
 * Immutable configuration holder for the Foundation SDK.
 * <p>
 * This class encapsulates all environment-driven settings required
 * by configuration, Kafka Streams, gRPC clients, persistence,
 * and health checks. Use {@link EnvConfigProvider} to load values
 * into an instance of this class at startup.
 * </p>
 */
@Value
@Builder
public class Config {

    /**
     * Comma-separated list of Kafka bootstrap servers (host:port).
     * Used to configure both Kafka clients and Kafka Streams.
     * Example: "broker1:9092,broker2:9092".
     */
    String kafkaBootstrapServers;

    /**
     * URL of the Schema Registry (if using Avro/Protobuf).
     * Example: "http://schemaregistry:8081".
     */
    String schemaRegistryUrl;

    /**
     * Application identifier for the Kafka Streams application.
     * This value is used as the Kafka Streams application.id.
     */
    String applicationId;

    /**
     * Name of the input Kafka topic for raw metadata messages.
     */
    String inputTopic;

    /**
     * Name of the output Kafka topic for enriched metadata messages.
     */
    String outputTopic;

    /**
     * Maximum number of pages per RPC batch when calling the Vision API.
     * If the number of pages exceeds this threshold, requests will be chunked.
     */
    int gvThreshold;

    /**
     * Whether to send each Vision API request in parallel (true)
     * or to batch sequentially up to gvThreshold (false).
     */
    boolean runGVInParallel;

    /**
     * File system path to the Google Cloud credentials JSON file.
     * This should point to a service account key with the Vision API scope.
     */
    String googleCredentialsPath;

    /**
     * Security protocol for Kafka connections. Example: "SSL" or "PLAINTEXT".
     */
    String kafkaSecurityProtocol;

    /**
     * Path to the SSL truststore file (if using SSL/TLS).
     */
    String kafkaTruststorePath;

    /**
     * Password for the SSL truststore (if using SSL/TLS).
     */
    String kafkaTruststorePassword;

    /**
     * Path to the SSL keystore file (if using SSL/TLS with client certs).
     */
    String kafkaKeystorePath;

    /**
     * Password for the SSL keystore (if using SSL/TLS with client certs).
     */
    String kafkaKeystorePassword;

    /**
     * <p>
     * Timeout in milliseconds for gRPC Vision API calls.
     * This value will be used to configure
     * {@code ImageAnnotatorSettings} transport channel settings.
     * </p>
     */
    long visionApiTimeoutMs;

    /**
     * Interval in seconds for health check liveness probes.
     */
    int healthCheckIntervalSec;

    /**
     * Base directory path where OCR-processed documents will be written.
     */
    String documentSinkPath;

    /**
     * Kafka topic name for publishing error envelopes.
     */
    String errorTopic;
}
