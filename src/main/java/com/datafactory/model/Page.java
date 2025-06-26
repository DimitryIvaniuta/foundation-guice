package com.datafactory.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Represents a single page within a document, including its
 * metadata and recognized text lines.
 */
@Value
@Builder
public class Page {
    /** Unique key for this page, often documentKey + pageNumber. */
    String pageKey;

    /** Zero-based page number within the document. */
    int pageNumber;

    /** The width of the page image in pixels. */
    int width;

    /** The height of the page image in pixels. */
    int height;

    /** Detected language code for this page (e.g., "en"). */
    String language;

    /** Rotation angle in degrees (0, 90, 180, or 270). */
    int rotation;

    /** Ordered list of recognized text lines. */
    List<Line> lines;
}