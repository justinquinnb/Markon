package com.justinquinnb.markon.model.conversion.parsing;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import java.util.PriorityQueue;

/**
 * The context surrounding a parsing operation, used to determine whether the parsing operation
 * should occur.
 */
public class ParsingContext {

    /**
     * The start index of the match to potentially be parsed within the surrounding/parent string
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
     * The initial surrounding/parent text prior to all parsing operations
     */
    private final String initialText;

    public ParsingContext(int matchStartIndex, String matchText, PriorityQueue<AbstractContent> currentlyParsedContent,
        String initialText) {
        this.matchStartIndex = matchStartIndex;
        this.matchText = matchText;
        this.currentlyParsedContent = currentlyParsedContent;
        this.initialText = initialText;
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

    public String getInitialText() {
        return initialText;
    }
}
