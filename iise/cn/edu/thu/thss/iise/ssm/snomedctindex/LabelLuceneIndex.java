/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: jintao05@gmail.com
 *
 * This program is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation with the version of 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package cn.edu.thu.thss.iise.ssm.snomedctindex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.wordnet.SynExpand;
import org.apache.lucene.wordnet.SynonymMap;
import org.tartarus.snowball.ext.englishStemmer;

import cn.edu.thu.thss.iise.util.FileUtil;

/**
 * every label in this index is unique
 *
 * @author Tao Jin
 *
 */
public class LabelLuceneIndex {

	private String parentDirectory = "snomedIndex";
	private File INDEX_DIR = new File(parentDirectory, "labelIndex");
	private IndexWriter indexWriter = null;
	private Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_CURRENT,
			"English", StandardAnalyzer.STOP_WORDS_SET);
	private Directory indexDir = null;
	private englishStemmer stemer = new englishStemmer();

	public LabelLuceneIndex(String parentDir) {
		parentDirectory = parentDir;
		INDEX_DIR = new File(parentDirectory, "labelIndex");
		try {
			// indexDir = new RAMDirectory(FSDirectory.open(INDEX_DIR));
			indexDir = FSDirectory.open(INDEX_DIR);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean open() {
		try {
			if (indexWriter != null) {
				indexWriter.close();
			}
			indexWriter = new IndexWriter(this.indexDir, analyzer, false,
					IndexWriter.MaxFieldLength.UNLIMITED);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean create() {
		try {
			if (indexWriter != null) {
				indexWriter.close();
			}
			indexWriter = new IndexWriter(this.indexDir, analyzer, true,
					IndexWriter.MaxFieldLength.UNLIMITED);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void close() {
		try {
			if (indexWriter != null) {
				indexWriter.optimize();
				indexWriter.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		close();
		FileUtil.deleteFile(INDEX_DIR.getAbsolutePath());
	}

	// check whether the label is contained in this index
	public boolean contain(String label) {
		try {
			IndexReader reader = IndexReader.open(this.indexDir, true);
			Searcher searcher = new IndexSearcher(reader);
			// use the boolean query
			HashSet<String> queryTermSet = new HashSet<String>();
			TokenStream stream = analyzer.tokenStream(
					LabelDocument.FIELD_LABEL, new StringReader(label));
			TermAttribute termAtt = stream.addAttribute(TermAttribute.class);
			stream.reset();
			while (stream.incrementToken()) {
				queryTermSet.add(termAtt.term());
			}
			stream.end();
			stream.close();

			// construct the query
			BooleanQuery bq = new BooleanQuery();
			Iterator<String> it = queryTermSet.iterator();
			while (it.hasNext()) {
				String s = it.next();
				Term term = new Term(LabelDocument.FIELD_LABEL, s);
				TermQuery termQuery = new TermQuery(term);
				bq.add(termQuery, Occur.MUST);
			}

			ExactLabelQueryResultCollector collector = new ExactLabelQueryResultCollector(
					reader, label);
			searcher.search(bq, collector);
			boolean ret = collector.isExistQueryLabel();
			reader.close();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addLabel(String label) {
		try {
			label = label.trim();
			if (contain(label)) {
				return false;
			}
			Document doc = LabelDocument.Document(label);
			indexWriter.addDocument(doc);
			indexWriter.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void deleteLabel() {
		System.out.println("unsupported now");
	}

	public TreeSet<SimilarLabelQueryResult> getSimilarLabels(String query,
			float similarity) {
		TreeSet<SimilarLabelQueryResult> ret = new TreeSet<SimilarLabelQueryResult>();
		if (query == null) {
			ret.add(new SimilarLabelQueryResult(null, 1));
			return ret;
		}
		try {
			IndexReader reader = IndexReader.open(this.indexDir, true);
			Searcher searcher = new IndexSearcher(reader);

			// get terms from query
			HashSet<String> queryTermSet = new HashSet<String>();
			TokenStream stream = analyzer.tokenStream(
					LabelDocument.FIELD_LABEL, new StringReader(query));
			TermAttribute termAtt = stream.addAttribute(TermAttribute.class);
			stream.reset();
			while (stream.incrementToken()) {
				queryTermSet.add(termAtt.term());
			}
			stream.end();
			stream.close();

			// construct the query
			BooleanQuery bq = new BooleanQuery();
			Iterator<String> it = queryTermSet.iterator();
			SynonymMap synMap = SynonymIndex.getSynonymMap();
			HashSet<String> expandedQueryTermSet = new HashSet<String>(
					queryTermSet);

			while (it.hasNext()) {
				String s = it.next();
				Term term = new Term(LabelDocument.FIELD_LABEL, s);
				TermQuery termQuery = new TermQuery(term);
				bq.add(termQuery, Occur.SHOULD);
				// expand using synonyms
				for (String syn : synMap.getSynonyms(s)) {
					stemer.setCurrent(syn);
					stemer.stem();
					syn = stemer.getCurrent();
					if (expandedQueryTermSet.add(syn)) {
						term = new Term(LabelDocument.FIELD_LABEL, syn);
						termQuery = new TermQuery(term);
						bq.add(termQuery, Occur.SHOULD);
					}
				}
			}

			// search in the label index
			SimilarLabelQueryResultCollector collector = new SimilarLabelQueryResultCollector(
					reader, queryTermSet, similarity);
			searcher.search(bq, collector);
			ret = collector.getQueryResult();
			searcher.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public float getStorageSizeInMB() {
		return FileUtil.getFileSizeInMB(INDEX_DIR.getAbsolutePath());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// test LabelLuceneIndex
		try {
			FileWriter fw = new FileWriter("labelsimilar.txt", false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("similarity, nlabels, similarsize, timeconsumed(ms)");
			bw.newLine();

			for (float similarity = 0.05f; similarity <= 1; similarity += 0.05f) {

				LabelLuceneIndex index = new LabelLuceneIndex(
						"processrepository/index");
				index.create();
				FileReader fr = new FileReader("labels.txt");
				BufferedReader br = new BufferedReader(fr);
				String label = br.readLine();
				int nLabel = 0;
				while (label != null) {
					nLabel++;
					// bw.write(label);
					// bw.newLine();
					long startTime = System.currentTimeMillis();
					TreeSet<SimilarLabelQueryResult> ret = index
							.getSimilarLabels(label, similarity);
					long endTime = System.currentTimeMillis();
					long timeConsumed = endTime - startTime;
					// Iterator<LabelQueryResult> it = ret.iterator();
					// while (it.hasNext()) {
					// LabelQueryResult t = it.next();
					// bw.write(t.toString());
					// bw.newLine();
					// }
					bw.write(String.valueOf(similarity) + ", "
							+ String.valueOf(nLabel) + ", "
							+ String.valueOf(ret.size()) + ", "
							+ String.valueOf(timeConsumed));
					bw.newLine();
					System.out.println(similarity + ", " + nLabel + ", "
							+ ret.size() + ", " + timeConsumed);
					// bw.write("#####################");
					// bw.newLine();
					index.addLabel(label);
					label = br.readLine();
				}
				index.close();
				br.close();
				fr.close();
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// // test SnowballAnalyzer
		// try {
		// Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_CURRENT,
		// "English", StandardAnalyzer.STOP_WORDS_SET); // or any other
		// // analyzer
		// TokenStream stream = analyzer.tokenStream("myfield",
		// new FileReader("labelInfo.txt"));
		// TermAttribute termAtt = stream.addAttribute(TermAttribute.class);
		//
		// stream.reset();
		//
		// FileWriter fw = new FileWriter("stemWord.txt", false);
		// BufferedWriter bw = new BufferedWriter(fw);
		//
		// int n = 0;
		// while (stream.incrementToken()) {
		// bw.write(termAtt.term());
		// bw.newLine();
		// n++;
		// }
		// System.out.println("in total: " + n);
		//
		// stream.end();
		// stream.close();
		//
		// bw.flush();
		// bw.close();
		// fw.close();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}
}
