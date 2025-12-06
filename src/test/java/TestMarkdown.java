import com.justinquinnb.markon.builtin.converters.BasicApplier;
import com.justinquinnb.markon.builtin.converters.BasicParser;
import com.justinquinnb.markon.builtin.langs.Markdown;
import com.justinquinnb.markon.model.MarkupLanguage;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the Markdown language
 */
public class TestMarkdown {
    private static final Logger logger = LoggerFactory.getLogger(TestMarkdown.class);
    private static final BasicParser parser = new BasicParser();
    private static final BasicApplier applier = new BasicApplier();
    private static final MarkupLanguage lang = new Markdown();

    @Test
    public void givenMarkdownBoldSingleLine_whenParsed_thenParsed() {
        System.out.println("-".repeat(160));
        String markdown ="**Hello** world!";
        String plainText = "Hello world!";

        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String parsedText = contentTree.getData().getDigestedString();
        assert parsedText.equals(plainText);
    }

    @Test
    public void givenMarkdownItalicSingleLine_whenParsed_thenParsed() {
        System.out.println("-".repeat(160));
        String markdown ="*Hello* world!";
        String plainText = "Hello world!";

        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String parsedText = contentTree.getData().getDigestedString();
        assert parsedText.equals(plainText);
    }

    @Test
    public void givenMarkdownHeadingSingleLine_whenParsed_thenParsed() {
        System.out.println("-".repeat(160));
        String markdown ="### Hello world!";
        String plainText = "Hello world!";

        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String parsedText = contentTree.getData().getDigestedString();
        assert parsedText.equals(plainText);
    }

    @Test
    public void givenMarkdownLineBreakSingleLine_whenParsed_thenParsed() {
        System.out.println("-".repeat(160));
        String markdown ="Hello world!<br>";
        String plainText = "Hello world!\n";

        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String parsedText = contentTree.getData().getDigestedString();
        assert parsedText.equals(plainText);
    }

    @Test
    public void givenMarkdownMixedSingleLine_whenParsed_thenParsed() {
        System.out.println("-".repeat(160));
        String markdown = "***Hello* world**. *Hi!* ***What's up?***";
        String plainText = "Hello world. Hi! What's up?";

        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String parsedText = contentTree.getData().getDigestedString();
        assert parsedText.equals(plainText);
    }

    @Test
    public void givenMarkdownMixedSingleLine_whenParsedAndMarkedUp_thenMarkdown() {
        System.out.println("-".repeat(160));
        String markdown = "***Hello* world**. *Hi!* ***What's up?***";

        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String markedUpText = applier.apply(contentTree, lang);
        assert markedUpText.equals(markdown);
    }

    @Test
    public void givenMultiLineMarkdownMixedMultiLine_whenParsed_thenParsed() {
        System.out.println("-".repeat(160));
        String markdown = "### **Awesome Header**\n***Hello* world**. *Hi!* ***What's up?***";
        String plainText = "Awesome Header\nHello world. Hi! What's up?";

        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String parsedText = contentTree.getData().getDigestedString();
        assert parsedText.equals(plainText);
    }

    @Test
    public void givenMultiLineMarkdownMixedMultiLine_whenParsedAndMarkedUp_thenMarkdown() {
        System.out.println("-".repeat(160));
        String markdown = "### **Awesome Header**\n***Hello* world**. *Hi!* ***What's up?***";

        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String markedUpText = applier.apply(contentTree, lang);
        assert markedUpText.equals(markdown);
    }
}
