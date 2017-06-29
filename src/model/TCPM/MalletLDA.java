package model.TCPM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;


public class MalletLDA {
	public ParallelTopicModel model;
	public Map<Integer, String> idx2word = new HashMap<Integer, String>();
	public String[][] topWords;
	public double[][] d2tDist;
	public double alpha=1.0, beta=0.01;
	public int K, iteration=2000, topK=50, threadNum=2;

	public MalletLDA(int K) {
		this.K = K;
	}

	public MalletLDA(double alpha, double beta, int K, int iteration, int topK, int threadNum) {
		this.alpha = alpha;
		this.beta = beta;
		this.K = K;
		this.iteration = iteration;
		this.topK = topK;
		this.threadNum = threadNum;
	}

	public MalletLDA(TCPMParameters params) {
		this.alpha = params.alpha;
		this.beta = params.beta;
		this.K = params.K;
		this.iteration = params.iteration;
		this.topK = params.topK;
		this.threadNum = params.threadNum;
	}

	public void runLDA(File id2itemFile, File pditemFile) throws IOException {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(id2itemFile), "UTF-8"));
		String currentLine;
		while ((currentLine = br.readLine()) != null) {
			String[] strs = currentLine.split("\t");
			int id = Integer.parseInt(strs[0]);
			String term = strs[1];
			idx2word.put(id, term);
		}

		// using mallet
		// Begin by importing documents from text to feature sequences
		ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

		// Pipes: lowercase, tokenize, remove stopwords, map to features
		// pipeList.add(new CharSequenceLowercase());
		pipeList.add(new CharSequence2TokenSequence(Pattern
				// .compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
				.compile("(\\S*)")));
		// pipeList.add( new TokenSequenceRemoveStopwords(new
		// File("stoplists/en.txt"), "UTF-8", false, false, false) );
		pipeList.add(new TokenSequence2FeatureSequence());

		InstanceList instances = new InstanceList(new SerialPipes(pipeList));

		Reader fileReader = new InputStreamReader(new FileInputStream(pditemFile), "UTF-8");
		// instances.addThruPipe(new CsvIterator(fileReader, Pattern
		// .compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"), 3, 2, 1)); // data,
		// // label, name, fields
		instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("(.*),(.*),(.*)$"), 3, 2, 1));

		// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
		// Note that the first parameter is passed as the sum over topics, while
		// the second is the parameter for a single dimension of the Dirichlet
		// prior.
		model = new ParallelTopicModel(K, alpha, beta);

		model.addInstances(instances);

		// Use two parallel samplers, which each look at one half the corpus and
		// combine
		// statistics after every iteration.
		model.setNumThreads(threadNum);

		// Run the model for 50 iterations and stop (this is for testing only,
		// for real applications, use 1000 to 2000 iterations)
		model.setNumIterations(iteration);
		model.estimate();

		topK = 50;
		Object[][] tws = model.getTopWords(topK);
		topWords = new String[tws.length][tws[0].length];
		for(int i = 0; i < tws.length; i++) {
			for(int j = 0; j < tws[0].length; j++) {
				try {
					int idx = Integer.parseInt((String)tws[i][j]);
					topWords[i][j] = idx + "\t" + idx2word.get(idx);
				} catch (Exception e) {	//may cause java.lang.ArrayIndexOutOfBoundsException
					// TODO: handle exception
					topWords[i][j] = "";
				}
			}
		}

		d2tDist = model.getDocumentTopics(true, true);
	}
}
