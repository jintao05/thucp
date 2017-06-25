package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators;

public class Stats {

	private int occurrences;
	private int distinctOccurrences;
	
	private boolean newTrace;
	
	public Stats(){
		
		this.occurrences = 1;
		this.distinctOccurrences = 1;
		
		this.newTrace = false;
	}
	
	public int getOccurrences(){ return this.occurrences; }
	public int getDistinctOccurrences(){ return this.distinctOccurrences; }
	
	public void addOccurence(){
		
		this.occurrences ++;
		if(this.newTrace){
			
			this.distinctOccurrences ++;
			this.newTrace = false;
		}
	}
	
	public void setNewTrace(){ this.newTrace = true; } 
	
	public void aggregate(Stats stats){
		
		this.occurrences += stats.getOccurrences();
		this.distinctOccurrences += stats.getDistinctOccurrences();
	}
}
