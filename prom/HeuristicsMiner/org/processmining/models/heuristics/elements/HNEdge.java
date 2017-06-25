package org.processmining.models.heuristics.elements;

import org.processmining.models.graphbased.EdgeID;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.processmining.models.graphbased.directed.ContainingDirectedGraphNode;

public class HNEdge<S extends HNNode, T extends HNNode> extends AbstractDirectedGraphEdge<S, T> implements ContainableDirectedGraphElement{

	private final EdgeID id = new EdgeID();
	private ContainingDirectedGraphNode parent;

	public HNEdge(S source, T target) {
		super(source, target);
	}

	public HNEdge(S source, T target, ContainingDirectedGraphNode parent) {
		this(source, target);
		this.parent = parent;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	public int compareTo(HNEdge<S, T> edge) {
		return edge.id.compareTo(id);
	}

	public int hashCode() {
		// Hashcode not based on source and target, which
		// respects contract that this.equals(o) implies
		// this.hashCode()==o.hashCode()
		return id.hashCode();
	}

	public boolean equals(Object o) {
		if (!(this.getClass().equals(o.getClass()))) {
			return false;
		}
		HNEdge<?, ?> edge = (HNEdge<?, ?>) o;

		return edge.id.equals(id);
	}

	public ContainingDirectedGraphNode getParent() {
		return parent;
	}
	public void setParent(ContainingDirectedGraphNode node) {
		this.parent=node;
	}

}
