//package org.processmining.plugins.heuristicsnet.visualizer;
//
//import javax.swing.JComponent;
//
//import org.processmining.contexts.uitopia.annotations.Visualizer;
//import org.processmining.framework.plugin.PluginContext;
//import org.processmining.framework.plugin.annotations.Plugin;
//import org.processmining.framework.plugin.annotations.PluginVariant;
////import org.processmining.models.graphbased.directed.jgraph.ProMJGraphVisualizer;
//import org.processmining.models.heuristics.HeuristicsNet;
//import org.processmining.models.heuristics.HeuristicsNetGraph;
//import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.gui.HeuristicsNetVisualizer;
//import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationGenerator;
//import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationSettings;
//
//@Plugin(name = "Visualize HeuristicsNet", parameterLabels = { "HeuristicsNet" }, returnLabels = { "HN Visualization - No Semantics" }, returnTypes = { JComponent.class })
//@Visualizer
//public class HeuristicsNetVisualization {
//
////	@PluginVariant(requiredParameterLabels = { 0 })
////	public static JComponent visualize(PluginContext context, HeuristicsNet[] population) {
////		return ProMJGraphVisualizer.visualizeGraph(new HeuristicsNetGraph(population[population.length - 1],
////				"Heuristics Net", false));
////	}
//
//	@PluginVariant(requiredParameterLabels = { 0 })
//	public static JComponent visualize(PluginContext context, HeuristicsNet[] population) {
//			
//		AnnotatedVisualizationGenerator generator = new AnnotatedVisualizationGenerator();
//		
//		AnnotatedVisualizationSettings settings = new AnnotatedVisualizationSettings();
//		HeuristicsNetGraph graph = generator.generate(population[population.length - 1], settings);
//		
//		return HeuristicsNetVisualizer.visualizeGraph(graph, population[population.length - 1], settings, context.getProgress());
//	}
//
//}
