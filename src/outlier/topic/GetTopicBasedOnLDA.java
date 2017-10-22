package outlier.topic;

import java.util.Map;

import com.hankcs.lda.Corpus;
import com.hankcs.lda.LdaGibbsSampler;
import com.hankcs.lda.LdaUtil;

public class GetTopicBasedOnLDA {
	public static void main(String[] args) throws Exception{
		GetTopicBasedOnLDA ins=new GetTopicBasedOnLDA();
		String corpusPath="data/OutlierDetection/corpus";
		ins.lda(corpusPath,13);
	}

	public void lda(String corpusPath,Integer K) throws Exception
    {
        // 1. Load corpus from disk
        Corpus corpus = Corpus.load(corpusPath);
        // 2. Create a LDA sampler
        LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
        // 3. Train it
        ldaGibbsSampler.gibbs(K);
        // 4. The phi matrix is a LDA model, you can use LdaUtil to explain it.
        double[][] phi = ldaGibbsSampler.getPhi();
        Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), 100000);
        LdaUtil.explain(topicMap);
        double[][] theta=ldaGibbsSampler.getTheta();
        LdaUtil.saveTheta(theta);
        // 5. TODO:Predict. I'm not sure whether it works, it is not stable.
//        int[] document = Corpus.loadDocument("data/mini/鍐涗簨_510.txt", corpus.getVocabulary());
//        double[] tp = LdaGibbsSampler.inference(phi, document);
//        Map<String, Double> topic = LdaUtil.translate(tp, phi, corpus.getVocabulary(), 10);
//        LdaUtil.explain(topic);
    }
}
