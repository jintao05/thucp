package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.processmining.models.heuristics.impl.HNSubSet;

public abstract class Operator {

	protected int mainElement;
	protected HNSubSet elements;
	
	//--------------------------
	
	protected boolean learningMode;
	protected float threshold;
	
	protected boolean newTrace;
	
	
	public Operator(int mainElement, HNSubSet otherElements){
		
		this.mainElement = mainElement;
		this.elements = otherElements;
		
		this.learningMode = true;
		this.threshold = Integer.MAX_VALUE;
		
		this.newTrace = true;
	}
	
	public void setLearningMode(){ this.learningMode = true;}
	public void setTestingMode(float threshold) {
		
		this.threshold = threshold;	
		this.learningMode = false;
	}
	public float getThreshold(){ return this.threshold; }
	
	protected String code(ArrayList<Integer> stack){
		
		int elementsSize = this.elements.size();
		StringBuffer code = new StringBuffer(elementsSize);
		
		for(int i = 0, element = this.elements.get(0); i < elementsSize; i++, element = this.elements.get(i)){
						
			if(stack.contains(element)) code.append("1");
			else code.append("0");
		}
		
		return code.toString();
	}
	
	protected String code(ArrayList<Integer> stack, HashMap<String, HashSet<String>> exceptions){
		
//		if(this.mainElement == 10){ 
//			
//			System.out.println(exceptions.toString()+"---------");
//			
//			System.out.println(">"+ stack.toString());}
		
		for(int i = 0; i < stack.size(); i++){

			int task = stack.get(i);
			String taskKey = String.valueOf(task);

			if(exceptions.containsKey(taskKey)){

				for(String exception : exceptions.get(taskKey)){

					int index = stack.size() - 1;
					while(index > i){
						
						if(String.valueOf(stack.get(index)).equals(exception)) stack.remove(index);
						index --;
					}
				}
			}
		}
		
//		if(this.mainElement == 10){ System.out.println(stack.toString()+"<");}
		
		int elementsSize = this.elements.size();
		StringBuffer code = new StringBuffer(elementsSize);
		
		for(int i = 0, element; i < elementsSize; i++){
						
			element = this.elements.get(i);
			
			if(stack.contains(element)) code.append("1");
			else code.append("0");
		}
		
		return code.toString();
	}
	
	
	public ArrayList<Integer> decode(String code){
		
		ArrayList<Integer> stack = new ArrayList<Integer>(this.elements.size());
		
		for(int i = 0; i < code.length(); i++){
			
			if(code.charAt(i) == '1') stack.add(this.elements.get(i));
		}
		
		return stack;
	}

	public int getMainElement(){ return this.mainElement; }
	public HNSubSet getElements(){ return this.elements; }
	
	public abstract HashMap<String, Stats> getLearnedPatterns();
	public abstract HashMap<String, Stats> getTestedPatterns();
	public abstract int learnedPatterns(boolean distinct);
	public abstract int testedPatterns(boolean distinct);
}
