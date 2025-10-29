package com.justinquinnb.markon.conversion.parsing;

import com.justinquinnb.markon.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.conversion.abstractlang.AbstractContentTree;
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
