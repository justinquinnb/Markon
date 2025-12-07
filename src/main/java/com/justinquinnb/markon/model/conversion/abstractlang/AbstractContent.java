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
        return (digestedString.length() == 0) ? this.getEndsBefore() : this.getEndsBefore() - 1;
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
     * Determines whether {@code this} content is surrounded by the given indices.
     * @param startIndex the start index to check for
     * @param endIndex the end index to check for
     * @return if {@code this} content is surrounded by the given indices
     */
    public boolean isSurroundedBy(int startIndex, int endIndex) {
        return startIndex <= this.getStartIndex() && this.getEndIndex() <= endIndex;
    }

    /**
     * Determines whether {@code this} content is surrounded by the given {@code content}.
     * @param content the content to check
     * @return if {@code this} content is surrounded by the given content
     */
    public boolean isSurroundedBy(AbstractContent content) {
        return isSurroundedBy(content.getStartIndex(), content.getEndIndex());
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
        for (AbstractContent c : surroundings) {
            boolean changesMade = c.adjustIfNeeded(startOfTarget, oldLength, newString);
            if (changesMade) {
                logger.trace("This content has been adjusted to:\n{}", c);
            } else {
                logger.trace("No adjustments made to this content.");
            }
        }
        logger.trace("All surroundings adjusted.");
    }

    /**
     * Adjusts this content if necessary to reflect a change elsewhere in the surrounding string.
     *
     * @param startOfTarget the start index of the substring being replaced
     * @param oldLength the length of the original, non-digested substring
     * @param newString the new, digested substring
     *
     * @return {@code true} if an adjustment was made, else {@code false}
     */
    public boolean adjustIfNeeded(int startOfTarget, int oldLength, String newString) {
        int oldEnd = startOfTarget + oldLength - 1; // Last index of the subject substring
        int newEnd = startOfTarget + newString.length() - 1; // Last index of the new substring

        logger.trace("Checking if the following requires adjustment, given context: "
            + "startOfTarget={}, oldEnd={}, newEnd={}, newString={}"
            + "\nlookingAtContent={}", startOfTarget, oldEnd, newEnd, newString, this);

        int thisStart = this.getStartIndex(); // First index of this substring
        int thisEnd = this.getEndIndex(); // Last index of this substring

        // Case 1 - This string is unaffected by the replacement
        if (thisEnd < startOfTarget) {
            logger.trace("This content is unaffected by the replacement.");
            return false;
        }

        // Case 2 - This content comes after the adjusted string
        if (oldEnd < thisStart || startOfTarget < thisStart) {
            // Calculate the difference between the old and new string lengths
            int difference = newEnd - oldEnd;
            logger.trace("This content starts (at {}) after the original string's end (at {}), so shifting by: {}",
                thisStart, oldEnd, difference);
            this.shiftStartIndex(difference);
            return true;
        }

        // Case 3 - This content surrounds the original
        // Replace original substring as it appears within this content with the new substring
        if (this.surrounds(startOfTarget, oldEnd)) {
            // If complete replacement, skip splicing
            if (thisStart == startOfTarget && thisEnd == oldEnd) {
                logger.trace("This content (spanning [{},{}]) is entirely replaced by the new "
                        + "string (spanning [{},{}]), so replacing this content's string entirely...",
                    thisStart, thisEnd, startOfTarget, newEnd);
                this.setDigestedString(newString);
            } else {
                logger.trace("This content (spanning [{},{}]) surrounds the original string "
                        + "(spanning [{},{}]), so splicing the new string into this content's original surroundings...",
                    thisStart, thisEnd, startOfTarget, oldEnd);

                // Get this content's text up until the index immediately before the old substring
                logger.trace("Left text spans, relative: [{},{}]", 0
                    , (startOfTarget != thisStart) ? startOfTarget : 0);
                String leftPart = "";
                if (startOfTarget > thisStart) {
                    leftPart = this.digestedString.substring(0, startOfTarget - thisStart);
                    logger.trace("Left text:\n{}", leftPart);
                } else {
                    logger.trace("No left text.");
                }

                // Get this content's text after the index immediately following the old substring
                int rightStart = (oldEnd + 1) - thisStart; // Convert to relative index
                logger.trace("Right text spans, relative: [{},{}]", rightStart, this.digestedString.length() - 1);
                String rightPart = "";
                if (rightStart < this.digestedString.length()) {
                    rightPart = this.digestedString.substring(rightStart);
                    logger.trace("Right text:\n{}", rightPart);
                } else {
                    logger.trace("No right text.");
                }

                // Wrap the new string with the original pieces around the old substring
                String newDigestedString = leftPart + newString + rightPart;

                // Update this content to reflect the new digested string
                logger.trace("Replacing this content's digested string with:\n{}",
                    newDigestedString);
                this.setDigestedString(newDigestedString);
            }
            return true;
        }

        return false;
    }

    /**
     * Gets a short string representation of {@code this} content.
     * @return a short string representation of {@code this} content of format
     * {@code className: digestedString}
     */
    public String toShortString() {
        return this.getClass().getSimpleName() + " [" + this.startIndex + "," +
            this.getEndIndex() + "]: " + this.digestedString;
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
