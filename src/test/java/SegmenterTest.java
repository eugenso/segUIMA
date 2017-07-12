import lt_hamburg.segmenter.annotator.TokenAnnotator;
import lt_hamburg.segmenter.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.apache.uima.fit.util.JCasUtil.select;


public class SegmenterTest {

    private JCas cas;
    private AnalysisEngine tokenizer;
    private final String input = "This is an example @User1, increase by 100$, 20 100% $money #wow #bullish.!!!1!? More information at https://goo.gl/asdf  .";

    @org.junit.Test
    public void Train() throws UIMAException {
        cas = JCasFactory.createJCas();
        cas.setDocumentText(input);
        cas.setDocumentLanguage("en");


        tokenizer = AnalysisEngineFactory.createEngine(TokenAnnotator.class);


        tokenizer.process(cas);
        for (Token tok : getTokens(cas)) {
            System.out.println(tok.getCoveredText() + "\t" + tok.getTokenType());
        }

    }

    private List<String> getTokenStrings(JCas cas) {
        List<String> tokenStrings = new ArrayList<>();
        Collection<Token> tokens = select(cas, Token.class);
        for (Annotation token : tokens) {
            tokenStrings.add(token.getCoveredText());
        }
        return tokenStrings;
    }

    private Collection<Token> getTokens(JCas cas) {
        Collection<Token> tokens = select(cas, Token.class);

        return tokens;
    }

}
