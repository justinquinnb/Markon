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

    @Test
    public void givenMarkdown_whenParsed_thenPlainText() {
        System.out.println("-".repeat(160));
        String markdown = "***Hello* world**. *Hi!* ***What's up?***";
        String plainText = "Hello world. Hi! What's up?";

        BasicParser parser = new BasicParser();
        MarkupLanguage lang = new Markdown();
        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String parsedText = contentTree.getData().getDigestedString();
        assert parsedText.equals(plainText);
    }

    @Test
    public void givenMarkdown_whenParsedAndMarkedUp_thenMarkdown() {
        System.out.println("-".repeat(160));
        String markdown = "***Hello* world**. *Hi!* ***What's up?***";

        BasicParser parser = new BasicParser();
        BasicApplier applier = new BasicApplier();
        MarkupLanguage lang = new Markdown();
        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String markedUpText = applier.apply(contentTree, lang);
        assert markedUpText.equals(markdown);
    }

    @Test
    public void givenMultilineMarkdown_whenParsed_thenPlainText() {
        System.out.println("-".repeat(160));
        String markdown = "### **Awesome Header**\n***Hello* world**. *Hi!* ***What's up?***";
        String plainText = "Awesome Header\nHello world. Hi! What's up?";

        BasicParser parser = new BasicParser();
        MarkupLanguage lang = new Markdown();
        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String parsedText = contentTree.getData().getDigestedString();
        assert parsedText.equals(plainText);
    }

    @Test
    public void givenMultilineMarkdown_whenParsedAndMarkedUp_thenMarkdown() {
        System.out.println("-".repeat(160));
        String markdown = "### **Awesome Header**\n***Hello* world**. *Hi!* ***What's up?***";

        BasicParser parser = new BasicParser();
        BasicApplier applier = new BasicApplier();
        MarkupLanguage lang = new Markdown();
        AbstractContentTree contentTree = parser.parse(markdown, lang);
        String markedUpText = applier.apply(contentTree, lang);
        assert markedUpText.equals(markdown);
    }
}
