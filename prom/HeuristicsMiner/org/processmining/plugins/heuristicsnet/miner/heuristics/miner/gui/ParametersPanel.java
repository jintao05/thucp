package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.gui;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;

import com.fluxicon.slickerbox.components.NiceIntegerSlider;
import com.fluxicon.slickerbox.components.NiceSlider.Orientation;
import com.fluxicon.slickerbox.factory.SlickerDecorator;
import com.fluxicon.slickerbox.factory.SlickerFactory;

public class ParametersPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6804610998661748174L;

	private HeuristicsMinerSettings settings;
	
	private JPanel thresholdsPanel, heuristicsPanel;
	
	private JLabel thresholdTitle, heuristicsTitle;
	/*
	 * [HV] Label l9 added for Ticket #3037.
	 */
	private JLabel l1, l2, l3, l4, l5, l6, l7, l8, l9;
	//private NiceIntegerSlider t1, t2, t3, t4, t5, t6;
	private NiceIntegerSlider t6;
	private JSlider doubleSlider1, doubleSlider2, logarithmicSlider1, logarithmicSlider2, doubleSlider3;
	/*
	 * [HV] Check box c3 added for Ticket #3037.
	 */
	private JCheckBox c1, c2, c3;

	public ClassifiersPanel classifiersPanel;
	
	public ParametersPanel() {
		
		this.settings = new HeuristicsMinerSettings();
		
		// Add fake classifier for parameters panel post-view
		HashSet<XEventClassifier> set = new HashSet<XEventClassifier>();
		XEventClassifier nameCl = new XEventNameClassifier();
        XEventClassifier lifeTransCl = new XEventLifeTransClassifier();
        XEventAttributeClassifier attrClass = new XEventAndClassifier(nameCl, lifeTransCl);
        set.add(attrClass);
		this.classifiersPanel = new ClassifiersPanel(set);
		this.init();
	}
	
	public ParametersPanel(Collection<XEventClassifier> classifiers){
		
		this.settings = new HeuristicsMinerSettings();
		this.classifiersPanel = new ClassifiersPanel(classifiers);
		this.init();
	}
	
	public ParametersPanel(HeuristicsMinerSettings settings){
		
		this.settings = settings;
		
		// Add fake classifier for parameters panel post-view
		HashSet<XEventClassifier> set = new HashSet<XEventClassifier>();
		XEventClassifier nameCl = new XEventNameClassifier();
		XEventClassifier lifeTransCl = new XEventLifeTransClassifier();
		XEventAttributeClassifier attrClass = new XEventAndClassifier(nameCl, lifeTransCl);
		set.add(attrClass);
		this.classifiersPanel = new ClassifiersPanel(set);
		this.init();
	}
	
	private double sliderValueFunction(int x, boolean logarithmic) {
		if (logarithmic) {
			//logarithmic scaling
			double result = Math.log((x + 0.001024) / 0.001024) / 0.160944;
			return new BigDecimal(result).setScale(3, RoundingMode.HALF_UP).doubleValue();
		}
		else {
			//normal scaling
			return x / 100.0;
		}		
	}
	
	private int sliderValueInverseFunction(double y, boolean logarithmic) {
		if (logarithmic) {
			//logarithmic scaling
			double result = 0.001024 + 0.001024 * (Math.expm1(y * 0.160944)+1);
			return (int) result;
		}
		else {
			//normal scaling
			return (int) (y * 100.0);
		}
	}
	
	private void init(){

		SlickerFactory factory = SlickerFactory.instance();
		SlickerDecorator decorator = SlickerDecorator.instance();
		
		this.thresholdsPanel = factory.createRoundedPanel(15, Color.gray);
		this.heuristicsPanel = factory.createRoundedPanel(15, Color.gray);
		
		this.thresholdTitle = factory.createLabel("Thresholds");
		this.thresholdTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
		this.thresholdTitle.setForeground(new Color(40,40,40));
		
		this.heuristicsTitle = factory.createLabel("Heuristics");
		this.heuristicsTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
		this.heuristicsTitle.setForeground(new Color(40,40,40));
		
		//this.t1 = factory.createNiceIntegerSlider("", 0, 100, (int) (this.settings.getRelativeToBestThreshold() * 100), Orientation.HORIZONTAL);
		//this.t2 = factory.createNiceIntegerSlider("", 0, 100, (int) (this.settings.getDependencyThreshold() * 100), Orientation.HORIZONTAL);
		//this.t3 = factory.createNiceIntegerSlider("", 0, 100, (int) (this.settings.getL1lThreshold() * 100), Orientation.HORIZONTAL);
		//this.t4 = factory.createNiceIntegerSlider("", 0, 100, (int) (this.settings.getL2lThreshold() * 100), Orientation.HORIZONTAL);
		//this.t5 = factory.createNiceIntegerSlider("", 0, 100, (int) (this.settings.getLongDistanceThreshold() * 100), Orientation.HORIZONTAL);
		this.t6 = factory.createNiceIntegerSlider("", 0, 100, (int) (this.settings.getAndThreshold() * 100), Orientation.HORIZONTAL);
		
		
		FlowLayout customLayout = new FlowLayout(FlowLayout.LEFT);
		customLayout.setHgap(2);
		int customPanelWidth = 405;
		JPanel doubleSliderPanel1 =  new JPanel(customLayout);
		doubleSliderPanel1.setBackground(Color.gray);
		JPanel doubleSliderPanel2 =  new JPanel(customLayout);
		doubleSliderPanel2.setBackground(Color.gray);
		JPanel logarithmicSliderPanel1 =  new JPanel(customLayout);
		logarithmicSliderPanel1.setBackground(Color.gray);
		JPanel logarithmicSliderPanel2 =  new JPanel(customLayout);
		logarithmicSliderPanel2.setBackground(Color.gray);
		JPanel doubleSliderPanel3 =  new JPanel(customLayout);
		doubleSliderPanel3.setBackground(Color.gray);
		
		final JLabel doubleSliderLabel1 = new JLabel(sliderValueFunction(500, false)+"");
		doubleSliderLabel1.setPreferredSize(new Dimension(60, 20));
		final JLabel doubleSliderLabel2 = new JLabel(sliderValueFunction(9000, false)+"");
		doubleSliderLabel2.setPreferredSize(new Dimension(60, 20));
		final JLabel logarithmicSliderLabel1 = new JLabel(sliderValueFunction(2000, true)+"");
		logarithmicSliderLabel1.setPreferredSize(new Dimension(60, 20));
		final JLabel logarithmicSliderLabel2 = new JLabel(sliderValueFunction(2000, true)+"");
		logarithmicSliderLabel2.setPreferredSize(new Dimension(60, 20));
		final JLabel doubleSliderLabel3 = new JLabel(sliderValueFunction(9000, false)+"");
		doubleSliderLabel3.setPreferredSize(new Dimension(60, 20));
		
		doubleSlider1 = new JSlider(0, 10000, 500);
		doubleSlider1.setPreferredSize(new Dimension(customPanelWidth - 60, 20));
		doubleSlider1.setBackground(Color.gray);
		doubleSlider1.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				String labelStr = String.valueOf(sliderValueFunction(doubleSlider1.getValue(), false));
				doubleSliderLabel1.setText(labelStr);
			}
		});
		doubleSlider2 = new JSlider(0, 10000, 9000);
		doubleSlider2.setPreferredSize(new Dimension(customPanelWidth - 60, 20));
		doubleSlider2.setBackground(Color.gray);
		doubleSlider2.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				String labelStr = String.valueOf(sliderValueFunction(doubleSlider2.getValue(), false));
				doubleSliderLabel2.setText(labelStr);
			}
		});
		logarithmicSlider1 = new JSlider(0, 10000, 2000);
		logarithmicSlider1.setPreferredSize(new Dimension(customPanelWidth - 60, 20));
		logarithmicSlider1.setBackground(Color.gray);
		logarithmicSlider1.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				String labelStr = String.valueOf(sliderValueFunction(logarithmicSlider1.getValue(), true));
				logarithmicSliderLabel1.setText(labelStr);
			}
		});
		logarithmicSlider2 = new JSlider(0, 10000, 2000);
		logarithmicSlider2.setPreferredSize(new Dimension(customPanelWidth - 60, 20));
		logarithmicSlider2.setBackground(Color.gray);
		logarithmicSlider2.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				String labelStr = String.valueOf(sliderValueFunction(logarithmicSlider2.getValue(), true));
				logarithmicSliderLabel2.setText(labelStr);
			}
		});
		doubleSlider3 = new JSlider(0, 10000, 9000);
		doubleSlider3.setPreferredSize(new Dimension(customPanelWidth - 60, 20));
		doubleSlider3.setBackground(Color.gray);
		doubleSlider3.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				String labelStr = String.valueOf(sliderValueFunction(doubleSlider3.getValue(), false));
				doubleSliderLabel3.setText(labelStr);
			}
		});
		
		doubleSliderPanel1.add(doubleSlider1);
		doubleSliderPanel1.add(doubleSliderLabel1);
		doubleSliderPanel2.add(doubleSlider2);
		doubleSliderPanel2.add(doubleSliderLabel2);
		logarithmicSliderPanel1.add(logarithmicSlider1);
		logarithmicSliderPanel1.add(logarithmicSliderLabel1);
		logarithmicSliderPanel2.add(logarithmicSlider2);
		logarithmicSliderPanel2.add(logarithmicSliderLabel2);
		doubleSliderPanel3.add(doubleSlider3);
		doubleSliderPanel3.add(doubleSliderLabel3);
		
		SlickerDecorator.instance().decorate(doubleSlider1);
		SlickerDecorator.instance().decorate(doubleSliderLabel1);
		doubleSliderLabel1.setForeground(new Color(50,50,50));
		SlickerDecorator.instance().decorate(doubleSlider2);
		SlickerDecorator.instance().decorate(doubleSliderLabel2);
		doubleSliderLabel2.setForeground(new Color(50,50,50));
		SlickerDecorator.instance().decorate(logarithmicSlider1);
		SlickerDecorator.instance().decorate(logarithmicSliderLabel1);
		logarithmicSliderLabel1.setForeground(new Color(50,50,50));
		SlickerDecorator.instance().decorate(logarithmicSlider2);
		SlickerDecorator.instance().decorate(logarithmicSliderLabel2);
		logarithmicSliderLabel2.setForeground(new Color(50,50,50));
		SlickerDecorator.instance().decorate(doubleSlider3);
		SlickerDecorator.instance().decorate(doubleSliderLabel3);
		doubleSliderLabel3.setForeground(new Color(50,50,50));
		
		
		this.l1 = factory.createLabel("Relative-to-best:");
		this.l1.setHorizontalAlignment(SwingConstants.RIGHT);
		this.l1.setForeground(new Color(40,40,40));
		this.l2 = factory.createLabel("Dependency:");
		this.l2.setHorizontalAlignment(SwingConstants.RIGHT);
		this.l2.setForeground(new Color(40,40,40));
		this.l3 = factory.createLabel("Length-one-loops:");
		this.l3.setHorizontalAlignment(SwingConstants.RIGHT);
		this.l3.setForeground(new Color(40,40,40));
		this.l4 = factory.createLabel("Length-two-loops:");
		this.l4.setHorizontalAlignment(SwingConstants.RIGHT);
		this.l4.setForeground(new Color(40,40,40));
		this.l5 = factory.createLabel("Long distance:");
		this.l5.setHorizontalAlignment(SwingConstants.RIGHT);
		this.l5.setForeground(new Color(40,40,40));
		this.l6 = factory.createLabel("AND splits");
		this.l6.setHorizontalAlignment(SwingConstants.RIGHT);
		this.l6.setForeground(new Color(40,40,40));
		this.l7 = factory.createLabel("All tasks connected:");
		this.l7.setHorizontalAlignment(SwingConstants.RIGHT);
		this.l7.setForeground(new Color(40,40,40));
		this.l8 = factory.createLabel("Long distance dependency:");
		this.l8.setHorizontalAlignment(SwingConstants.RIGHT);
		this.l8.setForeground(new Color(40,40,40));
		/*
		 * [HV] Check box c3 added for Ticket #3037.
		 */
		this.l9 = factory.createLabel("Ignore loop dependency thresholds:");
		this.l9.setHorizontalAlignment(SwingConstants.RIGHT);
		this.l9.setForeground(new Color(40,40,40));
		
		this.c1 = new JCheckBox();
		this.c1.setBackground(Color.GRAY);
		this.c1.setSelected(settings.isUseAllConnectedHeuristics());
		decorator.decorate(this.c1);
		this.c2 = new JCheckBox();
		this.c2.setBackground(Color.GRAY);
		this.c2.setSelected(settings.isUseLongDistanceDependency());
		decorator.decorate(this.c2);
		/*
		 * [HV] Check box c3 added for Ticket #3037.
		 */
		this.c3 = new JCheckBox();
		this.c3.setBackground(Color.GRAY);
		this.c3.setSelected(settings.isCheckBestAgainstL2L());
		decorator.decorate(this.c3);
				
		this.thresholdsPanel.setLayout(null);
		this.thresholdsPanel.add(this.thresholdTitle);
		this.thresholdsPanel.add(this.l1);
		//this.thresholdsPanel.add(this.t1);
		this.thresholdsPanel.add(doubleSliderPanel1);
		this.thresholdsPanel.add(this.l2);
		//this.thresholdsPanel.add(this.t2);
		this.thresholdsPanel.add(doubleSliderPanel2);
		this.thresholdsPanel.add(this.l3);
		//this.thresholdsPanel.add(this.t3);
		this.thresholdsPanel.add(logarithmicSliderPanel1);
		this.thresholdsPanel.add(this.l4);
		//this.thresholdsPanel.add(this.t4);
		this.thresholdsPanel.add(logarithmicSliderPanel2);
		this.thresholdsPanel.add(this.l5);
		//this.thresholdsPanel.add(this.t5);
		this.thresholdsPanel.add(doubleSliderPanel3);
		this.thresholdsPanel.add(this.l6);
		this.thresholdsPanel.add(this.t6);
		
		this.heuristicsPanel.setLayout(null);
		this.heuristicsPanel.add(this.heuristicsTitle);
		this.heuristicsPanel.add(this.l7);
		this.heuristicsPanel.add(this.c1);
		this.heuristicsPanel.add(this.l8);
		this.heuristicsPanel.add(this.c2);
		this.heuristicsPanel.add(this.l9);
		this.heuristicsPanel.add(this.c3);

		this.thresholdsPanel.setBounds(0, 50, 520, 240);
		this.thresholdTitle.setBounds(10, 10, 200, 30);
		this.l1.setBounds(25, 50, 100, 20);
		this.l2.setBounds(25, 80, 100, 20);
		this.l3.setBounds(20, 110, 105, 20);
		this.l4.setBounds(20, 140, 105, 20);
		this.l5.setBounds(25, 170, 100, 20);
		this.l6.setBounds(20, 200, 100, 20);
		//this.t1.setBounds(122, 50, 360, 20);
		//this.t2.setBounds(122, 80, 360, 20);
		//this.t3.setBounds(122, 110, 360, 20);
		//this.t4.setBounds(122, 140, 360, 20);
		//this.t5.setBounds(122, 170, 360, 20);
		this.t6.setBounds(122, 200, 360, 20);
		doubleSliderPanel1.setBounds(122, 45, customPanelWidth+20, 25);
		doubleSliderPanel2.setBounds(122, 75, customPanelWidth+20, 25);
		logarithmicSliderPanel1.setBounds(122, 105, customPanelWidth+20, 25);
		logarithmicSliderPanel2.setBounds(122, 135, customPanelWidth+20, 25);
		doubleSliderPanel3.setBounds(122, 165, customPanelWidth+20, 25);
		
		/*
		 * [HV] Increased size for Ticket #3037.
		 */
		this.heuristicsPanel.setBounds(0, 300, 520, 150);
		this.heuristicsTitle.setBounds(10, 10, 200, 30);
		this.l7.setBounds(20, 50, 260, 20);
		this.l8.setBounds(20, 80, 260, 20);
		/*
		 * [HV] Added for ticket #3037
		 */
		this.l9.setBounds(20, 110, 260, 20);
		this.c1.setBounds(282, 50, 25, 20);
		this.c2.setBounds(282, 80, 25, 20);
		/*
		 * [HV] Added for ticket #3037
		 */
		this.c3.setBounds(282, 110, 25, 20);
		
		this.setLayout(null);
		this.add(this.thresholdsPanel);
		this.add(this.heuristicsPanel);
		
		this.classifiersPanel.setBounds(0, 0, 520, 50);
		this.add(this.classifiersPanel);
		
		this.validate();
		this.repaint();
	}
	
	public void copySettings(HeuristicsMinerSettings settings){
		
		//this.t1.setValue((int) (settings.getRelativeToBestThreshold() * 100d));
		//this.t2.setValue((int) (settings.getDependencyThreshold() * 100d));
		//this.t3.setValue((int) (settings.getL1lThreshold() * 100d));
		//this.t4.setValue((int) (settings.getL2lThreshold() * 100d));
		//this.t5.setValue((int) (settings.getLongDistanceThreshold() * 100d));
		this.doubleSlider1.setValue(sliderValueInverseFunction(settings.getRelativeToBestThreshold() * 100d, false));
		this.doubleSlider2.setValue(sliderValueInverseFunction(settings.getDependencyThreshold() * 100d, false));
		this.logarithmicSlider1.setValue(sliderValueInverseFunction(settings.getL1lThreshold() * 100d, true));
		this.logarithmicSlider2.setValue(sliderValueInverseFunction(settings.getL2lThreshold() * 100d, true));
		this.doubleSlider3.setValue(sliderValueInverseFunction(settings.getLongDistanceThreshold() * 100d, false));
		this.t6.setValue((int) (settings.getAndThreshold() * 100d));
		this.c1.setSelected(settings.isUseAllConnectedHeuristics());
		this.c2.setSelected(settings.isUseLongDistanceDependency());
		/*
		 * [HV] Added for ticket #3037
		 */
		this.c3.setSelected(settings.isCheckBestAgainstL2L());
		
		this.classifiersPanel.eventClassComboBox.addItem(settings.getClassifier());
		this.classifiersPanel.eventClassComboBox.setSelectedItem(settings.getClassifier());
	}
	
	public HeuristicsMinerSettings getSettings(){ 
		
		//this.settings.setRelativeToBestThreshold(this.t1.getValue() / 100d);
		//this.settings.setDependencyThreshold(this.t2.getValue() / 100d);
		//this.settings.setL1lThreshold(this.t3.getValue() / 100d);
		//this.settings.setL2lThreshold(this.t4.getValue() / 100d);
		//this.settings.setLongDistanceThreshold(this.t5.getValue() / 100d);
		this.settings.setRelativeToBestThreshold(sliderValueFunction(doubleSlider1.getValue(), false) / 100d);
		this.settings.setDependencyThreshold(sliderValueFunction(doubleSlider2.getValue(), false) / 100d);
		this.settings.setL1lThreshold(sliderValueFunction(logarithmicSlider1.getValue(), true) / 100d);
		this.settings.setL2lThreshold(sliderValueFunction(logarithmicSlider2.getValue(), true) / 100d);
		this.settings.setLongDistanceThreshold(sliderValueFunction(doubleSlider3.getValue(), false) / 100d);
		this.settings.setAndThreshold(this.t6.getValue() / 100d);
		this.settings.setUseAllConnectedHeuristics(this.c1.isSelected());
		this.settings.setUseLongDistanceDependency(this.c2.isSelected());
		/*
		 * [HV] Added for ticket #3037
		 */
		this.settings.setCheckBestAgainstL2L(this.c3.isSelected());
		
		this.settings.setClassifier(classifiersPanel.eventClassComboBoxValue());
		
		return this.settings; 
	}
	
	public void setEnabled(boolean status){
		
		//this.t1.setEnabled(status);
		//this.t2.setEnabled(status);
		//this.t3.setEnabled(status);
		//this.t4.setEnabled(status);
		//this.t5.setEnabled(status);
		doubleSlider1.setEnabled(status);
		doubleSlider2.setEnabled(status);
		logarithmicSlider1.setEnabled(status);
		logarithmicSlider2.setEnabled(status);
		doubleSlider3.setEnabled(status);
		this.t6.setEnabled(status);
		this.c1.setEnabled(status);
		this.c2.setEnabled(status);
		/*
		 * [HV] Added for ticket #3037
		 */
		this.c3.setEnabled(status);
		
		this.classifiersPanel.eventClassComboBox.setEnabled(status);
	}
	
	public void removeAndThreshold(){
		
		this.thresholdsPanel.remove(this.l6);
		this.thresholdsPanel.remove(this.t6);
		
		this.thresholdsPanel.setBounds(0, 50, 520, 210);
		/*
		 * [HV] Increased size for Ticket #3037.
		 */
		this.heuristicsPanel.setBounds(0, 270, 520, 150);
		
		this.l6 = null;
	}
	
	public boolean hasAndThreshold(){ return (this.l6 != null); }
	
	public boolean equals(HeuristicsMinerSettings settings){
		
		boolean equals = false;
		
		//if(settings.getRelativeToBestThreshold() == (this.t1.getValue() / 100d)){
		if(settings.getRelativeToBestThreshold() == (sliderValueFunction(doubleSlider1.getValue(), false) / 100d)){
		
			//if(settings.getDependencyThreshold() == (this.t2.getValue() / 100d)){
			if(settings.getDependencyThreshold() == (sliderValueFunction(doubleSlider2.getValue(), false) / 100d)){
				
				//if(settings.getL1lThreshold() == (this.t3.getValue() / 100d)){
				if(settings.getL1lThreshold() == (sliderValueFunction(logarithmicSlider1.getValue(), true) / 100d)){
					
					//if(settings.getL2lThreshold() == (this.t4.getValue() / 100d)){
					if(settings.getL2lThreshold() == (sliderValueFunction(logarithmicSlider2.getValue(), true) / 100d)){
						
						//if(settings.getLongDistanceThreshold() == (this.t5.getValue() / 100d)){
						if(settings.getLongDistanceThreshold() == (sliderValueFunction(doubleSlider3.getValue(), false) / 100d)){
							
							if(settings.getAndThreshold() == (this.t6.getValue() / 100d)) { 
								
								if (settings.getClassifier() == (this.classifiersPanel.eventClassComboBoxValue())) {
									equals = true;
								}
							}
						}
					}
				}
			}
		}
		
		if(equals){
			
			if(settings.isUseAllConnectedHeuristics() != this.c1.isSelected()) equals = false;
			else if(settings.isUseLongDistanceDependency() != this.c2.isSelected()) equals = false;
		}
		
		return equals;
	}
}
