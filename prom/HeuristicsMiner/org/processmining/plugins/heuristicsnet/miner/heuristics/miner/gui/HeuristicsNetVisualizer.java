package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.gui;

// BVD: REMOVED THIS CODE AS I THINK ITS NOT NECESSARY ANYMORE

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

import org.jgraph.graph.AttributeMap.SerializablePoint2D;
import org.jgraph.graph.GraphConstants;
import org.processmining.framework.plugin.Progress;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.elements.ProMGraphPort;
import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationSettings;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayoutProgress;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

public class HeuristicsNetVisualizer {

	protected HeuristicsNetVisualizer() {
	};

	public static HeuristicsNetVisualization getVisualizationPanel(
			DirectedGraph<?, ?> graph,
			HeuristicsNet net,
			AnnotatedVisualizationSettings settings,
			Progress progress) {

		return getResultsPanel(graph, new ViewSpecificAttributeMap(), net, settings, progress);
	}

	public static HeuristicsNetVisualization getResultsPanel(DirectedGraph<?, ?> graph,
			ViewSpecificAttributeMap map, HeuristicsNet net, AnnotatedVisualizationSettings settings, Progress progress) {

		ProMJGraph jgraph = createJGraph(graph, map, progress);

//		return new ResultsPanel(jgraph);
		return new HeuristicsNetVisualization(jgraph, net, settings);
	}

	public static ProMJGraph createJGraph(DirectedGraph<?, ?> causalNet,
			ViewSpecificAttributeMap map, Progress progress){

//		ViewSpecificAttributeMap map = new ViewSpecificAttributeMap();
		GraphLayoutConnection layoutConnection = new GraphLayoutConnection(causalNet);

		ProMGraphModel model = new ProMGraphModel(causalNet);
		ProMJGraph jGraph = new ProMJGraph(model, map, layoutConnection);

//		this.test(model);

		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(15);
		layout.setFixRoots(false);

		layout.setOrientation(map.get(causalNet, AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));

		if(!layoutConnection.isLayedOut()){

			JGraphFacade facade = new JGraphFacade(jGraph);

			facade.setOrdered(false);
			facade.setEdgePromotion(true);
			facade.setIgnoresCellsInGroups(false);
			facade.setIgnoresHiddenCells(false);
			facade.setIgnoresUnconnectedCells(false);
			facade.setDirected(true);
			facade.resetControlPoints();
			if (layout instanceof JGraphHierarchicalLayout) {
				facade.run((JGraphHierarchicalLayout) layout, true);
			} else {
				facade.run(layout, true);
			}

			java.util.Map<?, ?> nested = facade.createNestedMap(true, true);

			jGraph.getGraphLayoutCache().edit(nested);
			layoutConnection.setLayedOut(true);
		}

		jGraph.setUpdateLayout(layout);

		return jGraph;
	}

//	public static ProMJGraph createJGraph(DirectedGraph<?, ?> graph,
//			ViewSpecificAttributeMap map, Progress progress){
//
//		GraphLayoutConnection con = new GraphLayoutConnection(graph);
//
//		ProMJGraph jgraph = new ProMJGraph(new ProMGraphModel(graph), map, con);
//
//		JGraphHierarchicalLayout layout = getHierarchicalLayout(progress);
//		layout.setOrientation(graph.getAttributeMap().get(AttributeMap.PREF_ORIENTATION, SwingConstants.SOUTH));
//
//
//		if (!graph.isLayedOut()) {
//
//				JGraphFacade facade = new JGraphFacade(jgraph);
//
//				facade.setOrdered(true);
//				facade.setEdgePromotion(true);
//				facade.setIgnoresCellsInGroups(false);
//				facade.setIgnoresHiddenCells(false);
//				facade.setIgnoresUnconnectedCells(false);
//				facade.setDirected(false);
//				facade.resetControlPoints();
//
//				facade.run(layout, false);
//
//				fixParallelEdges(facade, 15);
//
//				Map<?, ?> nested = facade.createNestedMap(true, false);
//
//				jgraph.getGraphLayoutCache().edit(nested);
//
////			}
//
//
//			graph.setLayedOut(true);
//
//		}
//
//		jgraph.repositionToOrigin();
//		jgraph.setUpdateLayout(layout);
//
//		return jgraph;
//	}

