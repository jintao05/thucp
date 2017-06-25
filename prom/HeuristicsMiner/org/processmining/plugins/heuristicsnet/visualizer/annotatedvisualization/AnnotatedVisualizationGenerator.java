package org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.jgraph.graph.GraphConstants;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.HeuristicsNetGraph;
import org.processmining.models.heuristics.elements.Activity;
import org.processmining.models.heuristics.elements.HNEdge;
import org.processmining.models.heuristics.elements.HNNode;
import org.processmining.models.jgraph.views.JGraphShapeView;
import org.processmining.plugins.heuristicsnet.SimpleHeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.HeuristicsMetrics;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

public class AnnotatedVisualizationGenerator {

	public HeuristicsNetGraph generate(HeuristicsNet net, AnnotatedVisualizationSettings settings){
		
		HeuristicsMetrics metrics = null;
		if(net instanceof SimpleHeuristicsNet) metrics = ((SimpleHeuristicsNet) net).getMetrics();
		
		int lineStyle;
		if(settings.getLineStyle().startsWith("S")) lineStyle = GraphConstants.STYLE_SPLINE;
		else{
			
			if(settings.getLineStyle().startsWith("O")) lineStyle = GraphConstants.STYLE_ORTHOGONAL;
			else lineStyle = GraphConstants.STYLE_BEZIER;
		}
		
		HeuristicsNetGraph graph = new HeuristicsNetGraph(net, "Heuristics Net", false);
	
		int maxSize = 0;
		HashMap<String, Integer> keys = new HashMap<String, Integer>();		
		for (XEventClass event : net.getActivitiesMappingStructures().getActivitiesMapping()) {
			
			keys.put(event.getId(), event.getIndex());
			maxSize = Math.max(event.getId().indexOf("+"), maxSize);
		}
		maxSize = Math.max(maxSize * 9, 90); 
		
		boolean isActivitiesIntMeasure = true;
		boolean isEdgesIntMeasure = true;
		
		double activitiesMaxMeasure = Double.NEGATIVE_INFINITY;
		double edgesMaxMeasure = Double.NEGATIVE_INFINITY;
		
		DoubleMatrix1D activityMeasures = null;
		DoubleMatrix2D edgesMeasures = null;
		
		if(!settings.getMeasureTransitions().equals("None")){
			
			if(settings.getMeasureTransitions().startsWith("F")){
				
				edgesMeasures = net.getArcUsage();
				edgesMaxMeasure = this.getMaximum(edgesMeasures);
			}
			else{
				
				if(metrics != null){
					
					if(settings.getMeasureTransitions().startsWith("D")){
						
						edgesMeasures = metrics.getDependencyMeasuresAccepted();
						edgesMaxMeasure = 1d;
						isEdgesIntMeasure = false;
					}
				}
			}
		}
		
		if(!settings.getMeasureEvents().equals("None")){
		
			if(settings.getMeasureEvents().startsWith("F")){
				
				int[] activitySupports = net.getActivitiesActualFiring();
				
				activityMeasures = cern.colt.matrix.DoubleFactory1D.dense.make(activitySupports.length);
				
				for(int i = 0; i < activitySupports.length; i++){
					
					double support = activitySupports[i];
					activityMeasures.set(i, support); 
					if(support > activitiesMaxMeasure) activitiesMaxMeasure = support;
				}
			}
			else{
				
				if(metrics != null){
					
					if(settings.getMeasureEvents().startsWith("E")){
						
						activityMeasures = metrics.getEndCounter();
						activitiesMaxMeasure = this.getMaximum(activityMeasures);
					}
					if(settings.getMeasureEvents().startsWith("S")){
						
						activityMeasures = metrics.getStartCounter();
						activitiesMaxMeasure = this.getMaximum(activityMeasures);
					}
				}
			}
		}
		
		
		
		
		
//		int[] activitySupports = net.getActivitiesActualFiring();
//		DoubleMatrix2D edgeSupports = net.getArcUsage();
//		for(int i = 0; i < keys.size(); i++){
//			
//			for(int j = 0; j < keys.size(); j++){
//				
//				double edgeSupport = edgeSupports.get(i, j);
//				if(edgeSupport > edgesMaxMeasure) edgesMaxMeasure = edgeSupport;
//			}
//			
//			double activitySupport = activitySupports[i];
//			if(activitySupport > activitiesMaxMeasure) activitiesMaxMeasure = activitySupport;
//		}
				
		
		for(HNEdge<? extends HNNode, ? extends HNNode> edge : graph.getEdges()){
			
			String sourceActivity = edge.getSource().getLabel();
			String targetActivity = edge.getTarget().getLabel();
			
			double edgeMeasure = edgesMaxMeasure; 
			
			String newLabel = null;
			if(!settings.getMeasureTransitions().equals("None")){
				
				edge.getAttributeMap().put(AttributeMap.SHOWLABEL, true);
				
				if(edgesMeasures != null){
					
					edgeMeasure = edgesMeasures.get(keys.get(sourceActivity), keys.get(targetActivity));
					
					if(isEdgesIntMeasure) newLabel = Long.toString(Math.round(edgeMeasure));
					else newLabel = Float.toString(Math.round(edgeMeasure * 1000) / 1000f);
				}
				else newLabel = "N/A";

				edge.getAttributeMap().put(AttributeMap.LABEL, newLabel);
				edge.getAttributeMap().put(AttributeMap.LABELALONGEDGE, true);
				
			}
			else{
				
				edge.getAttributeMap().put(AttributeMap.LABEL, "");
				edge.getAttributeMap().put(AttributeMap.SHOWLABEL, false);
			}
			
			edge.getAttributeMap().put(AttributeMap.LINEWIDTH, new Float(1));
			
			if(settings.isColorScalingTransitions()){
				
				Color edgeColor = getColor(edgeMeasure, edgesMaxMeasure);
				edge.getAttributeMap().put(AttributeMap.EDGECOLOR, edgeColor);
				edge.getAttributeMap().put(AttributeMap.LABELCOLOR, edgeColor);
			}
			
			edge.getAttributeMap().put(AttributeMap.STYLE, lineStyle);
			
		}
		
		for(Activity activity : graph.getActivities()){
			
			String activityLabel = activity.getLabel();
			
			if(keys.containsKey(activityLabel)){
				
				int activityID = keys.get(activityLabel);
				
				int divisorIndex = activityLabel.indexOf("+");
				String activityName;
				String activityType;
				if (divisorIndex < 0) {
					activityName = activityLabel;
					activityType = "";
				}
				else {
					activityName = activityLabel.substring(0, divisorIndex);
					activityType = activityLabel.substring(divisorIndex + 1);	
				}
					
				
	//			activity.getAttributeMap().put(AttributeMap.AUTOSIZE, false);
				activity.getAttributeMap().put(AttributeMap.SHAPE, JGraphShapeView.RECTANGLE);
				activity.getAttributeMap().put(AttributeMap.INSET, 0);
				activity.getAttributeMap().put(AttributeMap.SIZE, new Dimension(maxSize, 60));
				
				String newLabel;
				if(activityMeasures != null){
					
					Color activityColor = Color.BLACK;
					double activityMeasure = activityMeasures.get(activityID);
					
					if(settings.isColorScalingEvents()){

						activityColor = getColor(activityMeasure, activitiesMaxMeasure);
						
						activity.getAttributeMap().put(AttributeMap.STROKECOLOR, activityColor);
						activity.getAttributeMap().put(AttributeMap.LABELCOLOR, activityColor);
					}
					
					if(isActivitiesIntMeasure) 
						newLabel = getHTMLText(activityName, true, activityType, false, Long.toString(Math.round(activityMeasure)), false, activityColor);
					else newLabel = getHTMLText(activityName, true, activityType, false, Float.toString(Math.round(activityMeasure * 1000) / 1000f), false, activityColor); 
				}
				else newLabel = getHTMLText(activityName, true, activityType, false, null, false, Color.BLACK);
					
				activity.getAttributeMap().put(AttributeMap.LABEL, newLabel);
				
			}
		}
		
		if(!settings.isShowingUnconnectedTasks()){
			
			java.util.LinkedList<Activity> unconnectedActivities = new java.util.LinkedList<Activity>();
			for(Activity activity : graph.getActivities()){
				
				if(graph.getInEdges(activity).isEmpty()){
					
					if(graph.getOutEdges(activity).isEmpty()) unconnectedActivities.add(activity);
				}
			}
			
			for(Activity activity : unconnectedActivities) graph.removeActivity(activity);
		}
		
		return graph;
	}
	
