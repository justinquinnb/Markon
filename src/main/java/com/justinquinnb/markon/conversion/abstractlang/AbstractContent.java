package com.justinquinnb.markon.conversion.abstractlang;

/**
 * Content that has been parsed from marked-up text.
 */
public abstract class AbstractContent {
    private String digestedString = "";

    /**
     * Gets what's left of the ingested {@code String} after parsing.
     * @return the {@code String} that remains after parsing is performed
     * on some marked-up content
     */
    public String getDigestedString() {
        return this.digestedString;
    }

    /**
     * Changes what's left of the ingested {@code String} after parsing.
     * @param digestedString the {@code String} now considered to be remaining after parsing has
     *                       been performed on some marked-up content
     */
    public void setDigestedString(String digestedString) {
        this.digestedString = digestedString;
    }
}
