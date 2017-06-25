package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.gui;

// BVD: REMOVED THIS CODE AS I THINK ITS NOT NECESSARY ANYMORE

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.BasicMarqueeHandler;
import org.processmining.framework.util.Cleanable;
import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.ViewSpecificAttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.HeuristicsNetGraph;
import org.processmining.models.jgraph.ContextMenuCreator;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMJGraph;
import org.processmining.models.jgraph.elements.ProMGraphCell;
import org.processmining.models.jgraph.elements.ProMGraphEdge;
import org.processmining.models.jgraph.listeners.SelectionListener;
import org.processmining.plugins.heuristicsnet.AnnotatedHeuristicsNet;
import org.processmining.plugins.heuristicsnet.SimpleHeuristicsNet;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Operator;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Split;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.operators.Stats;
import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationGenerator;
import org.processmining.plugins.heuristicsnet.visualizer.annotatedvisualization.AnnotatedVisualizationSettings;

import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class HeuristicsNetVisualization extends JPanel implements Cleanable,
		ChangeListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1444524291294067108L;

	protected static final int MAX_ZOOM = 1200;

	// -------------------------------------------

	protected ProMJGraph graph;
	protected ProMJGraph pipGraph;
	protected final HeuristicsNet net;

	// -------------------------------------------

	protected JScrollPane scroll;
	private JPanel pipPanelON, pipPanelOFF;
	private PIPPanel pip;
	private JPanel zoomPanelON, zoomPanelOFF;
	private ZoomPanel zoom;
	private JPanel splitsPanel, joinsPanel;
	private AnnotationsPanel splits, joins;
	private JPanel parametersPanelON, parametersPanelOFF;
	private ParametersPanel parameters;
	// private JPanel optionsPanelOFF;
	private JPanel setupPanelON, setupPanelOFF;
	private SetupPanel setup;
	private JPanel fitnessPanel;

	private float zoomRatio, pipRatio;
	private double normalScale;
	private Rectangle normalBounds, zoomBounds, pipBounds;

	private boolean hasNodeSelected;

	private List<SelectionListener<?, ?>> selectionListeners = new ArrayList<SelectionListener<?, ?>>(
			0);
	private ContextMenuCreator creator = null;

	public HeuristicsNetVisualization(final ProMJGraph graph,
			final HeuristicsNet net,
			final AnnotatedVisualizationSettings settings) {

		this.setLayout(null);
		this.graph = graph;
		this.net = net;

		SlickerFactory factory = SlickerFactory.instance();
		SlickerDecorator decorator = SlickerDecorator.instance();

		this.addComponentListener(new java.awt.event.ComponentListener() {

			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
				resize();
			}
		});

		this.initGraph();

		AdjustmentListener aListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				repaintPIP(graph.getVisibleRect());
			}
		};
		MouseListener mListener = new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				if (hasNodeSelected) {

					joinsPanel.setVisible(false);
					joinsPanel.setEnabled(false);

					splitsPanel.setVisible(false);
					splitsPanel.setEnabled(false);
				}
			}

			public void mouseExited(MouseEvent e) {

				if (hasNodeSelected) {

					scroll.repaint();

					joinsPanel.repaint();
					splitsPanel.repaint();

					joinsPanel.setVisible(true);
					joinsPanel.setEnabled(true);

					splitsPanel.setVisible(true);
					splitsPanel.setEnabled(true);
				}

			}

			public void mousePressed(MouseEvent e) {

				zoomPanelOFF.setVisible(false);
				zoomPanelOFF.setEnabled(false);

				pipPanelOFF.setVisible(false);
				pipPanelOFF.setEnabled(false);

				setupPanelOFF.setVisible(false);
				setupPanelOFF.setEnabled(false);

				parametersPanelOFF.setVisible(false);
				parametersPanelOFF.setEnabled(false);
			}

			public void mouseReleased(MouseEvent e) {

				zoomPanelOFF.setVisible(true);
				zoomPanelOFF.setEnabled(true);

				pipPanelOFF.setVisible(true);
				pipPanelOFF.setEnabled(true);

				setupPanelOFF.setVisible(true);
				setupPanelOFF.setEnabled(true);

				parametersPanelOFF.setVisible(true);
				parametersPanelOFF.setEnabled(true);
			}
		};

		this.scroll = new JScrollPane(graph);
		this.scroll.getHorizontalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getVerticalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getHorizontalScrollBar().addMouseListener(mListener);
		this.scroll.getVerticalScrollBar().addMouseListener(mListener);
		decorator.decorate(this.scroll, Color.WHITE, Color.GRAY,
				Color.DARK_GRAY);
		this.scroll.setBorder(new DashedBorder(Color.LIGHT_GRAY));

		this.pipPanelON = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.pipPanelOFF = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.pipPanelON.setLayout(null);
		this.pipPanelOFF.setLayout(null);
		this.pip = new PIPPanel(factory, this.scroll, this.pipGraph, this);
		this.pip.setRect(graph.getBounds());
		this.pipPanelON.add(this.pip);
		this.pipPanelON.setVisible(false);
		this.pipPanelON.setEnabled(false);
		JLabel pipPanelTitle = factory.createLabel("PIP");
		pipPanelTitle.setForeground(Color.WHITE);
		pipPanelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		// pipPanelTitle.setUI(new VerticalLabelUI(true));
		this.pipPanelOFF.add(pipPanelTitle);
		pipPanelTitle.setBounds(10, 10, 30, 30);

		this.pipPanelOFF.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				int x = scroll.getHorizontalScrollBar().getValue();
				int y = scroll.getVerticalScrollBar().getValue();

				showSetup(false);
				showParameters(false);
				// showOptions(false);
				showZoom(false);
				showPIP(true);

				scroll.getHorizontalScrollBar().setValue((int) (x * pipRatio));
				scroll.getVerticalScrollBar().setValue((int) (y * pipRatio));

				if (hasNodeSelected) {

					joinsPanel.setVisible(false);
					joinsPanel.setEnabled(false);

					splitsPanel.setVisible(false);
					splitsPanel.setEnabled(false);
				}
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		this.pipPanelON.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {

				int x = scroll.getHorizontalScrollBar().getValue();
				int y = scroll.getVerticalScrollBar().getValue();

				boolean flag = false;
				if ((e.getY() >= pipPanelON.getHeight()) || (e.getY() <= 10))
					flag = true;
				else {

					if ((e.getX() >= pipPanelON.getWidth()) || (e.getX() <= 0))
						flag = true;
				}

				if (flag) {

					showPIP(false);

					scroll.getHorizontalScrollBar().setValue(
							(int) (x / pipRatio));
					scroll.getVerticalScrollBar()
							.setValue((int) (y / pipRatio));

					if (hasNodeSelected) {

						joinsPanel.repaint();
						splitsPanel.repaint();

						joinsPanel.setVisible(true);
						joinsPanel.setEnabled(true);

						splitsPanel.setVisible(true);
						splitsPanel.setEnabled(true);
					}
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		this.zoomPanelON = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.zoomPanelOFF = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.zoomPanelON.setLayout(null);
		this.zoomPanelOFF.setLayout(null);
		this.zoom = new ZoomPanel(factory, decorator, 100, MAX_ZOOM);
		this.zoom.addSliderChangeListener(this);
		this.zoomPanelON.add(this.zoom);
		this.zoomPanelON.setVisible(false);
		this.zoomPanelON.setEnabled(false);
		JLabel zoomPanelTitle = factory.createLabel("Zoom");
		zoomPanelTitle.setForeground(Color.WHITE);
		zoomPanelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		zoomPanelTitle.setUI(new VerticalLabelUI(true));
		this.zoomPanelOFF.add(zoomPanelTitle);
		zoomPanelTitle.setBounds(10, 10, 30, 55);

		this.zoomPanelOFF.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				int x = scroll.getHorizontalScrollBar().getValue();
				int y = scroll.getVerticalScrollBar().getValue();

				showSetup(false);
				showParameters(false);
				// showOptions(false);
				showPIP(false);
				showZoom(true);

				scroll.getHorizontalScrollBar().setValue((int) (x * zoomRatio));
				scroll.getVerticalScrollBar().setValue((int) (y * zoomRatio));

				if (hasNodeSelected) {

					joinsPanel.setVisible(false);
					joinsPanel.setEnabled(false);

					splitsPanel.setVisible(false);
					splitsPanel.setEnabled(false);
				}
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		this.zoomPanelON.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {

				int x = scroll.getHorizontalScrollBar().getValue();
				int y = scroll.getVerticalScrollBar().getValue();

				boolean flag = false;
				if (e.getX() >= zoom.getWidth())
					flag = true;
				else {

					if ((e.getY() >= zoom.getHeight()) || (e.getY() <= 0))
						flag = true;
				}

				if (flag) {

					showZoom(false);

					scroll.getHorizontalScrollBar().setValue(
							(int) (x / zoomRatio));
					scroll.getVerticalScrollBar().setValue(
							(int) (y / zoomRatio));

					if (hasNodeSelected) {

						joinsPanel.repaint();
						splitsPanel.repaint();

						joinsPanel.setVisible(true);
						joinsPanel.setEnabled(true);

						splitsPanel.setVisible(true);
						splitsPanel.setEnabled(true);
					}
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		if (this.net instanceof SimpleHeuristicsNet) {

			this.parametersPanelON = factory.createRoundedPanel(15,
					Color.LIGHT_GRAY);
			this.parametersPanelOFF = factory.createRoundedPanel(15,
					Color.DARK_GRAY);
			this.parametersPanelON.setLayout(null);
			this.parametersPanelOFF.setLayout(null);
			this.parameters = new ParametersPanel();
			this.parameters.copySettings(((SimpleHeuristicsNet) this.net)
					.getSettings());
			if (this.net instanceof AnnotatedHeuristicsNet) {

				this.parameters.removeAndThreshold();
				this.parameters.setBounds(10, 10, 520, 420);
			} else
				this.parameters.setBounds(10, 10, 520, 450);
			this.parameters.setBackground(Color.LIGHT_GRAY);
			this.parameters.setEnabled(false);
			this.parametersPanelON.add(this.parameters);
			this.parametersPanelON.setVisible(false);
			this.parametersPanelON.setEnabled(false);
			JLabel parametersPanelTitle = factory.createLabel("Parameters");
			parametersPanelTitle.setForeground(Color.WHITE);
			parametersPanelTitle.setFont(new java.awt.Font("Dialog",
					java.awt.Font.BOLD, 18));
			this.parametersPanelOFF.add(parametersPanelTitle);
			parametersPanelTitle.setBounds(12, 0, 105, 30);

			this.parametersPanelOFF.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {

					showSetup(false);
					// showOptions(false);
					showPIP(false);
					showZoom(false);
					showParameters(true);

					if (hasNodeSelected) {

						joinsPanel.setVisible(false);
						joinsPanel.setEnabled(false);

						splitsPanel.setVisible(false);
						splitsPanel.setEnabled(false);
					}
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}
			});

			this.parametersPanelON.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {

					boolean flag = false;
					if ((e.getX() >= parametersPanelON.getWidth())
							|| (e.getX() <= 0))
						flag = true;
					else {

						if ((e.getY() >= parametersPanelON.getHeight())
								|| (e.getY() <= 0))
							flag = true;
					}

					if (flag) {

						showParameters(false);

						if (hasNodeSelected) {

							joinsPanel.repaint();
							splitsPanel.repaint();

							joinsPanel.setVisible(true);
							joinsPanel.setEnabled(true);

							splitsPanel.setVisible(true);
							splitsPanel.setEnabled(true);
						}
					}
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}
			});
		} else
			this.parametersPanelON = new JPanel();

		// this.optionsPanelOFF = factory.createRoundedPanel(15,
		// Color.DARK_GRAY);
		// this.optionsPanelOFF.setLayout(null);
		// JLabel optionsPanelTitle = factory.createLabel("Options");
		// optionsPanelTitle.setForeground(Color.WHITE);
		// optionsPanelTitle.setFont(new java.awt.Font("Dialog",
		// java.awt.Font.BOLD, 18));
		// this.optionsPanelOFF.add(optionsPanelTitle);
		// optionsPanelTitle.setBounds(10, 10, 70, 30);

		this.setupPanelON = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.setupPanelOFF = factory.createRoundedPanel(15, Color.DARK_GRAY);
		this.setupPanelON.setLayout(null);
		this.setupPanelOFF.setLayout(null);
		this.setup = new SetupPanel(factory, decorator, settings);
		this.setupPanelON.add(this.setup);
		this.setupPanelON.setVisible(false);
		this.setupPanelON.setEnabled(false);
		JLabel setupPanelTitle = factory.createLabel("Setup");
		setupPanelTitle.setForeground(Color.WHITE);
		setupPanelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		setupPanelTitle.setUI(new VerticalLabelUI(false));
		this.setupPanelOFF.add(setupPanelTitle);
		setupPanelTitle.setBounds(0, 7, 30, 55);

		this.setupPanelOFF.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				// showOptions(false);
				showPIP(false);
				showZoom(false);
				showParameters(false);
				showSetup(true);

				if (hasNodeSelected) {

					joinsPanel.setVisible(false);
					joinsPanel.setEnabled(false);

					splitsPanel.setVisible(false);
					splitsPanel.setEnabled(false);
				}
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		this.setupPanelON.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {

				boolean flag = true;
				if ((e.getX() >= setupPanelON.getWidth()) || (e.getX() <= 0))
					flag = false;
				else {

					if ((e.getY() >= setupPanelON.getHeight())
							|| (e.getY() <= 0))
						flag = false;
				}

				if (!flag) {

					showSetup(flag);

					if (setup.hasChanged()) {

						redraw();
						hasNodeSelected = false;
					} else {

						if (hasNodeSelected) {

							joinsPanel.repaint();
							splitsPanel.repaint();

							joinsPanel.setVisible(true);
							joinsPanel.setEnabled(true);

							splitsPanel.setVisible(true);
							splitsPanel.setEnabled(true);
						}
					}
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		});

		this.fitnessPanel = factory.createRoundedPanel(15, Color.GRAY);
		this.fitnessPanel.setLayout(null);
		JLabel fitnessInfo = factory.createLabel("Fitness: "
				+ (Math.round(this.net.getFitness() * 10000) / 10000f));
		fitnessInfo.setForeground(Color.WHITE);
		fitnessInfo
				.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 18));
		this.fitnessPanel.add(fitnessInfo);
		fitnessInfo.setBounds(20, 0, 140, 30);

		this.joinsPanel = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.joinsPanel.setLayout(null);
		this.joins = new AnnotationsPanel(factory, decorator, null, "");
		this.joinsPanel.add(this.joins);
		this.joinsPanel.setVisible(false);
		this.joinsPanel.setEnabled(false);

		this.splitsPanel = factory.createRoundedPanel(15, Color.LIGHT_GRAY);
		this.splitsPanel.setLayout(null);
		this.splits = new AnnotationsPanel(factory, decorator, null, "");
		this.splitsPanel.add(this.splits);
		this.splitsPanel.setVisible(false);
		this.splitsPanel.setEnabled(false);

		this.add(this.zoomPanelON);
		this.add(this.zoomPanelOFF);
		this.add(this.pipPanelON);
		this.add(this.pipPanelOFF);
		this.add(this.parametersPanelON);
		this.add(this.parametersPanelOFF);
		// this.add(this.optionsPanelON);
		// this.add(this.optionsPanelOFF);
		this.add(this.setupPanelON);
		this.add(this.setupPanelOFF);
		this.add(this.fitnessPanel);
		this.add(this.joinsPanel);
		this.add(this.splitsPanel);
		this.add(this.scroll);

		this.setBackground(Color.WHITE);

		this.validate();
		this.repaint();
	}

	private void resize() {

		int width = this.getSize().width;
		int height = this.getSize().height;

		int pipHeight = 250;
		int pipWidth = (int) ((float) width / (float) height * pipHeight);
		this.pip.setBounds(10, 20, pipWidth, pipHeight);

		this.zoom.setHeight((int) (height * 0.66));

		int zoomWidth = this.zoom.getSize().width;
		int zoomHeight = this.zoom.getSize().height;

		this.pipRatio = (float) (height - pipHeight - 50)
				/ (float) (height - 60);
		this.zoomRatio = (float) (width - zoomWidth - 40)
				/ (float) (width - 60);
		this.normalBounds = new Rectangle(30, 30, width - 60, height - 60);
		this.zoomBounds = new Rectangle(10 + zoomWidth,
				30 + (int) ((1f - this.zoomRatio) * (height - 60)), width
						- zoomWidth - 40,
				(int) (this.zoomRatio * (height - 60)));
		this.pipBounds = new Rectangle(
				30 + ((int) ((1f - this.pipRatio) * (width - 60))),
				20 + pipHeight, (int) (this.pipRatio * (width - 60)), height
						- pipHeight - 50);

		this.normalScale = graph.getScale();

		this.scroll.setBounds(this.normalBounds);
		this.pipPanelON.setBounds(40, -10, pipWidth + 20, pipHeight + 30);
		this.pipPanelOFF.setBounds(40, -10, 50, 40);
		this.zoomPanelON.setBounds(0, 40, zoomWidth + 10, zoomHeight);
		this.zoomPanelOFF.setBounds(-10, 40, 40, 72);

		int parametersHeight = this.parameters.getHeight();
		this.parametersPanelON.setBounds(width - 580,
				height - parametersHeight, 540, parametersHeight + 10);
		this.parametersPanelOFF.setBounds(width - 165, height - 30, 125, 40);

		// this.optionsPanelOFF.setBounds(width - 130, -10, 90, 40);

		this.setup.setBounds(10, 10, 330, 420);
		this.setupPanelON.setBounds(width - 335, height - 370, 345, 330);
		this.setupPanelOFF.setBounds(width - 30, height - 115, 40, 75);

		this.fitnessPanel.setBounds(-10, height - 30, 160, 40);

		double fitRatio = scaleToFit(this.graph, this.scroll, false);
		this.zoom
				.setFitValue((int) Math.floor(fitRatio * this.zoomRatio * 100));
		this.scalePIP();

		this.joinsPanel.setBounds((int) (width / 2f) - 305, height - 300, 300,
				310);
		this.joins.setSize(300 - 20, 300 - 20);
		this.joins.setBounds(10, 10, 300 - 20, 300 - 20);

		this.splitsPanel.setBounds((int) (width / 2f) + 5, height - 300, 300,
				310);
		this.splits.setSize(300 - 20, 300 - 20);
		this.splits.setBounds(10, 10, 300 - 20, 300 - 20);
	}

	private void showZoom(boolean status) {

		zoomPanelOFF.setVisible(!status);
		zoomPanelOFF.setEnabled(!status);
		zoomPanelON.setVisible(status);
		zoomPanelON.setEnabled(status);

		if (status) {

			this.scroll.setBounds(this.zoomBounds);
			graph.setScale(this.normalScale * this.zoomRatio);
		} else {

			this.scroll.setBounds(this.normalBounds);
			graph.setScale(this.normalScale);
		}

	}

	private void showPIP(boolean status) {

		pipPanelOFF.setVisible(!status);
		pipPanelOFF.setEnabled(!status);
		pipPanelON.setVisible(status);
		pipPanelON.setEnabled(status);

		if (status) {

			this.scroll.setBounds(this.pipBounds);
			graph.setScale(this.normalScale * this.pipRatio);
		} else {

			this.scroll.setBounds(this.normalBounds);
			graph.setScale(this.normalScale);
		}
	}

	private void showParameters(boolean status) {

		parametersPanelOFF.setVisible(!status);
		parametersPanelOFF.setEnabled(!status);
		parametersPanelON.setVisible(status);
		parametersPanelON.setEnabled(status);
	}

	// private void showOptions(boolean status){
	//
	// optionsPanelOFF.setVisible(!status);
	// optionsPanelOFF.setEnabled(!status);
	// optionsPanelON.setVisible(status);
	// optionsPanelON.setEnabled(status);
	// }
	private void showSetup(boolean status) {

		setupPanelOFF.setVisible(!status);
		setupPanelOFF.setEnabled(!status);
		setupPanelON.setVisible(status);
		setupPanelON.setEnabled(status);
	}

	private void redraw() {

		int scrollPositionX = this.scroll.getHorizontalScrollBar().getValue();
		int scrollPositionY = this.scroll.getVerticalScrollBar().getValue();

		AnnotatedVisualizationGenerator generator = new AnnotatedVisualizationGenerator();
		HeuristicsNetGraph hng = generator.generate(this.net, this.setup
				.getSettings());

		this.graph = HeuristicsNetVisualizer.createJGraph(hng, new ViewSpecificAttributeMap(), null);

		this.initGraph();

		this.remove(this.scroll);

		AdjustmentListener aListener = new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {

				repaintPIP(graph.getVisibleRect());
			}
		};
		MouseListener mListener = new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {

				if (hasNodeSelected) {

					joinsPanel.setVisible(false);
					joinsPanel.setEnabled(false);

					splitsPanel.setVisible(false);
					splitsPanel.setEnabled(false);
				}
			}

			public void mouseExited(MouseEvent e) {

				if (hasNodeSelected) {

					joinsPanel.repaint();
					splitsPanel.repaint();

					joinsPanel.setVisible(true);
					joinsPanel.setEnabled(true);

					splitsPanel.setVisible(true);
					splitsPanel.setEnabled(true);
				}
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		};

		this.scroll = new JScrollPane(graph);
		SlickerDecorator.instance().decorate(this.scroll, Color.WHITE,
				Color.GRAY, Color.DARK_GRAY);
		this.scroll.getHorizontalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getVerticalScrollBar().addAdjustmentListener(aListener);
		this.scroll.getHorizontalScrollBar().addMouseListener(mListener);
		this.scroll.getVerticalScrollBar().addMouseListener(mListener);
		this.scroll.setBorder(new DashedBorder(Color.LIGHT_GRAY));
		this.add(this.scroll);
		this.scroll.setBounds(this.normalBounds);

		this.pip.setPIPgraph(this.pipGraph);
		this.pip.setParentScroll(this.scroll);
		this.scalePIP();

		this.graph.setScale(this.normalScale);

		this.scroll.getHorizontalScrollBar().setValue(scrollPositionX);
		this.scroll.getVerticalScrollBar().setValue(scrollPositionY);
	}

	private void initGraph() {

		this.graph.addGraphSelectionListener(new GraphSelectionListener() {

			@SuppressWarnings("unchecked")
			public void valueChanged(GraphSelectionEvent e) {

				DirectedGraphNode selectedCell = null;

				Object[] cells = e.getCells();
				Collection nodesAdded = new ArrayList<ProMGraphCell>();
				Collection edgesAdded = new ArrayList<ProMGraphEdge>();
				Collection nodesRemoved = new ArrayList<ProMGraphCell>();
				Collection edgesRemoved = new ArrayList<ProMGraphEdge>();
				Collection<?> nodes = graph.getProMGraph().getNodes();
				Collection<?> edges = graph.getProMGraph().getEdges();
				for (int i = 0; i < cells.length; i++) {
					Collection nodeList;
					Collection edgeList;

					boolean isCell = cells[i] instanceof ProMGraphCell;
					boolean isEdge = cells[i] instanceof ProMGraphEdge;

					if (e.isAddedCell(i)) {
						nodeList = nodesAdded;
						edgeList = edgesAdded;

						if (isCell && (selectedCell == null))
							selectedCell = ((ProMGraphCell) cells[i]).getNode();

					} else {
						nodeList = nodesRemoved;
						edgeList = edgesRemoved;
					}
					if (isCell) {
						DirectedGraphNode node = ((ProMGraphCell) cells[i])
								.getNode();
						if (nodes.contains(node)) {
							nodeList.add(node);
						}
					} else if (isEdge) {
						DirectedGraphEdge<?, ?> edge = ((ProMGraphEdge) cells[i])
								.getEdge();
						if (edges.contains(edge)) {
							edgeList.add(((ProMGraphEdge) cells[i]).getEdge());
						}
					}
				}
				SelectionListener.SelectionChangeEvent event = new SelectionListener.SelectionChangeEvent(
						nodesAdded, edgesAdded, nodesRemoved, edgesRemoved);
				for (SelectionListener listener : selectionListeners) {
					listener.SelectionChanged(event);
				}

				// retrieve inputs/outputs for the selected event
				if (net instanceof AnnotatedHeuristicsNet) {

					AnnotatedHeuristicsNet anet = (AnnotatedHeuristicsNet) net;

					if (nodesAdded.size() == 1) {

						String nodeLabel = selectedCell.getLabel();
						int index1 = nodeLabel.indexOf("<b>") + 3;
						int index2 = nodeLabel.indexOf("<br />");

						String nodeID = nodeLabel.substring(index1, index2 - 4);

						nodeLabel = nodeLabel.substring(index2 + 6);
						String nodeType = nodeLabel.substring(0, nodeLabel.indexOf("<br />"));
						
						if (!"".equals(nodeType)) {
							nodeID = nodeID + "+" + nodeType;
						}

						String key = anet.getKey(nodeID).toString();
						splits.update(anet.getSplit(key), nodeID, anet
								.getInvertedKeys());
						joins.update(anet.getJoin(key), nodeID, anet
								.getInvertedKeys());

						joinsPanel.repaint();
						joinsPanel.setVisible(true);
						joinsPanel.setEnabled(true);
						splitsPanel.repaint();
						splitsPanel.setVisible(true);
						splitsPanel.setEnabled(true);

						hasNodeSelected = true;
					} else {

						joinsPanel.setVisible(false);
						joinsPanel.setEnabled(false);
						splitsPanel.setVisible(false);
						splitsPanel.setEnabled(false);

						hasNodeSelected = false;
					}
				}
			}

		});
		this.graph.setTolerance(4);

		this.graph.setMarqueeHandler(new BasicMarqueeHandler() {
			private boolean test(MouseEvent e) {
				return SwingUtilities.isRightMouseButton(e)
						&& (e.getModifiers() & InputEvent.ALT_MASK) == 0;

			}

			public boolean isForceMarqueeEvent(MouseEvent event) {
				if (test(event)) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (test(e)) {
					e.consume();
				} else {
					super.mouseReleased(e);
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (test(e)) {
					synchronized (graph.getProMGraph()) {
						// Check for selection.
						// If the cell that is being clicked is part of the
						// selection,
						// we use the current selection.
						// otherwise, we use a new selection
						Object cell = graph.getFirstCellForLocation(e.getX(), e
								.getY());

						Collection<DirectedGraphElement> sel;
						if (cell == null) {
							// Nothing selected
							graph.clearSelection();
							sel = new ArrayList<DirectedGraphElement>(0);
						} else if (graph.getSelectionModel().isCellSelected(
								cell)) {
							// the current selection contains cell
							// use that selection
							sel = getSelectedElements();
						} else {
							// the current selection does not contain cell.
							// reset the selection to [cell]
							sel = new ArrayList<DirectedGraphElement>(1);
							sel.add(getElementForLocation(e.getX(), e.getY()));
							graph.setSelectionCell(cell);
						}
						if (creator != null) {
							JPopupMenu menu = creator.createMenuFor(graph
									.getProMGraph(), sel);
							if (menu != null) {
								menu.show(graph, e.getX(), e.getY());
							}
						}
					}
				} else {
					super.mousePressed(e);
				}
			}
		});

		this.graph.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {

				if (hasNodeSelected) {

					joinsPanel.repaint();
					splitsPanel.repaint();
				}
			}
		});

		// Collapse any expandable nodes that claim they are collapsed
		// This is not handled previously.