	protected static JGraphHierarchicalLayout getHierarchicalLayout(final Progress progress) {
		JGraphHierarchicalLayout layout = new JGraphHierarchicalLayout();

		if (progress != null) {
			layout.getProgress().addPropertyChangeListener(new PropertyChangeListener() {

				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals(JGraphLayoutProgress.MAXIMUM_PROPERTY)) {
						progress.setIndeterminate(false);
						progress.setMaximum(((Integer) evt.getNewValue()).intValue());
						Thread.yield();
					} else if (evt.getPropertyName().equals(JGraphLayoutProgress.PROGRESS_PROPERTY)) {
						progress.setValue(((Integer) evt.getNewValue()).intValue());
						Thread.yield();
					}
				}
			});
		}
		layout.setDeterministic(false);
		layout.setCompactLayout(false);
		layout.setFineTuning(true);
		layout.setParallelEdgeSpacing(20);

		return layout;
	}

	@SuppressWarnings("unchecked")
	protected static void fixParallelEdges(JGraphFacade facade, double spacing) {
		ArrayList edges = new ArrayList(facade.getEdges());
		for (Object edge : edges) {
			List points = facade.getPoints(edge);
			if (points.size() != 2) {
				continue;
			}
			Object sourceCell = facade.getSource(edge);
			Object targetCell = facade.getTarget(edge);
			Object sourcePort = facade.getSourcePort(edge);
			Object targetPort = facade.getTargetPort(edge);
			Object[] between = facade.getEdgesBetween(sourcePort, targetPort, false);
			if ((between.length == 1) && !(sourcePort == targetPort)) {
				continue;
			}
			Rectangle2D sCP = facade.getBounds(sourceCell);
			Rectangle2D tCP = facade.getBounds(targetCell);
			Point2D sPP = GraphConstants.getOffset(((ProMGraphPort) sourcePort).getAttributes());
			// facade. getBounds (sourcePort ) ;

			if (sPP == null) {
				sPP = new Point2D.Double(sCP.getCenterX(), sCP.getCenterY());
			}
			Point2D tPP = GraphConstants.getOffset(((ProMGraphPort) targetPort).getAttributes());
			// facade.getBounds(sourcePort);

			if (tPP == null) {
				tPP = new Point2D.Double(tCP.getCenterX(), tCP.getCenterY());
			}

			if (sourcePort == targetPort) {
				assert (sPP.equals(tPP));
				double x = sPP.getX();
				double y = sPP.getY();
				for (int i = 2; i < between.length + 2; i++) {
					List newPoints = new ArrayList(5);
					newPoints.add(new Point2D.Double(x - (spacing + i * spacing), y));
					newPoints.add(new Point2D.Double(x - (spacing + i * spacing), y - (spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x, y - (2 * spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x + (spacing + i * spacing), y - (spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x + (spacing), y - (spacing / 2 + i * spacing)));
					facade.setPoints(between[i - 2], newPoints);
				}

				continue;
			}

			double dx = (sPP.getX()) - (tPP.getX());
			double dy = (sPP.getY()) - (tPP.getY());
			double mx = (tPP.getX()) + dx / 2.0;
			double my = (tPP.getY()) + dy / 2.0;
			double slope = Math.sqrt(dx * dx + dy * dy);
			for (int i = 0; i < between.length; i++) {
				List newPoints = new ArrayList(3);
				double pos = 2 * i - (between.length - 1);
				if (facade.getSourcePort(between[i]) == sourcePort) {
					newPoints.add(sPP);
					newPoints.add(tPP);
				} else {
					newPoints.add(tPP);
					newPoints.add(sPP);
				}
				if (pos != 0) {
					pos = pos / 2;
					double x = mx + pos * spacing * dy / slope;
					double y = my - pos * spacing * dx / slope;
					newPoints.add(1, new SerializablePoint2D.Double(x, y));
				}
				facade.setPoints(between[i], newPoints);
			}
		}
	}

	public static JComponent visualizeGraph(
			DirectedGraph<?, ?> graph,
			HeuristicsNet net,
			AnnotatedVisualizationSettings settings,
			Progress progress) {

		return getResultsPanel(graph, new ViewSpecificAttributeMap(), net, settings, progress);
	}
}

