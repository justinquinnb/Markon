package com.justinquinnb.markon.builtin.langs;

import com.justinquinnb.markon.builtin.contenttypes.text.BlockQuoteText;
import com.justinquinnb.markon.builtin.contenttypes.text.BoldText;
import com.justinquinnb.markon.builtin.contenttypes.text.HeadingText;
import com.justinquinnb.markon.builtin.contenttypes.text.InlineCodeText;
import com.justinquinnb.markon.builtin.contenttypes.text.ItalicText;
import com.justinquinnb.markon.builtin.contenttypes.text.LineBreak;
import com.justinquinnb.markon.builtin.contenttypes.text.OrderedListText;
import com.justinquinnb.markon.builtin.contenttypes.text.UnorderedListText;
import com.justinquinnb.markon.model.MarkupLanguage;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.application.ApplicationRuleset;
import com.justinquinnb.markon.model.conversion.parsing.ParsingContext;
import com.justinquinnb.markon.model.conversion.parsing.ParsingRuleset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Specification for vanilla Markdown parsing and application.
 *
 * @see <a href="https://www.markdownguide.org/cheat-sheet/">Markdown Guide</a>
 * @see <a href="https://spec.commonmark.org/0.31.2/">CommonMark Specification</a>
 */
public class Markdown implements MarkupLanguage {
    private static final ParsingRuleset parsingRuleset = new ParsingRuleset(new ArrayList<>());

    private static final LinkedHashMap<
            Class<? extends AbstractContent>,
            Function<AbstractContent, String>
            > applicationRuleset = new LinkedHashMap<>();

    // REGEX
    private static final Pattern headingPattern = Pattern.compile("(^( {0,3})(?<!\\\\)(#{1,6}) .+$)|(^ {0,3}(.+)\\n([-=])+$)", Pattern.MULTILINE);
    private static final Pattern boldPattern = Pattern.compile("(?<!([*_]))(\\*\\*|__)([\\s\\S]+?)((\\2)(?!([*_])))", Pattern.MULTILINE);
    private static final Pattern italicPattern = Pattern.compile("(?<=\\*\\*|__|[^*_]|^)(((?<!\\\\)([*_])(?![*_]))([\\s\\S]+?)((?<!\\\\)(\\2)))(?=\\*\\*|__|[^*_]|$)", Pattern.MULTILINE);
    private static final Pattern lineBreakPattern = Pattern.compile("(( {2})|(<(\\s*)br(\\s*)(/?)>)|\\\\)$", Pattern.MULTILINE);
    private static final Pattern blockQuoteTextPattern = Pattern.compile("(^( ){0,3}>( )?(.*)$)(\\n^( ){0,3}>( )?(.*)$)*", Pattern.MULTILINE);
    private static final Pattern orderedListPattern = Pattern.compile("((^1(.|\\))( )+(.*))(\\n(( {4}(.*))?))*)(^(\\d{1,9}(.|\\))( )+(.*))(\\n(( {4}(.*))?))*)*", Pattern.MULTILINE);
    private static final Pattern unorderedListPattern = Pattern.compile("((^[-+*] +)(.*)$)((\\n^[-+*] )(.*)$)*", Pattern.MULTILINE);
    private static final Pattern inlineCodePattern = Pattern.compile("(?<!`)(((?<!\\\\)`){1,2})([\\s\\S]+?)(\\1)(?!`)", Pattern.MULTILINE);
    // codeBlockPattern
    // horizontalRule
    // link
    // image
    // HTML

    static {
        parsingRuleset.addRule(blockQuoteTextPattern, Markdown::parseBlockQuote, "Block Quote");
        parsingRuleset.addRule(orderedListPattern, Markdown::parseOrderedList, "Ordered List");
        parsingRuleset.addRule(unorderedListPattern, Markdown::parseUnorderedList, "Unordered List");
        parsingRuleset.addRule(headingPattern, Markdown::parseHeading, "Heading");
        parsingRuleset.addRule(boldPattern, Markdown::parseBold, "Bold");
        parsingRuleset.addRule(italicPattern, Markdown::parseItalic, "Italic");
        parsingRuleset.addRule(inlineCodePattern, Markdown::parseInlineCode, "Inline Code",
            Markdown::isNotInsideInlineCode);
        parsingRuleset.addRule(lineBreakPattern, Markdown::parseLineBreak, "Line Break");

        applicationRuleset.put(LineBreak.class, Markdown::applyLineBreak);
        applicationRuleset.put(InlineCodeText.class, Markdown::applyInlineCode);
        applicationRuleset.put(ItalicText.class, Markdown::applyItalic);
        applicationRuleset.put(BoldText.class, Markdown::applyBold);
        applicationRuleset.put(HeadingText.class, Markdown::applyHeading);
        applicationRuleset.put(UnorderedListText.class, Markdown::applyUnorderedList);
        applicationRuleset.put(OrderedListText.class, Markdown::applyOrderedList);
        applicationRuleset.put(BlockQuoteText.class, Markdown::applyBlockQuote);
    }

