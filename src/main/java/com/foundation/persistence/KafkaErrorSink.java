package com.foundation.persistence;


import com.datafactory.model.ErrorEnvelope;
import com.foundation.config.Config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Kafka-based implementation of {@link ErrorSink}.
 * <p>
 * Publishes {@link ErrorEnvelope} messages to a configured Kafka topic
 * using JSON serialization.
 * </p>
 * <p>
 * This class initializes a singleton Kafka producer with the following configs:
 * <ul>
 *   <li>acks=all</li>
 *   <li>retries=Integer.MAX_VALUE</li>
 *   <li>String key and value serializers</li>
 * </ul>
 * A shutdown hook ensures the producer is flushed and closed.
 * </p>
 */
@Singleton
public class KafkaErrorSink implements ErrorSink {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaErrorSink.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final KafkaProducer<String, String> producer;
    private final String topic;

    /**
     * Constructs the Kafka error sink.
     *
     * @param config the application configuration (provides bootstrap servers)
     * @param topic  the Kafka topic to which to publish error messages
     */
    @Inject
    public KafkaErrorSink(final Config config,
                          @javax.inject.Named("error.sink.topic") final String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));

        this.producer = new KafkaProducer<>(props);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            producer.flush();
            producer.close();
        }));
    }

    /**
     * Serializes the given {@link ErrorEnvelope} to JSON and publishes it
     * to the configured Kafka topic with the envelope's key as the record key.
     * <p>
     * Errors during serialization or publishing are logged.
     * </p>
     *
     * @param envelope the error envelope containing context and failure details
     */
    @Override
    public void publishError(ErrorEnvelope envelope) {
        try {
            String payload = MAPPER.writeValueAsString(envelope);
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, envelope.getKey(), payload);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOGGER.error("Failed to publish error envelope for key {}", envelope.getKey(), exception);
                }
            });
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize ErrorEnvelope for key {}", envelope.getKey(), e);
        }
    }
}
