package org.processmining.models.heuristics.impl;

import java.util.Iterator;
import java.util.Map;

import org.processmining.models.heuristics.HeuristicsNet;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * Implements the <code>HeuristicsNet</code> objects that are used by the
 * Genetic Miner algorithm.
 * 
 * @author Ana Karla Alves de Medeiros
 * 
 */

public class HeuristicsNetImpl implements Comparable<HeuristicsNet>, HeuristicsNet {

	private ActivitiesMappingStructures activitiesMappingStructures = null; //contains all the necessary mappings from activities to XEventClasses, and vice-versa. 

	//Internal structures based on the indexes for the activities
	private final HNSet[] inputSets; //input sets
	private final HNSet[] outputSets; // output sets
	private HNSubSet startActivities; //the HeuristicsNet can have multiple starting activities
	private HNSubSet endActivities; //the HeuristicsNet can have multiple end activities
	private double fitness; //fitness of the HeuristicsNet
	private final int size; //number of activities in the HeuristicsNet
	private int[] activitiesActualFiring; //Keeps track of how often activities have been executed during the log replay
	private DoubleMatrix2D arcUsage; //Keeps track of how often arcs have been used during the log replay

	//Constants used to build the string representation of a HeuristicsNet
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final String WME_HEADER = "Element";
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final char WME_NAME_DELIMITER = '\"';
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final String INPUT_SETS_HEADER = "In";
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final String OUTPUT_SETS_HEADER = "Out";
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final String EVENT_SEPARATOR = ":";
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final String EMPTY_SET = ".";
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final String AND_SEPARATOR = "&";
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final String OR_SEPARATOR = "|";
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final String SETS_SEPARATOR = "@";
	/**
	 * Constant used to build the <code>String</code> representation of a
	 * <code>HeuristicsNetImpl</code> object.
	 */
	public static final String FIELD_SEPARATOR = "/////////////////////";

