package com.foundation.streams;

import com.foundation.config.Config;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

/**
 * Dagger module responsible for providing and configuring the Kafka Streams client.
 * <p>
 * This module constructs the {@link Properties} required to initialize a
 * {@link KafkaStreams} instance—drawing values from the application {@link Config}—
 * and exposes a singleton {@link KafkaStreams} built from a provided {@link Topology}.
 * </p>
 * <p>
 * It also registers a shutdown hook to ensure graceful termination of the Streams application.
 * </p>
 *
 * @see Config
 * @see Topology
 * @see KafkaStreams
 */
@Module
public class KafkaStreamsModule {

    /**
     * Builds and provides the configuration {@link Properties} for Kafka Streams.
     * <p>
     * Configures bootstrap servers, application ID, default SerDes, schema registry,
     * and optional security (SSL/TLS) settings based on the provided {@link Config}.
     * </p>
     *
     * @param config the application configuration containing environment-driven settings
     * @return a singleton {@link Properties} instance for Kafka Streams
     */
    @Provides
    @Singleton
    public Properties provideStreamsConfig(final Config config) {
        Properties props = new Properties();

        // Kafka cluster bootstrap servers
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBootstrapServers());
        // Unique identifier for this Streams application
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getApplicationId());
        // Default SerDes: String for keys, String for values unless overridden downstream
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class.getName());

        // Schema registry URL for Avro/Protobuf integration (if used)
        props.put("schema.registry.url", config.getSchemaRegistryUrl());

        // Optional SSL/TLS configuration
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, config.getKafkaSecurityProtocol());
        if (config.getKafkaTruststorePath() != null && !config.getKafkaTruststorePath().isEmpty()) {
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, config.getKafkaTruststorePath());
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, config.getKafkaTruststorePassword());
        }
        if (config.getKafkaKeystorePath() != null && !config.getKafkaKeystorePath().isEmpty()) {
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, config.getKafkaKeystorePath());
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, config.getKafkaKeystorePassword());
        }

        // Tune commit interval and cache size for throughput/latency balance
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10_000);
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10 * 1024 * 1024);

        return props;
    }

    /**
     * Constructs and provides a singleton {@link KafkaStreams} instance.
     * <p>
     * The provided {@link Topology} defines the processing graph. A JVM shutdown
     * hook is registered to invoke {@code KafkaStreams.close()} on application exit,
     * ensuring a clean shutdown.
     * </p>
     *
     * @param streamsConfig the Kafka Streams configuration properties
     * @param topology      the processing topology to execute
     * @return a singleton KafkaStreams instance
     */
    @Provides
    @Singleton
    public KafkaStreams provideKafkaStreams(final Properties streamsConfig,
                                            final Topology topology) {
        KafkaStreams streams = new KafkaStreams(topology, streamsConfig);
        // Ensure graceful shutdown on JVM exit
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        return streams;
    }
}
