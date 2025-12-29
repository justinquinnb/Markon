package com.justinquinnb.markon.builtin.contenttypes.text;

import java.util.List;

/**
 * An unordered list of text items.
 */
public class UnorderedListText extends FormattedText {
    private final List<Integer> itemStartIndices;

    /**
     * Creates an ordered list of text items.
     * @param digestedString a newline ({@code \n}) delimited string of list items
     * @param itemStartIndices the start indices of each item in the {@code digestedString}
     */
    public UnorderedListText(String digestedString, List<Integer> itemStartIndices) {
        super(digestedString);
        this.itemStartIndices = itemStartIndices;
    }

    public List<Integer> getItemStartIndices() {
        return itemStartIndices;
    }

    public int getItemCount() {
        return itemStartIndices.size();
    }

    @Override
    public String toString() {
        return "{\ntype=" + this.getClass().getSimpleName() + ",\nstartIndex=" + this.getStartIndex() +
            ",\n(endIndex=" + this.getEndIndex() + "),\n(length=" + this.getLength() +
            "),\n(itemCount=" + this.getItemCount() + ")\ndigestedString=\n" + this.getDigestedString() + "\n}";
    }
}
