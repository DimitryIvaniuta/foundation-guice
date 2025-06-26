package com.datafactory.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * MetaData represents the initial payload for invoice processing.
 * <p>
 * It contains a unique document key and the list of image paths
 * for each page extracted from the source PDF.
 * </p>
 */
@Value
@Builder
public class MetaData {

    /**
     * Unique identifier for the invoice document.
     */
    String key;

    /**
     * File system paths to each page image of the invoice.
     * These images are later processed by the OCR pipeline.
     */
    List<String> pageImagePaths;
}