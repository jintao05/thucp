package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness;

import java.util.Iterator;
import java.util.Random;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ContinuousSemanticsParser;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * For every <code>HeuristicsNet</code> in a population,
 * this class calculates the ratio of the traces in a log that
 * could be completely replayed without problems and 
 * without "tokens being left behind".
 *  
 * @author Ana Karla Alves de Medeiros
 *
 */
public class ProperCompletion implements Fitness {

	private XLogInfo logInfo = null;
	private double[] numProperlyCompletedPIs = null;
	private HeuristicsNet[] population = null;
	private ContinuousSemanticsParser[] parser = null;
	private MapIdenticalHeuristicsNets mapping = null;
	private Random generator = null;

	/**
	 * Constructs a new proper completion fitness for the given log.
	 * All fitness values calculated by this object for populations
	 * of <code>HeuristicsNet</code> will be based on this log.
	 * @param logInfo information about the log
	 */
	public ProperCompletion(XLogInfo logInfo) {
		this.logInfo = logInfo;
		generator = new Random(Long.MAX_VALUE);
	}

	/**
	 * Calculates the proper completion fitness of every
	 * <code>HeuristicsNet</code> in the population.
	 * @param population array containing the <code>HeuristicsNet</code> for
	 * which a fitness value will be calculated.
	 */
	public HeuristicsNet[] calculate(HeuristicsNet[] population) {

		this.population = population;
		mapping = new MapIdenticalHeuristicsNets(this.population);

		createParser();
		createFitnessVariables();
		ProperCompletion.resetDuplicatesActualFiringAndArcUsage(this.population);
		calculatePartialFitness();

		return assignFitness();
	}

	/**
	 * Sets all arc usage and activities firing to zero.
	 * Note that these values will be updated again 
	 * during the fitness calculation based on
	 * how well the log can be replayed by 
	 * every <code>HeuristicsNet</code> in a population.
	 * @param population array containing the <code>HeuristicsNet</code> objects
	 * that will have their arc usage and activity firing counters reset. 
	 */
	public static void resetDuplicatesActualFiringAndArcUsage(HeuristicsNet[] population) {
		for (int i = 0; i < population.length; i++) {
			population[i].resetActivitiesActualFiring();
			population[i].resetArcUsage();
		}
	}

	private HeuristicsNet[] assignFitness() {

		double fitness = 0;
		double properlyCompleted = 0;
		int indexIdenticalIndividual = 0;

		for (int i = 0; i < population.length; i++) {
			indexIdenticalIndividual = mapping.getIndenticalHeuristicsNet(i);
			if (indexIdenticalIndividual < 0) {
				properlyCompleted = (numProperlyCompletedPIs[i]/logInfo.getNumberOfTraces());
				fitness = properlyCompleted;
				population[i].setFitness(fitness);
			} else {
				population[i].setFitness(population[indexIdenticalIndividual].getFitness());
			}
		}

		return population;
	}

	private void calculatePartialFitness() {

		XTrace pi = null;
		int numSimilarPIs = 0;

		Iterator<XTrace> logReaderInstanceIterator = logInfo.getLog().iterator();
		
		while (logReaderInstanceIterator.hasNext()) {
			try{
				pi = logReaderInstanceIterator.next();
				//TODO - Call here the correct method to get the number of pis!
//				numSimilarPIs = MethodsForWorkflowLogDataStructures.
//								getNumberSimilarProcessInstances(pi);
				numSimilarPIs = 1;
				for (int i = 0; i < population.length; i++) {
					DoubleMatrix2D arcUsage = population[i].getArcUsage().copy();
					int[] taskFiring = new int[population[i].getActivitiesActualFiring().length];
					System.arraycopy(population[i].getActivitiesActualFiring(),
							0, taskFiring, 0, taskFiring.length);
					if (mapping.getIndenticalHeuristicsNet(i) < 0) { //we need to compute the partial fitness
						parser[i].parse(pi);
						//partial assignment to variables
						if (parser[i].getProperlyCompleted() 
								&&	parser[i].getNumMissingTokens() == 0) {
							numProperlyCompletedPIs[i] += numSimilarPIs;
						} else { // the net didn't proper complete. We need to roll back the arcUsage
							population[i].setArcUsage(arcUsage);
							population[i].setActivitiesActualFiring(taskFiring);
						}

					}
				}
			}catch(NullPointerException npe){
				//the net does not contain the element to be parsed
				//proceed to the next process instance
			}
		}
	}

	private void createFitnessVariables() {
		numProperlyCompletedPIs = new double[population.length];
	}

	private void createParser() {
		//creating a parser for every individual
		parser = new ContinuousSemanticsParser[population.length];
		for (int i = 0; i < parser.length; i++) {
			if (mapping.getIndenticalHeuristicsNet(i) < 0) {
				parser[i] = new ContinuousSemanticsParser(population[i], generator);
			}
		}
	}

}
