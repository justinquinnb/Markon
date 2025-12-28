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
    private Function<AbstractContentTree, AbstractContentTree> preProcessor = null;
    private Function<String, String> postProcessor = null;

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

    public void setPreProcessor(Function<AbstractContentTree, AbstractContentTree> preProcessor) {
        this.preProcessor = preProcessor;
    }

    public Function<AbstractContentTree, AbstractContentTree> getPreProcessor() {
        return this.preProcessor;
    }

    public void setPostProcessor(Function<String, String> postProcessor) {
        this.postProcessor = postProcessor;
    }

    public Function<String, String> getPostProcessor() {
        return this.postProcessor;
    }

    @Override
    public Iterator<Entry<Class<? extends AbstractContent>, Function<AbstractContent, String>>> iterator() {
        return this.ruleset.entrySet().iterator();
    }
}
