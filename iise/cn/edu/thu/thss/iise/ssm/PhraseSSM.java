/**
 *
 */
package cn.edu.thu.thss.iise.ssm;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.apache.lucene.wordnet.SynonymMap;
import org.tartarus.snowball.ext.englishStemmer;

import cn.edu.thu.thss.iise.ssm.snomedctindex.SynonymIndex;

/**
 * @author Tao Jin
 *
 */
public class PhraseSSM {

	private static englishStemmer stemer = new englishStemmer();

	public static float getSimilarity(String phrase1, String phrase2) {
		return diceSemanticSimilarity(snowballTokenize(phrase1),
				snowballTokenize(phrase2));
	}

	/**
	 * Dice coefficient is a term based similarity measure (0-1) whereby the
	 * similarity measure is defined as twice the number of terms common to
	 * compared entitys divided by the total number of terms in both tested
	 * entities. The Coefficient result of 1 indicates identical vectors as
	 * where a 0 equals orthogonal vectors.
	 *
	 * Dices coefficient = (2*Common Terms) / (Number of terms in String1 +
	 * Number of terms in String2)
	 *
	 * semantic means we use synonyms from WordNet
	 *
	 * the label must be tokenized before this function called
	 *
	 * @param termSet1
	 * @param termSet2
	 * @return
	 */
	public static float diceSemanticSimilarity(HashSet<String> termSet1,
			HashSet<String> termSet2) {
		// calculate the similarity
		float numerator = 0;
		float denominator = 0;
		float similarity = 0;

		SynonymMap synMap = SynonymIndex.getSynonymMap();
		Iterator<String> it = termSet1.iterator();
		while (it.hasNext()) {
			String s = it.next();
			if (termSet2.contains(s)) {
				numerator += 1;
				continue;
			}
			for (String syn : synMap.getSynonyms(s)) {
				stemer.setCurrent(syn);
				stemer.stem();
				syn = stemer.getCurrent();
				if (termSet2.contains(syn)) {
					numerator += 1;
					break;
				}
			}
		}

		denominator = termSet1.size() + termSet2.size();
		similarity = (2 * numerator) / denominator;

		return similarity;
	}

	/**
	 * tokenize the given string, all the words are extracted, lowercased, all
	 * the stop words are removed, and all the words are replaced with their
	 * stem
	 *
	 * @param phrase
	 * @return
	 */
	private static HashSet<String> snowballTokenize(String phrase) {
		HashSet<String> ret = new HashSet<String>();
		try {
			Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_CURRENT,
					"English", StandardAnalyzer.STOP_WORDS_SET);

			TokenStream stream = analyzer.tokenStream("label", new StringReader(phrase));
			TermAttribute termAtt = stream.addAttribute(TermAttribute.class);
			stream.reset();
			while (stream.incrementToken()) {
				ret.add(termAtt.term());
			}
			stream.end();
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(getSimilarity("Heart","Myocardium structure"));
	}

}
