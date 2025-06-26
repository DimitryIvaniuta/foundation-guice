package com.datafactory.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Represents a complete OCR-processed document containing multiple pages.
 */
@Value
@Builder
public class Document {
    /** Unique document identifier. */
    String key;

    /** List of pages within this document. */
    List<Page> pages;
}