package com.justinquinnb.markon.model.conversion.parsing;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import java.util.List;

/**
 * A parser method's response
 */
public class ParserResponse {
    private final List<ParsedContent> parsedContent;

    public ParserResponse(ParsedContent... parsedContent) {
        this.parsedContent = List.of(parsedContent);
    }

    public ParserResponse(List<ParsedContent> parsedContent) {
        this.parsedContent = parsedContent;
    }

    public List<ParsedContent> getContent() {
        return parsedContent;
    }

    public static ParserResponse of(ParsedContent... parsedContent) {
        return new ParserResponse(List.of(parsedContent));
    }

    public static ParserResponse of(AbstractContent content) {
        return new ParserResponse(new ParsedContent(content));
    }

    public static ParserResponse of(AbstractContent content, boolean ignoreEmbedded) {
        return new ParserResponse(new ParsedContent(content, ignoreEmbedded));
    }
}
