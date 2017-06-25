package org.processmining.models.heuristics.elements;


import java.awt.Graphics2D;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.AttributeMap.ArrowType;
import org.processmining.models.shapes.Decorated;


public class Flow extends HNEdge<HNNode, HNNode> implements Decorated  {

	private IGraphElementDecoration decorator = null;

	public Flow(HNNode source, HNNode target, String label) {
		super(source, target);
		fillAttributes(label);
	}

	/**
	 * 
	 */
	private void fillAttributes(String label) {
		getAttributeMap().put(AttributeMap.EDGEEND, ArrowType.ARROWTYPE_CLASSIC);
		getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
		getAttributeMap().put(AttributeMap.SHOWLABEL, false);

	}

	public boolean equals(Object o) {
		return (o == this);
	}

	public IGraphElementDecoration getDecorator() {
		return decorator;
	}

	public void setDecorator(IGraphElementDecoration decorator) {
		this.decorator = decorator;
	}

	public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
		if (decorator != null) {
			decorator.decorate(g2d, x, y, width, height);
		}
	}
}
