package org.processmining.models.heuristics.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.heuristics.HeuristicsNet;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * Replays log traces in <code>HeuristicsNet</code> objects using a stop
 * semantics. This means that the parsing stops whenever activities are not
 * enabled during the log replay.
 * 
 * @author Ana Karla Alves de Medeiros
 */
public class StopSemanticsParser implements HeuristicsNetParser {

	private MarkingHeuristicsNet marking = null;
	protected Set<XEventClass> disabledElements = null;
	private XEventClasses xEventClassesInHeuristicsNet = null;
	private int numParsedElements = 0;
	private int numUnparsedElements = 0;
	private boolean properlyCompleted = false;
	private boolean completed = false;
	HeuristicsNet net = null;

	/**
	 * Constructs a <code>StopSemanticsParser</code> object. This object will
	 * replay log traces in the given <code>HeuristicsNet</code> object. The
	 * random aspect of this replaying process is based on the provided
	 * <code>Random</code> generator
	 * 
	 * @param net
	 *            heuristics net object
	 * @param generator
	 *            random generator used during the parsing of log traces
	 */
	public StopSemanticsParser(HeuristicsNet net, Random generator) {
		marking = new MarkingHeuristicsNet(net, generator);
		disabledElements = new HashSet<XEventClass>();
		xEventClassesInHeuristicsNet = net.getActivitiesMappingStructures().getXEventClasses();
		this.net = net;
	}

	private void reset() {
		marking.reset();
		disabledElements = null;
		disabledElements = new HashSet<XEventClass>();
		numParsedElements = 0;
		numUnparsedElements = 0;
		properlyCompleted = false;
		completed = false;
	}

	public boolean parse(XTrace trace) {
		XEventClass element = null;
		int numTokens = 0;
		int parsingTaskAtPosition = 0;
		DoubleMatrix2D arcUsageBeforeFiring = null;
		int[] duplicatesActualFiringBeforeFiring = null;

		reset();

		Iterator<XEvent> iterator = trace.iterator();
		while (iterator.hasNext()) {
			XEvent event = iterator.next();
			element = xEventClassesInHeuristicsNet.getClassOf(event);

			try {
				arcUsageBeforeFiring = net.getArcUsage();
				duplicatesActualFiringBeforeFiring = net.getActivitiesActualFiring();
				numTokens = marking.fire(element, trace, parsingTaskAtPosition);
			} catch (ArrayIndexOutOfBoundsException exc) {
				//The searched element does not exist in the individual.
				//This may happen when importing logs and not associating all
				//ATEentries to an element in the individual.
				numTokens++;
			} catch (IllegalArgumentException exc) {
				//The searched element does not exist in the individual.
				//This may happen when importing logs and not associating all
				//tasks in the log with tasks in the individual.
				numTokens++;
			} catch (NullPointerException exc) {
				//The searched element does not exist in the individual.
				//This may happen when importing logs and not associating all
				//tasks in the log with tasks in the individual.
				numTokens++;
			}

			parsingTaskAtPosition++;
			if (numTokens > 0) {
				disabledElements.add(element);
				numUnparsedElements = trace.size() - numParsedElements;
				//because the element was not enabled, we need to restore the "arc usage"
				//and "actual firing" to the values before the firing of element
				net.setArcUsage(arcUsageBeforeFiring);
				net.setActivitiesActualFiring(duplicatesActualFiringBeforeFiring);
				return false;
			}
			numParsedElements++;

		}

		properlyCompleted = marking.properlyCompleted();
		completed = true;

		return true;
	}

	/**
	 * Retrieves the set of event classes that were not enabled during the log
	 * replay
	 * 
	 * @return set of events that were disabled during log replay
	 */
	public Set<XEventClass> getDisabledElements() {
		return disabledElements;
	}

	/**
	 * Retrieves the number of event classes that were not enabled during the
	 * log replay
	 * 
	 * @return number of disabled activities
	 */
	public int getSizeDisabledElements() {
		return disabledElements.size();
	}

	/**
	 * Retrieves the number of activities in a log that could not be parsed
	 * without any problems
	 * 
	 * @return number of activities with parsing problems
	 */
	public int getNumUnparsedElements() {
		return numUnparsedElements;
	}

	/**
	 * Retrieves the number of activities in a log that could be parsed without
	 * any problems
	 * 
	 * @return number of activities without parsing problems
	 */
	public int getNumParsedElements() {
		return numParsedElements;
	}

	/**
	 * Indicates if the last trace to be replayed has properly completed (i.e.,
	 * no tokens are left behind and no problems were encountered during the log
	 * replay).
	 * 
	 * @return <code>true</code> if the trace replay completes properly,
	 *         <code>false</code> otherwise.
	 */
	public boolean getProperlyCompleted() {
		return properlyCompleted;
	}

	/**
	 * Indicates if the last trace to be replayed has completed (i.e., no
	 * problems where encountered during the log replay).
	 * 
	 * @return <code>true</code> if the trace replay completes,
	 *         <code>false</code> otherwise.
	 */
	public boolean getCompleted() {
		return completed;
	}

}
