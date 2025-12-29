package com.justinquinnb.markon.builtin.langs;

import com.justinquinnb.markon.builtin.contenttypes.text.BlockQuoteText;
import com.justinquinnb.markon.builtin.contenttypes.text.BoldText;
import com.justinquinnb.markon.builtin.contenttypes.text.HeadingText;
import com.justinquinnb.markon.builtin.contenttypes.text.ItalicText;
import com.justinquinnb.markon.builtin.contenttypes.text.LineBreak;
import com.justinquinnb.markon.builtin.contenttypes.text.OrderedListText;
import com.justinquinnb.markon.model.MarkupLanguage;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.application.ApplicationRuleset;
import com.justinquinnb.markon.model.conversion.parsing.ParsingRuleset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    private static final Pattern headingPattern = Pattern.compile("(^( {0,3})(?<!\\\\)(#{1,6}) .+$)|(^ {0,3}(.+)\\n([-=])+$)", Pattern.MULTILINE);
    private static final Pattern boldPattern = Pattern.compile("(?<!([*_]))(\\*\\*|__)([\\s\\S]+?)((\\2)(?!([*_])))", Pattern.MULTILINE);
    private static final Pattern italicPattern = Pattern.compile("(?<=\\*\\*|__|[^*_]|^)(((?<!\\\\)([*_])(?![*_]))([\\s\\S]+?)((?<!\\\\)(\\2)))(?=\\*\\*|__|[^*_]|$)", Pattern.MULTILINE);
    private static final Pattern lineBreakPattern = Pattern.compile("(( {2})|(<(\\s*)br(\\s*)>))\\n", Pattern.MULTILINE);
    private static final Pattern blockQuoteTextPattern = Pattern.compile("(^( ){0,3}>( )?(.*)$)(\\n^( ){0,3}>( )?(.*)$)*", Pattern.MULTILINE);
    private static final Pattern orderedListPattern = Pattern.compile("((^1(.|\\))( )+(.*))(\\n(( {4}(.*))?))*)(^(\\d{1,9}(.|\\))( )+(.*))(\\n(( {4}(.*))?))*)*", Pattern.MULTILINE);

    static {
        parsingRuleset.put(blockQuoteTextPattern, Markdown::parseBlockQuote);
        parsingRuleset.put(orderedListPattern, Markdown::parseOrderedList);
        parsingRuleset.put(headingPattern, Markdown::parseHeading);
        parsingRuleset.put(boldPattern, Markdown::parseBold);
        parsingRuleset.put(italicPattern, Markdown::parseItalic);
        parsingRuleset.put(lineBreakPattern, Markdown::parseLineBreak);

        applicationRuleset.put(LineBreak.class, Markdown::applyLineBreak);
        applicationRuleset.put(ItalicText.class, Markdown::applyItalic);
        applicationRuleset.put(BoldText.class, Markdown::applyBold);
        applicationRuleset.put(HeadingText.class, Markdown::applyHeading);
        applicationRuleset.put(OrderedListText.class, Markdown::applyOrderedList);
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

    public static String applyOrderedList(AbstractContent orderedListText) {
        StringBuilder markedUpString = new StringBuilder();
        OrderedListText orderedList = (OrderedListText)orderedListText;
        List<Integer> listItemStartIndices = orderedList.getItemStartIndices();
        List<Integer> itemNumbers = orderedList.getItemNumbers();

        for (int i = 0; i < listItemStartIndices.size(); i++) {
            int startOfItem = listItemStartIndices.get(i);
            if (i < listItemStartIndices.size() - 1) {
                int endOfItem = listItemStartIndices.get(i + 1) - 1;
                String listItemText = orderedList.getDigestedString().substring(startOfItem, endOfItem);
                listItemText = itemNumbers.get(i) + ". " + listItemText;
                markedUpString.append(listItemText).append("\n");
            } else {
                String listItemText = orderedList.getDigestedString().substring(startOfItem);
                listItemText = (i + 1) + ". " + listItemText;
                markedUpString.append(listItemText).append("\n");
            }
        }

        return markedUpString.toString();
    }

    public static OrderedListText parseOrderedList(String orderedListText) {
        Pattern listItem = Pattern.compile("(\\d)+. (.*)");
        Matcher matcher = listItem.matcher(orderedListText);
        List<Integer> itemStartIndices = new ArrayList<>();
        List<Integer> itemNumbers = new ArrayList<>();
        StringBuilder digestedString = new StringBuilder();
        int numCharsRemoved = 0;
        int numCharsAdded = 0;

        while (matcher.find()) {
            itemNumbers.add(Integer.parseInt(matcher.group().substring(0, matcher.group().indexOf('.'))));
            digestedString.append(matcher.group(2)).append("\n");
            numCharsRemoved += matcher.group(1).length();
            itemStartIndices.add(matcher.start() - numCharsRemoved + numCharsAdded);
            numCharsAdded += 1;
        }

        // Remove the last item's newline character
        int digestedStringLength = digestedString.length();
        digestedString.delete(digestedStringLength - 1, digestedStringLength);

        return new OrderedListText(digestedString.toString(), itemStartIndices, itemNumbers);
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
        AtomicInteger i = new AtomicInteger();
        blockQuoteText.lines().forEach(line -> {
                Matcher matcher = firstLetterPattern.matcher(line);
                if (matcher.find()) {
                    if (i.get() != 0) digestedString.append("\n");
                    digestedString.append(line.substring(matcher.start()));
                } else {
                    digestedString.append("\n");
                }
                i.getAndIncrement();
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

    public static String applyHeading(AbstractContent headingText) {
        HeadingText heading = (HeadingText)headingText;
        return "#".repeat(heading.getLevel()) + " " + heading.getDigestedString();
    }

    public static HeadingText parseHeading(String headingText) {
        int level = 0;
        String digestedText = "";

        // ATX-Style headings
        Matcher atxMatcher = Pattern.compile("^( {0,3})(?<!\\\\)(#{1,6}) .+$").matcher(headingText);
        if (atxMatcher.find()) {
            // Check if trailing heading syntax is used (text to digest is surrounded)
            Matcher surroundedMatcher = Pattern
                .compile("^( {0,3})(?<!\\\\)(#{1,6}) (.+) (((?<!\\\\)#)+(\\s)*)$")
                .matcher(headingText);
            int textEndsAtChar = headingText.length();
            if (surroundedMatcher.find()) {
                // The -1 accounts for space prior to trailing heading hashtags
                textEndsAtChar = surroundedMatcher.start(4) - 1;
            }

            while (headingText.charAt(level) == '#') {
                level++;
            }
            digestedText = headingText.substring(level + 1, textEndsAtChar);
        } else { // Setext-Style headings
            Matcher setextMatcher = Pattern.compile("^ {0,3}(.+)\\n([-=])+$").matcher(headingText);
            if (setextMatcher.find()) {
                digestedText = setextMatcher.group(1);
                level = setextMatcher.group(2).charAt(0) == '=' ? 1 : 2;
            }
        }

        return new HeadingText(digestedText, level);
    }
}
