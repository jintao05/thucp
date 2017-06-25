package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.processmining.models.heuristics.impl.HNSubSet;

public class Split extends Operator {

	private HashMap<String, Stats> splitsLearned;
	private int totalSplitsLearned;
	private int totalTracesLearned;
	
	//----------------------------
	
	private HashMap<String, Stats> splitsTested;
	private int totalSplitsTested;
	private int totalTracesTested;
	
	//----------------------------
	
//	private int correctCases;
//	private int incorrectCases;
//	private boolean correctTrace;
	
	//----------------------------
	
	private HashMap<String, HashSet<String>> triggers;
	
	private ArrayList<Integer> stack; 
	private boolean shortLoop;
	
	
	public Split(int mainElement, HNSubSet otherElements){
		
		super(mainElement, otherElements);

		this.splitsLearned = new HashMap<String, Stats>();
		this.totalSplitsLearned = 0;
		this.totalTracesLearned = 0;
		
		//----------------------------
		
		this.splitsTested = new HashMap<String, Stats>();
		this.totalSplitsTested = 0;
		this.totalTracesTested = 0;
		
		//----------------------------
		
//		this.correctCases = 0;
//		this.incorrectCases = 0;
//		this.correctTrace = true;
		
		//----------------------------

		this.stack = new ArrayList<Integer>();
		this.shortLoop = false;
		
		this.triggers = new HashMap<String, HashSet<String>>();
	}
	
	public void insertException(int taskA, int taskB){
		
		String taskAkey = String.valueOf(taskA);
		String taskBkey = String.valueOf(taskB);
		
		if(this.triggers.containsKey(taskAkey)){
			
			HashSet<String> temp = this.triggers.get(taskAkey);
			temp.add(taskBkey);
		}
		else{
			
			HashSet<String> temp = new HashSet<String>();
			temp.add(taskBkey);
			this.triggers.put(taskAkey, temp);
		}
		
		if(this.triggers.containsKey(taskBkey)){
			
			HashSet<String> temp = this.triggers.get(taskBkey);
			temp.add(taskAkey);
		}
		else{
			
			HashSet<String> temp = new HashSet<String>();
			temp.add(taskAkey);
			this.triggers.put(taskBkey, temp);
		}
	}
	
	public void insertException(ArrayList<Integer> exception){

		for(int i = 0; i < exception.size() - 1; i++){
			
			int taskA = exception.get(i);
			String taskAkey = String.valueOf(taskA);
			
			for(int j = i + 1; j < exception.size(); j++){
				
				int taskB = exception.get(j);
				String taskBkey = String.valueOf(taskB);
				
				if(this.triggers.containsKey(taskAkey)){
					
					HashSet<String> temp = this.triggers.get(taskAkey);
					temp.add(taskBkey);
				}
				else{
					
					HashSet<String> temp = new HashSet<String>();
					temp.add(taskBkey);
					this.triggers.put(taskAkey, temp);
				}
				
				if(this.triggers.containsKey(taskBkey)){
					
					HashSet<String> temp = this.triggers.get(taskBkey);
					temp.add(taskAkey);
				}
				else{
					
					HashSet<String> temp = new HashSet<String>();
					temp.add(taskAkey);
					this.triggers.put(taskBkey, temp);
				}
			}
		}
	}

	public void insertOccurrence(int element){
		
		if(element != this.mainElement){

			if(this.elements.contains(element)){
				
				this.stack.add(element);
				this.shortLoop = false;
			}
		}
		else this.flushStack();
	}
	
	public void flush(boolean endTrace){
		
//		if(!endTrace){
//			
//			System.out.println("Flush: "+this.mainElement+"\t"+this.stack.toString());
//			System.out.println(this.splits.toString());
//		}

		
		if(!endTrace){
			
//			if(!this.stack.isEmpty()) this.stack.remove(this.stack.size() - 1);
		}
		else{
			
			if(!this.stack.isEmpty()) this.flushStack();
			this.shortLoop = false;

//			if(correctTrace) System.out.println(this.mainElement+"\t"+correctTrace);
//			else System.err.println(this.mainElement+"\t"+correctTrace);
			
			if(super.learningMode) for(Stats stats : this.splitsLearned.values()) stats.setNewTrace();
			else for(Stats stats : this.splitsTested.values()) stats.setNewTrace();
			
			super.newTrace = true;
		}
		
		
//		if(!endTrace){
//			
//			System.out.println(this.splits.toString());
//		}
		
	}
	
