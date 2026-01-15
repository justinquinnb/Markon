package com.justinquinnb.markon.model.conversion.parsing;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;

/**
 * A single result from a parsing operation, usually packaged alongside others as part of a
 * {@link ParserResponse}.
 */
public class ParsedContent {
    private final AbstractContent content;
    private boolean ignoreEmbedded = false;

    public ParsedContent(AbstractContent content) {
        this.content = content;
    }

    public ParsedContent(AbstractContent content, boolean ignoreEmbedded) {
        this.content = content;
        this.ignoreEmbedded = ignoreEmbedded;
    }

    public AbstractContent getContent() {
        return content;
    }

    public boolean isEmbeddedIgnored() {
        return ignoreEmbedded;
    }
}
