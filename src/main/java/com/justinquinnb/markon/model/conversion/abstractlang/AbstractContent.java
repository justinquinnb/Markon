package com.justinquinnb.markon.model.conversion.abstractlang;

import java.util.PriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Content that has been parsed from marked-up text.
 */
public abstract class AbstractContent implements Comparable<AbstractContent> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractContent.class);

    private int startIndex;
    private String digestedString;

    protected AbstractContent(int startIndex, String digestedString) {
        this.startIndex = startIndex;
        this.digestedString = digestedString;
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public int getLength() {
        return this.digestedString.length();
    }

    /**
     * Gets the index of {@code this} content's last character.
     * @return the index of {@code this} content's last character
     */
    public int getEndIndex() {
        return this.getEndsBefore() - 1;
    }

    /**
     * Gets the index after the last character of {@code this} content.
     * @return the index that {@code this} content ends before
     */
    public int getEndsBefore() {
        return this.startIndex + this.digestedString.length();
    }

    public String getDigestedString() {
        return this.digestedString;
    }

    /**
     * Shifts the start index {@code amount}
     * @param amount the amount to shift the start index by
     */
    public void shiftStartIndex(int amount) {
        this.startIndex += amount;
    }

    public void setDigestedString(String digestedString) {
        this.digestedString = digestedString;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Determines whether {@code this} content surrounds the given indices.
     * @param startIndex the start index to check for
     * @param endIndex the end index to check for
     * @return if {@code this} content surrounds the given indices
     */
    public boolean surrounds(int startIndex, int endIndex) {
        return this.startIndex <= startIndex && endIndex <= this.getEndIndex();
    }

    /**
     * Determines whether {@code this} content surrounds the given {@code content}.
     * @param content the content to check
     * @return if {@code this} content surrounds the given indices
     */
    public boolean surrounds(AbstractContent content) {
        return this.surrounds(content.getStartIndex(), content.getEndIndex());
    }

    /**
     * Adjusts all surrounding content to reflect the new, digested substring.
     *
     * @param surroundings the surrounding content to adjust
     * @param startIndex the start index of the substring that was digested
     * @param oldLength the length of the original, non-digested substring
     * @param newString the new, digested substring
     */
    public static void adjustSurroundings(
        PriorityQueue<AbstractContent> surroundings, int startIndex, int oldLength, String newString
    ) {
        int shiftThreshold = startIndex + oldLength;
        int shiftAmt = newString.length() - oldLength;

        logger.trace("Adjusting surroundings with: Threshold={}, ShiftAmt={}, NewString=\"{}\"",
            shiftThreshold, shiftAmt, newString);

        for (AbstractContent c : surroundings) {
            logger.trace("Checking content: {}", c.getDigestedString());

            // Shift all siblings to reflect the shrinkage/inflation that the substring's digestion
            // caused
            if (shiftThreshold <= c.getEndsBefore() && c.getStartIndex() > 0) {
                c.shiftStartIndex(shiftAmt);
                logger.trace("Shifted start by: {}", shiftAmt);
            }

            // Replace all parent strings containing the original one
            if (c.surrounds(startIndex, startIndex + newString.length() - 1)) {
                String leftPiece = c.getDigestedString().substring(0, startIndex);
                String rightPiece = c.getDigestedString().substring(startIndex + oldLength);
                String oldStr = c.getDigestedString().substring(startIndex, startIndex + oldLength);
                String replacementStr = leftPiece + newString + rightPiece;
                c.setDigestedString(replacementStr);
                logger.trace("Replaced \"{}\" with \"{}\" to create:\n{}\n", oldStr, newString, c.getDigestedString());
            }
        }
    }

    @Override
    public String toString() {
        return "{\nstartIndex=" + this.startIndex + ",\n(endIndex=" + this.getEndIndex() + "),\n(length=" + this.getLength() +
            "),\ndigestedString=\n" + this.digestedString + "\n}";
    }

    /**
     * Assigns the natural order as descending content length.
     */
    @Override
    public int compareTo(AbstractContent o) {
        return Integer.compare(o.getLength(), this.getLength());
    }
}
