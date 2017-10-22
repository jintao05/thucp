package model.CPMRM;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.sun.org.apache.regexp.internal.recompile;

import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.AlgoCMSPADE;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.abstractions.ItemAbstractionPair;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.patterns.Pattern;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator_FatBitmap;
import ca.pfv.spmf.test.MainTestCMSPADE_saveToFile;


public class MileStoneMiner {
	private String[][] clinicalOrder;
	private HashMap<String, Integer> c2iHashMap;
	private HashMap<Integer, String> i2cHashMap;
	private HashMap<Integer, String> i2categHashMap;
	private ArrayList<HashSet<String>> milestone;
	private String outFileName = "./data/CPMRM/mileStone_Input_test.txt";
	private double maxKVaule;
	private double curKVaule;
	private int patientsSize;

	//将orders序列调整为按id-日期排序的序列
	public MileStoneMiner(String orders[][]) throws IOException {
		// TODO Auto-generated constructor stub
		this.c2iHashMap = new HashMap<String,Integer>();
		this.i2cHashMap = new HashMap<Integer,String>();
		this.i2categHashMap = new HashMap<Integer, String>();
		String ordName = "";
		int index = 1;
		Arrays.sort(orders, new Comparator<String[]>(){
            @Override
            public int compare(String[] o1, String[] o2) {
                return o1[0].compareTo(o2[0]);
            }
        });
		String curID = "",lastID = "";
		int startIndex = 0, endIndex = 0;
		this.patientsSize = 0;
		String curPatientName = "",lastPatientNameString = "";
		for(int i = 0; i < orders.length; i++){
			curPatientName = orders[i][0];
			if (!curPatientName.equals(lastPatientNameString)) {
				patientsSize++;
				lastPatientNameString = curPatientName;
			}
			ordName = orders[i][1];
			if(!c2iHashMap.containsKey(ordName)) {
				c2iHashMap.put(ordName, index);
				i2cHashMap.put(index, ordName);
				index++;
			}
			if(!i2categHashMap.containsKey(c2iHashMap.get(ordName))) i2categHashMap.put(c2iHashMap.get(ordName), orders[i][2]);
			curID = orders[i][0];
			if(lastID == "") lastID = curID;
			if(!curID.equals(lastID)) {
				Arrays.sort(orders, startIndex, endIndex+1, new Comparator<String[]>(){
		            @Override
		            public int compare(String[] o1, String[] o2) {
		            	return o1[3].compareTo(o2[3]);
		            }
		        });
				startIndex = i;
				endIndex = i;
			}
			else endIndex = i;
			lastID = curID;
		}
		Arrays.sort(orders, startIndex, endIndex+1, new Comparator<String[]>(){
            @Override
            public int compare(String[] o1, String[] o2) {
            	return o1[3].compareTo(o2[3]);
            }
        });
		this.clinicalOrder = orders;
		ExcelUtil rd = new ExcelUtil();
		rd.csvWrite(clinicalOrder,"./data/CPMRM/sorted_output_test.csv");

		//生成cm-spade算法的输入文件
		File outFile = new File(this.outFileName);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
		String line = "";
		HashSet<Integer> dayOrders = new HashSet<Integer>();
		String lastIdString = "", curIdString = "", lastDateString = "", curDateString = "";
		for (int i = 0; i < this.clinicalOrder.length; i++) {
			curIdString = clinicalOrder[i][0];
			if(lastIdString == "") lastIdString = curIdString;
			curDateString = clinicalOrder[i][3];
			if(lastDateString == "") {
				lastDateString = curDateString;
			}
			if(curIdString.equals(lastIdString)) {
				if(curDateString.equals(lastDateString)) {
					dayOrders.add(c2iHashMap.get(clinicalOrder[i][1]));
				}
				else {
					if (!dayOrders.isEmpty()) {
						for (Integer integer : dayOrders) {
							line = line + integer.toString() + " ";
						}
						line = line + "-1" + " ";
						dayOrders.clear();
					}
					dayOrders.add(c2iHashMap.get(clinicalOrder[i][1]));
				}
			}
			else {
				if (!dayOrders.isEmpty()) {
					for (Integer integer : dayOrders) {
						line = line + integer.toString() + " ";
					}
					line = line + "-1" + " "+"-2";
					dayOrders.clear();
				}
				writer.write(line);
				writer.newLine();
				line = "";
				dayOrders.add(c2iHashMap.get(clinicalOrder[i][1]));
			}
			lastDateString = curDateString;
			lastIdString = curIdString;
		}
		if (!dayOrders.isEmpty()) {
			for (Integer integer : dayOrders) {
				line = line + integer.toString() + " ";
			}
			line = line + "-1" + " "+"-2";
			dayOrders.clear();
		}
		writer.write(line);
		writer.close();
	}

