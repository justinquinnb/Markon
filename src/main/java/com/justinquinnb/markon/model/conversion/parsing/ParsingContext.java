package com.justinquinnb.markon.model.conversion.parsing;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.util.TextRegionIndices;
import java.util.PriorityQueue;

/**
 * The context surrounding a parsing operation, used to determine whether the parsing operation
 * should occur.
 */
public class ParsingContext {

    /**
     * The start index of the match to potentially be parsed within the surrounding/parent/source
     * string
     */
    private final int matchStartIndex;

    /**
     * The match text being to potentially parse
     */
    private final String matchText;

    /**
     * The abstract content tree as it stands before parsing potentially occurs
     */
    private final PriorityQueue<AbstractContent> currentlyParsedContent;

    /**
     * The text the match was derived from
     */
    private final String sourceText;

    /**
     * The text regions that have been escaped from parsing
     */
    private final PriorityQueue<TextRegionIndices> ignoredRegions;

    /**
     * Instantiates a collection of parsing context details in a single {@code ParsingContext}
     * object.
     *
     * @param matchStartIndex the start index of the match to potentially be parsed within the
     *                        surrounding/parent/source text
     * @param matchText the match text being to potentially parse
     * @param currentlyParsedContent the abstract content tree as it stands before parsing
     *                               potentially occurs
     * @param sourceText the text the match was derived from
     * @param ignoredRegions the text regions that have been escaped from parsing
     */
    public ParsingContext(
        int matchStartIndex, String matchText,
        PriorityQueue<AbstractContent> currentlyParsedContent, String sourceText,
        PriorityQueue<TextRegionIndices> ignoredRegions
    ) {
        this.matchStartIndex = matchStartIndex;
        this.matchText = matchText;
        this.currentlyParsedContent = currentlyParsedContent;
        this.sourceText = sourceText;
        this.ignoredRegions = ignoredRegions;
    }

    public int getMatchStartIndex() {
        return matchStartIndex;
    }

    public String getMatchText() {
        return matchText;
    }

    public PriorityQueue<AbstractContent> getCurrentlyParsedContent() {
        return currentlyParsedContent;
    }

    public String getSourceText() {
        return sourceText;
    }

    public PriorityQueue<TextRegionIndices> getIgnoredRegions() {
        return ignoredRegions;
    }

    public int getMatchEndIndex() {
        return matchStartIndex + matchText.length();
    }
}
