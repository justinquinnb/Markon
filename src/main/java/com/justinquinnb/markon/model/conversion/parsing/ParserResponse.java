package com.justinquinnb.markon.model.conversion.parsing;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;

/**
 * A parser method's response
 */
public class ParserResponse {
    private final AbstractContent content;
    private boolean ignoreEmbedded = false;

    public ParserResponse(AbstractContent content) {
        this.content = content;
    }

    public ParserResponse(AbstractContent content, boolean ignoreEmbedded) {
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
