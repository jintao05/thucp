package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness;

import java.util.Iterator;
import java.util.Random;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ContinuousSemanticsParser;

/**
 * Calculates the fitness of <code>HeuristicsNet</code> objects
 * in a population based on the ratio of activities
 * in a log that could be parsed (or replayed) without
 * problems. The ratio contains a punishment component based
 * on the amount of problems encountered during the log replay
 * <i>and</i> the amount of traces with parsing problems. In a
 * nutshell, this punishment factor benefits the 
 * <code>HeuristicsNet</code> objects that have fewer problems
 * scattered in fewer traces.
 * <p> Note that the parsing semantics of this fitness measure
 * is a continuous one (i.e.,  the log replay does not stop
 * when problems are encountered). 
 *   
 * @author Ana Karla Alves de Medeiros
 *
 */
public class ImprovedContinuousSemantics implements Fitness {
	private XLogInfo logInfo = null;
	private HeuristicsNet[] population = null;
	private ContinuousSemanticsParser[] parser = null;

	private double[] numPIsWithMissingTokens = null; //PI = process instance
	private double[] numMissingTokens = null; //PI = process instance
	private double[] numPIsWithExtraTokensLeftBehind = null;
	private double[] numExtraTokensLeftBehind = null;
	private double[] numParsedWMEs = null;
	private MapIdenticalHeuristicsNets mapping = null;

	private Random generator = null;

	
	/**
	 * Constructs a new improved continuous semantics 
	 * fitness for the given log. All fitness values 
	 * calculated by this object for populations
	 * of <code>HeuristicsNet</code> will be based on this log.
	 * @param logInfo information about the log
	 */
	public ImprovedContinuousSemantics(XLogInfo logInfo) {
		generator = new Random(Long.MAX_VALUE);
		this.logInfo = logInfo;
	}

	/**
	 * Calculates the improved continuous semantics fitness 
	 * of every <code>HeuristicsNet</code> in the population
	 * @param population array containing the 
	 * <code>HeuristicsNet</code> for which a fitness 
	 * value will be calculated
	 */	
	public HeuristicsNet[] calculate(HeuristicsNet[] population) {

		this.population = population;
		mapping = new MapIdenticalHeuristicsNets(this.population);
		createParser();
		resetDuplicatesActualFiringAndArcUsage();
		createFitnessVariables();
		calculatePartialFitness();

		return assignFitness();

	}

	private void resetDuplicatesActualFiringAndArcUsage() {
		for (int i = 0; i < population.length; i++) {
			population[i].resetActivitiesActualFiring();
			population[i].resetArcUsage();
		}
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

	private void createFitnessVariables() {
		numPIsWithMissingTokens = new double[population.length];
		numMissingTokens = new double[population.length];
		numPIsWithExtraTokensLeftBehind = new double[population.length];
		numExtraTokensLeftBehind = new double[population.length];
		numParsedWMEs = new double[population.length];
	}

	private void calculatePartialFitness() {

		XTrace pi = null;
		int numSimilarPIs = 0;
		int numMissingTokens = 0;
		int numExtraTokensLeftBehind = 0;

		Iterator<XTrace> logReaderInstanceIterator = logInfo.getLog().iterator();
		while (logReaderInstanceIterator.hasNext()) {
				pi = logReaderInstanceIterator.next();
				//TODO - Call here the correct method to get the number of pis!
//				numSimilarPIs = MethodsForWorkflowLogDataStructures.
//								getNumberSimilarProcessInstances(pi);
				numSimilarPIs = 1;
				for (int i = 0; i < population.length; i++) {
					if (mapping.getIndenticalHeuristicsNet(i) < 0) { //we need to compute the partial fitness
						parser[i].parse(pi);
						//partial assignment to variables
						numMissingTokens = parser[i].getNumMissingTokens();
						if (numMissingTokens > 0) {
							this.numPIsWithMissingTokens[i] += numSimilarPIs;
							this.numMissingTokens[i] += (numMissingTokens * numSimilarPIs);
						}

						numExtraTokensLeftBehind = parser[i].getNumExtraTokensLeftBehind();
						if (numExtraTokensLeftBehind > 0) {
							this.numPIsWithExtraTokensLeftBehind[i] += numSimilarPIs;
							this.numExtraTokensLeftBehind[i] += (numExtraTokensLeftBehind *
									numSimilarPIs);
						}
						numParsedWMEs[i] += (parser[i].getNumParsedElements() * numSimilarPIs);
					}
				}
		}
	}

	private HeuristicsNet[] assignFitness() {

		double fitness = 0;
		double numATEsAtLog = 0;
		double numPIsAtLog = 0;
		double missingTokensDenominator = 0.001;
		double unusedTokensDenominator = 0.001;
		int indexIdenticalIndividual = 0;

		numATEsAtLog = logInfo.getNumberOfEvents();
		numPIsAtLog = logInfo.getNumberOfTraces();

		for (int i = 0; i < population.length; i++) {

			indexIdenticalIndividual = mapping.getIndenticalHeuristicsNet(i);

			if (indexIdenticalIndividual < 0) {

				missingTokensDenominator = numPIsAtLog - numPIsWithMissingTokens[i] + 1;

				unusedTokensDenominator = numPIsAtLog - numPIsWithExtraTokensLeftBehind[i] + 1;

				fitness = (numParsedWMEs[i] - ((numMissingTokens[i] / missingTokensDenominator) +
						  (numExtraTokensLeftBehind[i] / unusedTokensDenominator))) / numATEsAtLog;

				population[i].setFitness(fitness);

			} else {

				population[i].setFitness(population[indexIdenticalIndividual].getFitness());
				population[i].setActivitiesActualFiring(population[indexIdenticalIndividual].
						getActivitiesActualFiring());
				population[i].setArcUsage(population[indexIdenticalIndividual].getArcUsage());

			}
		}

		return population;

	}

}
