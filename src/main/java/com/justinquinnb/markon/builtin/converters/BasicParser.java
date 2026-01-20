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
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        PriorityQueue<AbstractContent> parsedContent = parseAllContent(text, ruleset);
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
     * @param sourceText the text whose markup content to parse
     * @param ruleset the rules defining content discovery and parsing
     * @return all content parsed from the given {@code text}, ordered by length
     */
    private static PriorityQueue<AbstractContent> parseAllContent(String sourceText, ParsingRuleset ruleset) {
        // Place all content in a descending length-order queue for later tree construction
        PriorityQueue<AbstractContent> content = new PriorityQueue<>();

        // Wrap everything in a document
        content.add(new Document(sourceText));

        // The original text with all digestion applied to it so far
        String digestedText = sourceText;

        // Remember any escaped substrings
        PriorityQueue<TextRegionIndices> ignoredRegions = new PriorityQueue<>();

        // Apply each rule to parse the markup out of the original text
        List<ParsingRule> parsingRules = ruleset.getRules();
        logger.trace("Parsing using {} rules.", parsingRules.size());
        for (ParsingRule rule : parsingRules) {
            digestedText = parseAllRuleMatches(rule, digestedText, content, ignoredRegions);
        }

        logger.trace("All content parsed. Total count: {}\n", content.size());
        return content;
    }

    /**
     * Parses all matches of the given {@code rule}'s {@link Pattern} in the given {@code text}.
     *
     * @param rule the {@link ParsingRule} to apply to the text, dictating what pattern to match
     *             and how to handle matched text
     * @param sourceText the text to look for matches within and parse
     * @param content the list of existing parsed content to add any new parsed content to
     * @param ignoredRegions a list of text regions to ignore matches within
     *
     * @return the original {@code text} with all matches of the given {@code rule} replaced
     * with the digested form the {@code rule} generated from each match
     */
    private static String parseAllRuleMatches(
        ParsingRule rule, String sourceText, PriorityQueue<AbstractContent> content,
        PriorityQueue<TextRegionIndices> ignoredRegions
    ) {
        // The original text with all digestion applied to it so far
        String digestedText = sourceText;

        // Apply the rule to each of its matches
        logger.trace("Applying rule: {}", rule.getName());
        Matcher matcher = rule.getPattern().matcher(digestedText);
        while (matcher.find()) {
            logger.trace("Working text currently:\n{}", digestedText);

            // Response is considered a proposal because we won't accept it if no digestion occurred.
            // This is to prevent matcher resets on undigested matches, which result in infinite
            // loops on the same match.
            String proposedText = parseRuleMatch(rule, matcher, digestedText, content,
                ignoredRegions);

            // Only update the matcher if the text passed as an argument actually underwent
            // digestion
            if (!digestedText.equals(proposedText)) {
                logger.trace("Working text underwent digestion. Refreshing matcher.");
                digestedText = proposedText;
                matcher = rule.getPattern().matcher(digestedText);
            } else {
                logger.trace("Working text did not undergo digestion. Preserving existing matcher.");
            }
        }
        logger.trace("All matches processed for pattern.\n");
        return digestedText;
    }

    /**
     * Parses a single match of a {@code rule}'s {@link Pattern} in the given {@code sourceText}.
     *
     * @param rule the rule that indicated what to find and how to handle matches
     * @param matcher the matcher that found the match
     * @param sourceText the text that the matcher was applied to and thus the match came from
     * @param content the list of existing parsed content to add any new parsed content to
     * @param ignoredRegions a list of text regions to ignore matches within
     *
     * @return the {@code sourceText} with the matched text replaced with the digested form the
     * {@code rule} generated from each match
     */
    private static String parseRuleMatch(
        ParsingRule rule, Matcher matcher, String sourceText,
        PriorityQueue<AbstractContent> content, PriorityQueue<TextRegionIndices> ignoredRegions
    ) {
        String digestedText = sourceText;

        // Identify the match
        int matchLength = matcher.end() - matcher.start();
        String matchedText = matcher.group();
        logger.trace("Examining match:\n{}", matchedText);

        // Validate the match
        ParsingContext context = new ParsingContext(
            matcher.start(), matchedText, content, sourceText, ignoredRegions);
        if (matchIsValid(rule, context)) {
            // Parse the match into abstract content piece(s) and digested string(s)
            ParserResponse parserResponse = rule.getParser().apply(matchedText);

            // Add each parsed content piece to the working text and update surroundings
            // accordingly
            int i = 1;
            int parsedContentCount = parserResponse.getContent().size();
            logger.trace("Parsed into {} piece(s) of content:", parsedContentCount);
            for (ParsedContent parsedContent : parserResponse.getContent()) {
                AbstractContent abstractContent = parsedContent.getContent();
                abstractContent.setStartIndex(matcher.start()); // the start index doesn't change after parsing

                logger.trace("Content {}/{}:\n{}", i, parsedContentCount, parsedContent);

                // Update the parsing environment to reflect the changes
                digestedText = updateParsingEnvironment(context, parsedContent);
                i++;
            }
        } else {
            logger.trace("Skipping match.");
            matcher.find();
        }

        logger.trace("Match processing complete.\n");
        return digestedText;
    }

    /**
     * Updates the parsing environment (attainable via {@code context}) to reflect the changes made
     * by the {@code mutator}. Specifically, this means:
     * <ol>
     *     <li>Adjusting surrounding {@code AbstractContent} nodes to account for the
     *     {@code mutator}'s displacement via {@link AbstractContent#adjustSurroundings}</li>
     *     <li>Adding the {@code mutator} to the master content list</li>
     *     <li>Replacing the {@code mutator}'s match string with its digested string in the source
     *     text</li>
     *     <li>Updating the ignored regions list, if necessary</li>
     * </ol>
     *
     * @param context the context in which the parsing operation occurred
     * @param mutator the content derived from the parsing operation warranting environment changes
     */
    private static String updateParsingEnvironment(ParsingContext context, ParsedContent mutator) {
        logger.trace("Updating parsing environment...");

        // Update all parents
        AbstractContent.adjustSurroundings(
            context.getCurrentlyParsedContent(), context.getMatchStartIndex(),
            context.getMatchText().length(), mutator.getContent().getDigestedString());

        // Add the mutator to the master parsed content list
        logger.trace("Adding mutator to the master content list...");
        context.getCurrentlyParsedContent().add(mutator.getContent());

        logger.trace("Checking whether embedded contents should be ignored...");
        if (mutator.isEmbeddedIgnored()) {
            logger.trace("Match's embedded contents should be ignored.");
            context.getIgnoredRegions().add(new TextRegionIndices(
                mutator.getContent().getStartIndex(), mutator.getContent().getEndIndex())
            );
        } else {
            logger.trace("Embedded contents should not be ignored.");
        }

        // Replace the exact instance of matched text with the digested text
        logger.trace("Replacing matched text with digested text...");
        String leftPiece = context.getSourceText().substring(0, context.getMatchStartIndex());
        String rightPiece = context.getSourceText().substring(context.getMatchEndIndex());

        logger.trace("Parsing environment successfully updated.");

        return leftPiece + mutator.getContent().getDigestedString() + rightPiece;
    }

    /**
     * Determines whether a given match is valid provided the {@code rule} used to find it and the
     * {@code context} it was found within.
     *
     * @return {@code true} if the match is valid and should be parsed, else {@code false} (the
     * match should be ignored)
     */
    private static boolean matchIsValid(ParsingRule rule, ParsingContext context) {
        logger.trace("Validating match: {}", context.getMatchText());

        // Ensure the match isn't in an ignored region
        logger.trace("Checking whether match is in an escaped region...");

        TextRegionIndices matchRegion = new TextRegionIndices(context.getMatchStartIndex(),
            context.getMatchEndIndex());

        boolean isInIgnoredRegion = isInIgnoredRegion(matchRegion, context.getIgnoredRegions());

        boolean isFilteredOut = false;
        if (isInIgnoredRegion) {
            logger.trace("Match is in an ignored region.");
        } else {
            logger.trace("Match is not in an ignored region.");

            // Determine the match's parsing suitability (only if region isn't ignored
            // from the get-go)
            logger.trace("Applying match filter...");
            isFilteredOut = rule.getFilter().apply(context);
            if (isFilteredOut) {
                logger.trace("Match is blocked by filter.");
            } else {
                logger.trace("Match is not blocked by filter.");
            }
        }

        boolean matchIsValid = !(isInIgnoredRegion || isFilteredOut);
        if (matchIsValid) {
            logger.trace("Match is suitable for parsing.");
        } else {
            logger.trace("Match is invalid.");
        }

        return matchIsValid;
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

    /**
     * Builds a tree of {@code AbstractContent} objects from the given list of size-sorted
     * {@code content}.
     * @param content the list of size-sorted {@code AbstractContent} objects to build a tree from
     *
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
}
