package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.processmining.models.heuristics.impl.HNSubSet;

public class Join extends Operator{
	
	private HashMap<String, Stats> joinsLearned;
	private int totalJoinsLearned;
	private int totalTracesLearned;
	
	//----------------------------
	
	private HashMap<String, Stats> joinsTested;
	private int totalJoinsTested;
	private int totalTracesTested;
	
	//----------------------------
	
//	private int correctCases;
//	private int incorrectCases;
//	private boolean correctTrace;
	
	//----------------------------

	private HashMap<String, HashSet<String>> triggers;
	
	private ArrayList<Integer> stack; 
	private boolean shortLoop;
	private int lastIndex;
	
	
	
	public Join(int mainElement, HNSubSet otherElements){
		
		super(mainElement, otherElements);
		
		this.joinsLearned = new HashMap<String, Stats>();
		this.totalJoinsLearned = 0;
		this.totalTracesLearned = 0;
		
		//----------------------------
		
		this.joinsTested = new HashMap<String, Stats>();
		this.totalJoinsTested = 0;
		this.totalTracesTested = 0;
		
		//----------------------------
		
//		this.correctCases = 0;
//		this.incorrectCases = 0;
//		this.correctTrace = true;

		//----------------------------
						
		this.stack = new ArrayList<Integer>();
		this.shortLoop = true;
		this.lastIndex = 0;
		
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

	public void insertOccurrence(ArrayList<Integer> elements){
	
		
		if(!elements.isEmpty()){
			
			for(int index = elements.size() - 1; index >= this.lastIndex; index --){
			
				int element = elements.get(index);
				
				if(element != this.mainElement){
					
					if(this.elements.contains(element)){
						
						this.stack.add(element);
						this.shortLoop = false;
					}

				}
				else this.flushStack();
			}
			
			this.flushStack();
			this.lastIndex = elements.size() + 1;
		}
	}
	
	public void flush(boolean endTrace){

		if(!endTrace){
			
		}
		else{
		
			if(!this.stack.isEmpty()) this.flushStack();
			this.shortLoop = false;
			this.lastIndex = 0;
			
			if(super.learningMode) for(Stats stats : this.joinsLearned.values()) stats.setNewTrace();
			else for(Stats stats : this.joinsTested.values()) stats.setNewTrace();
			super.newTrace = true;
		}
	}
	
	private void flushStack(){
		
		if(this.stack.isEmpty() && this.shortLoop) this.stack.add(this.mainElement);
		
		if(!this.stack.isEmpty()){
		
			String code = this.code(this.stack, this.triggers);
			
			if(super.learningMode){
				
				if(this.joinsLearned.containsKey(code)){
					
					Stats stats = this.joinsLearned.get(code);
					stats.addOccurence();

					this.totalJoinsLearned ++;
					if(super.newTrace){
						
						this.totalTracesLearned ++;
						super.newTrace = false;
					}
				}
				else{
					
					this.joinsLearned.put(code, new Stats());
					this.totalJoinsLearned ++;
					
					if(super.newTrace){
						
						this.totalTracesLearned ++;
						super.newTrace = false;
					}
				}
			}
			else{
				
				if(this.joinsTested.containsKey(code)){
					
					Stats stats = this.joinsTested.get(code);
					stats.addOccurence();

					this.totalJoinsTested ++;
					if(super.newTrace){
						
						this.totalTracesTested ++;
						super.newTrace = false;
					}
				}
				else{
					
					this.joinsTested.put(code, new Stats());
					this.totalJoinsTested ++;
					
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
		
		this.joinsTested.clear();
		this.totalJoinsTested = 0;
		this.totalTracesTested = 0;
	}

	public float getAccuracy(){
		
		
		
		return 0f;
	}
	
	public int getSupport(){ return this.totalJoinsLearned;}
	
	public void print(HashMap<String, String> keys){
		
		String mainElement = keys.get(String.valueOf(this.mainElement));
		if (mainElement.indexOf("+") != -1) {
			mainElement = mainElement.substring(0, mainElement.indexOf("+"));
		}
		System.out.println("Inputs of "+mainElement + "\n");
		
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
			
			ArrayList<String> stack = new ArrayList<String>(this.joinsLearned.size());
			while(stack.size() < this.joinsLearned.size()){
				
				String maxCode = null;
				int maxSupport = -1;
				
				for(java.util.Map.Entry<String, Stats> entry : this.joinsLearned.entrySet()){
					
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
				
				for(char value : code.toCharArray()) System.out.print(value + " ");
				
				int support = this.joinsLearned.get(code).getOccurrences();
				float percentage = 100f * support / this.totalJoinsLearned;
				System.out.println("\t"+ support + "\t" + percentage + "\t" + ((float)support / (float) this.joinsLearned.get(code).getDistinctOccurrences()));
			}
			
			//--------------------------------------
			System.out.println();
			
			stack = new ArrayList<String>(this.joinsTested.size());
			while(stack.size() < this.joinsTested.size()){
				
				String maxCode = null;
				int maxSupport = -1;
				
				for(java.util.Map.Entry<String, Stats> entry : this.joinsTested.entrySet()){
					
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
				
				for(char value : code.toCharArray()) System.out.print(value + " ");
				
				int support = this.joinsTested.get(code).getOccurrences();
				float percentage = 100f * support / this.totalJoinsTested;
				System.out.println("\t"+ support + "\t" + percentage + "\t" + ((float)support / (float) this.joinsTested.get(code).getDistinctOccurrences()));
			}
		}
		else System.out.println("None");
		
//		System.out.println("Joins: "+this.joins.toString());
		/*
		if(!this.triggers.isEmpty()){
			
			System.out.println("\nConcurrent Inputs:");
			
			for(java.util.Map.Entry<String, ArrayList<Join>> entry : this.triggers.entrySet()){
				
				String nextTask = keys.get(entry.getKey());
				nextTask = nextTask.substring(0, nextTask.indexOf("+"));
				ArrayList<Join> concurrents = entry.getValue();
				
				for(Join concurrent : concurrents){
					
					String temp = keys.get(concurrent.toString());
					temp = temp.substring(0, temp.indexOf("+"));
					
					System.out.print(temp + " (");
					System.out.println("if " + mainElement + " follows " + nextTask + ")");
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
		
		String h1 = super.threshold+";"+noisePercentage+";Join;"+mainElement+";";
		
		if(this.elements.size() > 0){
			
			for(java.util.Map.Entry<String, Stats> entry : this.joinsTested.entrySet()){
				
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
				float percentage = 100f * occurrences / this.totalJoinsTested;
				float occurrencesTrace = ((float) occurrences / (float) this.joinsTested.get(code).getDistinctOccurrences());

				String h2 = pattern.trim() + ";" + pn + ";" + occurrences + ";" + percentage + ";" + occurrencesTrace;
				
				System.out.println(h1 + h2);
			}
		}
	}
	
	public String toString(){return String.valueOf(this.mainElement);}
	
	public HashMap<String, Stats> getLearnedPatterns(){ return this.joinsLearned; }
	public HashMap<String, Stats> getTestedPatterns(){ return this.joinsTested; }
	public int learnedPatterns(boolean distinct){ 

		if(distinct) return this.totalTracesLearned;
		else return this.totalJoinsLearned; 
	}
	public int testedPatterns(boolean distinct){ 

		if(distinct) return this.totalTracesTested;
		else return this.totalJoinsTested; 
	}
	public boolean isSupportedPattern(String code){
		
		boolean isSupportedPattern = false;
		
		if(this.joinsLearned.containsKey(code)){
			
			Stats supportedPatternStats = this.joinsLearned.get(code);
			
			float supportedPatternWeight = 100f * supportedPatternStats.getOccurrences() / this.totalJoinsLearned;
			
			if(supportedPatternWeight >= super.threshold) isSupportedPattern = true;
		}
		
		return isSupportedPattern;
	}	
}