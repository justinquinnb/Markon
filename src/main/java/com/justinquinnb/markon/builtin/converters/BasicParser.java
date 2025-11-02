package com.justinquinnb.markon.builtin.converters;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;
import com.justinquinnb.markon.model.conversion.abstractlang.Document;
import com.justinquinnb.markon.model.conversion.parsing.MarkupParser;
import com.justinquinnb.markon.model.conversion.parsing.ParsingRuleset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A basic, built-in markup parser
 */
public class BasicParser implements MarkupParser {
    private static final Logger logger = LoggerFactory.getLogger(BasicParser.class);

    @Override
    public AbstractContentTree parse(String text, ParsingRuleset ruleset) {
        String preparedText = ruleset.getPreProcessor().apply(text);

        PriorityQueue<AbstractContent> parsedContent = parseContent(preparedText, ruleset);
        return buildTree(parsedContent);
    }

    /**
     * Parses all content in the given {@code text} using the provided {@code ruleset}.
     * @param text the text whose markup content to parse
     * @param ruleset the rules defining content discovery and parsing
     * @return all content parsed from the given {@code text}, ordered by length
     */
    private static PriorityQueue<AbstractContent> parseContent(String text, ParsingRuleset ruleset) {
        PriorityQueue<AbstractContent> content = new PriorityQueue<>();

        // Wrap everything in a document
        content.add(new Document(text));

        // The original text with all digestion applied up to any given moment
        String workingText = text;
        logger.trace("Parsing all content...");
        for (Entry<Pattern, Function<String, ? extends AbstractContent>> rule : ruleset) {
            logger.trace("Applying rule for pattern: {}", rule.getKey().pattern());
            Matcher matcher = rule.getKey().matcher(workingText);
            while (matcher.find()) {
                logger.trace("Working text currently:\n{}", workingText);
                // Identify the match
                int matchLength = matcher.end() - matcher.start();
                String matchedText = matcher.group();
                logger.trace("Found match:\n{}", matchedText);

                // Parse the match
                AbstractContent parsedContent = rule.getValue().apply(matchedText);
                parsedContent.setStartIndex(matcher.start()); // the start index doesn't change after parsing

                // Update all parents
                AbstractContent.adjustSurroundings(
                    content, matcher.start(), matchLength, parsedContent.getDigestedString());

                logger.trace("Digested match into parsedContent:\n{}\n\n", parsedContent);
                content.add(parsedContent);

                // Replace the exact instance of matched text with the digested text
                String leftPiece = workingText.substring(0, matcher.start());
                String rightPiece = workingText.substring(matcher.end());

                workingText = leftPiece + parsedContent.getDigestedString() + rightPiece;
                matcher = rule.getKey().matcher(workingText);
            }
        }

        logger.trace("All content parsed.");
        return content;
    }

    /**
     * Builds a tree of {@code AbstractContent} objects from the given list of size-sorted
     * {@code content}.
     *
     * @param content the list of size-sorted {@code AbstractContent} objects to build a tree from
     * @return a tree of {@code AbstractContent} objects built from the given list, representing
     * the content's hierarchy
     */
    private static AbstractContentTree buildTree(PriorityQueue<AbstractContent> content) {
        AbstractContentTree root = new AbstractContentTree(content.poll());
        List<AbstractContentTree> possibleParents = new ArrayList<>();
        possibleParents.add(root);

        // For every piece of content...
        logger.trace("Building tree from parsed content...");
        while(!content.isEmpty()) {
            AbstractContent currentContent = content.poll();

            boolean foundSpot = false;
            int i = 0;

            // Locate its parent
            logger.trace("Determining placement of content:\n{}", currentContent);
            while(!foundSpot && i < possibleParents.size()) {
                AbstractContentTree possibleParent = possibleParents.get(i);
                logger.trace("Checking parent:\n{}", possibleParent);

                // Which is determined as the node that surrounds the current piece
                if (possibleParent.getData().surrounds(currentContent)) {
                    logger.trace("Parent found!");
                    AbstractContentTree newNode = new AbstractContentTree(currentContent);
                    possibleParent.addChild(newNode);
                    newNode.setParent(possibleParent);
                    possibleParents.add(newNode);
                    foundSpot = true;
                }
                i++;
            }
        }

        logger.trace("Tree built.");
        return root;
    }
}
