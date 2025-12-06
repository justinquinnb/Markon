package com.justinquinnb.markon.builtin.converters;

import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContent;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;
import com.justinquinnb.markon.model.conversion.application.ApplicationRuleset;
import com.justinquinnb.markon.model.conversion.application.MarkupApplier;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicApplier implements MarkupApplier {
    private static final Logger logger = LoggerFactory.getLogger(BasicApplier.class);

    @Override
    public String apply(AbstractContentTree tree, ApplicationRuleset ruleset) {
        logger.trace("Applying all markup to...\n{}", tree);
        recursiveApply(tree, tree, ruleset);
        String markedUpContent = tree.getData().getDigestedString();

        logger.trace("Applying postprocessor to...\n{}", markedUpContent);
        String postprocessedText = ruleset.getPostProcessor().apply(markedUpContent);

        logger.trace("All markup applied to produce:\n{}", postprocessedText);
        return postprocessedText;
    }

    private static void recursiveApply(
        AbstractContentTree wholeTree, AbstractContentTree subTree, ApplicationRuleset ruleset
    ) {
        if (!subTree.isLeaf()) {
            logger.trace("Searching among leaves...");

            for (AbstractContentTree child : subTree.getChildren()) {
                recursiveApply(wholeTree, child, ruleset);
            }
        }

        // If child is leaf, we've hit the end of our current path
        // So, apply the markup and adjust all other nodes above and adjacent
        AbstractContent currentNode = subTree.getData();
        String oldString = currentNode.getDigestedString();

        logger.trace("Leaf found. Applying {} markup to:\n{}",
            currentNode.getClass().getSimpleName(), currentNode);
        Function<AbstractContent, String> markupApplier = ruleset
            .getApplicationRuleset().get(currentNode.getClass());

        // Apply the markup
        String newString = markupApplier.apply(currentNode);
        logger.trace("Markup applied to produce: {}", newString);
        int oldLength = oldString.length();

        if (subTree.getParent() != null) {
            logger.trace("Marked as completed.");
            subTree.setVisited(true);
            wholeTree.adjust(currentNode.getStartIndex(), oldLength, newString);
        }
        logger.trace("Application complete.\n");
    }
}
