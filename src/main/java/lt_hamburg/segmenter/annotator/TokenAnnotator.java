package lt_hamburg.segmenter.annotator;

import de.tudarmstadt.lt.seg.Segment;
import de.tudarmstadt.lt.seg.SegmentType;
import de.tudarmstadt.lt.seg.sentence.ISentenceSplitter;
import de.tudarmstadt.lt.seg.sentence.RuleSplitter;
import de.tudarmstadt.lt.seg.token.ITokenizer;
import de.tudarmstadt.lt.seg.token.RuleTokenizer;
import lt_hamburg.segmenter.type.Token;
import org.apache.uima.UIMAFramework;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import static org.apache.uima.fit.util.JCasUtil.select;

/**
 * UIMA Annotator that uses the LT segmenter to create UIMA type tokens.
 * Also produces DkPro Token Annotations for interoperability with other systems
 */
public class TokenAnnotator extends JCasAnnotator_ImplBase {
    private ThreadLocal<RuleSplitter> splitter = ThreadLocal.withInitial(() -> new RuleSplitter());

    @Override
    public void initialize(UimaContext context)
            throws ResourceInitializationException {
        super.initialize(context);
    }

    ThreadLocal<RuleTokenizer> tok = ThreadLocal.withInitial(() -> new RuleTokenizer());

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        String text = aJCas.getDocumentText();
        String lang = aJCas.getDocumentLanguage();

        RuleTokenizer to = tok.get();
        to.initParam(lang);

        splitter.get().initParam(lang, true).init(text).stream().sequential()
                .filter(s -> s.type == SegmentType.SENTENCE)
                .forEach( s -> {
                    to.init(s.asString()).stream()
                            .filter(t -> t.isReadable())
                            .forEach(t -> {
                                System.out.println(t.asString());
                                System.out.println(aJCas.getDocumentText().substring(s.begin + t.begin, s.begin + t.end));
                                addToken(aJCas, t.begin, t.end, t.type.name());
                            });

                    System.out.println("sssssssssssssssssssssssssssssssssssss");
                });


        for (Token t : select(aJCas, Token.class)) {
            createToken(aJCas, t.getBegin(), t.getEnd());
        }
    }


    private void addToken(JCas jcas, int begin, int end, String type) {
        Token token = new Token(jcas, begin, end);
        token.setTokenType(type);
        token.addToIndexes();
    }


    private void createToken(JCas aJCas, int begin, int end) {
        de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token tok = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(
                aJCas, begin, end);
        tok.addToIndexes();
    }
}
