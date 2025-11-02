package com.justinquinnb.markon.builtin.converters;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;
import com.justinquinnb.markon.model.conversion.application.ApplicationRuleset;
import com.justinquinnb.markon.model.conversion.application.MarkupApplier;

public class BasicApplier implements MarkupApplier {

    @Override
    public String apply(AbstractContentTree tree, ApplicationRuleset ruleset) {
        AbstractContentTree workingTree = tree; // TODO make this a deep copy at some point
        //
        return "Tets";
    }
}
