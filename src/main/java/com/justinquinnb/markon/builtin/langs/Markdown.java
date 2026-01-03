package com.justinquinnb.markon.builtin.langs;

import com.justinquinnb.markon.builtin.contenttypes.text.BlockQuoteText;
import com.justinquinnb.markon.builtin.contenttypes.text.BoldText;
import com.justinquinnb.markon.builtin.contenttypes.text.CodeBlockText;
import com.justinquinnb.markon.builtin.contenttypes.text.HeadingText;
import com.justinquinnb.markon.builtin.contenttypes.text.InlineCodeText;
import com.justinquinnb.markon.builtin.contenttypes.text.ItalicText;
import com.justinquinnb.markon.builtin.contenttypes.text.LineBreak;
import com.justinquinnb.markon.builtin.contenttypes.text.OrderedListText;
import com.justinquinnb.markon.builtin.contenttypes.text.ThematicBreak;
import com.justinquinnb.markon.builtin.contenttypes.text.UnorderedListText;
import com.justinquinnb.markon.model.MarkupLanguage;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.application.ApplicationRuleset;
import com.justinquinnb.markon.model.conversion.parsing.ParserResponse;
import com.justinquinnb.markon.model.conversion.parsing.ParsingContext;
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
    private static final Pattern codeBlockPattern = Pattern.compile(
        "((^\\\\(\\t| {4})(.*)\\n)?(^(\\t| {4})(.*))(\\n^(\\t| {4})(.*))*)", Pattern.MULTILINE);
    private static final Pattern thematicBreakPattern = Pattern.compile("^ {0,3}([-*_]){3,}$", Pattern.MULTILINE);
    // (extended codeblock (^(```|~~~)(.*)\n((.*)\n)*\12)
    // link
    // image
    // HTML

    static {
        parsingRuleset.addRule(codeBlockPattern, Markdown::parseCodeBlock, "Code Block", Markdown::isEscapedCodeBlock);
        parsingRuleset.addRule(blockQuoteTextPattern, Markdown::parseBlockQuote, "Block Quote");
        parsingRuleset.addRule(orderedListPattern, Markdown::parseOrderedList, "Ordered List");
        parsingRuleset.addRule(unorderedListPattern, Markdown::parseUnorderedList, "Unordered List");
        parsingRuleset.addRule(inlineCodePattern, Markdown::parseInlineCode, "Inline Code");
        parsingRuleset.addRule(headingPattern, Markdown::parseHeading, "Heading");
        parsingRuleset.addRule(boldPattern, Markdown::parseBold, "Bold");
        parsingRuleset.addRule(italicPattern, Markdown::parseItalic, "Italic");
        parsingRuleset.addRule(lineBreakPattern, Markdown::parseLineBreak, "Line Break");
        parsingRuleset.addRule(thematicBreakPattern, Markdown::parseThematicBreak, "Thematic Break");

        applicationRuleset.put(ThematicBreak.class, Markdown::applyThematicBreak);
        applicationRuleset.put(LineBreak.class, Markdown::applyLineBreak);
        applicationRuleset.put(ItalicText.class, Markdown::applyItalic);
        applicationRuleset.put(BoldText.class, Markdown::applyBold);
        applicationRuleset.put(HeadingText.class, Markdown::applyHeading);
        applicationRuleset.put(InlineCodeText.class, Markdown::applyInlineCode);
        applicationRuleset.put(UnorderedListText.class, Markdown::applyUnorderedList);
        applicationRuleset.put(OrderedListText.class, Markdown::applyOrderedList);
        applicationRuleset.put(BlockQuoteText.class, Markdown::applyBlockQuote);
        applicationRuleset.put(CodeBlockText.class, Markdown::applyCodeBlock);
    }

    @Override
    public ApplicationRuleset getApplicationRuleset() {
        return new ApplicationRuleset(applicationRuleset);
    }

    @Override
    public ParsingRuleset getParsingRuleset() {
        return parsingRuleset;
    }

    public static String applyThematicBreak(AbstractContent thematicBreak) {
        return "---";
    }

    public static ParserResponse parseThematicBreak(String thematicBreak) {
        return new ParserResponse(new ThematicBreak());
    }

    public static boolean isEscapedCodeBlock(ParsingContext parsingContext) {
        return parsingContext.getMatchText().startsWith("\\");
    }

    public static String applyCodeBlock(AbstractContent codeBlockText) {
        StringBuilder markedUpString = new StringBuilder();
        codeBlockText.getDigestedString().lines().forEach(line -> {
            if (line.equals("\n")) {
                markedUpString.append("    \n");
            } else {
                markedUpString.append("    ").append(line);
            }
        });

        return markedUpString.toString();
    }

    public static ParserResponse parseCodeBlock(String codeBlockText) {
        Pattern codeLine = Pattern.compile("( {4}|\t)(.*)");
        Matcher matcher = codeLine.matcher(codeBlockText);
        StringBuilder digestedString = new StringBuilder();

        // Locate each list item
        while (matcher.find()) {
            digestedString.append(matcher.group(2)).append("\n");
        }

        // Remove the last item's newline character
        int digestedStringLength = digestedString.length();
        digestedString.delete(digestedStringLength - 1, digestedStringLength);

        CodeBlockText codeBlock = new CodeBlockText(digestedString.toString());
        return new ParserResponse(codeBlock, true);
    }

    public static String applyInlineCode(AbstractContent inlineCodeText) {
        // Ensure the digested string is surrounded by two backticks if it contains backticks itself
        if (inlineCodeText.getDigestedString().contains("`")) {
            return "``" + ((InlineCodeText)inlineCodeText).getDigestedString() + "``";
        } else {
            return "`" + ((InlineCodeText)inlineCodeText).getDigestedString() + "`";
        }
    }

    public static ParserResponse parseInlineCode(String inlineCodeText) {
        String prefix = inlineCodeText.substring(0, 2);
        int affixLength = prefix.equals("``") ? 2 : 1;

        InlineCodeText inlineCode = new InlineCodeText(inlineCodeText.substring(affixLength,
            inlineCodeText.length() - affixLength));
        return new ParserResponse(inlineCode, true);
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

    public static ParserResponse parseUnorderedList(String unorderedListText) {
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

        UnorderedListText unorderedList = new UnorderedListText(digestedString.toString(),
            itemStartIndices);
        return new ParserResponse(unorderedList);
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

    public static ParserResponse parseOrderedList(String orderedListText) {
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

        OrderedListText orderedList = new OrderedListText(digestedString.toString(), itemStartIndices, itemNumbers);
        return new ParserResponse(orderedList);
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

    public static ParserResponse parseBlockQuote(String blockQuoteText) {
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

        BlockQuoteText blockQuote = new BlockQuoteText(digestedString.toString());
        return new ParserResponse(blockQuote);
    }

    public static String applyLineBreak(AbstractContent lineBreak) {
        return "  ";
    }

    public static ParserResponse parseLineBreak(String lineBreak) {
        return new ParserResponse(new LineBreak());
    }

    public static String applyBold(AbstractContent boldText) {
        return "**" + ((BoldText)boldText).getDigestedString() + "**";
    }

    public static ParserResponse parseBold(String boldText) {
        BoldText bold = new BoldText(boldText.substring(2, boldText.length() - 2));
        return new ParserResponse(bold);
    }

    public static String applyItalic(AbstractContent italicText) {
        return "*" + ((ItalicText)italicText).getDigestedString() + "*";
    }

    public static ParserResponse parseItalic(String italicText) {
        ItalicText italics = new ItalicText(italicText.substring(1, italicText.length() - 1));
        return new ParserResponse(italics);
    }

    public static String applyHeading(AbstractContent headingText) {
        HeadingText heading = (HeadingText)headingText;
        return "#".repeat(heading.getLevel()) + " " + heading.getDigestedString();
    }

    public static ParserResponse parseHeading(String headingText) {
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

        HeadingText heading = new HeadingText(digestedText, level);
        return new ParserResponse(heading);
    }
}
