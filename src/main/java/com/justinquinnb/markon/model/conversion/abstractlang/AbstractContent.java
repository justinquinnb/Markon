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
     * @param startOfTarget the start index of the substring being replaced
     * @param oldLength the length of the original, non-digested substring
     * @param newString the new, digested substring
     */
    public static void adjustSurroundings(
        Collection<AbstractContent> surroundings, int startOfTarget, int oldLength, String newString
    ) {
        logger.trace("Adjusting surroundings...");
        for (AbstractContent c : surroundings) {
            c.adjustIfNeeded(startOfTarget, oldLength, newString);
        }
        logger.trace("All surroundings adjusted.");
    }

    /**
     * Adjusts this content if necessary to reflect a change elsewhere in the surrounding string.
     *
     * @param startOfTarget the start index of the substring being replaced
     * @param oldLength the length of the original, non-digested substring
     * @param newString the new, digested substring
     */
    public void adjustIfNeeded(int startOfTarget, int oldLength, String newString) {
        /*
        Multiple cases:

        1. This content ends completely before the start of the subject
        2. This content starts completely after the end of the original substring (>= startOfSubject + oldLength)
        3. This content starts before the start of the subject and ends after the end of the original substring
         */

        logger.trace("Checking if content requires adjustment...");
        logger.trace("Context: startOfTarget={}, oldLength={}, newString={}, lookingAtContent=\n{}",
            startOfTarget, oldLength, newString, this);

        int oldEnd = startOfTarget + oldLength - 1; // Last index of the subject substring
        int newEnd = startOfTarget + newString.length() - 1; // Last index of the new substring

        int thisStart = this.getStartIndex(); // First index of this substring
        int thisEnd = this.getEndIndex(); // Last index of this substring

        // Case 1 - This string is unaffected by the replacement
        if (thisEnd < startOfTarget) {
            logger.trace("This content is unaffected by the replacement.");
        }

        // Case 2 - Shift this according to the difference in new string and old string lengths
        boolean changesMade = false;
        if (oldEnd < thisStart) {
            // Calculate the difference between the old and new string lengths
            int difference = newEnd - oldEnd;
            logger.trace("This content starts (at {}) after the original string's end (at {}), so shifting by: {}",
                thisStart, oldEnd, difference);
            this.shiftStartIndex(difference);
            changesMade = true;
        }

        // Case 3 - This content surrounds the original
        // Replace original substring as it appears within this content with the new substring
        if (this.surrounds(startOfTarget, oldEnd)) {
            // If complete replacement, skip splicing
            if (thisStart == startOfTarget && thisEnd == oldEnd) {
                logger.trace("This content (spanning [{},{}]) is entirely replaced by the new "
                    + "string (spanning [{},{}]), so replacing this content's string entirely...",
                    thisStart, thisEnd, startOfTarget, oldEnd);
                this.setDigestedString(newString);
            } else {
                logger.trace("This content (spanning [{},{}]) surrounds the original string "
                        + "(spanning [{},{}]), so splicing new into original surroundings...",
                    thisStart, thisEnd, startOfTarget, oldEnd);

                // Get this content's text up until the index immediately before the old substring
                String leftPart = this.digestedString.substring(0, startOfTarget);
                logger.trace("Text left of original substring:\n{}", leftPart);

                // Get this content's text after the index immediately following the old substring
                String rightPart = this.digestedString.substring(oldEnd + 1);
                logger.trace("Text right of original substring:\n{}", rightPart);

                // Wrap the new string with the original pieces around the old substring
                String newDigestedString = leftPart + newString + rightPart;

                // Update this content to reflect the new digested string
                logger.trace("Replacing this content's digested string with:\n{}",
                    newDigestedString);
                this.setDigestedString(newDigestedString);
            }
            changesMade = true;
        }

        if (changesMade) {
            logger.trace("This content has been adjusted to:\n{}", this);
        } else {
            logger.trace("No adjustments made to this content.");
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