	/**
	 * Builds a <code>HeuristicsNetImpl</code> objects based on a given
	 * <code>ActivitiesMapping</code> object.
	 * 
	 * @param activitiesMappingStructures
	 *            object with the correct number of activities per
	 *            <code>XEventClass</code> for a given log.
	 */
	public HeuristicsNetImpl(ActivitiesMappingStructures activitiesMappingStructures) {

		this.activitiesMappingStructures = activitiesMappingStructures;

		size = this.activitiesMappingStructures.getActivitiesMapping().length;

		inputSets = new HNSet[size];

		outputSets = new HNSet[size];

		fitness = 0;

		activitiesActualFiring = new int[size];

		arcUsage = DoubleFactory2D.sparse.make(size, size, 0.0);

		startActivities = new HNSubSet();

		endActivities = new HNSubSet();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * resetActivitiesActualFiring()
	 */
	public void resetActivitiesActualFiring() {
		for (int i = 0; i < activitiesActualFiring.length; i++) {
			activitiesActualFiring[i] = 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#resetArcUsage()
	 */
	public void resetArcUsage() {
		for (int row = 0; row < arcUsage.rows(); row++) {
			for (int column = 0; column < arcUsage.columns(); column++) {
				arcUsage.setQuick(row, column, 0.0);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * getActivitiesActualFiring()
	 */
	public int[] getActivitiesActualFiring() {
		return activitiesActualFiring;
	}

	public int[] getActivitiesActualFiring(Map<Integer, Integer> oldNewIndexMap, int newSize) {
		// TODO Auto-generated method stub

		int[] newActivitiesActualFiring = new int[newSize];
		Iterator<Integer> iterator = oldNewIndexMap.keySet().iterator();
		if (oldNewIndexMap.keySet().size() != activitiesActualFiring.length) {
			return null;
		}
		while (iterator.hasNext()) {
			Integer i = iterator.next();
			newActivitiesActualFiring[oldNewIndexMap.get(i)] = activitiesActualFiring[i];
		}

		return newActivitiesActualFiring;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.models.heuristics.impl.HeuristicsNet#getArcUsage()
	 */

	public DoubleMatrix2D getArcUsage() {
		return arcUsage;
	}

	public DoubleMatrix2D getArcUsage(Map<Integer, Integer> oldNewIndexMap, int newSize) {
		// TODO Auto-generated method stub

		DoubleMatrix2D newArcUsage = DoubleFactory2D.sparse.make(newSize, newSize, 0.0);

		if (oldNewIndexMap.keySet().size() != size) {
			return null;
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int newI = oldNewIndexMap.get(i);
				int newJ = oldNewIndexMap.get(j);
				newArcUsage.setQuick(newI, newJ, arcUsage.getQuick(i, j));
			}
		}

		return newArcUsage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * setActivitiesActualFiring(int[])
	 */
	public boolean setActivitiesActualFiring(int[] newActivitiesActualFiring) {

		if (activitiesActualFiring.length == newActivitiesActualFiring.length) {
			activitiesActualFiring = newActivitiesActualFiring;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#setArcUsage(cern
	 * .colt.matrix.DoubleMatrix2D)
	 */
	public boolean setArcUsage(DoubleMatrix2D newArcUsage) {

		if ((arcUsage.rows() == newArcUsage.rows()) && (arcUsage.columns() == newArcUsage.columns())) {
			arcUsage = newArcUsage;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * increaseElementActualFiring(int, int)
	 */
	public void increaseElementActualFiring(int activity, int amount) {
		//Note: it does not increase directly +1 because the traces may be grouped.
		activitiesActualFiring[activity] += amount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#increaseArcUsage
	 * (int, org.processmining.models.heuristics.impl.HNSubSet, int)
	 */
	public void increaseArcUsage(int activity, HNSubSet usedInputActivities, int amount) {
		for (int inputElementPosition = 0; inputElementPosition < usedInputActivities.size(); inputElementPosition++) {
			arcUsage.setQuick(usedInputActivities.get(inputElementPosition), activity, (arcUsage.getQuick(
					usedInputActivities.get(inputElementPosition), activity) + amount));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#getStartActivities
	 * ()
	 */
	public HNSubSet getStartActivities() {
		return startActivities;
	}

	public HNSubSet getStartActivities(Map<Integer, Integer> oldNewIndexMap) {
		// TODO Auto-generated method stub
		return startActivities.deepCopy(oldNewIndexMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#getEndActivities()
	 */
	public HNSubSet getEndActivities() {
		return endActivities;
	}

	public HNSubSet getEndActivities(Map<Integer, Integer> oldNewIndexMap) {
		// TODO Auto-generated method stub
		return endActivities.deepCopy(oldNewIndexMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#setStartActivities
	 * (org.processmining.models.heuristics.impl.HNSubSet)
	 */
	public void setStartActivities(HNSubSet activities) {
		startActivities = activities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#setEndActivities
	 * (org.processmining.models.heuristics.impl.HNSubSet)
	 */
	public void setEndActivities(HNSubSet activities) {
		endActivities = activities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.models.heuristics.impl.HeuristicsNet#size()
	 */
	public int size() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#setFitness(double)
	 */
	public void setFitness(double newFitnessValue) {
		fitness = newFitnessValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * getActivitiesMappingStructures()
	 */

	public ActivitiesMappingStructures getActivitiesMappingStructures() {
		return activitiesMappingStructures;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * setActivitiesMappingStructures
	 * (org.processmining.models.heuristics.impl.ActivitiesMappingStructures)
	 */
	public boolean setActivitiesMappingStructures(ActivitiesMappingStructures newActivitiesMappingStructures) {

		return false;
		//TODO implement this method!

		//if the new XEventClasses is the equal to the current one
		//in the HeuristicsNet, nothing needs to be done
		//		if(!newActivitiesMappingStructures.getXEventClasses().getClasses().containsAll(this.activitiesMappingStructures.getXEventClasses().getClasses())
		//				|| !this.activitiesMappingStructures.getXEventClasses().getClasses().containsAll(newActivitiesMappingStructures.getXEventClasses().getClasses())){

		//	 TODO Correct this method to use XEventClasses

		//			int[] newDuplicatesMapping = new int[this.duplicatesMapping.length];
		//	
		//			int leNumber = 0;
		//			//rebuilding the duplicates mapping first
		//			for (int i = 0; i < newDuplicatesMapping.length; i++) {
		//	
		//				try {
		//					XEventClasses le = events.getEvent(duplicatesMapping[i]);
		//					leNumber = newEvents.findLogEventNumber(le.getModelElementName(),
		//							   le.getEventType());
		//				} catch (ArrayIndexOutOfBoundsException exc) {
		//					leNumber = -1; //since the task does not exist in the log
		//	
		//				}
		//				newDuplicatesMapping[i] = leNumber;
		//			}
		//	
		//			//since the new duplicates mapping could be successfully created,
		//			//the new LogEvents can replace the old one.
		//			this.events = newEvents;
		//			this.duplicatesMapping = newDuplicatesMapping;
		//			this.reverseDuplicatesMapping = buildReverseDuplicatesMapping(this.duplicatesMapping);
		//			
		//		
		//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.models.heuristics.impl.HeuristicsNet#getFitness()
	 */
	public double getFitness() {
		return fitness;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#setInputSet(int,
	 * org.processmining.models.heuristics.impl.HNSet)
	 */
	public boolean setInputSet(int index, HNSet sets) {
		return setSet(inputSets, index, sets);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#setOutputSet(int,
	 * org.processmining.models.heuristics.impl.HNSet)
	 */
	public boolean setOutputSet(int index, HNSet sets) {
		return setSet(outputSets, index, sets);
	}

	/**
	 * Updates a set of a given activity in a given target structure. In our
	 * case, this target structure can be the input or output sets.
	 * 
	 * @param target
	 *            Specify the target (input or output sets)
	 * @param index
	 *            activity index
	 * @param sets
	 *            new set for this activity
	 * @return true if the update was successful, false otherwise.
	 */
	private boolean setSet(HNSet[] target, int index, HNSet sets) {
		if (index < size()) {
			target[index] = sets;
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#getInputSets()
	 */
	public HNSet[] getInputSets() {
		return inputSets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#getOutputSets()
	 */
	public HNSet[] getOutputSets() {
		return outputSets;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#getInputSet(int)
	 */
	public HNSet getInputSet(int index) {
		return getSet(inputSets, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#getOutputSet(int)
	 */
	public HNSet getOutputSet(int index) {
		return getSet(outputSets, index);
	}

	/**
	 * Retrieves a given set of an activity. Note that, in our case, the target
	 * array will be the input or output sets.
	 * 
	 * @param target
	 *            array to retrieve the set from.
	 * @param index
	 *            activity index.
	 * @return Set linked to this activity index in the target array.
	 */
	private HNSet getSet(HNSet[] target, int index) {
		if ((index >= 0) && (index < size())) {
			return target[index];
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.models.heuristics.impl.HeuristicsNet#toString()
	 */
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();

		sb.append("==== Activities Mapping Part (begin) ====").append(activitiesMappingStructures).append(
				"==== Activities Mapping Part (end) ====").append("\n");
		for (int i = 0; i < size(); i++) {
			sb.append(WME_HEADER).append(" ").append(i).append(":\n");
			sb.append(INPUT_SETS_HEADER).append(": ").append(inputSets[i].toString()).append("\n");
			sb.append(OUTPUT_SETS_HEADER).append(": ").append(outputSets[i].toString()).append("\n");
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#compareTo(org.
	 * processmining.models.heuristics.impl.HeuristicsNetImpl)
	 */
	public int compareTo(HeuristicsNet otherHeuristicsNet) {

		if (fitness > otherHeuristicsNet.getFitness()) {
			return 1;
		}
		if (fitness == otherHeuristicsNet.getFitness()) {
			return 0;
		}
		return -1;
	}

	/**
	 * Returns a deep cloning of the current HeuristicsNet.
	 */
	@Override
	protected HeuristicsNet clone() {

		HeuristicsNet copy = null;

		//creating the copy...

		copy = new HeuristicsNetImpl(activitiesMappingStructures);

		//writing the input/output sets...
		for (int i = 0; i < size(); i++) {
			copy.setInputSet(i, inputSets[i].deepCopy());
			copy.setOutputSet(i, outputSets[i].deepCopy());
		}

		//copying the fitness...
		copy.setFitness(fitness);

		//copying the start/end tasks...
		copy.setStartActivities(startActivities);
		copy.setEndActivities(endActivities);

		//making deep copy of the "duplicates actual firing"...
		int[] dacCopy = new int[activitiesActualFiring.length];
		System.arraycopy(activitiesActualFiring, 0, dacCopy, 0, activitiesActualFiring.length);

		//copying the "duplicates actual firing"
		copy.setActivitiesActualFiring(dacCopy);

		//copying the "arc usage"
		copy.setArcUsage(arcUsage.copy());

		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.models.heuristics.impl.HeuristicsNet#copyNet()
	 */
	public HeuristicsNet copy() {
		return clone();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#equals(org.
	 * processmining.models.heuristics.impl.HeuristicsNet)
	 */
	public boolean equals(Object other) {

		//checking the the other net is not null
		if (!(other instanceof HeuristicsNet) || (other == null)) {
			return false;
		} else {
			//checking if the nets have the same size
			HeuristicsNet otherNet = (HeuristicsNet) other;
			if (size() != otherNet.size()) {
				return false;
			}

			//checking if the nets link to the same XEventClasses in terms of content
			if (!getActivitiesMappingStructures().equals(otherNet.getActivitiesMappingStructures())) {
				return false;
			}

			//checking if input and output sets are the same
			for (int i = 0; i < size(); i++) {
				if (!getInputSet(i).equals(otherNet.getInputSet(i))
						|| !getOutputSet(i).equals(otherNet.getOutputSet(i))) {
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.processmining.models.heuristics.impl.HeuristicsNet#hashCode()
	 */
	@Override
	public int hashCode() {
		int hashCode = activitiesMappingStructures.hashCode();
		for (int i = 0; i < size(); i++) {
			hashCode += ((getInputSet(i).hashCode() + getOutputSet(i).hashCode()) * 31 ^ (size - i + 1));
		}
		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.processmining.models.heuristics.impl.HeuristicsNet#getAllElementsInputSet
	 * (int)
	 */
	public HNSubSet getAllElementsInputSet(int index) {
		return HNSet.getUnionSet(inputSets[index]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * getAllElementsOutputSet(int)
	 */
	public HNSubSet getAllElementsOutputSet(int index) {
		return HNSet.getUnionSet(outputSets[index]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * getInputSetsWithElement(int, int)
	 */
	public HNSet getInputSetsWithElement(int index, int element) {
		return getSetsWithElement(inputSets, index, element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * getOutputSetsWithElement(int, int)
	 */

	public HNSet getOutputSetsWithElement(int index, int element) {
		return getSetsWithElement(outputSets, index, element);
	}

	/*
	 * Retrieves subsets from an INPUT/OUTPUT set of "index". These subsets
	 * contain "element". Returns null if there is not such subsets with
	 * element.
	 */
	private HNSet getSetsWithElement(HNSet[] indSet, int index, int element) {
		HNSet set = null;
		HNSubSet subSet = null;
		HNSet filterFromSet = null;

		if (index < indSet.length) {
			filterFromSet = indSet[index];
			set = new HNSet();
			for (int i = 0; i < filterFromSet.size(); i++) {
				subSet = filterFromSet.get(i);
				if (subSet.contains(element)) {
					set.add(subSet);
				}
			}
		}
		return set;
	}

	/**
	 * Returns an array contains all the activities that are in the subset of
	 * the HNSet structure. In practice, this array represents the multiset of
	 * the union of all the subsets in the HNSet.
	 * 
	 * @param set
	 *            HNSet with the subsets to be joined.
	 * @return array with all the activities in the subsets of set.
	 */
	public static final int[] getElements(HNSet set) {

		int[] multiset = null;
		int size = 0;
		HNSubSet subset = null;

		for (int i = 0; i < set.size(); i++) {
			size += set.get(i).size();
		}

		multiset = new int[size];

		for (int i = 0, iMultiset = 0; i < set.size(); i++) {
			subset = set.get(i);
			for (int j = 0; j < subset.size(); j++) {
				multiset[iMultiset++] = subset.get(j);
			}

		}

		return multiset;
	}

	/*
	 * This method disconnects the activities that are not used during the
	 * parsing of a log. The main idea is to remove activities that are not
	 * used.
	 */
	private void disconnectUnusedActivities() {
		HNSubSet unfiredElements;

		//identifying the tasks that did not fire during the parsing...
		unfiredElements = identifyUnfiredActivities();

		//cleaning the in/out sets of the unfired tasks
		for (int iUnfiredElements = 0; iUnfiredElements < unfiredElements.size(); iUnfiredElements++) {
			inputSets[unfiredElements.get(iUnfiredElements)] = new HNSet();
			outputSets[unfiredElements.get(iUnfiredElements)] = new HNSet();

		}

		//removing the connections to the unfired elements
		for (int i = 0; i < size; i++) {
			for (int iUnfiredElements = 0; iUnfiredElements < unfiredElements.size(); iUnfiredElements++) {
				//clean input sets...
				inputSets[i] = HNSet.removeElementFromSubsets(inputSets[i], unfiredElements.get(iUnfiredElements));
				//clean output sets...
				outputSets[i] = HNSet.removeElementFromSubsets(outputSets[i], unfiredElements.get(iUnfiredElements));
			}
		}

	}

	/*
	 * This method disconnects the arcs that are used fewer times than a given
	 * threshold (inclusive). In other words, only the arcs that are used more
	 * times than 'threshold' are kept.
	 * 
	 * @param threshold double value of the threshold.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * disconnectArcsUsedBelowThreshold(double)
	 */
	public void disconnectArcsUsedBelowThreshold(double threshold) {

		//disconnecting unused input arcs
		for (int row = 0; row < arcUsage.rows(); row++) {
			for (int column = 0; column < arcUsage.columns(); column++) {
				if (arcUsage.get(row, column) <= threshold) {
					outputSets[row] = HNSet.removeElementFromSubsets(outputSets[row], column);
					inputSets[column] = HNSet.removeElementFromSubsets(inputSets[column], row);
					arcUsage.set(row, column, 0.0);

				}
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.processmining.models.heuristics.impl.HeuristicsNet#
	 * disconnectUnusedElements()
	 */

	public void disconnectUnusedElements() {

		disconnectUnusedActivities();
		disconnectArcsUsedBelowThreshold(0.0);

	}

	/**
	 * Retrieves a set containing the activities that have not been fired during
	 * the parsing of a log.
	 * 
	 * @return HNSubSet with the unfired activities.
	 */
	private HNSubSet identifyUnfiredActivities() {
		HNSubSet unfiredElements = new HNSubSet();
		for (int i = 0; i < activitiesActualFiring.length; i++) {
			if (activitiesActualFiring[i] <= 0) {
				unfiredElements.add(i);
			}
		}
		return unfiredElements;
	}

}
