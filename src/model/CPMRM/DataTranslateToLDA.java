package model.CPMRM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DataTranslateToLDA {
	private int stepNumber = 0;
	private String databaseAddress = "./data/CPMRM/orders/step";
	private ArrayList<List<String>>  stepSets;

	public DataTranslateToLDA(String filename, ArrayList<HashSet<String>> milestone) throws IOException{
		this.stepNumber = milestone.size();
		//若文件夹不存在，则创建新文件夹
		File[] files = new File[stepNumber];
		for (int i = 0; i < files.length; i++) {
			String name = this.databaseAddress+String.valueOf(i+1);
			files[i] = new File(name);
			if(!files[i].exists()) files[i].mkdir();
		}

		BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		stepSets = new ArrayList<List<String>>();
		for (int i = 0; i < stepNumber; i++) {
			stepSets.add(new ArrayList<String>());
		}
		String line = "";
		while ((line = rd.readLine()) != null) {
             String[] words = line.split(" ");
             int i = 0;
             HashSet<String> tempSet = milestone.get(i);
             boolean sign = false;//表示当天的医嘱是否都要 ——true时为全要，false时为不一定。
             for (String word : words)
             {
            	 if(word.equals("-1")) {
            		 if(sign && i < stepNumber-1) {
            			 i++;
            			 tempSet = milestone.get(i);
            		 }
            		 sign = false;
            		 continue;
            	 }
            	 else if (word.equals("-2")) {
            		 break;
            	 }
            	 //word为医嘱时
            	 else {
            		 if (sign) {
            			 addWordToStepSet(i, word);
            			 continue;
            		 }
            		 //若当前关键医嘱阶段中含有该医嘱
            		 if(tempSet.contains(word)) {
            			 sign = true;
            			 addWordToStepSet(i, word);
            		 }
            		 else {
            			 addWordToStepSet(i, word);
            		 }
				}
             }
        }
        rd.close();
//        System.out.println("各阶段医嘱集合：");
//        for (int i = 0; i < stepSets.size(); i++) {
//        	System.out.println("第"+(i+1)+"阶段： "+stepSets.get(i));
//		}
        BufferedWriter[] wt = new BufferedWriter[stepNumber];
        for(int i = 0; i < stepNumber; i++) {
 			String outputname = databaseAddress+String.valueOf(i+1)+"/"+"output_step"+String.valueOf(i+1)+".txt";
 			wt[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputname)));
 			for(String word:stepSets.get(i)) {
 				wt[i].append(word);
 				wt[i].newLine();
 			}
 		}
        for(int i = 0; i < stepNumber; i++) {
        	wt[i].close();
 		}
	}

	private void addWordToStepSet(int stepIndex,String word) {
		stepSets.get(stepIndex).add(word);
	}
	public int getStepNumber() {
		return this.stepNumber;
	}
}
