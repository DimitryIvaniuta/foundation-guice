package com.github.dimitryivaniuta.foundation.persistence;

/**
 * Defines the contract for publishing error envelopes resulting from
 * OCR or processing failures to an external error sink (e.g., Kafka).
 * <p>
 * Implementations should ensure reliable delivery or appropriate logging
 * of failed records for later analysis or replay.
 * </p>
 */
public interface ErrorSink<E> {

    /**
     * Publishes the given error envelope to the configured sink.
     *
     * @param error the error envelope containing context and error details
     */
    void publish(E error);
}