    @Override
    public ApplicationRuleset getApplicationRuleset() {
        return new ApplicationRuleset(applicationRuleset);
    }

    @Override
    public ParsingRuleset getParsingRuleset() {
        return parsingRuleset;
    }

    /**
     * Checks whether a given inline code match uses escaped interior syntax
     * {@code `unescaped ``escaped```}, indicating parsing should not occur.
     *
     * @param parsingContext the context within which the match has been found
     * @return {@code true} if the match is not escaped, interior inline code, else {@code false}
     */
    public static boolean isNotInsideInlineCode(ParsingContext parsingContext) {
        // Extract the relevant parsing context
        PriorityQueue<AbstractContent> currentlyParsedContent =
            parsingContext.getCurrentlyParsedContent();
        AbstractContent[] content = currentlyParsedContent.toArray(new AbstractContent[0]);
        int matchStartIndex = parsingContext.getMatchStartIndex();
        int matchEndIndex = matchStartIndex + parsingContext.getMatchText().length() - 1;

        // Check whether the match lies within inline code
        for (AbstractContent abstractContent : content) {
            if (abstractContent instanceof InlineCodeText inlineCode) {
                int inlineCodeStartIndex = inlineCode.getStartIndex();
                int inlineCodeEndIndex =
                    inlineCodeStartIndex + inlineCode.getDigestedString().length() - 1;

                if (inlineCodeStartIndex <= matchStartIndex
                    && matchEndIndex <= inlineCodeEndIndex
                ) {
                    return false;
                }
            }
        }

        return true;
    }

    public static String applyInlineCode(AbstractContent inlineCodeText) {
        // Ensure the digested string is surrounded by two backticks if it contains backticks itself
        if (inlineCodeText.getDigestedString().contains("`")) {
            return "``" + ((InlineCodeText)inlineCodeText).getDigestedString() + "``";
        } else {
            return "`" + ((InlineCodeText)inlineCodeText).getDigestedString() + "`";
        }
    }

    public static InlineCodeText parseInlineCode(String inlineCodeText) {
        String prefix = inlineCodeText.substring(0, 2);
        int affixLength = prefix.equals("``") ? 2 : 1;

        return new InlineCodeText(inlineCodeText.substring(affixLength,
            inlineCodeText.length() - affixLength));
    }

    public static String applyUnorderedList(AbstractContent orderedListText) {
        StringBuilder markedUpString = new StringBuilder();
        UnorderedListText unorderedList = (UnorderedListText)orderedListText;
        List<Integer> listItemStartIndices = unorderedList.getItemStartIndices();

        for (int i = 0; i < listItemStartIndices.size(); i++) {
            int startOfItem = listItemStartIndices.get(i);
            // Determine where each list item ends
            if (i < listItemStartIndices.size() - 1) {
                int endOfItem = listItemStartIndices.get(i + 1) - 1;
                String listItemText = unorderedList.getDigestedString().substring(startOfItem, endOfItem);
                markedUpString.append(listItemText).append("\n");
            } else {
                String listItemText = unorderedList.getDigestedString().substring(startOfItem);
                markedUpString.append(listItemText).append("\n");
            }
        }

        return markedUpString.toString();
    }

    public static UnorderedListText parseUnorderedList(String unorderedListText) {
        Pattern listItem = Pattern.compile("([-+*]) +(.*)");
        Matcher matcher = listItem.matcher(unorderedListText);
        List<Integer> itemStartIndices = new ArrayList<>();
        StringBuilder digestedString = new StringBuilder();
        int numCharsRemoved = 0;
        int numCharsAdded = 0;

        // Locate each list item
        while (matcher.find()) {
            digestedString.append(matcher.group(2)).append("\n");
            numCharsRemoved += matcher.group(1).length();
            itemStartIndices.add(matcher.start() - numCharsRemoved + numCharsAdded);
            numCharsAdded += 1;
        }

        // Remove the last item's newline character
        int digestedStringLength = digestedString.length();
        digestedString.delete(digestedStringLength - 1, digestedStringLength);

        return new UnorderedListText(digestedString.toString(), itemStartIndices);
    }

    public static String applyOrderedList(AbstractContent orderedListText) {
        StringBuilder markedUpString = new StringBuilder();
        OrderedListText orderedList = (OrderedListText)orderedListText;
        List<Integer> listItemStartIndices = orderedList.getItemStartIndices();
        List<Integer> itemNumbers = orderedList.getItemNumbers();

        for (int i = 0; i < listItemStartIndices.size(); i++) {
            int startOfItem = listItemStartIndices.get(i);
            // Determine where each list item ends
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
        Pattern listItem = Pattern.compile("(\\d)+. +(.*)");
        Matcher matcher = listItem.matcher(orderedListText);
        List<Integer> itemStartIndices = new ArrayList<>();
        List<Integer> itemNumbers = new ArrayList<>();
        StringBuilder digestedString = new StringBuilder();
        int numCharsRemoved = 0;
        int numCharsAdded = 0;

        // Locate each list item
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
