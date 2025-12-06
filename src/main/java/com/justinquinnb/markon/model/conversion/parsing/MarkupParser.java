package com.justinquinnb.markon.model.conversion.parsing;

import com.justinquinnb.markon.model.MarkupLanguage;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;

/**
 * A type capable of parsing marked-up text into the language-agnostic {@link AbstractContentTree}
 * representation.
 */
public interface MarkupParser {

    /**
     * Parses the provided marked-up {@code text} using the provided {@code ruleset} into an
     * {@link AbstractContentTree}.
     *
     * @param text the marked-up (or non-marked-up) text to parse
     * @param ruleset the rules to use when parsing the text
     * @return the {@code AbstractContentTree} representation of the input {@code text}
     */
    public AbstractContentTree parse(String text, ParsingRuleset ruleset);

    /**
     * Parses the provided marked-up {@code text} using the provided {@code ruleset} into an
     * {@link AbstractContentTree}.
     *
     * @param text the marked-up (or non-marked-up) text to parse
     * @param language the language the marked-up text is in
     * @return the {@code AbstractContentTree} representation of the input {@code text}
     */
    public default AbstractContentTree parse(String text, MarkupLanguage language) {
        return parse(text, language.getParsingRuleset());
    }
}
