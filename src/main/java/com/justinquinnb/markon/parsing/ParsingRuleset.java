package com.justinquinnb.markon.parsing;

import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A mapping of regular expression {@link Pattern}s to match-ingesting
 * parser methods that defines translation from a markup language to
 * the language-agnostic {@link ParsedMarkupContentTree} representation.
 */
public interface ParsingRuleset {
    public HashMap<Pattern, Function<String, ParsedMarkupContent>> getRuleset();
}