	private void flushStack(){
		
		if(this.stack.isEmpty() && this.shortLoop) this.stack.add(this.mainElement);
		
		if(!this.stack.isEmpty()){
		
			String code = this.code(this.stack, this.triggers);
			
			if(super.learningMode){
				
				if(this.splitsLearned.containsKey(code)){
					
					Stats stats = this.splitsLearned.get(code);
					stats.addOccurence();

					this.totalSplitsLearned ++;
					if(super.newTrace){
						
						this.totalTracesLearned ++;
						super.newTrace = false;
					}
				}
				else{
					
					this.splitsLearned.put(code, new Stats());
					this.totalSplitsLearned ++;
					
					if(super.newTrace){
						
						this.totalTracesLearned ++;
						super.newTrace = false;
					}
				}
			}
			else{
				
				if(this.splitsTested.containsKey(code)){
					
					Stats stats = this.splitsTested.get(code);
					stats.addOccurence();

					this.totalSplitsTested ++;
					if(super.newTrace){
						
						this.totalTracesTested ++;
						super.newTrace = false;
					}
				}
				else{
					
					this.splitsTested.put(code, new Stats());
					this.totalSplitsTested ++;
					
					if(super.newTrace){
						
						this.totalTracesTested ++;
						super.newTrace = false;
					}
				}
			}
			
			this.stack.clear();
		}
		this.shortLoop = true;
	}
	
	public void reset(){
		
		this.splitsTested.clear();
		this.totalSplitsTested = 0;
		this.totalTracesTested = 0;
	}
	
	public float getAccuracy(){

		
		return 0f;
	}
	
	public int getSupport(){ return this.totalSplitsLearned;}
	
	public void print(HashMap<String, String> keys){
		
		String mainElement = keys.get(String.valueOf(this.mainElement));
		if (mainElement.indexOf("+") != -1) {
			mainElement = mainElement.substring(0, mainElement.indexOf("+"));
		}
		System.out.println("Outputs of "+mainElement + "\n");
		
//		System.out.println("Other elements: "+this.elements.toString());
		
		if(this.elements.size() > 0){
		
			for(int i = 0; i < this.elements.size(); i++){
			
				String element = keys.get(String.valueOf(this.elements.get(i)));
				if (element.indexOf("+") != -1) {
					element = element.substring(0, element.indexOf("+"));
				}
				System.out.print(element + " ");
			}
			System.out.println("\t#\t%\t#/trace");
			
			
			ArrayList<String> stack = new ArrayList<String>(this.splitsLearned.size());
			while(stack.size() < this.splitsLearned.size()){
				
				String maxCode = null;
				int maxSupport = -1;
				
				for(java.util.Map.Entry<String, Stats> entry : this.splitsLearned.entrySet()){
					
					String code = entry.getKey();
					if(!stack.contains(code)){
						
						int support = entry.getValue().getOccurrences();
						
						if(support > maxSupport){
							
							maxSupport = support;
							maxCode = code;
						}
					}
				}
				
				if(maxCode != null) stack.add(maxCode);
			}
			
			for(String code : stack){
				
				for(char value : code.toCharArray()) System.out.print(value+" ");
				
				int support = this.splitsLearned.get(code).getOccurrences();
				float percentage = 100f * support / this.totalSplitsLearned;
				System.out.println("\t"+ support + "\t" + percentage + "\t" + ((float)support / (float) this.splitsLearned.get(code).getDistinctOccurrences()));
			}
			
			//--------------------------------------
			
			System.out.println();
			
			stack = new ArrayList<String>(this.splitsTested.size());
			while(stack.size() < this.splitsTested.size()){
				
				String maxCode = null;
				int maxSupport = -1;
				
				for(java.util.Map.Entry<String, Stats> entry : this.splitsTested.entrySet()){
					
					String code = entry.getKey();
					if(!stack.contains(code)){
						
						int support = entry.getValue().getOccurrences();
						
						if(support > maxSupport){
							
							maxSupport = support;
							maxCode = code;
						}
					}
				}
				
				if(maxCode != null) stack.add(maxCode);
			}
			
			for(String code : stack){
				
				for(char value : code.toCharArray()) System.out.print(value+" ");
				
				int support = this.splitsTested.get(code).getOccurrences();
				float percentage = 100f * support / this.totalSplitsTested;
				System.out.println("\t"+ support + "\t" + percentage + "\t" + ((float)support / (float) this.splitsTested.get(code).getDistinctOccurrences()));
			}
		}
		else System.out.println("None");
		
//		System.out.println("Splits: "+splits.toString());
		/*
		if(!this.triggers.isEmpty()){
			
			System.out.println("\nConcurrent Outputs:");
			
			for(java.util.Map.Entry<String, ArrayList<Split>> entry : this.triggers.entrySet()){
				
				String nextTask = keys.get(entry.getKey());
				nextTask = nextTask.substring(0, nextTask.indexOf("+"));
				ArrayList<Split> concurrents = entry.getValue();
				
				for(Split concurrent : concurrents){
					
					String temp = keys.get(concurrent.toString());
					temp = temp.substring(0, temp.indexOf("+"));
					
					System.out.print(temp + " (");
					System.out.println("if " + nextTask + " follows " + mainElement + ")");
				}

			}
		}*/
		
//		System.out.println("Concurrents: "+this.triggers.toString());
		
		System.out.println("--x--");
	}
	
