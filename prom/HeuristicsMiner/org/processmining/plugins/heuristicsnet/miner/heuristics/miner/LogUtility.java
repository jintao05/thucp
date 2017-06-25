package org.processmining.plugins.heuristicsnet.miner.heuristics.miner;

import java.util.Collection;
import java.util.HashSet;

import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;

public class LogUtility {
	
    public static Collection<XEventClassifier> getEventClassifiers(XLog log) {

        Collection<XEventClassifier> result = new HashSet<XEventClassifier>();
        result.addAll(log.getClassifiers());

        if (result.isEmpty()) {
            XEventClassifier nameCl = new XEventNameClassifier();
            XEventClassifier lifeTransCl = new XEventLifeTransClassifier();
            XEventAttributeClassifier attrClass = new XEventAndClassifier(nameCl, lifeTransCl);
            result.add(attrClass);
        }

        return result;
    }
    
    
}
