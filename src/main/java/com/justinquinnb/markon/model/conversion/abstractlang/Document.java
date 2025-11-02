package com.justinquinnb.markon.model.conversion.abstractlang;

/**
 * The entire, digested document
 */
public class Document extends AbstractContent {

    public Document(String digestedString) {
        super(0, digestedString);
    }
}
