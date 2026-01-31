package langconversion.markdown.parsing;

import com.justinquinnb.markon.builtin.converters.BasicApplier;
import com.justinquinnb.markon.builtin.converters.BasicParser;
import com.justinquinnb.markon.builtin.langs.Markdown;
import com.justinquinnb.markon.model.MarkupLanguage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import util.ResultComparer;

/**
 * Tests the Markdown language
 */
@DisplayName("basicMarkdownHeading")
public class TestHeading {
    private static final BasicParser parser = new BasicParser();
    private static final BasicApplier applier = new BasicApplier();
    private static final MarkupLanguage lang = new Markdown();
    private static final ResultComparer comparer = new ResultComparer(parser, applier, lang);

    // ATX Style
    // Valid, Pure, Spaced, Unwrapped
    // https://spec.commonmark.org/0.31.2/#example-62
    @Test
    public void AtxStyleValidPureSpacedUnwrapped() {
        comparer.digestionIsValid("# foo", "foo");
    }

    // Valid, Pure, Spaced, Wrapped
    // https://spec.commonmark.org/0.31.2/#example-72
    @Test
    public void AtxStyleValidPureSpacedWrapped() {
        comparer.digestionIsValid("# foo ###### ", "foo");
    }

    // Valid, Pure, Unspaced, Unwrapped
    // Not in CommonMark spec, but very frequently permitted
    @Test
    public void AtxStyleValidPureUnspacedUnwrapped() {
        comparer.digestionIsValid("#foo", "foo");
    }

    // Valid, Pure, Unspaced, Wrapped
    // Not in CommonMark spec, but very frequently permitted
    @Test
    public void AtxStyleValidPureUnspacedWrapped() {
        comparer.digestionIsValid("#foo #######", "foo");
    }

    // Valid, With Embedded
    @Test
    public void AtxStyleValidWithEmbedded() {
        comparer.digestionIsValid("# foo *bar*", "foo bar");
    }
}
