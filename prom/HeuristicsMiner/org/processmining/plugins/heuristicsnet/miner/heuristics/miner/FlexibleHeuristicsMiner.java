package org.processmining.plugins.heuristicsnet.miner.heuristics.miner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.HNSubSet;
import org.processmining.plugins.heuristicsnet.AnnotatedHeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.HeuristicsMetrics;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Join;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Split;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

public class FlexibleHeuristicsMiner extends HeuristicsMiner {


	public FlexibleHeuristicsMiner(PluginContext context, XLog log) {
		super(context, log);
	}

	public FlexibleHeuristicsMiner(PluginContext context, XLog log, XLogInfo logInfo) {
		super(context, log, logInfo);
	}

	public FlexibleHeuristicsMiner(PluginContext context, XLog log, HeuristicsMinerSettings settings) {
		super(context, log, settings);
	}

	public FlexibleHeuristicsMiner(PluginContext context, XLog log, XLogInfo logInfo, HeuristicsMinerSettings settings) {
		super(context, log, logInfo, settings);
	}

	public HeuristicsNet mine() {

		HeuristicsNet net = super.mine();

//		context.getProgress().setMaximum(0);
//		context.getProgress().setMaximum(10);
//		context.getProgress().setCaption("Annotating the HeuristicsNet...");
//		context.getProgress().setIndeterminate(false);

		long startTime = (new Date()).getTime();

		AnnotatedHeuristicsNet anet = new AnnotatedHeuristicsNet(net, this.keys, super.metrics, super.settings);

		this.annotate(anet, super.metrics);

		long finishTime = (new Date()).getTime();

		System.out.println(("\nAnnotating Time: "+(finishTime - startTime)/1000.0)+" seconds");

		anet.print();

		return anet;
	}


