package com.justinquinnb.markon.model.conversion.parsing;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An indication of what syntax corresponds to what content type and how text with that
 * syntax should be parsed and interpreted.
 */
public class ParsingRule {

    /**
     * The pattern used to match the desired syntax
     */
    private final Pattern pattern;

    /**
     * The function used to digest the match into {@link AbstractContent}
     */
    private final Function<String, ? extends AbstractContent> parser;

    /**
     * The rule's name. By default, the {@link #pattern} is used.
     */
    private String name;

    /**
     * The condition used to determine whether a match should be parsed or ignored.
     */
    private Function<ParsingContext, Boolean> filter;

    public ParsingRule(Pattern pattern, Function<String, ? extends AbstractContent> parser) {
        this.pattern = pattern;
        this.parser = parser;

        this.name = pattern.pattern();
        this.filter = context -> true; // Always parse
    }

    public ParsingRule(Pattern pattern, Function<String, ? extends AbstractContent> parser,
        String name) {
        this(pattern, parser);
        this.name = name;
    }

    public ParsingRule(Pattern pattern, Function<String, ? extends AbstractContent> parser,
        Function<ParsingContext, Boolean> filter) {
        this(pattern, parser);
        this.filter = filter;
    }

    public ParsingRule(Pattern pattern, Function<String, ? extends AbstractContent> parser,
        String name, Function<ParsingContext, Boolean> filter) {
        this(pattern, parser);
        this.name = name;
        this.filter = filter;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Function<String, ? extends AbstractContent> getParser() {
        return parser;
    }

    public String getName() {
        return name;
    }

    public Function<ParsingContext, Boolean> getFilter() {
        return filter;
    }
}
