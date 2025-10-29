package com.justinquinnb.markon.abstractlang;

import java.util.List;

/**
 * A language-agnostic representation of formatted, text-centric content.
 */
public class AbstractContentTree {
    private AbstractContent parent;
    private AbstractContent data;
    private List<AbstractContentTree> children;

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
        List<AbstractContentTree> children
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
        this(parent, data, null);
    }

    /**
     * Creates a new {@code AbstractContentTree} with the given data.
     *
     * @param data the data of {@code this} {@code AbstractContentTree} node
     */
    public AbstractContentTree(AbstractContent data) {
        this(null, data, null);
    }

    /**
     * Creates a new {@code AbstractContentTree} with the given data and children.
     *
     * @param data the data of {@code this} {@code AbstractContentTree} node
     * @param children the children of {@code this} {@code AbstractContentTree}
     */
    public AbstractContentTree(
        AbstractContent data,
        List<AbstractContentTree> children
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

    public List<AbstractContentTree> getChildren() {
        return children;
    }

    public void setChildren(List<AbstractContentTree> children) {
        this.children = children;
    }
}
