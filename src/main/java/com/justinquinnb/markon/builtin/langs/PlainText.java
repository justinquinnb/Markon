package com.justinquinnb.markon.builtin.langs;

import com.justinquinnb.markon.builtin.contenttypes.text.BoldText;
import com.justinquinnb.markon.builtin.contenttypes.text.HeadingText;
import com.justinquinnb.markon.builtin.contenttypes.text.ItalicText;
import com.justinquinnb.markon.model.MarkupLanguage;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.application.ApplicationRuleset;
import com.justinquinnb.markon.model.conversion.parsing.ParsingRuleset;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Specification for plaintext representations of marked-up text.
 */
public class PlainText implements MarkupLanguage {
    private static final LinkedHashMap<
        Pattern,
        Function<String, ? extends AbstractContent>
        > parsingRuleset = new LinkedHashMap<>();

    private static final LinkedHashMap<
        Class<? extends AbstractContent>,
        Function<AbstractContent, String>
        > applicationRuleset = new LinkedHashMap<>();

    static {
        applicationRuleset.put(BoldText.class, PlainText::applyBold);
        applicationRuleset.put(ItalicText.class, PlainText::applyItalic);
        applicationRuleset.put(HeadingText.class, PlainText::applyHeader);
    }

    private static String applyBold(AbstractContent boldText) {
        return boldText.getDigestedString().toUpperCase();
    }

    private static String applyItalic(AbstractContent italicText) {
        return "*" + italicText.getDigestedString() + "*";
    }

    private static String applyHeader(AbstractContent headerText) {
        return headerText.getDigestedString() + "\n";
    }

    @Override
    public ParsingRuleset getParsingRuleset() {
        return new ParsingRuleset(parsingRuleset);
    }

    @Override
    public ApplicationRuleset getApplicationRuleset() {
        return new ApplicationRuleset(applicationRuleset);
    }
}
