package com.justinquinnb.markon.model.conversion.abstractlang;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Content that has been parsed from marked-up text.
 */
public abstract class AbstractContent implements Comparable<AbstractContent> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractContent.class);

    private int startIndex = 0;
    private String digestedString;

    protected AbstractContent(String digestedString) {
        this.digestedString = digestedString;
    }

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
        return this.getStartIndex() <= startIndex && endIndex <= this.getEndIndex();
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
     * @param substringStart the start index of the substring that was digested
     * @param oldLength the length of the original, non-digested substring
     * @param newString the new, digested substring
     */
    public static void adjustSurroundings(
        Collection<AbstractContent> surroundings, int substringStart, int oldLength, String newString
    ) {
        for (AbstractContent c : surroundings) {
            c.adjust(substringStart, oldLength, newString);
        }
    }

    /**
     * Adjusts this content if necessary to reflect a change elsewhere in the surrounding string.
     *
     * @param substringStart start index of the substring that was changed
     * @param oldLength the length of the original, unchanged substring
     * @param newString the new, changed substring
     */
    public void adjust(int substringStart, int oldLength, String newString) {
        // Shift all siblings to reflect the shrinkage/inflation that the substring's digestion
        // caused
        int shiftAmount = newString.length() - oldLength;
        int substringEnd = substringStart + oldLength - 1;
        int shiftThreshold = substringStart + oldLength;

        logger.trace("Considering adjustment of:\n{}", this);
        logger.trace("Adjusting surroundings with context: SharedStart={} ShiftThreshold={}, ShiftAmount={} NewString=\n{}",
            substringStart, shiftThreshold, shiftAmount, newString);

        logger.trace("Content being checked spans [{},{}]. Content to adjust for spans [{},{}].",
            this.getStartIndex(), this.getEndIndex(), substringStart, substringEnd);

        // Check if this instance is completely after the changed substring - if so, shift it
        boolean shouldShift = shiftThreshold <= this.getStartIndex() && this.getStartIndex() >= 0;
        if (shouldShift) {
            this.shiftStartIndex(shiftAmount);
            logger.trace("Shifted start by: {}", shiftAmount);
        }

        // Determine whether this string contains or overlaps with the adjusted substring
        boolean shouldReplace = !shouldShift &&
            this.getStartIndex() <= substringEnd &&
            this.getEndIndex() >= substringStart;

        if (shouldReplace) {
            logger.trace("Current string to replace substring of:\n{}", this.getDigestedString());

            // Calculate the overlap between the old substring and this instance
            int overlapStart = Math.max(substringStart, this.getStartIndex());
            int overlapEnd = Math.min(substringEnd, this.getEndIndex());

            // Calculate positions within this instance's string
            int leftEnd = overlapStart - this.getStartIndex();
            int rightStart = overlapEnd - this.getStartIndex() + 1;

            logger.trace("Left piece spans [{},{}]", 0, leftEnd - 1);
            String leftPiece = this.getDigestedString().substring(0, leftEnd);

            logger.trace("Right piece spans [{},{}]", rightStart, this.getDigestedString().length());
            String rightPiece = this.getDigestedString().substring(rightStart);

            String oldStr = this.getDigestedString().substring(leftEnd, rightStart);
            String replacementStr = leftPiece + newString + rightPiece;

            this.setDigestedString(replacementStr);
            logger.trace("Replaced \"{}\" with \"{}\" to create:\n{}", oldStr, newString, this.getDigestedString());
        }

        if (!shouldShift && !shouldReplace) {
            logger.trace("No adjustments needed.");
        } else {
            logger.trace("Adjustment completed:\n{}\n", this);
        }
    }

    @Override
    public String toString() {
        return "{\ntype=" + this.getClass().getSimpleName() + ",\nstartIndex=" + this.startIndex +
            ",\n(endIndex=" + this.getEndIndex() + "),\n(length=" + this.getLength() +
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
