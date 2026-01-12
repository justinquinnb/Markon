package com.justinquinnb.markon.builtin.contenttypes.interactive;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import java.net.URL;
import java.util.Optional;

/**
 * A hyperlink
 */
public class Link extends AbstractContent {
    /**
     * The destination of the link
     */
    private String target;

    /**
     * The tooltip (title) for the link's destination
     */
    private Optional<String> title = Optional.empty();

    public Link(String digestedString, String target) {
        super(digestedString);
        this.target = target;
    }

    public Link(String digestedString, String target, String title) {
        super(digestedString);
        this.target = target;
        this.title = Optional.of(title);
    }

    public Link(String digestedString, String target, Optional<String> title) {
        super(digestedString);
        this.target = target;
        this.title = title;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public Optional<String> getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Optional.of(title);
    }

    public void removeTitle() {
        this.title = Optional.empty();
    }
}