	private void annotate(AnnotatedHeuristicsNet net, HeuristicsMetrics metrics){


		for(Integer integerKey : keys.values()){

			String key = String.valueOf(integerKey);

			Split split = new Split(integerKey, metrics.getOutputSet(integerKey));
			net.insertSplit(key, split);

			Join join = new Join(integerKey, metrics.getInputSet(integerKey));
			net.insertJoin(key, join);
		}

		HashMap<String, HashSet<String>> reachableOutputSets = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> reachableInputSets = new HashMap<String, HashSet<String>>();
		for(Integer integerKey : keys.values()){

			String key = String.valueOf(integerKey);

			reachableOutputSets.put(key, this.findReachableTasks(integerKey, true, metrics));
			reachableInputSets.put(key, this.findReachableTasks(integerKey, false, metrics));
		}

		/*
		for(Integer integerKey : keys.values()){

			String key = String.valueOf(integerKey);

			Split split = net.getSplit(key);
//				this.splits.get(key);
			Join join = net.getJoin(key);
//				this.joins.get(key);

//			HNSubSet outputs = this.outputSet[integerKey];
//			HNSubSet inputs = this.inputSet[integerKey];

			HashSet<String> reachableOutputSet = reachableOutputSets.get(key);
			HashSet<String> reachableInputSet = reachableInputSets.get(key);

			for(int i = 0; i < metrics.getOutputSet().length; i++){

				if(i != integerKey){

					if(reachableOutputSets.get(String.valueOf(i)).contains(key)){

						ArrayList<Integer> intersection = this.intersection(reachableOutputSet, metrics.getOutputSet(i));

						System.out.println("B: "+integerKey);
						System.out.println("A: "+i);
						System.out.print("Outputs B: [");
						for(int j = 0; j < metrics.getOutputSet(integerKey).size(); j++) System.out.print(metrics.getOutputSet(integerKey).get(j)+", ");
						System.out.println();
						System.out.print("Outputs A: [");
						for(int j = 0; j < metrics.getOutputSet(i).size(); j++) System.out.print(metrics.getOutputSet(i).get(j)+", ");
						System.out.println();
						System.out.println("Reachable: "+reachableOutputSet.toString());
						System.out.println("Intersection: "+intersection.toString());
						System.out.println();

						for(Integer element : intersection){

							split.insertException(net.getSplit(String.valueOf(i)), element);
//							split.insertException(this.splits.get(String.valueOf(i)), element);
						}
					}

					if(reachableInputSets.get(String.valueOf(i)).contains(key)){

						ArrayList<Integer> intersection = this.intersection(reachableInputSet, metrics.getInputSet(i));

						for(Integer element : intersection){

							join.insertException(net.getJoin(String.valueOf(i)), element);
//							join.insertException(this.joins.get(String.valueOf(i)), element);
						}
					}
				}
			}

		}


//		for(Integer integerKey : this.keys.values()){
//
//			String key = String.valueOf(integerKey);
//
//			System.out.println(key);
//			System.out.println(this.findReachableTasks(integerKey, true).toString());
//			System.out.println(this.findReachableTasks(integerKey, false).toString());
//			System.out.println("----------------------");
//
//		}

		 */

		for(Integer integerKey : keys.values()){

			String key = String.valueOf(integerKey);

			Split split = net.getSplit(key);
			Join join = net.getJoin(key);

//			HashSet<String> reachableOutputSet = reachableOutputSets.get(key);
//			HashSet<String> reachableInputSet = reachableInputSets.get(key);

			HNSubSet outputs = metrics.getOutputSet(integerKey);
			HNSubSet inputs = metrics.getInputSet(integerKey);

			for(int outputIndex = 0; outputIndex < outputs.size(); outputIndex++){

				int output = outputs.get(outputIndex);

				if(output != integerKey){

					HashSet<String> reachableOutputSet = reachableOutputSets.get(String.valueOf(output));

					ArrayList<Integer> intersection = this.intersection(reachableOutputSet, outputs);

					if(!intersection.isEmpty()){

						if(intersection.size() == 1){

							if(intersection.get(0) != output){

								intersection.add(output);
								split.insertException(intersection);
							}

//							System.out.println("OUTPUT: "+integerKey+" -> "+output+" = "+intersection.toString());
						}
						else{

							split.insertException(intersection);
//							System.out.println("OUTPUT: "+integerKey+" -> "+output+" = "+intersection.toString());
						}

					}
				}
			}

			for(int inputIndex = 0; inputIndex < inputs.size(); inputIndex++){

				int input = inputs.get(inputIndex);

				if(input != integerKey){

					HashSet<String> reachableInputSet = reachableInputSets.get(String.valueOf(input));

					ArrayList<Integer> intersection = this.intersection(reachableInputSet, inputs);

					if(!intersection.isEmpty()){

						if(intersection.size() == 1){

							if(intersection.get(0) != input){

								intersection.add(input);
								join.insertException(intersection);
							}

//							System.out.println("OUTPUT: "+integerKey+" -> "+input+" = "+intersection.toString());
						}
						else{

							join.insertException(intersection);
//							System.out.println("OUTPUT: "+integerKey+" -> "+input+" = "+intersection.toString());
						}
					}
				}
			}
		}
//
//		System.out.println("\\\\\\\\\\\\\\\\\\\\\\");
//		for(Integer integerKey : keys.values()){
//
//			String key = String.valueOf(integerKey);
//
//			Split split = net.getSplit(key);
//			Join join = net.getJoin(key);
//
//			HNSubSet outputs = metrics.getOutputSet(integerKey);
//			HNSubSet inputs = metrics.getInputSet(integerKey);
//
//			for(int i = 0; i < outputs.size() - 1; i++){
//
//				int outputA = outputs.get(i);
//
//				for(int j = i + 1; j < outputs.size(); j++){
//
//					int outputB = outputs.get(j);
//
//					double ab = metrics.getDependencyMeasuresAccepted(outputA, outputB);
//					double ba = metrics.getDependencyMeasuresAccepted(outputB, outputA);
//
//					boolean isParallel = ((ab + ba) <= settings.getAndThreshold());
//
//					System.out.println(ab + " + " + ba + " <= "+settings.getAndThreshold());
//					System.out.println(isParallel+"\n");
//
//					if(isParallel){
//
//						split.insertException(outputA, outputB);
////						System.out.println("OUTPUT: "+integerKey+" -> "+outputA+" and "+outputB);
//					}
//				}
//			}
//
//			for(int i = 0; i < inputs.size() - 1; i++){
//
//				int inputA = inputs.get(i);
//
//				for(int j = i + 1; j < inputs.size(); j++){
//
//					int inputB = inputs.get(j);
//
//					double ab = metrics.getDependencyMeasuresAccepted(inputA, inputB);
//					double ba = metrics.getDependencyMeasuresAccepted(inputB, inputA)
//
//					boolean isParallel = ((ab + ba) <= settings.getAndThreshold());
//
//					System.out.println(ab + " + " + ba + " <= "+settings.getAndThreshold());
//					System.out.println(isParallel+"\n");
//
//					if(isParallel){
//
//						join.insertException(inputA, inputB);
////						System.out.println("INPUT: "+integerKey+" <- "+inputA+" and "+inputB);
//					}
//				}
//			}
//		}

		HashSet<Split> stackSplits = new HashSet<Split>(net.splitsCount());
		HashSet<Join> stackJoins = new HashSet<Join>(net.joinsCount());
		for(XTrace trace : log){

			ArrayList<Integer> elements = new ArrayList<Integer>(trace.size());
			for (XEvent event : trace) {

				String eventKey = logInfo.getEventClasses(settings.getClassifier()).getClassOf(event).getId();
//				String eventName = event.getAttributes().get("concept:name").toString();
//				String eventType = event.getAttributes().get("lifecycle:transition").toString();
//
////				XExtendedEvent extendedEvent = XExtendedEvent.wrap(event);
//
////				String eventKey = extendedEvent.getName() + "+" + extendedEvent.getTransition();
//				String eventKey = eventName + "+" + eventType;
				Integer eventIndex = keys.get(eventKey);
				String eventIndexKey = String.valueOf(eventIndex);

				stackSplits.add(net.getSplit(eventIndexKey));
				for(Split temp : stackSplits) temp.insertOccurrence(eventIndex);


//				Split split = splits.get(eventIndexKey);
//				split.insertOccurrence(eventIndex);
//
//				for(Split temp : stackSplits){
//
//					if(temp != split) temp.insertOccurrence(eventIndex);
//				}
//				stackSplits.remove(split);
//				stackSplits.add(0, split);


				net.getJoin(String.valueOf(eventIndex)).insertOccurrence(elements);
				stackJoins.add(net.getJoin(eventIndexKey));

				elements.add(eventIndex);

//				System.out.println();

			}

//			System.out.println(elements.toString());

			for(Split temp : stackSplits) temp.flush(true);
			stackSplits.clear();


			for(Join temp : stackJoins) temp.flush(true);
			stackJoins.clear();
		}

	}