//		for (Object n : graph.getGraphLayoutCache().getCells(true, false,
//				false, false)) {
//			if (((ProMGraphCell) n).getNode() instanceof Expandable) {
//				Expandable ex = (Expandable) ((ProMGraphCell) n).getNode();
//				
//				if (ex.isCollapsed()) { ex.collapse(); }
//			}
//		}
		
		GraphLayoutConnection con = new GraphLayoutConnection(this.graph.getProMGraph());

		ProMGraphModel model = new ProMGraphModel(graph.getProMGraph());
		this.pipGraph = new ProMJGraph(model, true, graph.getViewSpecificAttributes(), con) {

			private static final long serialVersionUID = -4671278744184554287L;

			@Override
			protected void changeHandled() {

				scalePIP();
				repaintPIP(graph.getVisibleRect());
			}
		};

		this.hasNodeSelected = false;
	}

	public double getScale() {
		return graph.getScale();
	}

	public void setScale(double d) {

		int b = (int) (100.0 * d);
		b = Math.max(b, 1);
		b = Math.min(b, MAX_ZOOM);
		this.zoom.setValue(b);
	}

	protected void repaintPIP(Rectangle2D rect) {

		double s = factorMultiplyGraphToPIP();
		double x = Math.max(1, s * rect.getX());
		double y = Math.max(1, s * rect.getY());
		double w = Math.min(s * rect.getWidth(), this.pip.getVisWidth() - 1);
		double h = Math.min(s * rect.getHeight(), this.pip.getVisHeight() - 1);
		rect = new Rectangle2D.Double(x, y, w, h);
		this.pip.setRect(rect);
		this.pip.repaint();
	}

	public double factorMultiplyGraphToPIP() {
		return pipGraph.getScale() / graph.getScale();
	}

	protected void scalePIP() {
		this.pipGraph.setScale(scaleToFit(this.pipGraph, this.pip, false));
	}

	protected double scaleToFit(ProMJGraph graph, Container container,
			boolean reposition) {

		Rectangle2D bounds = graph.getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		if (reposition) {

			graph.repositionToOrigin();
			x = 0;
			y = 0;
		}

		Dimension size = container.getSize();

		double ratio = Math.min(size.getWidth() / (bounds.getWidth() + x), size
				.getHeight()
				/ (bounds.getHeight() + y));

		return ratio;
	}

	public void paint(Graphics g) {
		super.paint(g);
	}

	public DirectedGraphElement getElementForLocation(double x, double y) {
		Object cell = graph.getFirstCellForLocation(x, y);
		if (cell instanceof ProMGraphCell) {
			return ((ProMGraphCell) cell).getNode();
		}
		if (cell instanceof ProMGraphEdge) {
			return ((ProMGraphEdge) cell).getEdge();
		}
		return null;
	}

	public Collection<DirectedGraphNode> getSelectedNodes() {
		List<DirectedGraphNode> nodes = new ArrayList<DirectedGraphNode>();
		for (Object o : graph.getSelectionCells()) {
			if (o instanceof ProMGraphCell) {
				nodes.add(((ProMGraphCell) o).getNode());
			}
		}
		return nodes;
	}

	public Collection<DirectedGraphEdge<?, ?>> getSelectedEdges() {
		List<DirectedGraphEdge<?, ?>> edges = new ArrayList<DirectedGraphEdge<?, ?>>();
		for (Object o : graph.getSelectionCells()) {
			if (o instanceof ProMGraphEdge) {
				edges.add(((ProMGraphEdge) o).getEdge());
			}
		}
		return edges;
	}

	public Collection<DirectedGraphElement> getSelectedElements() {
		List<DirectedGraphElement> elements = new ArrayList<DirectedGraphElement>();
		for (Object o : graph.getSelectionCells()) {
			if (o instanceof ProMGraphCell) {
				elements.add(((ProMGraphCell) o).getNode());
			} else if (o instanceof ProMGraphEdge) {
				elements.add(((ProMGraphEdge) o).getEdge());
			}
		}
		return elements;
	}

	public void cleanUp() {

		graph.cleanUp();
		pipGraph.cleanUp();
	}

	public void stateChanged(ChangeEvent e) {

		Object source = e.getSource();

		if (source instanceof JSlider) {

			graph.setScale(((JSlider) source).getValue() / 100.0);
			repaintPIP(graph.getVisibleRect());

			this.normalScale = graph.getScale() / this.zoomRatio;
		}
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalize() throws Throwable {

		try {
			cleanUp();
		} finally {
			super.finalize();
		}
	}

}

class ZoomPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8415559591750873766L;
	private final JSlider slider;
	private JLabel sliderMinValue, sliderMaxValue, sliderFitValue, sliderValue;

	private int fitZoom;

	public ZoomPanel(SlickerFactory factory, SlickerDecorator decorator,
			int fitZoom, int maximumZoom) {

		super(null);

		this.fitZoom = fitZoom;

		this.slider = factory.createSlider(1);

		this.slider.setMinimum(1);
		this.slider.setMaximum(maximumZoom);
		this.slider.setValue(fitZoom);

		this.slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				update();
			}
		});

		this.sliderMinValue = factory.createLabel("0%");
		this.sliderMaxValue = factory.createLabel(maximumZoom + "%");
		this.sliderFitValue = factory.createLabel("Fit  >");
		this.sliderValue = factory.createLabel(fitZoom + "%");

		this.sliderMinValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.sliderMaxValue.setHorizontalAlignment(SwingConstants.CENTER);
		this.sliderFitValue.setHorizontalAlignment(SwingConstants.RIGHT);
		this.sliderValue.setHorizontalAlignment(SwingConstants.LEFT);

		this.sliderMinValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.sliderMaxValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.sliderFitValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));
		this.sliderValue.setFont(new java.awt.Font("Dialog",
				java.awt.Font.BOLD, 14));

		this.sliderMinValue.setForeground(Color.GRAY);
		this.sliderMaxValue.setForeground(Color.GRAY);
		this.sliderFitValue.setForeground(Color.GRAY);
		this.sliderValue.setForeground(Color.DARK_GRAY);

		this.add(this.slider);
		this.add(this.sliderMinValue);
		this.add(this.sliderMaxValue);
		this.add(this.sliderFitValue);
		this.add(this.sliderValue);

		this.setBackground(Color.LIGHT_GRAY);
	}

	public void setHeight(int height) {

		this.setSize(115, height);

		int sliderHeight = height - 60;

		// this.title.setBounds(0, (int) (height * 0.5) - 25, 30, 50);

		this.slider.setBounds(35, 30, 30, sliderHeight);
		this.sliderMaxValue.setBounds(0, 10, 100, 20);
		this.sliderMinValue.setBounds(0, height - 30, 100, 20);

		int value = this.slider.getValue();
		int span = this.slider.getMaximum() - this.slider.getMinimum();
		int position = 33 + (int) ((float) (this.slider.getMaximum() - this.fitZoom)
				/ (float) span * (sliderHeight - 28));
		this.sliderFitValue.setBounds(0, position, 40, 20);

		if (value == this.fitZoom)
			this.sliderValue.setBounds(65, position, 60, 20);
		else {

			position = 33 + (int) ((float) (this.slider.getMaximum() - value)
					/ (float) span * (sliderHeight - 28));
			this.sliderValue.setBounds(65, position, 60, 20);
		}
	}

	private void update() {

		int value = this.slider.getValue();

		int span = this.slider.getMaximum() - this.slider.getMinimum();
		int position = 33 + (int) ((float) (this.slider.getMaximum() - value)
				/ (float) span * (this.slider.getBounds().height - 28));

		this.sliderValue.setText(value + "%");
		this.sliderValue.setBounds(65, position, 60, 20);
	}

	public void setValue(int value) {
		this.slider.setValue(value);
	}

	public void setFitValue(int value) {

		this.fitZoom = value;

		int span = this.slider.getMaximum() - this.slider.getMinimum();
		int position = 33 + (int) ((float) (this.slider.getMaximum() - value)
				/ (float) span * (this.slider.getBounds().height - 28));
		this.sliderFitValue.setBounds(0, position, 40, 20);
	}

	public void addSliderChangeListener(ChangeListener listener) {
		this.slider.addChangeListener(listener);
	}
}

