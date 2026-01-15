package com.justinquinnb.markon.builtin.converters;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;
import com.justinquinnb.markon.model.conversion.abstractlang.Document;
import com.justinquinnb.markon.model.conversion.parsing.MarkupParser;
import com.justinquinnb.markon.model.conversion.parsing.ParsedContent;
import com.justinquinnb.markon.model.conversion.parsing.ParserResponse;
import com.justinquinnb.markon.model.conversion.parsing.ParsingContext;
import com.justinquinnb.markon.model.conversion.parsing.ParsingRule;
import com.justinquinnb.markon.model.conversion.parsing.ParsingRuleset;
import com.justinquinnb.markon.model.conversion.util.TextRegionIndices;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic, built-in markup parser
 */
public class BasicParser implements MarkupParser {
    private static final Logger logger = LoggerFactory.getLogger(BasicParser.class);

    @Override
    public AbstractContentTree parse(String text, ParsingRuleset ruleset) {
        if (ruleset.getPreProcessor() != null) {
            logger.trace("Applying preprocessor to:\n{}", text);
            text = ruleset.getPreProcessor().apply(text);
        }

        logger.trace("Parsing all content from text:\n{}", text);
        PriorityQueue<AbstractContent> parsedContent = parseContent(text, ruleset);
        AbstractContentTree abstractContentTree = buildTree(parsedContent);

        if (ruleset.getPostProcessor() != null) {
            logger.trace("Applying postprocessor to:\n{}", abstractContentTree);
            abstractContentTree = ruleset.getPostProcessor().apply(abstractContentTree);
        }

        logger.trace("Markup parsing completed to produce:\n{}", abstractContentTree);
        return abstractContentTree;
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
        List<ParsingRule> parsingRules = ruleset.getRules();

        // Remember any escaped substrings
        PriorityQueue<TextRegionIndices> ignoredRegions = new PriorityQueue<>();

        // Apply each rule to parse the markup out of the original text
        for (ParsingRule rule : parsingRules) {
            logger.trace("Applying rule: {}", rule.getName());
            Matcher matcher = rule.getPattern().matcher(workingText);
            while (matcher.find()) {
                logger.trace("Working text currently:\n{}", workingText);
                // Identify the match
                int matchLength = matcher.end() - matcher.start();
                String matchedText = matcher.group();
                logger.trace("Found match:\n{}", matchedText);

                // Ensure the match isn't in an ignored region
                logger.trace("Checking whether match is in an escaped region...");
                boolean isInIgnoredRegion = isInIgnoredRegion(
                    new TextRegionIndices(matcher.start(), matcher.end()), ignoredRegions);
                boolean isFilteredOut = false;
                if (isInIgnoredRegion) {
                    logger.trace("Match is in an ignored region.");
                } else {
                    logger.trace("Match is not in an ignored region.");

                    // Determine the match's parsing suitability (only if region isn't ignored
                    // from the get-go)
                    ParsingContext context = new ParsingContext(matcher.start(), matchedText, content, text);
                    logger.trace("Applying match filter...");
                    isFilteredOut = rule.getFilter().apply(context);
                    if (isFilteredOut) {
                        logger.trace("Match is blocked by filter.");
                    } else {
                        logger.trace("Match is not blocked by filter.");
                    }
                }

                // Skip invalid matches
                if (isInIgnoredRegion || isFilteredOut) {
                    logger.trace("Skipping match.");
                    matcher.find();
                } else {
                    logger.trace("Match is suitable for parsing.");
                    // Parse the match
                    ParserResponse parserResponse = rule.getParser().apply(matchedText);

                    // Add each parsed content piece to the working text and update surroundings
                    // accordingly
                    logger.trace("Parsed into:");
                    for (ParsedContent parsedContent : parserResponse.getContent()) {
                        AbstractContent abstractContent = parsedContent.getContent();
                        abstractContent.setStartIndex(matcher.start()); // the start index doesn't change after parsing
                        logger.trace(parsedContent.toString());

                        // Update all parents
                        AbstractContent.adjustSurroundings(
                            content, matcher.start(), matchLength, abstractContent.getDigestedString());

                        content.add(abstractContent);

                        // Replace the exact instance of matched text with the digested text
                        String leftPiece = workingText.substring(0, matcher.start());
                        String rightPiece = workingText.substring(matcher.end());

                        workingText = leftPiece + abstractContent.getDigestedString() + rightPiece;
                        matcher = rule.getPattern().matcher(workingText);

                        logger.trace("Checking whether embedded contents should be ignored...");
                        if (parsedContent.isEmbeddedIgnored()) {
                            logger.trace("Match's embedded contents should be ignored.");
                            ignoredRegions.add(new TextRegionIndices(
                                abstractContent.getStartIndex(), abstractContent.getEndIndex()));
                        } else {
                            logger.trace("Embedded contents should not be ignored.");
                        }
                    }
                }
                logger.trace("Match processing complete.\n");
            }
            logger.trace("All matches processed for pattern.\n");
        }

        logger.trace("All content parsed.\n");
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

            // Search backwards through possible parents to find the most specific (deepest) match
            logger.trace("Determining placement of content:\n{}", currentContent);
            for (int i = possibleParents.size() - 1; i >= 0 && !foundSpot; i--) {
                AbstractContentTree possibleParent = possibleParents.get(i);
                logger.trace("Checking parent:\n{}", possibleParent.getData());

                // Which is determined as the node that surrounds the current piece
                if (possibleParent.getData().surrounds(currentContent)) {
                    logger.trace("Parent found!");
                    AbstractContentTree newNode = new AbstractContentTree(currentContent);
                    possibleParent.addChild(newNode);
                    newNode.setParent(possibleParent);
                    possibleParents.add(newNode);
                    foundSpot = true;
                }
            }
        }

        logger.trace("Parsing complete.");
        return root;
    }

    /**
     * Check whether the given {@code matchRegion} falls within any of the provided
     * {@code ignoredRegions}.
     * @param matchRegion the region of text that was matched
     * @param ignoredRegions the regions of text to ignore
     * @return {@code true} if the match region overlaps with any of the escaped regions, else
     * {@code false}
     */
    private static boolean isInIgnoredRegion(TextRegionIndices matchRegion,
        PriorityQueue<TextRegionIndices> ignoredRegions) {
        PriorityQueue<TextRegionIndices> copy = new PriorityQueue<>(ignoredRegions);

        while (!copy.isEmpty()) {
            TextRegionIndices region = copy.poll();
            // Match starts after the currently viewed region ends, therefore, keep checking
            // This is the most likely case, so check it first
            boolean regionIsBefore = region.getEndIndex() < matchRegion.getStartIndex();
            if (regionIsBefore) continue;

            // Match ends before the currently viewed region starts
            // Therefore, there are no more relevant escaped regions to check... the match is
            // unescaped
            boolean regionIsAfter = matchRegion.getEndIndex() < region.getStartIndex();
            if (regionIsAfter) return false;

            // Match must be (at least partially) within the currently viewed, escaped region
            // So, ignore the match
            return true;
        }

        return false;
    }
}
