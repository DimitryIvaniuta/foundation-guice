package com.datafactory.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

/**
 * ErrorEnvelope encapsulates the context and details for a processing error
 * encountered during OCR or metadata extraction.
 * <p>
 * It contains the original record key, the original payload that failed,
 * an error message describing the failure, and a timestamp of when the error occurred.
 * </p>
 */
@Value
@Builder
public class ErrorEnvelope {

    /**
     * The unique key of the record that failed processing.
     */
    String key;

    /**
     * The original MetaData payload that was being processed when the error occurred.
     */
    MetaData original;

    /**
     * A descriptive error message or exception message indicating the failure reason.
     */
    String errorMessage;

    /**
     * The timestamp when the error occurred.
     */
    Instant timestamp;
}
