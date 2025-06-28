package com.github.dimitryivaniuta.foundation.grpc;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Thrown when the Vision client cannot be initialized
 * due to I/O or credential errors.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class VisionClientInitializationException extends RuntimeException {

    private final String credentialsPath;

    /**
     * Constructs a new exception indicating failure to initialize
     * the Vision client.
     *
     * @param credentialsPath path to the GCP credentials that failed to load
     * @param cause the underlying I/O exception
     */
    public VisionClientInitializationException(String credentialsPath, Throwable cause) {
        super(String.format(
                "Failed to initialize ImageAnnotatorClient with credentials at '%s'",
                credentialsPath
        ), cause);
        this.credentialsPath = credentialsPath;
    }
}