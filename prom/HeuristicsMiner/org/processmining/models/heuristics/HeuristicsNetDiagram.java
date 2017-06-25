package org.processmining.models.heuristics;

import java.util.Collection;
import java.util.Set;

import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.heuristics.elements.Activity;
import org.processmining.models.heuristics.elements.Flow;
import org.processmining.models.heuristics.elements.Gateway;
import org.processmining.models.heuristics.elements.Gateway.GatewayType;
import org.processmining.models.heuristics.elements.HNEdge;
import org.processmining.models.heuristics.elements.HNNode;

public interface HeuristicsNetDiagram extends DirectedGraph<HNNode, HNEdge<? extends HNNode, ? extends HNNode>> {

	String getLabel();

	//Activities
	Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation, boolean bMultiinstance,
			boolean bCollapsed);

	Activity removeActivity(Activity activity);

	Collection<Activity> getActivities();

	//Gateways
	Gateway addGateway(String label, GatewayType gatewayType);

	Gateway removeGateway(Gateway gateway);

	Collection<Gateway> getGateways();

	//Flows
	Flow addFlow(HNNode source, HNNode target, String label);

	Set<Flow> getFlows();
}
