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

/**
 * Replays log traces in <code>HeuristicsNet</code> objects using a continuous
 * semantics. This means that the parsing process proceeds even when activities
 * are not enabled during the log replay. Instead, any problems encountered
 * during the parsing are registered and can be retrieved via the various "get"
 * methods that are provided in this class.
 * 
 * @author Ana Karla Alves de Medeiros
 */

public class ContinuousSemanticsParser implements HeuristicsNetParser {

	protected MarkingHeuristicsNet marking = null;
	protected Set<XEventClass> disabledElements = null;
	protected int numUnparsedElements = 0;
	protected XEventClasses xEventClassesInHeuristicsNet = null;
	protected int numParsedElements = 0;
	protected int numMissingTokens = 0;
	protected int numExtraTokensLeftBehind = 0;

	////////////////////////////////
	protected int traceSize = 0;

	protected boolean properlyCompleted = false;

	/**
	 * Constructs a <code>ContinuousSemanticsParser</code> object. This object
	 * will replay log traces in the given <code>HeuristicsNet</code> object.
	 * The random aspect of this replaying process is based on the provided
	 * <code>Random</code> generator
	 * 
	 * @param net
	 *            heuristics net object
	 * @param generator
	 *            random generator used during the parsing of log traces
	 */
	public ContinuousSemanticsParser(HeuristicsNet net, Random generator) {
		marking = new MarkingHeuristicsNet(net, generator);
		disabledElements = new HashSet<XEventClass>();
		xEventClassesInHeuristicsNet = net.getActivitiesMappingStructures().getXEventClasses();
	}

	/**
	 * Resets this parser object back to its initial state.
	 * 
	 */
	protected void reset() {
		marking.reset();
		disabledElements = new HashSet<XEventClass>();
		properlyCompleted = false;
		numParsedElements = 0;
		numUnparsedElements = 0;
		numMissingTokens = 0;
		numExtraTokensLeftBehind = 0;

	}

	public boolean parse(XTrace trace) {
		int numTokens = 0;
		XEventClass element = null;
		int parsingActivityAtPosition = 0;

		reset();

		Iterator<XEvent> iterator = trace.iterator();
		while (iterator.hasNext()) {
			XEvent event = iterator.next();
			element = xEventClassesInHeuristicsNet.getClassOf(event);

			try {
				numTokens = marking.fire(element, trace, parsingActivityAtPosition);
				if (numTokens > 0) {
					numMissingTokens += numTokens;
					disabledElements.add(element);
				} else {
					numParsedElements++;
				}

			} catch (ArrayIndexOutOfBoundsException exc) {
				//The searched element does not exist in the individual.
				//This may happen when importing logs and not associating all
				//ATEentries to an element in the individual.
				registerProblemWhileParsing(element);
			} catch (IllegalArgumentException exc) {
				//The searched element does not exist in the individual.
				//This may happen when importing logs and not associating all
				//tasks in the log with tasks in the individual.
				registerProblemWhileParsing(element);
			} catch (NullPointerException exc) {
				//The searched element does not exist in the individual.
				//This may happen when importing logs and not associating all
				//tasks in the log with tasks in the individual.
				registerProblemWhileParsing(element);
			}

			parsingActivityAtPosition++;
		}

		numUnparsedElements = trace.size() - numParsedElements;

		if (disabledElements.size() > 0) { //checking if there were missing tokens
			//I needed to use the variable 'disabledElements' because the marking.properlyCompleted()
			//will not work when one of the exceptions in the try/catch above are raised.
			properlyCompleted = false;
		} else { //checking if tokens are left behind...
			properlyCompleted = marking.properlyCompleted();
		}
		if (marking.endPlace()) {
			numExtraTokensLeftBehind = marking.getNumberTokens() - 1;
			//note that the extra tokens in the end place are also counted.
		} else {
			numExtraTokensLeftBehind = marking.getNumberTokens();
		}

		return true;
	}

	/**
	 * Registers parsing problems for activities that are part of the
	 * <code>HeuristicsNet</code> object connected to this
	 * <code>ContinuousSemantics</code> parser.
	 * 
	 * @param element
	 *            log event that could not be parsed.
	 */
	protected void registerProblemWhileParsing(XEventClass element) {
		numMissingTokens++;
		disabledElements.add(element);
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
	 * Retrieves the number of missing tokens during the replay process
	 * 
	 * @return number of missing tokens
	 */
	public int getNumMissingTokens() {
		return numMissingTokens;
	}

	/**
	 * Retrieves number of tokens that have been left behind after parsing log
	 * traces
	 * 
	 * @return number of unused tokens after completely parsing traces
	 */
	public int getNumExtraTokensLeftBehind() {
		return numExtraTokensLeftBehind;
	}

	/**
	 * Indicates if the last trace to be replayed has properly completed (i.e.,
	 * no tokens are left behind).
	 * 
	 * @return <code>true</code> if the trace replay completes properly,
	 *         <code>false</code> otherwise.
	 */
	public boolean getProperlyCompleted() {
		return properlyCompleted;
	}

	/**
	 * Retrieves the number of activities in the log that could be parsed
	 * without problems
	 * 
	 * @return number of activities in the log that could be successfully parsed
	 */

	public int getNumParsedElements() {
		return numParsedElements;
	}

}
