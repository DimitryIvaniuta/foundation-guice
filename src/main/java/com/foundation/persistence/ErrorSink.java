package com.foundation.persistence;

import com.datafactory.model.ErrorEnvelope;

/**
 * Defines the contract for publishing error envelopes resulting from
 * OCR or processing failures to an external error sink (e.g., Kafka).
 * <p>
 * Implementations should ensure reliable delivery or appropriate logging
 * of failed records for later analysis or replay.
 * </p>
 */
public interface ErrorSink {

    /**
     * Publishes the given {@link ErrorEnvelope} to the configured sink.
     *
     * @param envelope the error envelope containing context and error details
     */
    void publishError(ErrorEnvelope envelope);
}