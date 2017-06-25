package org.processmining.plugins.heuristicsnet.visualizer;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginLevel;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.HeuristicsNetGraph;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.gui.HeuristicsNetVisualizer;
import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationGenerator;
import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationSettings;

@Plugin(name = "Visualize HeuristicsNet with Annotations", level = PluginLevel.Regular, parameterLabels = { "HeuristicsNet" }, returnLabels = { "HN Annotated Visualization - No Semantics" }, returnTypes = { JComponent.class })
@Visualizer
public class HeuristicsNetAnnotatedVisualization {

	@PluginVariant(requiredParameterLabels = { 0 })
	public static JComponent visualize(PluginContext context, HeuristicsNet net) {

		AnnotatedVisualizationGenerator generator = new AnnotatedVisualizationGenerator();

		AnnotatedVisualizationSettings settings = new AnnotatedVisualizationSettings();
		HeuristicsNetGraph graph = generator.generate(net, settings);

		//return HeuristicsNetVisualizer.visualizeGraph(graph, net, settings, context.getProgress());
		return HeuristicsNetVisualizer.visualizeGraph(graph, net, settings, null);
	}
}
