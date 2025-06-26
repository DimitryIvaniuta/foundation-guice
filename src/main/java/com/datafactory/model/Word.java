package com.datafactory.model;

import lombok.Builder;
import lombok.Value;

/**
 * Represents a single recognized word with its textual content
 * and bounding-box coordinates on the page.
 */
@Value
@Builder
public class Word {
    /** The textual content of the word. */
    String text;

    /** The x-coordinate of the top-left corner of the bounding box. */
    int x1;

    /** The y-coordinate of the top-left corner of the bounding box. */
    int y1;

    /** The x-coordinate of the bottom-right corner of the bounding box. */
    int x2;

    /** The y-coordinate of the bottom-right corner of the bounding box. */
    int y2;
}
