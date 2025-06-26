package com.datafactory.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Represents a line of text consisting of multiple words,
 * preserving reading order.
 */
@Value
@Builder
public class Line {
    /** Ordered list of words in this line. */
    List<Word> words;
}