class PIPPanel extends JPanel implements MouseListener, MouseMotionListener {

	private static final int PIPSIZE = 250;

	private static final long serialVersionUID = 5563202305263696868L;

	// new FlowLayout(FlowLayout.LEADING, 0, 0);
	private Rectangle2D rect;
	private Stroke stroke = new BasicStroke(2);
	private Color color = Color.BLUE;
	private JScrollPane parentScroll;
	private final HeuristicsNetVisualization panel;

	private ProMJGraph pipGraph;

	public PIPPanel(SlickerFactory factory, JScrollPane parentScroll,
			ProMJGraph pipGraph, HeuristicsNetVisualization panel) {

		super(new BorderLayout());

		this.setPIPgraph(pipGraph);

		this.parentScroll = parentScroll;
		this.panel = panel;

		setPreferredSize(new Dimension(PIPSIZE, PIPSIZE));
		setMinimumSize(new Dimension(PIPSIZE, PIPSIZE));
		setMaximumSize(new Dimension(PIPSIZE, PIPSIZE));
		pipGraph.setMaximumSize(new Dimension(PIPSIZE, PIPSIZE));
		setSize(new Dimension(PIPSIZE, PIPSIZE));
	}

	public void setPIPgraph(ProMJGraph pipGraph) {

		if (this.pipGraph != null)
			this.remove(this.pipGraph);

		this.pipGraph = pipGraph;

		for (int i = 0; i < pipGraph.getMouseListeners().length; i++) {
			pipGraph.removeMouseListener(pipGraph.getMouseListeners()[0]);
		}
		for (int i = 0; i < pipGraph.getMouseMotionListeners().length; i++) {
			pipGraph.removeMouseMotionListener(pipGraph
					.getMouseMotionListeners()[0]);
		}
		pipGraph.addMouseMotionListener(this);
		pipGraph.addMouseListener(this);

		this.add(pipGraph);
	}

