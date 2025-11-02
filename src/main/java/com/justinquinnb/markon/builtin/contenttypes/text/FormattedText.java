package com.justinquinnb.markon.builtin.contenttypes.text;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;

/**
 * Formatted text
 */
public abstract class FormattedText extends AbstractContent {

    public FormattedText(int startIndex, String digestedString) {
        super(startIndex, digestedString);
    }
    // Nothing yet, merely for grouping right now
}
