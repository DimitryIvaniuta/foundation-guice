package com.github.dimitryivaniuta.foundation.persistence;

import java.io.IOException;

/**
 * Defines the contract for persisting OCR-processed documents to an external sink.
 * <p>
 * Implementations may write documents to disk, cloud storage, databases, or message queues.
 * </p>
 *
 */
public interface DocumentSink<T> {

    /**
     * Persists the given document to the configured sink.
     * <p>
     * Implementations should handle creation of necessary resources (e.g., directories,
     * connections) and throw an {@link IOException} if persistence fails.
     * </p>
     *
     * @param document the OCR-processed document to persist
     * @throws IOException if an I/O error occurs during persistence
     */
    void write(T document) throws IOException;
}
