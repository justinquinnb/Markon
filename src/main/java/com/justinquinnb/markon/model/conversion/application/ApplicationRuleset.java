package com.justinquinnb.markon.model.conversion.application;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;
import com.justinquinnb.markon.model.conversion.abstractlang.Document;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * A LinkedHashMapping of {@link AbstractContent} types to markup-application methods that defines translation
 * from the language-agnostic {@link AbstractContentTree} to the desired markup language.
 */
public class ApplicationRuleset implements
    Iterable<Entry<Class<? extends AbstractContent>, Function<AbstractContent, String>>>
{
    private final LinkedHashMap<Class<? extends AbstractContent>, Function<AbstractContent, String>> ruleset;
    private Function<String, String> postProcessor = this::doNothing;

    /**
     * Creates a new {@code ApplicationRuleset} with the given ruleset and post-processor.
     * @param ruleset the mapping that defines derivation of marked-up text from an
     * {@link AbstractContentTree}
     * @param postProcessor the function applied to text immediately after it has been marked-up
     */
    public ApplicationRuleset(
        LinkedHashMap<Class<? extends AbstractContent>, Function<AbstractContent, String>> ruleset,
        Function<String, String> postProcessor
    ) {
        this.ruleset = ruleset;

        if (!ruleset.containsKey(Document.class)) {
            this.ruleset.put(Document.class, ApplicationRuleset::toUnboundedDoc);
        }

        this.postProcessor = postProcessor;
    }

    /**
     * Creates a new {@code ApplicationRuleset} with the given ruleset and post-processor.
     * @param ruleset the mapping that defines derivation of marked-up text from an
     * {@link AbstractContentTree}
     */
    public ApplicationRuleset(
        LinkedHashMap<Class<? extends AbstractContent>, Function<AbstractContent, String>> ruleset
    ) {
        this.ruleset = ruleset;
        if (!ruleset.containsKey(Document.class)) {
            this.ruleset.put(Document.class, ApplicationRuleset::toUnboundedDoc);
        }
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
     * The default rule applied to {@link Document}-type content.
     * @param content the content to process
     * @return the content with no markup applied
     */
    private static String toUnboundedDoc(AbstractContent content) {
        return content.getDigestedString();
    }

    /**
     * Gets the {@link ApplicationRuleset} that defines derivation of text using {@code this}
     * markup language from an {@link AbstractContentTree}.
     *
     * @return the {@link ApplicationRuleset} that defines derivation of text using {@code this}
     * markup language from an {@link AbstractContentTree}
     */
    public LinkedHashMap<Class<? extends AbstractContent>, Function<AbstractContent, String>> getApplicationRuleset() {
        return this.ruleset;
    }

    /**
     * Gets {@code this} ruleset's post-processor.
     * @return {@code this} ruleset's post-processor
     */
    public Function<String, String> getPostProcessor() {
        return this.postProcessor;
    }

    @Override
    public Iterator<Entry<Class<? extends AbstractContent>, Function<AbstractContent, String>>> iterator() {
        return this.ruleset.entrySet().iterator();
    }
}