	private HashSet<String> findReachableTasks(Integer startingTask, boolean isStartToEnd, HeuristicsMetrics metrics){

		HashSet<String> reachableTasks = new HashSet<String>();
		HashSet<String> reachedTasks = new HashSet<String>();

		this.findReachableTasksEngine(startingTask, reachableTasks, reachedTasks, isStartToEnd, metrics);

		return reachableTasks;
	}

	private void findReachableTasksEngine(Integer currentTask, HashSet<String> reachableTasks, HashSet<String> reachedTasks, boolean isStartToEnd, HeuristicsMetrics metrics){

		reachedTasks.add(String.valueOf(currentTask));

		HNSubSet elements;
		if(isStartToEnd) elements = metrics.getOutputSet(currentTask);
		else elements = metrics.getInputSet(currentTask);

		for(int i = 0; i < elements.size(); i++){

			int nextTask = elements.get(i);
			String nextTaskKey = String.valueOf(nextTask);

			reachableTasks.add(nextTaskKey);

			if(!reachedTasks.contains(nextTaskKey)){

				this.findReachableTasksEngine(nextTask, reachableTasks, reachedTasks, isStartToEnd, metrics);
			}
		}
	}

	public ArrayList<Integer> intersection(HashSet<String> list1, HNSubSet list2){

		ArrayList<Integer> intersection = new ArrayList<Integer>();
		for(int i = 0; i < list2.size(); i++){

			int element = list2.get(i);
			String elementKey = String.valueOf(list2.get(i));

			if(list1.contains(elementKey)) intersection.add(element);
		}

		intersection.trimToSize();

		return intersection;
	}

}
