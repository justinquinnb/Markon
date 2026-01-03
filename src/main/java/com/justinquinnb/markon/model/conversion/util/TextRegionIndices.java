package com.justinquinnb.markon.model.conversion.util;

/**
 * A region of text, as defined by its start and end indices
 */
public class TextRegionIndices implements Comparable<TextRegionIndices> {
    private final int startIndex;
    private final int endIndex;

    public TextRegionIndices(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }


    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Assigns the natural order as ascending region start
     */
    @Override
    public int compareTo(TextRegionIndices o) {
        return Integer.compare(this.getStartIndex(), o.getStartIndex());
    }
}