	public String getOutFileName(){
		return this.outFileName;
	}
	public HashMap<String, Integer> getc2iHashMap(){
		return this.c2iHashMap;
	}
	public HashMap<Integer, String> geti2cHashMap(){
		return this.i2cHashMap;
	}
	public HashMap<Integer, String> geti2categHashMap(){
		return this.i2categHashMap;
	}



	public ArrayList<HashSet<String>> getMileStone(int stepnumber,StringBuffer result) throws IOException {
		ArrayList<HashSet<String>> Llist = new ArrayList<HashSet<String>>();
		ArrayList<Pattern> mileStonesList;
		double support = 0.5,kValue;
//		cmSpade(support,c2categHashMap,Llist);
		mileStonesList = cmSpade(support,i2categHashMap,stepnumber);
		kValue = this.curKVaule;
//		System.out.println(mileStonesList.toString());
		for (Pattern pattern : mileStonesList) {
			List<ItemAbstractionPair> elements = pattern.getElements();
			String item;
			for (int i = 0; i < elements.size();i++) {
				item = elements.get(i).getItem().toString();
				if(i >= Llist.size()) {
					HashSet<String> set = new HashSet<String>();
					Llist.add(set);
				}
				if (!Llist.get(i).contains(item)) {
					Llist.get(i).add(item);
				}
			}
		}
		result.append("\n 支持度为"+support+",K = "+kValue+",关键路径："+Llist.toString()+"\n");
		System.out.println("支持度为"+support+",K = "+kValue+",关键路径："+Llist.toString());
		return Llist;
	}

	private ArrayList<Pattern> cmSpade(double support, HashMap<Integer, String> i2categHashMap,int stepnumber) throws IOException{
		String outputPath ="./data/CPMRM/mileStone_Output_test.txt";
		// Load a sequence database
        boolean keepPatterns = true;
        boolean verbose = false;

        AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
        boolean dfs=true;

        // if you set the following parameter to true, the sequence ids of the sequences where
        // each pattern appears will be shown in the result
        boolean outputSequenceIdentifiers = false;
        IdListCreator idListCreator = IdListCreator_FatBitmap.getInstance();
        CandidateGenerator candidateGenerator = CandidateGenerator_Qualitative.getInstance();
        SequenceDatabase sequenceDatabase = new SequenceDatabase(abstractionCreator, idListCreator);
        sequenceDatabase.loadFile(this.outFileName, support);
//        System.out.println(sequenceDatabase.toString());

        AlgoCMSPADE algorithm = new AlgoCMSPADE(support,dfs,abstractionCreator);
        ArrayList<Pattern> mileStonesList = algorithm.runAlgorithm(sequenceDatabase, candidateGenerator,keepPatterns,verbose,outputPath, outputSequenceIdentifiers,i2categHashMap,stepnumber);
        curKVaule = algorithm.getMaxKValue();
        if(curKVaule > maxKVaule) maxKVaule = curKVaule;
//        System.out.println("Relative Minimum support = "+support);
//        System.out.println(algorithm.getNumberOfFrequentPatterns()+ " frequent patterns.");
//        System.out.println(algorithm.printStatistics());
        return mileStonesList;
	}

	public double getCurKValue() {
		return this.curKVaule;
	}
	public double getMaxKValue() {
		return this.maxKVaule;
	}

    private static String fileToPath(String filename) throws UnsupportedEncodingException {
        URL url = MainTestCMSPADE_saveToFile.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }


}
