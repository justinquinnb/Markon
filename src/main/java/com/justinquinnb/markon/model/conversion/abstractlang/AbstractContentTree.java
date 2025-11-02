package com.justinquinnb.markon.model.conversion.abstractlang;

import java.util.List;
import java.util.PriorityQueue;

/**
 * A language-agnostic representation of formatted, text-centric content.
 */
public class AbstractContentTree implements Comparable<AbstractContentTree> {
    private AbstractContent parent;
    private AbstractContent data;
    private PriorityQueue<AbstractContentTree> children;

    /**
     * Creates a new {@code AbstractContentTree} with the given parent, data, and children.
     *
     * @param parent the parent node of {@code this} {@code AbstractContentTree}
     * @param data the data of {@code this} {@code AbstractContentTree} node
     * @param children the children of {@code this} {@code AbstractContentTree}
     */
    public AbstractContentTree(
        AbstractContent parent,
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
    public AbstractContentTree(AbstractContent parent, AbstractContent data) {
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

    public AbstractContent getParent() {
        return parent;
    }

    public void setParent(AbstractContent parent) {
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

    public void addChild(AbstractContentTree child) {
        children.add(child);
    }

    public AbstractContentTree pollChild() {
        return children.poll();
    }

    /**
     * Assigns the natural order as ascending start index.
     */
    @Override
    public int compareTo(AbstractContentTree o) {
        return Integer.compare(o.getData().getStartIndex(), this.getData().getStartIndex());
    }
}