	public void setParentScroll(JScrollPane scroll) {
		this.parentScroll = scroll;
	}

	public double getVisWidth() {
		return pipGraph.getPreferredSize().getWidth() - 1;
	}

	public double getVisHeight() {
		return pipGraph.getPreferredSize().getHeight() - 1;
	}

	@Override
	public void paint(Graphics g) {

		super.paint(g);
		if (rect != null) {
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(color);
			g2d.setStroke(stroke);
			g2d.draw(rect);
		}
	}

	public Rectangle2D getRect() {
		return rect;
	}

	public void setRect(Rectangle2D rect) {
		this.rect = rect;
	}

	public void mouseDragged(MouseEvent evt) {
		if (SwingUtilities.isLeftMouseButton(evt)) {
			// a is the point in the graph where I dragged to
			if (pressPoint == null) {
				// I didn't start dragging inside rectangle.
				return;
			}
			double offsetX = pressPoint.getX() - rect.getX();
			double offsetY = pressPoint.getY() - rect.getY();
			pressPoint = evt.getPoint();
			double x = evt.getPoint().getX() - offsetX;
			double y = evt.getPoint().getY() - offsetY;
			drawMain(x, y);
		} else if (SwingUtilities.isRightMouseButton(evt)) {
			Point endDragPoint = evt.getPoint();

			Rectangle2D visRect = new Rectangle2D.Double(0, 0, getVisWidth(),
					getVisHeight());
			if (visRect.contains(endDragPoint) && startDragPoint != null) {

				rect = new Rectangle2D.Double(startDragPoint.getX(),
						startDragPoint.getY(), 0, 0);
				rect.add(endDragPoint);
				repaint();
			}
		}
	}

