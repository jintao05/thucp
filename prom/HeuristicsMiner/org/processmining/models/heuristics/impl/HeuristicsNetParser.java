package org.processmining.models.heuristics.impl;

import org.deckfour.xes.model.XTrace;

/**
 * Interface of the parsers for <code>HeuristicsNet</code> objects.
 * 
 * @author Peter van den Brand and Ana Karla Alves de Medeiros
 */

public interface HeuristicsNetParser {
	/**
	 * Replays a log trace in a <code>HeuristicNet</code> object.
	 * 
	 * @param trace
	 *            log trace to be replayed (or parsed).
	 * @return <code>true</code> if the trace could be replayed,
	 *         <code>false</code> otherwise.
	 */
	public boolean parse(XTrace trace);

}
