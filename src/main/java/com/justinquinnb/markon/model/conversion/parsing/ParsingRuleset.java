package com.justinquinnb.markon.model.conversion.parsing;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;
import com.justinquinnb.markon.model.conversion.abstractlang.Document;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A mapping of regular expression {@link Pattern}s to match-ingesting parser methods that defines
 * translation from a markup language to the language-agnostic {@link AbstractContentTree}
 * representation.
 */
public class ParsingRuleset implements
    Iterable<Entry<Pattern, Function<String, ? extends AbstractContent>>>
{
    private final LinkedHashMap<Pattern, Function<String, ? extends AbstractContent>> ruleset;
    private Function<String, String> preProcessor = this::doNothing;

    /**
     * Creates a new {@code ParsingRuleset} with the given ruleset and pre-processor.
     * @param ruleset the rules that define markup to {@link AbstractContent} conversion
     * @param preProcessor the function applied to marked-up text immediately before parsing
     */
    public ParsingRuleset(
        LinkedHashMap<Pattern, Function<String, ? extends AbstractContent>> ruleset,
        Function<String, String> preProcessor
    ) {
        this.ruleset = ruleset;
        this.preProcessor = preProcessor;
    }

    /**
     * Creates a new {@code ParsingRuleset} with the given ruleset and pre-processor.
     * @param ruleset the rules that define markup to {@link AbstractContent} conversion
     */
    public ParsingRuleset(LinkedHashMap<Pattern, Function<String, ? extends AbstractContent>> ruleset) {
        this.ruleset = ruleset;
    }

    /**
     * The default pre-processor used if one is not specified.
     * @param text the text to process
     * @return the processed text to use in parsing
     */
    private String doNothing(String text) {
        return text;
    }

    /**
     * Gets the mapping that defines derivation of an {@link AbstractContentTree}
     * from text written in {@code this} markup language.
     *
     * @return the mapping that defines derivation of an {@link AbstractContentTree}
     * from text written in {@code this} markup language
     */
    public LinkedHashMap<Pattern, Function<String, ? extends AbstractContent>> getParsingRuleset() {
        return this.ruleset;
    }

    /**
     * Gets {@code this} ruleset's pre-processor.
     * @return {@code this} ruleset's pre-processor
     */
    public Function<String, String> getPreProcessor() {
        return this.preProcessor;
    }

    @Override
    public Iterator<Entry<Pattern, Function<String, ? extends AbstractContent>>> iterator() {
        return this.ruleset.entrySet().iterator();
    }
}