	private Point2D pressPoint = null;
	private Point startDragPoint;
	private Rectangle2D lastRect;

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		// store the point where I clicked the mouse
		if (rect != null) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				pressPoint = null;
				Point2D a = e.getPoint();
				if (rect.contains(a)) {
					pressPoint = a;
				}
				stroke = new BasicStroke(2);
				color = Color.BLUE;
			} else if (SwingUtilities.isRightMouseButton(e)) {
				pressPoint = null;
				startDragPoint = null;
				Rectangle2D visRect = new Rectangle2D.Double(0, 0,
						getVisWidth(), getVisHeight());
				if (visRect.contains(e.getPoint())) {
					startDragPoint = e.getPoint();
					lastRect = rect;
					rect = null;
					stroke = new BasicStroke(1);
					color = Color.GRAY;
					repaint();
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			stroke = new BasicStroke(2);
			color = Color.BLUE;
			if (rect == null || lastRect == null) {
				return;
			}

			double f = panel.factorMultiplyGraphToPIP();

			double w = Math.max(parentScroll.getViewport().getExtentSize()
					.getWidth()
					* f, lastRect.getWidth())
					/ lastRect.getWidth();
			double h = Math.max(parentScroll.getViewport().getExtentSize()
					.getHeight()
					* f, lastRect.getHeight())
					/ lastRect.getHeight();

			double scaleFactor = rect.getWidth() / lastRect.getWidth() / w;
			scaleFactor = Math.max(scaleFactor, rect.getHeight()
					/ lastRect.getHeight() / h);
			double x = rect.getMinX();
			double y = rect.getMinY();

			panel.setScale(panel.getScale() / scaleFactor);
			drawMain(x, y);
			// repaint();

		}
	}

	public void drawMain(double x, double y) {
		// The point I have now should be translated back
		// to a point in the main graph.

		x = Math.min(x, getVisWidth() - rect.getWidth());
		y = Math.min(y, getVisHeight() - rect.getHeight());
		x = Math.max(x, 0);
		y = Math.max(y, 0);

		double f = panel.factorMultiplyGraphToPIP();

		parentScroll.getViewport().setViewPosition(
				new Point((int) Math.round(x / f), (int) Math.round(y / f)));

	}

}

class AnnotationsPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6615860311124501461L;

	private String opID;

	private JLabel title, none;
	private JComboBox perspective;
	private JComboBox metric;
	private JPanel annotationsPanel;
	private JScrollPane annotationsScroll;
	private JTable patterns, connections;

	// -------------------------------------

	public AnnotationsPanel(SlickerFactory factory, SlickerDecorator decorator,
			Operator op, String opID) {

		this.opID = opID;

		this.setLayout(null);

		this.title = factory.createLabel("");
		this.title.setForeground(Color.darkGray);
		this.title
				.setFont(new java.awt.Font("Dialog", java.awt.Font.ITALIC, 18));

		this.none = factory.createLabel("None");
		this.none.setForeground(Color.darkGray);
		this.none.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 16));
		this.none.setVisible(false);

		this.perspective = factory.createComboBox(new String[] { "Connections",
				"Patterns" });
		this.perspective.setSelectedItem("Patterns");
		this.perspective.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (perspective.getSelectedIndex() == 1)
					annotationsScroll.setViewportView(patterns);
				else
					annotationsScroll.setViewportView(connections);
			}

		});

		this.metric = factory.createComboBox(new String[] { "Frequency" });
		this.metric.setSelectedItem("Frequency");

		this.annotationsScroll = new JScrollPane();
		this.annotationsScroll.setBorder(javax.swing.BorderFactory
				.createEmptyBorder());
		decorator.decorate(this.annotationsScroll, Color.WHITE, Color.GRAY,
				Color.DARK_GRAY);
		this.annotationsScroll.getViewport().setBackground(Color.WHITE);

		this.initTables(op, null);

		this.annotationsPanel = factory.createRoundedPanel(15, Color.WHITE);
		this.annotationsPanel.setLayout(null);
		this.annotationsPanel.add(this.none);
		this.annotationsPanel.add(this.metric);
		this.annotationsPanel.add(this.annotationsScroll);

		this.add(this.title);
		this.add(this.perspective);
		this.add(this.annotationsPanel);

		this.setBackground(Color.LIGHT_GRAY);
	}

	public void setSize(int width, int height) {

		super.setSize(width, height);

		this.title.setBounds(0, 0, width, 20);
		this.none.setBounds(20, 40, width - 60, 20);
		this.perspective.setBounds(0, 30, width, 20);
		this.annotationsPanel.setBounds(0, 60, width, height - 60);

		this.annotationsScroll.setBounds(5, 40, width - 10, height - 105);
		this.metric.setBounds(width - 105, 10, 100, 20);
	}

	private void initTables(Operator op, HashMap<String, String> keys) {

		int elements = 0;
		int patterns = 0;

		if (op != null) {

			elements = op.getElements().size();
			patterns = op.getLearnedPatterns().size();
		}

		// ------------------

		this.patterns = new JTable();

		this.patterns.setGridColor(Color.GRAY);
		this.patterns.setBackground(Color.WHITE);
		this.patterns.setSelectionBackground(Color.LIGHT_GRAY);
		this.patterns.setSelectionForeground(Color.DARK_GRAY);
		this.patterns.setShowVerticalLines(false);

		final Class<?>[] classesTypes = new Class<?>[elements + 2];
		final boolean[] classesEdit = new boolean[elements + 2];
		for (int i = 0; i < elements; i++) {

			classesEdit[i] = false;
			classesTypes[i] = Boolean.class;
		}
		classesEdit[elements] = false;
		classesTypes[elements] = String.class;
		classesEdit[elements + 1] = false;
		classesTypes[elements + 1] = String.class;

		javax.swing.table.DefaultTableModel newTableModelP = new javax.swing.table.DefaultTableModel(
				new Object[][] {}, new String[] {}) {
			private static final long serialVersionUID = -3760751300047576804L;
			Class<?>[] types = classesTypes;
			boolean[] canEdit = classesEdit;

			public Class<?> getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		};

		int sum = 0;
		ArrayList<Integer> stackC = new ArrayList<Integer>(elements);
		for (int i = 0; i < elements; i++)
			stackC.add(new Integer(0));

		if (op != null) {

			ArrayList<String> stackP = new ArrayList<String>(patterns);
			ArrayList<Integer> stackV = new ArrayList<Integer>(patterns);
			for (java.util.Map.Entry<String, Stats> entry : op
					.getLearnedPatterns().entrySet()) {

				int occurrences = entry.getValue().getOccurrences();

				boolean isInserted = false;
				for (int i = 0; i < stackP.size(); i++) {

					if (occurrences > stackV.get(i)) {

						stackP.add(i, entry.getKey());
						stackV.add(i, occurrences);
						isInserted = true;
						break;
					}
				}
				if (!isInserted) {

					stackP.add(entry.getKey());
					stackV.add(occurrences);
				}

				sum += occurrences;
			}

			Boolean[][] p = new Boolean[elements][patterns];
			String[][] m = new String[2][patterns];

			for (int i = 0; i < stackP.size(); i++) {

				String code = stackP.get(i);
				int occurrences = stackV.get(i);

				float percentage = Math.round((float) occurrences / (float) sum
						* 10000) / 100f;

				for (int j = 0; j < elements; j++) {

					if (code.charAt(j) == '1') {

						p[j][i] = true;

						Integer temp = stackC.remove(j);
						temp += occurrences;
						stackC.add(j, temp);
					} else
						p[j][i] = false;
				}
				m[0][i] = " " + String.valueOf(occurrences);
				m[1][i] = percentage + "%";
			}

			for (int i = 0; i < elements; i++)
				newTableModelP.addColumn("", p[i]);

			newTableModelP.addColumn("", m[0]);
			newTableModelP.addColumn("", m[1]);
		}

		final TableCellRenderer headerRenderer = new VerticalTableHeaderCellRenderer();

		this.patterns.setModel(newTableModelP);

		if (elements > 0) {

			for (int i = 0; i < elements + 2; i++) {

				TableColumn column = this.patterns.getColumnModel()
						.getColumn(i);

				if (i < elements) {

					String headerValue = " "
							+ this.convertID(keys.get(String.valueOf(op
									.getElements().get(i))));

					if (headerValue.length() > 20)
						headerValue = headerValue.substring(0, 17) + "...";

					column.setMinWidth(20);
					column.setMaxWidth(20);
					column.setHeaderValue(headerValue);
				}
				column.setHeaderRenderer(headerRenderer);
			}
			TableColumn column1 = this.patterns.getColumnModel().getColumn(
					elements);
			TableColumn column2 = this.patterns.getColumnModel().getColumn(
					elements + 1);

			column1.setMinWidth(60);
			column1.setMaxWidth(60);
			column2.setMinWidth(50);
			column2.setMaxWidth(50);

			this.patterns.getTableHeader().setBackground(Color.WHITE);
		}

		// ------------------

		this.connections = new JTable();

		this.connections.setGridColor(Color.GRAY);
		this.connections.setBackground(Color.WHITE);
		this.connections.setSelectionBackground(Color.LIGHT_GRAY);
		this.connections.setSelectionForeground(Color.DARK_GRAY);
		this.connections.setShowVerticalLines(false);

		javax.swing.table.DefaultTableModel newTableModelC = new javax.swing.table.DefaultTableModel(
				new Object[][] {}, new String[] {}) {
			private static final long serialVersionUID = -9201456440598163559L;
			Class<?>[] types = new Class<?>[] { String.class, String.class,
					String.class };
			boolean[] canEdit = new boolean[] { false, false, false };

			public Class<?> getColumnClass(int columnIndex) {
				return types[columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		};

		if (op != null) {

			ArrayList<Integer> stackI = new ArrayList<Integer>(elements);
			for (int i = 0; i < stackC.size(); i++) {

				int value = stackC.get(i);

				boolean isInserted = false;
				for (int j = 0; j < stackI.size(); j++) {

					int temp = stackC.get(stackI.get(j));

					if (value > temp) {

						stackI.add(j, new Integer(i));
						isInserted = true;
						break;
					}
				}
				if (!isInserted) {

					stackI.add(new Integer(i));
				}
			}

			String[][] m = new String[3][elements];

			for (int i = 0; i < stackC.size(); i++) {

				int element = op.getElements().get(stackI.get(i));
				String elementID = this.convertID(keys.get(String
						.valueOf(element)));
				int occurrences = stackC.get(stackI.get(i));

				float percentage = Math.round((float) occurrences / (float) sum
						* 10000) / 100f;

				m[0][i] = elementID;
				m[1][i] = String.valueOf(occurrences);
				m[2][i] = percentage + "%";
			}

			for (int i = 0; i < 3; i++)
				newTableModelC.addColumn("", m[i]);
		}

		this.connections.setModel(newTableModelC);

		if (op != null) {

			TableColumn column1 = this.connections.getColumnModel()
					.getColumn(1);
			TableColumn column2 = this.connections.getColumnModel()
					.getColumn(2);

			column1.setMinWidth(60);
			column1.setMaxWidth(60);
			column2.setMinWidth(50);
			column2.setMaxWidth(50);
		}

		// ------------------

		if (this.perspective.getSelectedIndex() == 1)
			this.annotationsScroll.setViewportView(this.patterns);
		else
			this.annotationsScroll.setViewportView(this.connections);
	}

	public void update(Operator op, String opID, HashMap<String, String> keys) {

		if (!this.opID.equals(opID)) {

			this.opID = opID;

			String text = "";
			if (op instanceof Split)
				text = "Outputs of " + this.convertID(opID);
			else
				text = "Inputs of " + this.convertID(opID);

			this.title.setText(text);
			this.title.setToolTipText(text);

			if (op.getLearnedPatterns().isEmpty()) {

				this.none.setVisible(true);
				// this.perspective.setVisible(false);
				this.perspective.setEnabled(false);
				// this.metric.setVisible(false);
				this.metric.setEnabled(false);
				this.annotationsScroll.setVisible(false);
				this.annotationsScroll.setEnabled(false);
			} else {

				this.none.setVisible(false);
				// this.perspective.setVisible(true);
				this.perspective.setEnabled(true);
				// this.metric.setVisible(true);
				this.metric.setEnabled(true);
				this.annotationsScroll.setVisible(true);
				this.annotationsScroll.setEnabled(true);

				this.initTables(op, keys);
			}
		}
	}

	private String convertID(String id) {

		int index = id.indexOf("+");

		if (index == -1) {
			return id; 
		}
		else {
			return id.substring(0, index) + " (" + id.substring(index + 1) + ")";
		}
	}
}

// class OptionsPanel extends JPanel {
//	
// private final JLabel title;
//	
// public OptionsPanel(SlickerFactory factory, SlickerDecorator decorator){
//		
// this.setLayout(null);
//		
// this.title = factory.createLabel("Options");
// this.title.setForeground(Color.DARK_GRAY);
// this.title.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
// }
// }

class SetupPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8408305033071764421L;
	private final AnnotatedVisualizationSettings settings;
	private boolean hasChanged;

	// ----------------------------------

	private JPanel nodesPanel, edgesPanel;
	private JLabel nodesTitle, edgesTitle;

	private JLabel n1, n2, n3;
	private JLabel e1, e2, e3;

	private JCheckBox nShowUT, nColor;
	private JComboBox nMeasure;

	private JCheckBox eColor;
	private JComboBox eMeasure, eStyle;

	public SetupPanel(SlickerFactory factory, SlickerDecorator decorator,
			final AnnotatedVisualizationSettings settings) {

		this.hasChanged = false;
		this.settings = settings;

		this.setLayout(null);

		this.nodesTitle = factory.createLabel("Events");
		this.nodesTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		this.nodesTitle.setForeground(new Color(40, 40, 40));

		this.nodesPanel = factory.createRoundedPanel(15, Color.gray);
		this.nodesPanel.setLayout(null);

		this.n1 = factory.createLabel("Show unconnected tasks:");
		this.n1.setHorizontalAlignment(SwingConstants.RIGHT);
		this.n1.setForeground(new Color(40, 40, 40));
		this.n2 = factory.createLabel("Color scaling:");
		this.n2.setHorizontalAlignment(SwingConstants.RIGHT);
		this.n2.setForeground(new Color(40, 40, 40));
		this.n3 = factory.createLabel("Measure:");
		this.n3.setHorizontalAlignment(SwingConstants.RIGHT);
		this.n3.setForeground(new Color(40, 40, 40));

		this.nShowUT = new JCheckBox();
		this.nShowUT.setSelected(settings.isShowingUnconnectedTasks());
		this.nShowUT.setBackground(Color.GRAY);
		this.nShowUT.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setShowingUnconnectedTasks(nShowUT.isSelected());
			}
		});

		this.nColor = new JCheckBox();
		this.nColor.setSelected(settings.isColorScalingEvents());
		this.nColor.setBackground(Color.GRAY);
		this.nColor.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setColorScalingEvents(nColor.isSelected());
			}
		});
		this.nMeasure = factory.createComboBox(new String[] { "None",
				"End Counter", "Frequency", "Start Counter" });
		this.nMeasure.setSelectedItem(settings.getMeasureEvents());
		this.nMeasure.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setMeasureEvents((String) nMeasure.getSelectedItem());
			}
		});

		this.nodesPanel.add(this.nodesTitle);
		this.nodesPanel.add(this.n1);
		this.nodesPanel.add(this.n2);
		this.nodesPanel.add(this.n3);
		this.nodesPanel.add(this.nShowUT);
		this.nodesPanel.add(this.nColor);
		this.nodesPanel.add(this.nMeasure);

		this.nodesPanel.setBounds(0, 0, 315, 150);
		this.nodesTitle.setBounds(10, 10, 100, 30);
		this.n1.setBounds(20, 50, 150, 20);
		this.n2.setBounds(20, 80, 150, 20);
		this.n3.setBounds(20, 110, 150, 20);
		this.nShowUT.setBounds(175, 50, 25, 20);
		this.nColor.setBounds(175, 80, 25, 20);
		this.nMeasure.setBounds(175, 110, 120, 20);

		// -------------------------------------------

		this.edgesTitle = factory.createLabel("Transitions");
		this.edgesTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD,
				18));
		this.edgesTitle.setForeground(new Color(40, 40, 40));

		this.e1 = factory.createLabel("Color scaling:");
		this.e1.setHorizontalAlignment(SwingConstants.RIGHT);
		this.e1.setForeground(new Color(40, 40, 40));
		this.e2 = factory.createLabel("Measure:");
		this.e2.setHorizontalAlignment(SwingConstants.RIGHT);
		this.e2.setForeground(new Color(40, 40, 40));
		this.e3 = factory.createLabel("Line style:");
		this.e3.setHorizontalAlignment(SwingConstants.RIGHT);
		this.e3.setForeground(new Color(40, 40, 40));

		this.eColor = new JCheckBox();
		this.eColor.setSelected(settings.isColorScalingTransitions());
		this.eColor.setBackground(Color.GRAY);
		this.eColor.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setColorScalingTransitions(eColor.isSelected());
			}
		});
		this.eMeasure = factory.createComboBox(new String[] { "None",
				"Dependency", "Frequency" });
		this.eMeasure.setSelectedItem(settings.getMeasureTransitions());
		this.eMeasure.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setMeasureTransitions((String) eMeasure
						.getSelectedItem());
			}
		});
		this.eStyle = factory.createComboBox(new String[] { "Beizer",
				"Orthogonal", "Spline" });
		this.eStyle.setSelectedItem(settings.getLineStyle());
		this.eStyle.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				hasChanged = true;
				settings.setLineStyle((String) eStyle.getSelectedItem());
			}
		});

		this.edgesPanel = factory.createRoundedPanel(15, Color.gray);
		this.edgesPanel.setLayout(null);

		this.edgesPanel.add(this.edgesTitle);
		this.edgesPanel.add(this.e1);
		this.edgesPanel.add(this.e2);
		this.edgesPanel.add(this.e3);
		this.edgesPanel.add(this.eColor);
		this.edgesPanel.add(this.eMeasure);
		this.edgesPanel.add(this.eStyle);

		this.edgesPanel.setBounds(0, 160, 315, 150);
		this.edgesTitle.setBounds(10, 10, 100, 30);
		this.e1.setBounds(20, 50, 150, 20);
		this.e2.setBounds(20, 80, 150, 20);
		this.e3.setBounds(20, 110, 150, 20);
		this.eColor.setBounds(175, 50, 25, 20);
		this.eMeasure.setBounds(175, 80, 120, 20);
		this.eStyle.setBounds(175, 110, 120, 20);

		this.add(this.nodesPanel);
		this.add(this.edgesPanel);

		this.setBackground(Color.LIGHT_GRAY);
	}

	public AnnotatedVisualizationSettings getSettings() {

		this.hasChanged = false;

		return this.settings;
	}

	public boolean hasChanged() {
		return this.hasChanged;
	}
}

