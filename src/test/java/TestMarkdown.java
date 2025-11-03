import com.justinquinnb.markon.builtin.converters.BasicApplier;
import com.justinquinnb.markon.builtin.converters.BasicParser;
import com.justinquinnb.markon.builtin.langs.Markdown;
import com.justinquinnb.markon.model.conversion.abstractlang.AbstractContentTree;
import org.junit.jupiter.api.Test;

/**
 * Tests the Markdown language
 */
public class TestMarkdown {
//    @Test
//    public void givenMarkdown_whenParsed_thenPlainText() {
//        String markdown = "***Hello* world**. *Hi!* ***What's up?***";
//        String plainText = "Hello world. Hi! What's up?";
//
//        BasicParser parser = new BasicParser();
//        Markdown lang = new Markdown();
//        AbstractContentTree contentTree = parser.parse(markdown, lang.getParsingRuleset());
//        String parsedText = contentTree.getData().getDigestedString();
//        System.out.println(parsedText);
//        assert parsedText.equals(plainText);
//    }

    @Test
    public void givenPlainText_whenMarkedUp_thenMarkdown() {
        String markdown = "***Hello* world**. *Hi!* ***What's up?***";
        //markdown = "### **Awesome Header**\n***Hello* world**. *Hi!* ***What's up?***";

        BasicParser parser = new BasicParser();
        BasicApplier applier = new BasicApplier();
        Markdown lang = new Markdown();
        AbstractContentTree contentTree = parser.parse(markdown, lang.getParsingRuleset());
        String markedUpText = applier.apply(contentTree, lang.getApplicationRuleset());
        System.out.println(markedUpText);
        assert markedUpText.equals(markdown);
    }
}
