package com.justinquinnb.markon.parsing;

/**
 * Content that has been parsed from marked-up text.
 */
public abstract class ParsedMarkupContent {
    private String digestedString = "";

    /**
     * Gets what's left of the ingested {@code String} after parsing.
     * @return the {@code String} that remains after parsing is performed
     * on some marked-up content
     */
    public String getDigestedString() {
        return this.digestedString;
    }
}
