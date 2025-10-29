package com.justinquinnb.markon.parsing;

import com.justinquinnb.markon.abstractlang.AbstractContent;
import com.justinquinnb.markon.abstractlang.AbstractContentTree;
import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A mapping of regular expression {@link Pattern}s to match-ingesting parser methods that defines
 * translation from a markup language to the language-agnostic {@link AbstractContentTree}
 * representation.
 */
public interface ParsingRuleset {
    public HashMap<Pattern, Function<String, AbstractContent>> getParsingRuleset();
}
