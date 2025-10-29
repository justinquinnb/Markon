package com.justinquinnb.markon.application;

import com.justinquinnb.markon.abstractlang.AbstractContentTree;

/**
 * A type capable of applying markup to the content in the language-agnostic
 * {@link AbstractContentTree} to arrive at text in the desired markup language.
 */
public interface MarkupApplier {

    /**
     * Applies the desired markup rules to the content provided in the abstract content
     * {@code tree}, producing marked-up text.
     *
     * @param tree the abstract content tree to generate marked-up text from
     * @param ruleset the rules to use when applying markup
     * @return the {@code AbstractContentTree} represented by text in the markup language defined by
     * the provided {@code ruleset}
     */
    public String apply(AbstractContentTree tree, ApplicationRuleset ruleset);
}
