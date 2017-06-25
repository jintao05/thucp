package org.processmining.plugins.heuristicsnet.miner.heuristics.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.HNSet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.heuristicsnet.AnnotatedHeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Join;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Split;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Stats;


@Plugin(name = "Convert Heuristics net into Petri net", 
		level = PluginLevel.Regular,
		parameterLabels = {"HeuristicsNet"},
		returnLabels = {"Petri net", "Marking"},
		returnTypes = {Petrinet.class, Marking.class },
		userAccessible = true,
		help = "Converts heuristics net (or causal matrices) into Petri net")

public class HeuristicsNetToPetriNetConverter {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "J.T.S. Ribeiro", email = "j.t.s.ribeiro@tue.nl", website = "http://is.tm.tue.nl/staff/jribeiro", pack = "HeuristicsMiner")
	@PluginVariant(variantLabel = "Default Settings", requiredParameterLabels = { 0 })
	
	public static Object[] converter(PluginContext context, HeuristicsNet hn) {
		
		if (hn instanceof AnnotatedHeuristicsNet) {
			
			AnnotatedHeuristicsNet ahn = (AnnotatedHeuristicsNet) hn;
			HashMap<String, String> keys = ahn.getInvertedKeys();
			
			Petrinet net = PetrinetFactory.newPetrinet("Petrinet");
			Marking m = new Marking();
			
			Transition[] transitions = new Transition[keys.size()];
			Place[] outputPlaces = new Place[keys.size()];
			Place[] inputPlaces = new Place[keys.size()];
			
			for(java.util.Map.Entry<String, String> entry : keys.entrySet()){
			
				int index = Integer.valueOf(entry.getKey());
				
				transitions[index] = net.addTransition(entry.getValue());
				inputPlaces[index] = net.addPlace("pi" + index);
				outputPlaces[index] = net.addPlace("po" + index);
				
				net.addArc(inputPlaces[index], transitions[index]);
				net.addArc(transitions[index], outputPlaces[index]);
			}
			
			for(String task : keys.keySet()){
				
				int index = Integer.valueOf(task);
				
				Split split = ahn.getSplit(task);
				Join join = ahn.getJoin(task);
				
				int outputSize = split.getElements().size();
				int inputSize = join.getElements().size();
				
				if(inputSize == 0) m.add(inputPlaces[index]);
				
				//-----------------
				
				HashMap<String, Stats> patterns = split.getLearnedPatterns();
				
				for(java.util.Map.Entry<String, Stats> entry : patterns.entrySet()){
					
					if(isValidPattern(entry.getKey())){
						
						ArrayList<Integer> outputs = split.decode(entry.getKey());
						
						boolean flag = true;
						for(Integer output : outputs){
							
							if(ahn.getJoin(String.valueOf(output)).getElements().size() > outputSize){
								
								flag = false;
								break;
							}
						}
						
						if(flag){
							
							Transition hiddenTransition = net.addTransition("tau");
							hiddenTransition.setInvisible(true);
							
							net.addArc(outputPlaces[index], hiddenTransition);
							
							for(Integer output : outputs){
										
								net.addArc(hiddenTransition, inputPlaces[output]);
							}
						}
					}
				}
				
				//-----------------
				
				patterns = join.getLearnedPatterns();
				
				for(java.util.Map.Entry<String, Stats> entry : patterns.entrySet()){
					
					if(isValidPattern(entry.getKey())){
						
						ArrayList<Integer> inputs = join.decode(entry.getKey());
						
						boolean flag = true;
						for(Integer input : inputs){
							
							if(ahn.getSplit(String.valueOf(input)).getElements().size() >= inputSize){
								
								flag = false;
								break;
							}
						}
						
						if(flag){
							
							Transition hiddenTransition = net.addTransition("tau");
							hiddenTransition.setInvisible(true);
							
							net.addArc(hiddenTransition, inputPlaces[index]);
							
							for(Integer input : inputs){
								
								net.addArc(outputPlaces[input], hiddenTransition);
							}
						}
					}
				}
			}
					
			context.addConnection(new InitialMarkingConnection(net, m));
			return new Object[] { net, m };
		}
		else{
			
			XEventClass[] activities = hn.getActivitiesMappingStructures().getActivitiesMapping();
			
			Petrinet net = PetrinetFactory.newPetrinet("Petrinet");
			Marking m = new Marking();
			
			Transition[] transitions = new Transition[activities.length];
			Place[] outputPlaces = new Place[activities.length];
			Place[] inputPlaces = new Place[activities.length];

			for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
				
				transitions[activityIndex] = net.addTransition(activities[activityIndex].toString());
				inputPlaces[activityIndex] = net.addPlace("pi" + activityIndex);
				outputPlaces[activityIndex] = net.addPlace("po" + activityIndex);
				
				net.addArc(inputPlaces[activityIndex], transitions[activityIndex]);
				net.addArc(transitions[activityIndex], outputPlaces[activityIndex]);
			}
			
			for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
				
				HNSet inputActivitiesSet = hn.getInputSet(activityIndex);
				HNSet outputActivitiesSet = hn.getOutputSet(activityIndex);
				
				LinkedList<Conjunction> inputs = computeConjunctions(inputActivitiesSet);
				LinkedList<Conjunction> outputs = computeConjunctions(outputActivitiesSet);
				
				if(inputs.size() == 0) m.add(inputPlaces[activityIndex]);
					
				for(Conjunction ic : inputs){
					
					boolean flag = true;
					for(int i = 0; i < ic.size(); i++){
						
						if(hn.getOutputSet(ic.getElement(i)).size() >= inputActivitiesSet.size()){
							
							flag = false;
							break;
						}
					}
					
					if(flag){
						
						Transition hiddenTransition = net.addTransition("tau");
						hiddenTransition.setInvisible(true);
						
						net.addArc(hiddenTransition, inputPlaces[activityIndex]);
											
						for(int i = 0; i < ic.size(); i++){
							
							int element = ic.getElement(i);
							net.addArc(outputPlaces[element], hiddenTransition);
						}
					}
					
				}
				
				for(Conjunction oc : outputs){
					
					
					boolean flag = true;
					for(int i = 0; i < oc.size(); i++){
						
						if(hn.getInputSet(oc.getElement(i)).size() > outputActivitiesSet.size()){
							
							flag = false;
							break;
						}
					}
					
					if(flag){
					
						Transition hiddenTransition = net.addTransition("tau");
						hiddenTransition.setInvisible(true);
						
						net.addArc(outputPlaces[activityIndex], hiddenTransition);
											
						for(int i = 0; i < oc.size(); i++){
							
							int element = oc.getElement(i);
							net.addArc(hiddenTransition, inputPlaces[element]);
						}
					}
					
				}
								
//				System.out.println(inputActivitiesSet.toString() + "\t" + outputActivitiesSet.toString());
			}
			
			context.addConnection(new InitialMarkingConnection(net, m));
			return new Object[] { net, m };
		}
	}

		
	private static boolean isValidPattern(String code){
		
		if(code.contains("1")) return true;
		else return false;
	}
	
	private static LinkedList<Conjunction> computeConjunctions(HNSet set){

		LinkedList<Conjunction> result = new LinkedList<Conjunction>();
		
		if(set.size() > 0){
		
			int[] indices = new int[set.size()]; 
			for(int i = 0; i < set.size(); i++) indices[i] = set.get(i).size() - 1;
			
			while(indices[0] >= 0){
				
				Conjunction c = new Conjunction();
				for(int i = 0; i < set.size(); i++){
					
					int element = set.get(i).get(indices[i]);
					c.addElement(element);
				}
				result.add(c);
				
				for(int i = set.size() - 1; i >= 0; i--){
					
					indices[i] --;
					if(indices[i] < 0){
						
						if(i > 0) indices[i] = set.get(i).size() - 1;
					}
					else break;
				}
			}
		}
		
		return result;
	}
}

class Conjunction{

	private LinkedList<Integer> elements;

	public Conjunction(){ this.elements = new LinkedList<Integer>(); }

	public int getElement(int index){ return this.elements.get(index); }
	public void addElement(int element){ this.elements.add(new Integer(element)); }
	public int size(){ return this.elements.size(); }

	public String toString(){ 

		StringBuffer buffer = new StringBuffer();

		boolean firstElement = true;
		for(Integer element : elements){

			if(firstElement){

				buffer.append(element.toString());
				firstElement = false;
			}
			else buffer.append(" AND " + element.toString());
		}

		return buffer.toString(); 
	}
}
