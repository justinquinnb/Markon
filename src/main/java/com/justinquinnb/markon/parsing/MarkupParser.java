package com.justinquinnb.markon.parsing;

/**
 * A type capable of parsing marked-up text into the language-agnostic
 * {@link ParsedMarkupContentTree} representation.
 */
public interface MarkupParser {

    /**
     * Parses the provided marked-up {@code text} using the provided {@code ruleset} into a
     * {@link ParsedMarkupContentTree}.
     *
     * @param text the marked-up (or non-marked-up) text to parse
     * @param ruleset the rules to use when parsing the text
     * @return the {@code ParsedMarkupContentTree} representation of the input {@code text}
     */
    public ParsedMarkupContentTree parse(String text, ParsingRuleset ruleset);
}