class VerticalTableHeaderCellRenderer extends
		javax.swing.table.DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5792846837172592507L;

	public VerticalTableHeaderCellRenderer() {

		setOpaque(false);

		setHorizontalAlignment(LEFT);
		setHorizontalTextPosition(CENTER);
		setVerticalAlignment(CENTER);
		setVerticalTextPosition(TOP);
		setUI(new VerticalLabelUI(false));
	}

	protected javax.swing.Icon getIcon(JTable table, int column) {

		javax.swing.RowSorter.SortKey sortKey = getSortKey(table, column);
		if (sortKey != null && sortKey.getColumn() == column) {
			javax.swing.SortOrder sortOrder = sortKey.getSortOrder();
			switch (sortOrder) {
			case ASCENDING:
				return VerticalSortIcon.ASCENDING;
			case DESCENDING:
				return VerticalSortIcon.DESCENDING;
			}
		}
		return null;
	}

	private enum VerticalSortIcon implements javax.swing.Icon {

		ASCENDING, DESCENDING;
		private javax.swing.Icon icon = javax.swing.UIManager
				.getIcon("Table.ascendingSortIcon");

		/**
		 * Paints an icon suitable for the header of a sorted table column,
		 * rotated by 90 degrees clockwise. This rotation is applied to compensate the
		 * rotation already applied to the passed in Graphics reference by the
		 * VerticalLabelUI.
		 * <P>
		 * The icon is retrieved from the UIManager to obtain an icon
		 * appropriate to the L&F.
		 * 
		 * @param c
		 *            the component to which the icon is to be rendered
		 * @param g
		 *            the graphics context
		 * @param x
		 *            the X coordinate of the icon's top-left corner
		 * @param y
		 *            the Y coordinate of the icon's top-left corner
		 */
		public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
			switch (this) {
			case ASCENDING:
				icon = javax.swing.UIManager.getIcon("Table.ascendingSortIcon");
				break;
			case DESCENDING:
				icon = javax.swing.UIManager
						.getIcon("Table.descendingSortIcon");
				break;
			}
			int maxSide = Math.max(getIconWidth(), getIconHeight());
			Graphics2D g2 = (Graphics2D) g.create(x, y, maxSide, maxSide);
			g2.rotate((Math.PI / 2));
			g2.translate(0, -maxSide);
			icon.paintIcon(c, g2, 0, 0);
			g2.dispose();
		}

		/**
		 * Returns the width of the rotated icon.
		 * 
		 * @return the <B>height</B> of the contained icon
		 */
		public int getIconWidth() {
			return icon.getIconHeight();
		}

		/**
		 * Returns the height of the rotated icon.
		 * 
		 * @return the <B>width</B> of the contained icon
		 */
		public int getIconHeight() {
			return icon.getIconWidth();
		}
	}

	protected javax.swing.RowSorter.SortKey getSortKey(JTable table, int column) {
		javax.swing.RowSorter<?> rowSorter = table.getRowSorter();
		if (rowSorter == null) {
			return null;
		}

		List<?> sortedColumns = rowSorter.getSortKeys();
		if (sortedColumns.size() > 0) {
			return (javax.swing.RowSorter.SortKey) sortedColumns.get(0);
		}
		return null;
	}

	@Override
	public java.awt.Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		setIcon(getIcon(table, column));
		setBorder(null);
		return this;
	}
}

class DashedBorder extends javax.swing.border.LineBorder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1357931293759243135L;

	public DashedBorder(Color color) {
		super(color);
	}

	public void paintBorder(java.awt.Component comp, Graphics g, int x1,
			int x2, int y1, int y2) {

		Stroke old = ((Graphics2D) g).getStroke();
		BasicStroke bs = new BasicStroke(5.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, new float[] { 15.0f, 30.0f },
				2.0f);
		((Graphics2D) g).setStroke(bs);
		super.paintBorder(comp, g, x1, x2, y1, y2);
		((Graphics2D) g).setStroke(old);
	}
}
