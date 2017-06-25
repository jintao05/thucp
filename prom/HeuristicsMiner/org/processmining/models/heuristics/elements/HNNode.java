package org.processmining.models.heuristics.elements;

import java.awt.Color;

import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.processmining.models.graphbased.directed.ContainingDirectedGraphNode;


public class HNNode extends AbstractDirectedGraphNode implements ContainableDirectedGraphElement  {

	public static final Color ABSTRACTBACKGROUNDCOLOR = new Color(120, 140, 248);
	public static final Color ABSTRACTBORDERCOLOR = new Color(20, 20, 20);
	public static final Color ABSTRACTTEXTCOLOR = new Color(10, 10, 10, 240);

	public static final Color ADJACENTBACKGROUNDCOLOR = new Color(255, 255, 255);

	public static final Color CLUSTERBACKGROUNDCOLOR = new Color(120, 140, 248);
	public static final Color CLUSTERBORDERCOLOR = new Color(20, 20, 20);
	public static final Color CLUSTERTEXTCOLOR = new Color(10, 10, 10, 240);

	public static final Color EDGECOLOR = new Color(150, 150, 150);
	public static final Color EDGECORRELATEDCOLOR = new Color(20, 20, 20);
	public static final Color EDGEUNCORRELATEDCOLOR = new Color(200, 200, 200);

	public static final Color LABELCOLOR = new Color(120, 120, 120);

	public static final Color PRIMITIVEBACKGROUNDCOLOR = new Color(240, 230, 200);
	public static final Color PRIMITIVEBORDERCOLOR = new Color(20, 20, 20);
	public static final Color PRIMITIVETEXTCOLOR = new Color(0, 0, 0, 230);
	
	
	private final AbstractDirectedGraph<HNNode, HNEdge<? extends HNNode, ? extends HNNode>> graph;

	public HNNode(AbstractDirectedGraph<HNNode, HNEdge<? extends HNNode, ? extends HNNode>> bpmndiagram) {
		super();
		graph = bpmndiagram;
	}

	public AbstractDirectedGraph<HNNode, HNEdge<? extends HNNode, ? extends HNNode>> getGraph() {
		return graph;
	}

	public ContainingDirectedGraphNode getParent() { return null; }

}
