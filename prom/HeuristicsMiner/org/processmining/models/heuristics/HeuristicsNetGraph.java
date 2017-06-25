package org.processmining.models.heuristics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.Pair;
import org.processmining.models.heuristics.elements.Activity;
import org.processmining.models.heuristics.elements.Gateway;
import org.processmining.models.heuristics.elements.Gateway.GatewayType;
import org.processmining.models.heuristics.impl.HNSet;
import org.processmining.models.heuristics.impl.HNSubSet;

/**
 * Builds a graphical representation for a <code>HeuristicsNet</code> object.
 * 
 * @author Ana Karla Alves de Medeiros
 * 
 */
public class HeuristicsNetGraph extends HeuristicsNetDiagramImpl {

	//TODO - This class needs to be adapted to deal with hyper graphs
	//The current implementation is a quick fix to get a graphical
	//visualization of Heuristics nets

	/**
	 * Constructs the graphical representation of a <code>HeuristicsNet</code>
	 * object.
	 * 
	 * @param heuristicsNet
	 *            heuristics net to which the graphical representation will be
	 *            built.
	 * @param label
	 *            string used to identify this graphical representation
	 * @param showSemantics
	 *            boolean indicating if the semantics of the split/joint points
	 *            in the <code>HeuristicsNet</code> object should be shown (
	 *            <code>true</code>) or not (<code>false</code>).
	 */
	public HeuristicsNetGraph(HeuristicsNet heuristicsNet, String label, boolean showSemantics) {
		super(label);
		if (showSemantics) {
			buildGraphicalRepresentationWithSemanticsSplitJoinPoints(heuristicsNet);
		} else {
			buildGraphicalRepresentationWithoutSemanticsSplitJoinPoints(heuristicsNet);
		}
	}

	private void buildGraphicalRepresentationWithoutSemanticsSplitJoinPoints(HeuristicsNet net) {
		//adding the activities
		XEventClass[] activities = net.getActivitiesMappingStructures().getActivitiesMapping();
		Activity[] graphActivities = new Activity[activities.length];
		Set<Pair<Activity, Activity>> existingFlows = new HashSet<Pair<Activity, Activity>>();

		for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
			//adding the activities
			graphActivities[activityIndex] = addActivity(activities[activityIndex].toString(), false, false, false,
					false, false);
		} //finished adding the activities + their input/output gateways

		//Connecting the activities
		for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
			//for every activity, get its output subsets and add a flow to each element in output
			HNSet outputActivitiesSet = net.getOutputSet(activityIndex);
			for (int outputSubsetIndex = 0; outputSubsetIndex < outputActivitiesSet.size(); outputSubsetIndex++) {
				HNSubSet subset = outputActivitiesSet.get(outputSubsetIndex);
				for (int outputActivityIndex = 0; outputActivityIndex < subset.size(); outputActivityIndex++) {
					Pair<Activity, Activity> pair = new Pair<Activity, Activity>(graphActivities[activityIndex],
							graphActivities[subset.get(outputActivityIndex)]);
					if (!existingFlows.contains(pair)) {
						addFlow(graphActivities[activityIndex], graphActivities[subset.get(outputActivityIndex)], null);
						existingFlows.add(pair);
					}
				}
			}

		}
	}

	private void buildGraphicalRepresentationWithSemanticsSplitJoinPoints(HeuristicsNet net) {
		//adding the activities and their input/output gateways
		XEventClass[] activities = net.getActivitiesMappingStructures().getActivitiesMapping();
		Activity[] graphActivities = new Activity[activities.length];
		ArrayList<Map<HNSubSet, Gateway>> activitiesOutputGateways = new ArrayList<Map<HNSubSet, Gateway>>();
		ArrayList<Map<HNSubSet, Gateway>> activitiesInputGateways = new ArrayList<Map<HNSubSet, Gateway>>();

		for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
			//adding the activities
			graphActivities[activityIndex] = addActivity(activities[activityIndex].toString(), false, false, false,
					false, false);

			//adding the output gateways
			HNSet outputActivitiesSet = net.getOutputSet(activityIndex);
			HashMap<HNSubSet, Gateway> outputSubsetToGateway = new HashMap<HNSubSet, Gateway>();
			for (int outputSubsetIndex = 0; outputSubsetIndex < outputActivitiesSet.size(); outputSubsetIndex++) {
				HNSubSet subset = outputActivitiesSet.get(outputSubsetIndex);
				Gateway gateway = addGateway(subset.toString(), GatewayType.DATABASED);
				outputSubsetToGateway.put(subset, gateway);
				addFlow(graphActivities[activityIndex], gateway, null);
			}
			activitiesOutputGateways.add(activityIndex, outputSubsetToGateway);

			//adding the input gateways
			HNSet inputActivitiesSet = net.getInputSet(activityIndex);
			HashMap<HNSubSet, Gateway> inputSubsetToGateway = new HashMap<HNSubSet, Gateway>();
			for (int inputSubsetIndex = 0; inputSubsetIndex < inputActivitiesSet.size(); inputSubsetIndex++) {
				HNSubSet subset = inputActivitiesSet.get(inputSubsetIndex);
				Gateway gateway = addGateway(subset.toString(), GatewayType.DATABASED);
				inputSubsetToGateway.put(subset, gateway);
				addFlow(gateway, graphActivities[activityIndex], null);
			}
			activitiesInputGateways.add(activityIndex, inputSubsetToGateway);
		} //finished adding the activities + their input/output gateways

		//Now, we need to connect the gateways to each other.
		//I.e., output gateways need to be connected to their respective
		//input gateways
		for (int activityIndex = 0; activityIndex < activities.length; activityIndex++) {
			//for every activity, get its output subsets
			Map<HNSubSet, Gateway> outputSubsetToGateway = activitiesOutputGateways.get(activityIndex);
			for (HNSubSet outputSubSet : outputSubsetToGateway.keySet()) {
				for (int indexActivitySubSet = 0; indexActivitySubSet < outputSubSet.size(); indexActivitySubSet++) {
					int indexOutputActivity = outputSubSet.get(indexActivitySubSet);
					HNSet targetInputSubsets = net.getInputSetsWithElement(indexOutputActivity, activityIndex);
					for (int indexTargetInputSubset = 0; indexTargetInputSubset < targetInputSubsets.size(); indexTargetInputSubset++) {
						HNSubSet targetInputSubset = targetInputSubsets.get(indexTargetInputSubset);
						if (activitiesInputGateways.get(indexOutputActivity).containsKey(targetInputSubset)) {
							addFlow(outputSubsetToGateway.get(outputSubSet), activitiesInputGateways.get(
									indexOutputActivity).get(targetInputSubset), null);
						}
					}
				}
			}
		}
	}

}