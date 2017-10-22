package model.CPMRM;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hankcs.lda.Corpus;
import com.hankcs.lda.LdaGibbsSampler;
import com.hankcs.lda.LdaUtil;

public class LDACluster {
	private int stepNumber;
	StringBuffer result = new StringBuffer();
	private ArrayList<Integer> eventsInEachStep;
	private String databaseAddress = "./data/CPMRM/orders/step";
	public LDACluster(int n, String filename) throws IOException{
		this.stepNumber = n;
		this.eventsInEachStep = new ArrayList<Integer>();
		BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String line;
        while ((line = rd.readLine()) != null) {
             String[] words = line.split(" ");
             for (String word : words)
             {
                 this.eventsInEachStep.add(Integer.valueOf(word));
             }
         }
         rd.close();
	}

	public void cluster(HashMap<Integer, String> i2cHashMap) throws IOException{
		String filename = "";
		for(int i = 1; i <= stepNumber; i++) {
			filename = this.databaseAddress + String.valueOf(i);
			result.append(" -----------------------------------   \n");
			result.append(" 阶段"+i+":\n");
			System.out.println(" -----------------------------------   ");
			System.out.println("阶段"+i+":");
			runLDA(filename, this.eventsInEachStep.get(i-1), 5,i2cHashMap);
		}
	}


    public void runLDA(String filename, int k, int wordNumber, HashMap<Integer, String> i2cHashMap) throws IOException{

    	// 1. 从磁盘载入语料
//    	Corpus corpus = Corpus.load("data/test");
    	Corpus corpus = Corpus.load(filename);
    	// 2. 创建 LDA 采样器
    	LdaGibbsSampler ldaGibbsSampler = new LdaGibbsSampler(corpus.getDocument(), corpus.getVocabularySize());
    	// 3. 训练，目标10个主题
    	ldaGibbsSampler.gibbs(k);
    	// 4. phi 矩阵是唯一有用的东西，用 LdaUtil 来展示最终的结果
    	double[][] phi = ldaGibbsSampler.getPhi();
    	Map<String, Double>[] topicMap = LdaUtil.translate(phi, corpus.getVocabulary(), wordNumber);
    	LdaUtil.explain(topicMap,i2cHashMap,result);
    }

    public StringBuffer getResult()
    {
       return result;
    }
}
