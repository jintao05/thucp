package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deckfour.xes.classification.XEventClassifier;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class ClassifiersPanel extends JPanel {

    private static final long serialVersionUID = -8450553056044622482L;

//	TODO: typed combo box generates error on build server (== JAVA 6)
//	private JComboBox<XEventClassifier> eventClassComboBox;

    public JComboBox<XEventClassifier> eventClassComboBox;

    @SuppressWarnings("unchecked")
    public ClassifiersPanel(Collection<XEventClassifier> options) {
        // store the input options (i.e. classifiers) as an array and sort it.
        Object[] optionsArr = options.toArray();
        Arrays.sort(optionsArr);
        this.eventClassComboBox = SlickerFactory.instance().createComboBox(optionsArr);

        this.setOpaque(false);
        this.setLayout(new GridBagLayout());

        // create a label
        JLabel label = SlickerFactory.instance().createLabel("Please select classifier to use:");

        // create poitioning constraints
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;

        this.add(label, c);

        c.gridy = 1;
        this.add(eventClassComboBox,c);
    }

    public XEventClassifier eventClassComboBoxValue() {
        return this.eventClassComboBox.getItemAt(this.eventClassComboBox.getSelectedIndex());
    }

}