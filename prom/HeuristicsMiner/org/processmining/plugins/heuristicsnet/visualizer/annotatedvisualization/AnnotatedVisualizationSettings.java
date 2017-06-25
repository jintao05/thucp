package org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization;

public class AnnotatedVisualizationSettings {

	private boolean showUnconnectedTasks;
	private boolean colorScalingEvents;
	private boolean colorScalingTransitions;
	private String measureEvents;
	private String measureTransitions;
	private String lineStyle;
	
	public AnnotatedVisualizationSettings(){
		
		this.showUnconnectedTasks = false;
		this.colorScalingEvents = true;
		this.colorScalingTransitions = true;
		this.measureEvents = "Frequency";
		this.measureTransitions = "Frequency";
		this.lineStyle = "Spline";
	}

	public boolean isShowingUnconnectedTasks(){ return this.showUnconnectedTasks;}
	public void setShowingUnconnectedTasks(boolean value){ this.showUnconnectedTasks = value; }
	
	public boolean isColorScalingEvents() { return colorScalingEvents; }
	public void setColorScalingEvents(boolean colorScalingEvents) { this.colorScalingEvents = colorScalingEvents; }

	public boolean isColorScalingTransitions() { return colorScalingTransitions; }
	public void setColorScalingTransitions(boolean colorScalingTransitions) { this.colorScalingTransitions = colorScalingTransitions; }

	public String getMeasureEvents() { return measureEvents; }
	public void setMeasureEvents(String measureEvents) { this.measureEvents = measureEvents; }

	public String getMeasureTransitions() { return measureTransitions; }
	public void setMeasureTransitions(String measureTransitions) { this.measureTransitions = measureTransitions; }

	public String getLineStyle() { return lineStyle; }
	public void setLineStyle(String lineStyle) { this.lineStyle = lineStyle; }
}
