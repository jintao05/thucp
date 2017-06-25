package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.fitness;

import java.util.Iterator;
import java.util.Random;

import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.heuristics.impl.ContinuousSemanticsParser;

/**
 * Calculates the fitness of <code>HeuristicsNet</code> objects
 * in a population based on a weighed sum of (i) the ratio of 
 * parsed activities without problems and (ii) the ratio of 
 * properly completed traces.
 * <p> Additional methods are provided to set and retrieve
 * the values of the two different weights used for every
 * part of the sum above. Furthermore, the parsing semantics
 * is a continuous one (i.e.,  the log replay does not stop
 * when problems are encountered).  
 *  
 * @author Ana Karla Alves de Medeiros
 *
 */
public class ContinuousSemantics implements Fitness {
	private XLogInfo logInfo = null;
	private HeuristicsNet[] population = null;
	private ContinuousSemanticsParser[] parser = null;

	private MapIdenticalHeuristicsNets mapping = null;

	private double[] numDisabledWMEs = null; //WME = Workflow Model Element
	private double[] numParsedWMEs = null; //WME = Workflow Model Element
	private double[] numProperlyCompletedPIs = null; //PI = process instance

	private double numEnabledConstant = 0.40; //weight for the parsed activities without problems
	private double numProperlyCompletedConstant = 0.60; //weight for the traces that could be **properly** completed during the log replay

	private Random generator = null;

	/**
	 * Constructs a new continuous semantics fitness for the given log.
	 * All fitness values calculated by this object for populations
	 * of <code>HeuristicsNet</code> will be based on this log.
	 * @param logInfo information about the log
	 */	
	public ContinuousSemantics(XLogInfo logInfo) {
		this.logInfo = logInfo;
		generator = new Random(Long.MAX_VALUE);
	}

	
	/**
	 * Calculates the continuous semantics fitness of every
	 * <code>HeuristicsNet</code> in the population
	 * @param population array containing the <code>HeuristicsNet</code> for
	 * which a fitness value will be calculated
	 */		
	public HeuristicsNet[] calculate(HeuristicsNet[] population) {

		this.population = population;
		mapping = new MapIdenticalHeuristicsNets(this.population);
		createParser();
		ProperCompletion.resetDuplicatesActualFiringAndArcUsage(this.population);
		createFitnessVariables();
		calculatePartialFitness();

		return assignFitness();

	}
	
	/**
	 * Returns the value used to weigh the impact of the 
	 * ratio of properly completed traces during the log replay
	 * @return double value of this weight
	 */
	public double getNumProperlyCompletedConstant() {
		return numProperlyCompletedConstant;
	}

	/**
	 * Returns the value used to weigh the impact of the 
	 * ratio of parsed activities without problems during the log replay
	 * @return double value of this weight
	 */
	public double getNumEnabledConstant() {
		return numEnabledConstant;
	}

		
	/**
	 * Sets the value used to weigh the impact of the 
	 * ratio of properly completed traces during the log replay
	 * @param newValue new double value of this weight
	 */
	public void setNumProperlyCompletedConstant(double newValue) {
		numProperlyCompletedConstant = newValue;
	}

	/**
	 * Sets the value used to weigh the impact of the 
	 * ratio of parsed activities without problems during the log replay
	 * @param newValue new double value of this weight
	 */
	public void setNumEnabledConstant(double newValue) {
		numEnabledConstant = newValue;
	}

	private void createParser() {
		//creating a parser for every individual
		parser = new ContinuousSemanticsParser[population.length];
		for (int i = 0; i < parser.length; i++) {
			if (mapping.getIndenticalHeuristicsNet(i) < 0) {
				parser[i] = new ContinuousSemanticsParser(population[i],
						generator);
			}
		}
	}

	private void createFitnessVariables() {
		numDisabledWMEs = new double[population.length];
		numParsedWMEs = new double[population.length];
		numProperlyCompletedPIs = new double[population.length];
	}

	private void calculatePartialFitness() {

		XTrace pi = null;
		int numSimilarPIs = 0;

		Iterator<XTrace> logReaderInstanceIterator = logInfo.getLog().iterator();
		while (logReaderInstanceIterator.hasNext()) {
			pi =  logReaderInstanceIterator.next();
			//TODO - Call here the correct method to get the number of pis!
//			numSimilarPIs = MethodsForWorkflowLogDataStructures.
//							getNumberSimilarProcessInstances(pi);
			numSimilarPIs = 1;
			for (int i = 0; i < population.length; i++) {
				if (mapping.getIndenticalHeuristicsNet(i) < 0) { //we need to compute the partial fitness
					parser[i].parse(pi);
					//partial assignment to variables
					numDisabledWMEs[i] += (parser[i].getNumUnparsedElements() 
							* numSimilarPIs);
					numParsedWMEs[i] += (parser[i].getNumParsedElements() 
							* numSimilarPIs);
					if (parser[i].getProperlyCompleted()) {
						numProperlyCompletedPIs[i] += numSimilarPIs;
					}
				}
			}
		}
	}

	private HeuristicsNet[] assignFitness() {

		double fitness = 0;
		double enabled = 0;
		double properlyCompleted = 0;
		int indexIdenticalIndividual = 0;

		for (int i = 0; i < population.length; i++) {

			indexIdenticalIndividual = mapping.getIndenticalHeuristicsNet(i);

			if (indexIdenticalIndividual < 0) {
				
				enabled = numParsedWMEs[i]/logInfo.getNumberOfEvents();
				properlyCompleted = numProperlyCompletedPIs[i]/logInfo.getNumberOfTraces();
				fitness = (numEnabledConstant * enabled) 
						    + (numProperlyCompletedConstant * properlyCompleted);
				population[i].setFitness(fitness);
				
			} else {

				population[i].setFitness(
						population[indexIdenticalIndividual].getFitness()
						);
				population[i].setActivitiesActualFiring(
						population[indexIdenticalIndividual].getActivitiesActualFiring()
						);
				population[i].setArcUsage(
						population[indexIdenticalIndividual].getArcUsage()
						);
			}

		}

		return population;

	}

}