	private double getMaximum(DoubleMatrix1D vector){
		
		double maximum = Double.NEGATIVE_INFINITY;
		
		for(int index = 0; index < vector.size(); index++){
			
			double value = vector.get(index);
			
			if(value > maximum) maximum = value;
		}
		
		return maximum;
	}
	
	private double getMaximum(DoubleMatrix2D matrix){
		
		double maximum = Double.NEGATIVE_INFINITY;
	
		for(int row = 0; row < matrix.rows(); row++){
			
			for(int column = 0; column < matrix.columns(); column++){
				
				double value = matrix.get(row, column);
				
				if(value > maximum) maximum = value;
			}
		}
		
		return maximum;
	}
	
	//----------------------------------
	
	private static String getHTMLText(String line1, boolean isLine1Bold, String line2, boolean isLine2Bold, String line3, boolean isLine3Bold, Color color){
		
		StringBuffer text = new StringBuffer();
		
		text.append("<html><body style='text-align:center;color:");
		text.append(getRGBColor(color));
		text.append("'>");
		if(isLine1Bold){
			
			text.append("<b>");
			text.append(line1);
			text.append("</b><br />");
		}
		else{
			
			text.append(line1);
			text.append("<br />");
		}
		if(isLine2Bold){
			
			text.append("<b>");
			text.append(line2);
			text.append("</b><br />");
		}
		else{
			
			text.append(line2);
			text.append("<br />");
		}
		if(line3 != null){
			
			if(isLine3Bold){
				
				text.append("<b>");
				text.append(line3);
				text.append("</b>");
			}
			else text.append(line3);
		}

		text.append("</body></html>");
		
		return text.toString();
	}
	
	private static Color getColor(double level, double maxLevel){
		
		if(level == maxLevel) return Color.BLACK;
		else{
			
			int colorLevel = 190 -(int) Math.round((level / maxLevel) * 190);
			
			return new Color(colorLevel, colorLevel, colorLevel);
		}
	}
	
	private static String getRGBColor(Color color){
		return "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";
	}
}
