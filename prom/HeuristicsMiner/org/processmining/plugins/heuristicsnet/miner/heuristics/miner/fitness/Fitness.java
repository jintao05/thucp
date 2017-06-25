package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness;

import org.processmining.models.heuristics.HeuristicsNet;


/**
 * Interface of classes to calculate the fitness of <code>HeuristcNet</code>
 * objects in a given population.
 * 
 * @author Ana Karla Alves de Medeiros
 */

public interface Fitness {

	/**
	 * Calculates the fitness of every <code>HeuristicsNet</code> (i.e., individuals) in a population.
	 * @param population array containing various <code>HeuristicsNet</code>.
	 * @return array containing <code>HeuristicsNet</code> with updated fitness values.
	 */
	public HeuristicsNet[] calculate(HeuristicsNet[] population);

}
