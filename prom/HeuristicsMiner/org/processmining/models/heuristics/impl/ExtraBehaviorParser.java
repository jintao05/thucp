package org.processmining.models.heuristics.impl;

import java.util.Iterator;
import java.util.Random;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.heuristics.HeuristicsNet;

/**
 * Replays log traces in <code>HeuristicsNet</code> objects using a continuous
 * semantics (see {@link ContinuousSemanticsParser} for more details).
 * Additionally, this parser keeps track of how many activities are enabled
 * during the log replay.
 * 
 * @author Ana Karla Alves de Medeiros
 */

public class ExtraBehaviorParser extends ContinuousSemanticsParser {

	private int numTotalEnabledElements = 0;

	/**
	 * Constructs a <code>ExtraBehaviorParser</code> object. This object will
	 * replay log traces in the given <code>HeuristicsNet</code> object. The
	 * random aspect of this replaying process is based on the provided
	 * <code>Random</code> generator
	 * 
	 * @param net
	 *            heuristics net object
	 * @param generator
	 *            random generator used during the parsing of log traces
	 */
	public ExtraBehaviorParser(HeuristicsNet net, Random generator) {
		super(net, generator);
	}

	/**
	 * Resets the current internal structures of this parser to their initial
	 * states.
	 */
	@Override
	protected void reset() {
		super.reset();
		numTotalEnabledElements = 0;
	}

	@Override
	public boolean parse(XTrace trace) {
		int numTokens = 0;
		XEventClass element = null;
		int parsingTaskAtPosition = 0;

		reset();

		Iterator<XEvent> iterator = trace.iterator();
		while (iterator.hasNext()) {
			XEvent event = iterator.next();
			element = xEventClassesInHeuristicsNet.getClassOf(event);
			numTotalEnabledElements += marking.getCurrentNumEnabledElements();
			try {
				numTokens = marking.fire(element, trace, parsingTaskAtPosition);
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

			parsingTaskAtPosition++;

		}

		numUnparsedElements = trace.size() - numParsedElements;

		if (disabledElements.size() > 0) { //checking if there were missing tokens
			//I needed to use the variable 'disabledElements' because the marking.properlyCompleted()
			//will not work when one of the exceptions in the try/catch abore are raised.
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
	 * Retrieves that total number of enabled activities during the log replay
	 * 
	 * @return number of enabled activities during log replay
	 */
	public int getNumTotalEnabledElements() {
		return numTotalEnabledElements;
	}

}
