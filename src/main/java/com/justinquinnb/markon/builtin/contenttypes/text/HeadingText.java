package com.justinquinnb.markon.builtin.contenttypes.text;

/**
 * A text heading
 */
public class HeadingText extends FormattedText {
    private int level;

    /**
     * Creates a heading with text {@code text} of level {@code level}.
     * @param digestedText the text of the heading
     * @param level the level of the heading (generally 1-6, with 1 the most important)
     */
    public HeadingText(String digestedText, int level) {
        super(digestedText);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
