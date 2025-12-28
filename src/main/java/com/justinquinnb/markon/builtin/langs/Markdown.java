package com.justinquinnb.markon.builtin.langs;

import com.justinquinnb.markon.builtin.contenttypes.text.BlockQuoteText;
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
import java.util.regex.Matcher;
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
    private static final Pattern headingPattern = Pattern.compile("^(?<!\\\\)(#{1,6}) .*$", Pattern.MULTILINE);
    private static final Pattern boldPattern = Pattern.compile("(?<!([*_]))(\\*\\*|__)([\\s\\S]+?)((\\2)(?!([*_])))", Pattern.MULTILINE);
    private static final Pattern italicPattern = Pattern.compile("(?<=\\*\\*|__|[^*_]|^)(((?<!\\\\)([*_])(?![*_]))([\\s\\S]+?)((?<!\\\\)(\\2)))(?=\\*\\*|__|[^*_]|$)", Pattern.MULTILINE);
    private static final Pattern lineBreakPattern = Pattern.compile("(( {2})|(<(\\s*)br(\\s*)>))\\n", Pattern.MULTILINE);
    private static final Pattern blockQuoteTextPattern = Pattern.compile("(^> (.*)$)(\\n^( *)(.*)\\n)*(^>( *))*", Pattern.MULTILINE);

    static {
        parsingRuleset.put(blockQuoteTextPattern, Markdown::parseBlockQuote);
        parsingRuleset.put(headingPattern, Markdown::parseHeading);
        parsingRuleset.put(boldPattern, Markdown::parseBold);
        parsingRuleset.put(italicPattern, Markdown::parseItalic);
        parsingRuleset.put(lineBreakPattern, Markdown::parseLineBreak);

        applicationRuleset.put(LineBreak.class, Markdown::applyLineBreak);
        applicationRuleset.put(ItalicText.class, Markdown::applyItalic);
        applicationRuleset.put(BoldText.class, Markdown::applyBold);
        applicationRuleset.put(HeadingText.class, Markdown::applyHeader);
        applicationRuleset.put(BlockQuoteText.class, Markdown::applyBlockQuote);
    }

    @Override
    public ApplicationRuleset getApplicationRuleset() {
        return new ApplicationRuleset(applicationRuleset);
    }

    @Override
    public ParsingRuleset getParsingRuleset() {
        return new ParsingRuleset(parsingRuleset);
    }

    public static String applyBlockQuote(AbstractContent blockQuoteText) {
        StringBuilder markedUpString = new StringBuilder();
        blockQuoteText.getDigestedString().lines().forEach(line -> {
            if (line.equals("\n")) {
                markedUpString.append(">\n");
            } else {
                markedUpString.append("> ").append(line);
            }
        });

        return markedUpString.toString();
    }

    public static BlockQuoteText parseBlockQuote(String blockQuoteText) {
        StringBuilder digestedString = new StringBuilder();
        Pattern firstLetterPattern = Pattern.compile("[^> ]");
        blockQuoteText.lines().forEach(line -> {
                Matcher matcher = firstLetterPattern.matcher(line);
                if (matcher.find()) {
                    System.out.println("Found character after blockquote: " + line.substring(matcher.start()));
                    digestedString.append(line.substring(matcher.start()));
                } else {
                    digestedString.append("\n");
                }
            }
            );
        return new BlockQuoteText(digestedString.toString());
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
