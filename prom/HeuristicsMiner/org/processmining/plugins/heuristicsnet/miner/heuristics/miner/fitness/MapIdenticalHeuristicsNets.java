package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness;

import java.util.Arrays;

import org.processmining.models.heuristics.HeuristicsNet;


/**
 * This class builds a mapping of identical 
 * <code>HeuristicNet</code> objects (i.e., individuals) 
 * in a give population.
 * <p>The equality is based on the <code>equals</code> method of
 * <code>HeuristicsNet</code>.
 * 
 * @author Ana Karla Alves de Medeiros
 *
 */
public class MapIdenticalHeuristicsNets {

	private int[] mapIndividuals;

	/**
	 * Constructs the mapping that identifying the <code>HeuristicsNets</code>
	 * objects (i.e. individuals) that are the same in a given population.
	 * @param population array containing the <code>HeuristicsNet</code> objects
	 * to which the mapping should be construct
	 */
	public MapIdenticalHeuristicsNets(HeuristicsNet[] population) {

		createMapIndividuals(population);

	}

	private void createMapIndividuals(HeuristicsNet[] population) {

		mapIndividuals = new int[population.length];

		Arrays.fill(mapIndividuals, -1);

		for (int i = 0; i < population.length; i++) {
			for (int j = 0; j < i; j++) {
				if (population[i].equals(population[j])) {
					mapIndividuals[i] = j;
					break;
				}
			}
		}

	}
	
	/**
	 * Identifies which other <code>HeuristicsNet</code>
	 * object in the population is identical to the  
	 * <code>HeuristicsNet</code> object in the provided index
	 * in the population.
	 * @param heuristicNetIndex the index (or position) of the <code>HeuristicsNet</code> object in the population
	 * @return the position of the identical  <code>HeuristicsNet</code> object in the population. The returned
	 * value will be greater or equal to 0 if the given <code>HeuristicsNet</code> object is identical
	 * to another one in the population. Otherwise, -1 will be returned.
	 */
	public final int getIndenticalHeuristicsNet(int heuristicNetIndex) {
		return mapIndividuals[heuristicNetIndex];

	}
}

