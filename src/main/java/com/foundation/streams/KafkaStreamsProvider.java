package com.foundation.streams;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

/**
 * Provider for a singleton {@link KafkaStreams} instance configured with
 * application-specific topology and properties.
 * <p>
 * This class initializes the Kafka Streams client on construction and
 * registers a JVM shutdown hook to ensure graceful shutdown of the Streams
 * application. It implements {@link Provider} to integrate seamlessly with
 * Dagger or other DI frameworks that support JSR-330.
 * </p>
 *
 * @see KafkaStreamsModule
 */
@Singleton
public class KafkaStreamsProvider implements Provider<KafkaStreams> {

    /**
     * The singleton KafkaStreams instance managed by this provider.
     */
    private final KafkaStreams streams;

    /**
     * Constructs the provider by building the KafkaStreams instance using the
     * provided {@link Topology} and {@link Properties}.
     * <p>
     * A shutdown hook is registered to invoke {@code streams.close()} when the
     * JVM exits, ensuring the Streams application shuts down cleanly.
     * </p>
     *
     * @param streamsConfig the Kafka Streams configuration properties
     * @param topology      the processing topology to execute
     */
    @Inject
    public KafkaStreamsProvider(final Properties streamsConfig,
                                final Topology topology) {
        this.streams = new KafkaStreams(topology, streamsConfig);
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    /**
     * Returns the singleton {@link KafkaStreams} instance.
     * <p>
     * Subsequent calls return the same instance created during provider construction.
     * </p>
     *
     * @return the singleton KafkaStreams application
     */
    @Override
    public KafkaStreams get() {
        return streams;
    }
}
