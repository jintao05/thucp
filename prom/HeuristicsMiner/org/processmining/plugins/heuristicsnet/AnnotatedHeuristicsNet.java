package org.processmining.plugins.heuristicsnet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.HNSubSet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.HeuristicsMetrics;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Join;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Split;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Stats;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

public class AnnotatedHeuristicsNet extends SimpleHeuristicsNet {
	
	protected HashMap<String, Integer> keys;
	
	private HashMap<String, Split> splits;
	private HashMap<String, Join> joins;
	
	public AnnotatedHeuristicsNet(HeuristicsNet net, HeuristicsMetrics metrics, HeuristicsMinerSettings settings){
		
		super(net, metrics, settings);
		
		this.splits = new HashMap<String, Split>();
		this.joins = new HashMap<String, Join>();
	}
	
	public AnnotatedHeuristicsNet(HeuristicsNet net, HashMap<String, Integer> keys, HeuristicsMetrics metrics, HeuristicsMinerSettings settings){
		
		this(net, metrics, settings);
		
		this.keys = keys;
	}
	
	public Split insertSplit(String key, Split split){ return this.splits.put(key, split); }
	public Join insertJoin(String key, Join join){ return this.joins.put(key, join); }
	
	public Split getSplit(String key){ return this.splits.get(key); }
	public Join getJoin(String key){ return this.joins.get(key); }
	
	public Collection<Split> getSplits(){ return this.splits.values(); }
	public Collection<Join> getJoins(){ return this.joins.values(); }
	
	public Set<Entry<String, Split>> getSplitEntries(){ return this.splits.entrySet(); }
	public Set<Entry<String, Join>> getJoinEntries(){ return this.joins.entrySet(); }
	
	public int splitsCount(){ return this.splits.size(); }
	public int joinsCount(){ return this.joins.size(); }
	
	public Integer getKey(String primaryKey){ return this.keys.get(primaryKey); }
	
	public HashMap<String, String> getInvertedKeys(){
		
		HashMap<String, String> invertedKeys = new HashMap<String, String>();
		for(java.util.Map.Entry<String, Integer> entry : this.keys.entrySet()){
			
			invertedKeys.put(String.valueOf(entry.getValue()), entry.getKey());
		}
		
		return invertedKeys;
	}
	

	public void print(){
		
		System.out.println(this.keys.toString());
		
		HashMap<String, String> invertedKeys = this.getInvertedKeys();
		
//		System.out.println(" -> OUT <-");
		for(Split split : this.splits.values()){
			
			split.print(invertedKeys);
			
			HNSubSet outputs =  split.getElements();
			for(java.util.Map.Entry<String,Stats> entry : split.getLearnedPatterns().entrySet()){
				
				String code = entry.getKey();
				for(int i = 0; i < code.length(); i++){
									
					if(code.charAt(i) == '1') System.out.print(invertedKeys.get(outputs.get(i)+"")+" ");
				}
				System.out.println(": "+entry.getValue().getOccurrences());
			}
		}
		
//		System.out.println(" -> IN <-");
		for(Join join : this.joins.values()) join.print(invertedKeys);
	}
}
