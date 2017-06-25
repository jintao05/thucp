package org.processmining.plugins.heuristicsnet;

import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.HeuristicsNetImpl;
import org.processmining.plugins.heuristicsnet.miner.heuristics.HeuristicsMetrics;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

public class SimpleHeuristicsNet extends HeuristicsNetImpl {

	private HeuristicsMetrics metrics;
	private HeuristicsMinerSettings settings;
	
	public SimpleHeuristicsNet(HeuristicsNet net, HeuristicsMetrics metrics, HeuristicsMinerSettings settings){
		
		super(net.getActivitiesMappingStructures());
		
		super.setStartActivities(net.getStartActivities());
		super.setEndActivities(net.getEndActivities());
		
		super.setFitness(net.getFitness());
		
		super.setArcUsage(net.getArcUsage());
		
		for (int i = 0; i < net.size(); i++) {
			
			super.setInputSet(i, net.getInputSet(i));
			super.setOutputSet(i, net.getOutputSet(i));
		}

		super.setActivitiesActualFiring(net.getActivitiesActualFiring());
		
		this.metrics = metrics;
		this.settings = settings;
	}
	
	public boolean isDependecyAccepted(int firstTask, int secondTask){
		
		return this.metrics.getDependencyMeasuresAccepted().get(firstTask, secondTask) > 0d;
	}
	
	public HeuristicsMetrics getMetrics(){ return this.metrics; }
	public HeuristicsMinerSettings getSettings(){ return this.settings; }
}
