package com.justinquinnb.markon.parsing;

import java.util.List;

/**
 * A language-agnostic representation of formatted, text-centric content.
 */
public class ParsedMarkupContentTree {
    private ParsedMarkupContent parent;
    private ParsedMarkupContent data;
    private List<ParsedMarkupContentTree> children;

    /**
     * Creates a new {@code ParsedMarkupContentTree} with the given parent, data, and children.
     *
     * @param parent the parent node of {@code this} {@code ParsedMarkupContentTree}
     * @param data the data of {@code this} {@code ParsedMarkupContentTree} node
     * @param children the children of {@code this} {@code ParsedMarkupContentTree}
     */
    public ParsedMarkupContentTree(
        ParsedMarkupContent parent,
        ParsedMarkupContent data,
        List<ParsedMarkupContentTree> children
    ) {
        this.parent = parent;
        this.data = data;
        this.children = children;
    }

    /**
     * Creates a new {@code ParsedMarkupContentTree} with the given parent and data.
     *
     * @param parent the parent node of {@code this} {@code ParsedMarkupContentTree}
     * @param data the data of {@code this} {@code ParsedMarkupContentTree} node
     */
    public ParsedMarkupContentTree(ParsedMarkupContent parent, ParsedMarkupContent data) {
        this(parent, data, null);
    }

    /**
     * Creates a new {@code ParsedMarkupContentTree} with the given data.
     *
     * @param data the data of {@code this} {@code ParsedMarkupContentTree} node
     */
    public ParsedMarkupContentTree(ParsedMarkupContent data) {
        this(null, data, null);
    }

    /**
     * Creates a new {@code ParsedMarkupContentTree} with the given data and children.
     *
     * @param data the data of {@code this} {@code ParsedMarkupContentTree} node
     * @param children the children of {@code this} {@code ParsedMarkupContentTree}
     */
    public ParsedMarkupContentTree(
        ParsedMarkupContent data,
        List<ParsedMarkupContentTree> children
    ) {
        this(null, data, children);
    }

    public ParsedMarkupContent getParent() {
        return parent;
    }

    public void setParent(ParsedMarkupContent parent) {
        this.parent = parent;
    }

    public ParsedMarkupContent getData() {
        return data;
    }

    public void setData(ParsedMarkupContent data) {
        this.data = data;
    }

    public List<ParsedMarkupContentTree> getChildren() {
        return children;
    }

    public void setChildren(List<ParsedMarkupContentTree> children) {
        this.children = children;
    }
}
