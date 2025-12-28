package com.justinquinnb.markon.builtin.contenttypes.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An ordered list of text items.
 */
public class OrderedListText extends FormattedText {
    private final List<Integer> itemStartIndices;
    private final List<Integer> itemNumbers;

    /**
     * Creates an ordered list of text items.
     * @param digestedString a newline ({@code \n}) delimited string of list items
     * @param itemStartIndices the start indices of each item in the {@code digestedString}
     */
    public OrderedListText(String digestedString, List<Integer> itemStartIndices) {
        super(digestedString);
        this.itemStartIndices = itemStartIndices;
        this.itemNumbers = new ArrayList<>();

        for (int i = 0; i < itemStartIndices.size(); i++) {
            itemNumbers.add(i + 1);
        }
    }

    public OrderedListText(String digestedString, List<Integer> itemStartIndices, List<Integer> itemNumbers) {
        super(digestedString);
        this.itemStartIndices = itemStartIndices;
        this.itemNumbers = itemNumbers;
    }

    public List<Integer> getItemStartIndices() {
        return itemStartIndices;
    }

    public List<Integer> getItemNumbers() {
        return itemNumbers;
    }

    public int getItemCount() {
        return itemStartIndices.size();
    }

    @Override
    public String toString() {
        List<String> numsAsStrs = itemNumbers.stream().map(String::valueOf).toList();
        String itemNumsStr = String.join( ", ", numsAsStrs);
        return "{\ntype=" + this.getClass().getSimpleName() + ",\nstartIndex=" + this.getStartIndex() +
            ",\n(endIndex=" + this.getEndIndex() + "),\n(length=" + this.getLength() +
            "),\n(itemCount=" + this.getItemCount() + ")\nitemNumbers=[" + itemNumsStr + "]\ndigestedString=\n" + this.getDigestedString() + "\n}";
    }
}