	public void printToPivotTable(HashMap<String, String> keys, float noisePercentage){
		
		String mainElement = keys.get(String.valueOf(this.mainElement));
		if (mainElement.indexOf("+") != -1) {
			mainElement = mainElement.substring(0, mainElement.indexOf("+"));
		}
		
		String h1 = super.threshold+";"+noisePercentage+";Split;"+mainElement+";";
		
		if(this.elements.size() > 0){
			
			for(java.util.Map.Entry<String, Stats> entry : this.splitsTested.entrySet()){
				
				String code = entry.getKey();
				
				String pn = "N";
				if(this.isSupportedPattern(code)) pn = "P";
				
				String pattern = "";
				ArrayList<Integer> stack = super.decode(code);
				for(Integer activity : stack){
					
					String temp = keys.get(activity.toString());
					if (temp.indexOf("+") != -1) {
						temp = temp.substring(0, temp.indexOf("+"));
					}
					
					pattern += temp + " ";
				}
				
				
//				for(int i = 0; i < code.length(); i++){
//					
//					if(code.charAt(i) == '1'){
//						
//						String temp = keys.get(String.valueOf(this.elements.get(i)));
//						temp = temp.substring(0, temp.indexOf("+"));
//						
//						pattern += temp + " ";
//					}
//				}
				
				int occurrences = entry.getValue().getOccurrences();
				float percentage = 100f * occurrences / this.totalSplitsTested;
				float occurrencesTrace = ((float) occurrences / (float) this.splitsTested.get(code).getDistinctOccurrences());

				String h2 = pattern.trim() + ";" + pn + ";" + occurrences + ";" + percentage + ";" + occurrencesTrace;
				
				System.out.println(h1 + h2);
			}
		}
	}
	
	public String toString(){return String.valueOf(this.mainElement);}
	
	public HashMap<String, Stats> getLearnedPatterns(){ return this.splitsLearned; }
	public HashMap<String, Stats> getTestedPatterns(){ return this.splitsTested; }
	public int learnedPatterns(boolean distinct){ 

		if(distinct)return this.totalTracesLearned; 
		else return this.totalSplitsLearned; 
	}
	public int testedPatterns(boolean distinct){ 

		if(distinct) return this.totalTracesTested;
		else return this.totalSplitsTested; 
	}
	public boolean isSupportedPattern(String code){

		boolean isSupportedPattern = false;

		if(this.splitsLearned.containsKey(code)){

			Stats supportedPatternStats = this.splitsLearned.get(code);

			float supportedPatternWeight = 100f * supportedPatternStats.getOccurrences() / this.totalSplitsLearned;

			if(supportedPatternWeight >= super.threshold) isSupportedPattern = true;
		}

		return isSupportedPattern;
	}	
}
