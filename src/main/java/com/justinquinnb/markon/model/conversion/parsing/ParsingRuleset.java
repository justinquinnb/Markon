package com.justinquinnb.markon.model.conversion.parsing;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A mapping of regular expression {@link Pattern}s to match-ingesting parser methods that defines
 * translation from a markup language to the language-agnostic {@link AbstractContentTree}
 * representation.
 */
public class ParsingRuleset implements Iterable<ParsingRule>
{
    private final List<ParsingRule> rules;
    private Function<String, String> preProcessor = null;
    private Function<AbstractContentTree, AbstractContentTree> postProcessor = null;

    /**
     * Creates a new {@code ParsingRuleset} with the given ruleset and pre-processor.
     * @param rules the rules that define markup to {@link AbstractContent} conversion
     */
    public ParsingRuleset(List<ParsingRule> rules) {
        this.rules = rules;
    }

    /**
     * Gets the mapping that defines derivation of an {@link AbstractContentTree}
     * from text written in {@code this} markup language.
     *
     * @return the mapping that defines derivation of an {@link AbstractContentTree}
     * from text written in {@code this} markup language
     */
    public List<ParsingRule> getRules() {
        return this.rules;
    }

    public void setPreProcessor(Function<String, String> preProcessor) {
        this.preProcessor = preProcessor;
    }

    public Function<String, String> getPreProcessor() {
        return this.preProcessor;
    }

    public void setPostProcessor(Function<AbstractContentTree, AbstractContentTree> postProcessor) {
        this.postProcessor = postProcessor;
    }

    public Function<AbstractContentTree, AbstractContentTree> getPostProcessor() {
        return this.postProcessor;
    }

    public void addRule(Pattern pattern, Function<String, AbstractContent> parser) {
        this.rules.add(new ParsingRule(pattern, parser));
    }

    public void addRule(Pattern pattern, Function<String, AbstractContent> parser, String name) {
        this.rules.add(new ParsingRule(pattern, parser, name));
    }

    public void addRule(Pattern pattern, Function<String, AbstractContent> parser,
        Function<ParsingContext, Boolean> parsingCondition) {
        this.rules.add(new ParsingRule(pattern, parser, parsingCondition));
    }

    public void addRule(Pattern pattern, Function<String, AbstractContent> parser, String name,
        Function<ParsingContext, Boolean> parsingCondition) {
        this.rules.add(new ParsingRule(pattern, parser, name, parsingCondition));
    }

    @Override
    public Iterator<ParsingRule> iterator() {
        return this.rules.iterator();
    }
}
