package com.justinquinnb.markon.builtin.langs;

import com.justinquinnb.markon.builtin.contenttypes.text.BoldText;
import com.justinquinnb.markon.builtin.contenttypes.text.HeadingText;
import com.justinquinnb.markon.builtin.contenttypes.text.ItalicText;
import com.justinquinnb.markon.builtin.contenttypes.text.LineBreak;
import com.justinquinnb.markon.model.MarkupLanguage;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.application.ApplicationRuleset;
import com.justinquinnb.markon.model.conversion.parsing.ParsingRuleset;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Specification for vanilla Markdown parsing and application.
 *
 * @see <a href="https://www.markdownguide.org/cheat-sheet/">Markdown Guide</a>
 */
public class Markdown implements MarkupLanguage {
    private static final LinkedHashMap<
        Pattern,
        Function<String, ? extends AbstractContent>
        > parsingRuleset = new LinkedHashMap<>();

    private static final LinkedHashMap<
            Class<? extends AbstractContent>,
            Function<AbstractContent, String>
            > applicationRuleset = new LinkedHashMap<>();

    // REGEX
    private static final Pattern headingPattern = Pattern.compile("^(#{1,6})s*(.*?)s*#*s*$", Pattern.MULTILINE);
    private static final Pattern boldPattern = Pattern.compile("(?<!([*_]))(\\*\\*|__)(.+?)((\\2)(?!([*_])))");
    private static final Pattern italicPattern = Pattern.compile("(?<=\\*\\*|__|[^*_]|^)((([*_])(?![*_]))(.+?)((?<![*_])(\\2)))(?=\\*\\*|__|[^*_]|$)");
    private static final Pattern lineBreakPattern = Pattern.compile("(\\s{2})|(<(\\s*)br>(\\s*))$", Pattern.MULTILINE);

    static {
        parsingRuleset.put(headingPattern, Markdown::parseHeading);
        parsingRuleset.put(boldPattern, Markdown::parseBold);
        parsingRuleset.put(italicPattern, Markdown::parseItalic);
        parsingRuleset.put(lineBreakPattern, Markdown::parseLineBreak);

        applicationRuleset.put(LineBreak.class, Markdown::applyLineBreak);
        applicationRuleset.put(ItalicText.class, Markdown::applyItalic);
        applicationRuleset.put(BoldText.class, Markdown::applyBold);
        applicationRuleset.put(HeadingText.class, Markdown::applyHeader);
    }

    @Override
    public ApplicationRuleset getApplicationRuleset() {
        return new ApplicationRuleset(applicationRuleset);
    }

    @Override
    public ParsingRuleset getParsingRuleset() {
        return new ParsingRuleset(parsingRuleset);
    }

    public static String applyLineBreak(AbstractContent lineBreak) {
        return "  ";
    }

    public static LineBreak parseLineBreak(String lineBreak) {
        return new LineBreak();
    }

    public static String applyBold(AbstractContent boldText) {
        return "**" + ((BoldText)boldText).getDigestedString() + "**";
    }

    public static BoldText parseBold(String boldText) {
        return new BoldText(boldText.substring(2, boldText.length() - 2));
    }

    public static String applyItalic(AbstractContent italicText) {
        return "*" + ((ItalicText)italicText).getDigestedString() + "*";
    }

    public static ItalicText parseItalic(String italicText) {
        return new ItalicText(italicText.substring(1, italicText.length() - 1));
    }

    public static String applyHeader(AbstractContent headerText) {
        HeadingText header = (HeadingText)headerText;
        return "#".repeat(header.getLevel()) + " " + header.getDigestedString();
    }

    public static HeadingText parseHeading(String headingText) {
        int level = 0;
        while (headingText.charAt(level) == '#') {
            level++;
        }
        return new HeadingText(headingText.substring(level + 1), level);
    }
}
