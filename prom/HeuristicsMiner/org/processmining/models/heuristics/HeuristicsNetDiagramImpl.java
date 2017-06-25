package org.processmining.models.heuristics;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

import org.processmining.framework.providedobjects.SubstitutionType;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.heuristics.elements.Activity;
import org.processmining.models.heuristics.elements.Flow;
import org.processmining.models.heuristics.elements.Gateway;
import org.processmining.models.heuristics.elements.Gateway.GatewayType;
import org.processmining.models.heuristics.elements.HNEdge;
import org.processmining.models.heuristics.elements.HNNode;

@SubstitutionType(substitutedType = HeuristicsNetDiagram.class)
public class HeuristicsNetDiagramImpl extends AbstractDirectedGraph<HNNode, HNEdge<? extends HNNode, ? extends HNNode>>  
implements HeuristicsNetDiagram {

	protected final Set<Activity> activities;
	protected final Set<Gateway> gateways;
	protected final Set<Flow> flows;

	public HeuristicsNetDiagramImpl(String label) {
		super();
		activities = new LinkedHashSet<Activity>();
		gateways = new LinkedHashSet<Gateway>();
		flows = new LinkedHashSet<Flow>();
		getAttributeMap().put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		getAttributeMap().put(AttributeMap.LABEL, label);
	}

	@Override
	protected HeuristicsNetDiagramImpl getEmptyClone() {
		return new HeuristicsNetDiagramImpl(getLabel());
	}

	protected Map<DirectedGraphElement, DirectedGraphElement> cloneFrom(
			DirectedGraph<HNNode, HNEdge<? extends HNNode, ? extends HNNode>> graph) {
		HeuristicsNetDiagram bpmndiagram = (HeuristicsNetDiagram) graph;
		HashMap<DirectedGraphElement, DirectedGraphElement> mapping = new HashMap<DirectedGraphElement, DirectedGraphElement>();

		for (Activity a : bpmndiagram.getActivities()) {

			mapping.put(
					a,
					addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(),
							a.isBMultiinstance(), a.isBCollapsed()));
		}

		for (Gateway g : bpmndiagram.getGateways()) {

			mapping.put(g, addGateway(g.getLabel(), g.getGatewayType()));
		}

		for (Flow f : bpmndiagram.getFlows()) {

			mapping.put(
					f,
					addFlow((HNNode) mapping.get(f.getSource()), (HNNode) mapping.get(f.getTarget()),
							f.getLabel()));
		}

		getAttributeMap().clear();
		AttributeMap map = bpmndiagram.getAttributeMap();
		for (String key : map.keySet()) {
			getAttributeMap().put(key, map.get(key));
		}
		return mapping;
	}

	@SuppressWarnings("rawtypes")
	public void removeEdge(DirectedGraphEdge edge) {
		if (edge instanceof Flow) {
			flows.remove(edge);
		} else {
			assert (false);
		}
		graphElementRemoved(edge);
	}

	public Set<HNEdge<? extends HNNode, ? extends HNNode>> getEdges() {
		Set<HNEdge<? extends HNNode, ? extends HNNode>> edges = new HashSet<HNEdge<? extends HNNode, ? extends HNNode>>();
		edges.addAll(flows);
		return edges;
	}

	public Set<HNNode> getNodes() {
		Set<HNNode> nodes = new HashSet<HNNode>();
		nodes.addAll(activities);
		nodes.addAll(gateways);
		return nodes;
	}

	public void removeNode(DirectedGraphNode node) {
		if (node instanceof Activity) {
			removeActivity((Activity) node);
		} else if (node instanceof Gateway) {
			removeGateway((Gateway) node);
		} else {
			assert (false);
		}
	}

	public Activity addActivity(String label, boolean bLooped, boolean bAdhoc, boolean bCompensation,
			boolean bMultiinstance, boolean bCollapsed) {
		Activity a = new Activity(this, label, bLooped, bAdhoc, bCompensation, bMultiinstance, bCollapsed);
		activities.add(a);
		graphElementAdded(a);
		return a;
	}

	public Flow addFlow(HNNode source, HNNode target, String label) {
		Flow f = new Flow(source, target, label);
		flows.add(f);
		graphElementAdded(f);
		return f;
	}

	public Gateway addGateway(String label, GatewayType gatewayType) {
		Gateway g = new Gateway(this, label, gatewayType);
		gateways.add(g);
		graphElementAdded(g);
		return g;
	}

	public Collection<Activity> getActivities() {
		return activities;
	}

	public Set<Flow> getFlows() {
		return Collections.unmodifiableSet(flows);
	}

	public Collection<Gateway> getGateways() {
		return gateways;
	}

	public Activity removeActivity(Activity activity) {
		removeSurroundingEdges(activity);
		return removeNodeFromCollection(activities, activity);
	}

	public Gateway removeGateway(Gateway gateway) {
		removeSurroundingEdges(gateway);
		return removeNodeFromCollection(gateways, gateway);
	}
}
