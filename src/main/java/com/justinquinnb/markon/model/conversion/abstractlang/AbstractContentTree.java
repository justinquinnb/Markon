package com.justinquinnb.markon.model.conversion.abstractlang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;

/**
 * A language-agnostic representation of formatted, text-centric content.
 */
public class AbstractContentTree implements Comparable<AbstractContentTree> {
    // Prefix pieces
    private static final String VERTICAL = "│";
    private static final String HORIZONTAL = "─";
    private static final String RIGHT = "└";
    private static final String TEE = "├";
    private static final String SPACE = " ";
    private static final int PREFIX_SIZE = 3;

    private AbstractContentTree parent;
    private AbstractContent data;
    private PriorityQueue<AbstractContentTree> children;

    private boolean wasVisited = false;

    /**
     * Creates a new {@code AbstractContentTree} with the given parent, data, and children.
     *
     * @param parent the parent node of {@code this} {@code AbstractContentTree}
     * @param data the data of {@code this} {@code AbstractContentTree} node
     * @param children the children of {@code this} {@code AbstractContentTree}
     */
    public AbstractContentTree(
        AbstractContentTree parent,
        AbstractContent data,
        PriorityQueue<AbstractContentTree> children
    ) {
        this.parent = parent;
        this.data = data;
        this.children = children;
    }

    /**
     * Creates a new {@code AbstractContentTree} with the given parent and data.
     *
     * @param parent the parent node of {@code this} {@code AbstractContentTree}
     * @param data the data of {@code this} {@code AbstractContentTree} node
     */
    public AbstractContentTree(AbstractContentTree parent, AbstractContent data) {
        this(parent, data, new PriorityQueue<>());
    }

    /**
     * Creates a new {@code AbstractContentTree} with the given data.
     *
     * @param data the data of {@code this} {@code AbstractContentTree} node
     */
    public AbstractContentTree(AbstractContent data) {
        this(null, data, new PriorityQueue<>());
    }

    /**
     * Creates a new {@code AbstractContentTree} with the given data and children.
     *
     * @param data the data of {@code this} {@code AbstractContentTree} node
     * @param children the children of {@code this} {@code AbstractContentTree}
     */
    public AbstractContentTree(
        AbstractContent data,
        PriorityQueue<AbstractContentTree> children
    ) {
        this(null, data, children);
    }

    public AbstractContentTree getParent() {
        return parent;
    }

    public void setParent(AbstractContentTree parent) {
        this.parent = parent;
    }

    public AbstractContent getData() {
        return data;
    }

    public void setData(AbstractContent data) {
        this.data = data;
    }

    public PriorityQueue<AbstractContentTree> getChildren() {
        return children;
    }

    public void setChildren(PriorityQueue<AbstractContentTree> children) {
        this.children = children;
    }

    public boolean wasVisited() {
        return this.wasVisited;
    }

    public void setVisited(boolean visited) {
        this.wasVisited = visited;
    }

    public void addChild(AbstractContentTree child) {
        children.add(child);
    }

    public AbstractContentTree pollChild() {
        return children.poll();
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public Collection<AbstractContent> asCollection() {
        return asCollection(this);
    }

    private static Collection<AbstractContent> asCollection(AbstractContentTree root) {
        ArrayList<AbstractContent> content = new ArrayList<>();
        content.add(root.getData());
        for (AbstractContentTree child : root.getChildren()) {
            content.addAll(asCollection(child));
        }
        return content;
    }

    /**
     * Adjusts {@code this} tree where necessary to reflect a change in one of its node's strings.
     *
     * @param substringStart the start index of the substring that was changed
     * @param oldLength the length of the original, unchanged substring
     * @param newString the new, changed substring
     */
    public void adjust(int substringStart, int oldLength, String newString) {
        this.data.adjustIfNeeded(substringStart, oldLength, newString);

        for (AbstractContentTree child : this.getChildren()) {
            if (!child.wasVisited()) {
                child.adjust(substringStart, oldLength, newString);
            }
        }
    }

    /**
     * Assigns the natural order as ascending start index.
     */
    @Override
    public int compareTo(AbstractContentTree o) {
        return Integer.compare(o.getData().getStartIndex(), this.getData().getStartIndex());
    }

    /**
     * Prints a pretty representation of {@code this} tree.
     *
     * @return a vertical, left-justified representation of the tree
     * @author <a href="https://www.baeldung.com/java-print-binary-tree-diagram">...</a>
     */
    @Override
    public String toString() {
        if (data == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(data.getDigestedString());

        List<AbstractContentTree> childList = new ArrayList<>(children);
        int numOfChildren = childList.size();

        for (int i = 0; i < numOfChildren; i++) {
            AbstractContentTree child = childList.get(i);
            boolean isLastChild = i == (numOfChildren - 1);
            String prefix = isLastChild ?
                (RIGHT + HORIZONTAL.repeat(PREFIX_SIZE - 1)) :
                (TEE + HORIZONTAL.repeat(PREFIX_SIZE - 1));
            traverseNodes(sb, "", prefix, child, !isLastChild);
        }

        return sb.toString();
    }

    private void traverseNodes(StringBuilder sb, String padding, String prefix,
        AbstractContentTree node, boolean hasAnotherSibling
    ) {
        if (node == null) {
            return;
        }

        sb.append("\n");
        sb.append(padding);
        sb.append(prefix);
        sb.append(node.data.toShortString());

        // Build padding for children
        String childPadding = padding +
            (hasAnotherSibling ?
                (VERTICAL + SPACE.repeat(PREFIX_SIZE - 1)) :
                SPACE.repeat(PREFIX_SIZE));

        List<AbstractContentTree> childList = new ArrayList<>(node.getChildren());
        int numOfChildren = childList.size();

        for (int i = 0; i < numOfChildren; i++) {
            AbstractContentTree child = childList.get(i);
            boolean isLastChild = i == (numOfChildren - 1);
            String childPrefix = isLastChild ?
                (RIGHT + HORIZONTAL.repeat(PREFIX_SIZE - 1)) :
                (TEE + HORIZONTAL.repeat(PREFIX_SIZE - 1));
            traverseNodes(sb, childPadding, childPrefix, child, !isLastChild);
        }
    }
}
