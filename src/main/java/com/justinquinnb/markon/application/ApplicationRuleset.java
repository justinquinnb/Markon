package com.justinquinnb.markon.application;

import com.justinquinnb.markon.abstractlang.AbstractContent;
import com.justinquinnb.markon.abstractlang.AbstractContentTree;
import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A mapping of {@link AbstractContent} types to markup-application methods that defines translation
 * from the language-agnostic {@link AbstractContentTree} to the desired markup language.
 */
public interface ApplicationRuleset {
    public HashMap<Class<AbstractContent>, Function<AbstractContent, String>> getApplicationRuleset();
}
