package org.processmining.models.heuristics;

import java.util.Map;

import org.processmining.models.heuristics.impl.ActivitiesMappingStructures;
import org.processmining.models.heuristics.impl.HNSet;
import org.processmining.models.heuristics.impl.HNSubSet;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * Common interface of the <code>HeuristicNet</code> objects. These objects are
 * used in algorithms like the Genetic Miner etc.
 * 
 * @author Ana Karla Alves de Medeiros
 * 
 */
public interface HeuristicsNet {

	/**
	 * Resets the variable that keeps track of activities that are actually
	 * fired during the parsing of a log. The activities that do not fire are
	 * not shown in the graphical representation of the HeuristicsNet.<br>
	 * In this case, all activities have their counters set to 0.
	 */
	public abstract void resetActivitiesActualFiring();

	/**
	 * Resets the variable that keeps track of how often arcs are actually used
	 * during the parsing of a log by this HeuristicsNet.<br>
	 * In this case, all arc usages are set to 0.
	 */
	public abstract void resetArcUsage();

	/**
	 * Retrieves the variable that keeps track of how often activities have been
	 * executed (or fired) during the parsing of a log.
	 * 
	 * @return An array containing the number of times that a given activity has
	 *         fired.
	 */
	public abstract int[] getActivitiesActualFiring();

	/**
	 * Retrieves the variable that keeps track of how often arcs have been used
	 * during the parsing of a log.
	 * 
	 * @return A matrix containing the number of times that a given arc has been
	 *         used.
	 */

	public abstract DoubleMatrix2D getArcUsage();

	/**
	 * Sets the variable that keeps track of how often activities have been
	 * executed during the parsing of a log by this HeuristicsNet.
	 * 
	 * @param newActivitiesActualFiring
	 *            array containing the new values for the activities firing.
	 *            This variable should have the same length of the current
	 *            variable used to keep track of the activities firing.
	 * @return <code>true</code> if the variable has been successfully updated,
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean setActivitiesActualFiring(int[] newActivitiesActualFiring);

	/**
	 * Sets the variable that keeps track of how often arcs are actually used
	 * during the parsing of a log by this HeuristicsNet.
	 * 
	 * @param newArcUsage
	 *            matrix containing the new arc usage values for the activities.
	 *            This variable should have the same dimension that the current
	 *            matrix has.
	 * @return <code>true</code> if the variable has been successfully updated,
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean setArcUsage(DoubleMatrix2D newArcUsage);

	/**
	 * Increases by the specified amount the number of times an activity has
	 * fired.
	 * 
	 * @param activity
	 *            activity to have the number of firing increased.
	 * @param amount
	 *            value to be added to the current amount of time the activity
	 *            has fired.
	 */
	public abstract void increaseElementActualFiring(int activity, int amount);

	/**
	 * Increases arc usage in this <code>HeuristicsNet</code> object.
	 * 
	 * @param activity
	 *            index of the activity to which output arcs should be
	 *            increased.
	 * @param usedInputActivities
	 *            <code>HNSubSet</code> with the activities to which these
	 *            output arcs connect to. In other words, set containing the
	 *            output activities.
	 * @param amount
	 *            value by which each output arc usage should be increased.
	 */
	public abstract void increaseArcUsage(int activity, HNSubSet usedInputActivities, int amount);

	/**
	 * Retrieves the current set of start activities for this
	 * <code>HeuristicsNet</code> object.
	 * 
	 * @return <code>HNSubSet</code> with the start activities.
	 */
	public abstract HNSubSet getStartActivities();

	/**
	 * Retrieves the current set of end activities for this
	 * <code>HeuristicsNet</code> object.
	 * 
	 * @return <code>HNSubSet</code> with the end activities.
	 */
	public abstract HNSubSet getEndActivities();

	/**
	 * Sets the start activities of this <code>HeuristicsNet</code> object.
	 * 
	 * @param activities
	 *            <code>HNSubSet</code> with start activities.
	 */
	public abstract void setStartActivities(HNSubSet activities);

	/**
	 * Sets the end activities of this <code>HeuristicsNet</code> object.
	 * 
	 * @param activities
	 *            <code>HNSubSet</code> with end activities.
	 */
	public abstract void setEndActivities(HNSubSet activities);

	/**
	 * Retrieves the number of activities in this <code>HeuristicsNet</code>
	 * object.
	 * 
	 * @return number of activities.
	 */
	public abstract int size();

	/**
	 * Sets the fitness value of this <code>HeuristicsNet</code> object.
	 * 
	 * @param newFitnessValue
	 *            double representing the new fitness value.
	 */
	public abstract void setFitness(double newFitnessValue);

	/**
	 * Retrieves the current <code>ActivitiesMappingStructures</code> that is
	 * used by this <code>HeuristicsNet</code> object.
	 * 
	 * @return <code>ActivitiesMappingStructures</code> currently used by this
	 *         <code>HeuristicsNet</code> object.
	 */

	public abstract ActivitiesMappingStructures getActivitiesMappingStructures();

	/**
	 * Replaces the current <code>ActivitiesMappingStructures</code> that is
	 * used by this <code>HeuristicsNet</code> object. Furthermore, all other
	 * related structured of the <code>HeuristicsNet</code> object are also
	 * updated!
	 * 
	 * @param newActivitiesMappingStructures
	 * @return <code>true</code> if the variable has been successfully updated,
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean setActivitiesMappingStructures(ActivitiesMappingStructures newActivitiesMappingStructures);

	/**
	 * Retrieves the current fitness of this <code>HeuristicsNet</code> object.
	 * 
	 * @return double value representing the fitness.
	 */
	public abstract double getFitness();

	/**
	 * Sets the input sets of a given activity in this
	 * <code>HeuristicsNet</code> object.
	 * 
	 * @param index
	 *            activity index.
	 * @param sets
	 *            new input set.
	 * @return <code>true</code> if the update was successful,
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean setInputSet(int index, HNSet sets);

	/**
	 * Sets the output sets of a given activity in this
	 * <code>HeuristicsNet</code> object.
	 * 
	 * @param index
	 *            activity index.
	 * @param sets
	 *            new output set.
	 * @return <code>true</code> if the update was successful,
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean setOutputSet(int index, HNSet sets);

	/**
	 * Retrieves the array that contains all the input sets of the activities
	 * for this <code>HeuristicsNet</code> object.
	 * 
	 * @return array with activities' input sets.
	 */
	public abstract HNSet[] getInputSets();

	/**
	 * Retrieves the array that contains all the output sets of the activities
	 * for this <code>HeuristicsNet</code> object.
	 * 
	 * @return array with activities' output sets.
	 */
	public abstract HNSet[] getOutputSets();

	/**
	 * Retrieves the input set of a given activity in this
	 * <code>HeuristicsNet</code> object.
	 * 
	 * @param index
	 *            activity index.
	 * @return input set of this activity.
	 */
	public abstract HNSet getInputSet(int index);

	/**
	 * Retrieves the output set of a given activity in this
	 * <code>HeuristicsNet</code> object.
	 * 
	 * @param index
	 *            activity index.
	 * @return output set of this activity.
	 */
	public abstract HNSet getOutputSet(int index);

	/**
	 * Creates a string representation of this <code>HeuristicsNet</code>
	 * object.
	 * 
	 * @return string representation of this <code>HeuristicsNet</code> object.
	 */
	public abstract String toString();

	/**
	 * Compares other <code>HeuristicsNet</code> object with this
	 * <code>HeuristicsNet</code> object. The comparison is based on their
	 * fitness values.
	 * 
	 * @param otherHeuristicsNet
	 *            other <code>HeuristicsNet</code> object to compare with this
	 *            <code>HeuristicsNet</code> object.
	 * @return 0 if the two HeuristicsNets have the same fitness value, 1 if
	 *         this object's fitness value is bigger than the fitness value of
	 *         the other object, -1 if the this object's fitness value is
	 *         smaller than the other object's fitness value.
	 */
	public abstract int compareTo(HeuristicsNet otherHeuristicsNet);

	/**
	 * Makes a copy (deep clone) of this <code>HeuristicsNet</code> object.
	 * 
	 * @return a copy of this <code>HeuristicsNet</code> object.
	 */
	public abstract HeuristicsNet copy();

	/**
	 * Checks if a given <code>HeuristicsNet</code> object is equal to this
	 * <code>HeuristicsNet</code> object. Two <code>HeuristicsNet</code> objects
	 * are equal when: (i) they have the same size; (ii) they contain the same
	 * <code>ActivitiesMappingStructures</code>; and (iii) their input and
	 * output sets are the same.
	 * 
	 * @param other
	 *            <code>HeuristicsNet</code> object to compare with this one.
	 * @return <code>true</code> if the two <code>HeuristicsNet</code> objects
	 *         satisfy the three conditions above, <code>false</code> otherwise.
	 */
	public abstract boolean equals(Object other);

	/**
	 * Computes the hashCode for this HeuristicsNet.
	 * 
	 * @return hashcode value.
	 */
	public abstract int hashCode();

	/**
	 * Retrieves a set containing all the input activities of a given activity.
	 * 
	 * @param index
	 *            activity to which the union set should be computed.
	 * @return <code>HNSubSet</code> with all input activities for this
	 *         activity.
	 */
	public abstract HNSubSet getAllElementsInputSet(int index);

	/**
	 * Retrieves a set containing all the output activities of a given activity.
	 * 
	 * @param index
	 *            activity to which the union set should be computed.
	 * @return <code>HNSubSet</code> with all output activities for this
	 *         activity.
	 */
	public abstract HNSubSet getAllElementsOutputSet(int index);

	/**
	 * Retrieves subsets from an input set of an activity. These subsets contain
	 * a certain element (or activity).
	 * 
	 * @param index
	 *            activity whose input set must the searched.
	 * @param element
	 *            activity to be contained in the input subsets.
	 * @return a <code>HNSet</code> with the subsets that contain
	 *         <code>element</code>, or <code>null</code> otherwise.
	 */
	public abstract HNSet getInputSetsWithElement(int index, int element);

	/**
	 * Retrieves subsets from an output set of an activity. These subsets
	 * contain a certain element (or activity).
	 * 
	 * @param index
	 *            activity whose output set must the searched.
	 * @param element
	 *            activity to be contained in the output subsets.
	 * @return a <code>HNSet</code> with the subsets that contain
	 *         <code>element</code>, or <code>null</code> otherwise.
	 */

	public abstract HNSet getOutputSetsWithElement(int index, int element);

	/**
	 * Removes from this <code>HeuristicsNet</code> object all the arcs that
	 * have not been used up to a given threshold.
	 * 
	 * @param threshold
	 *            double value of the threshold to be used during the arc
	 *            pruning.
	 */
	public abstract void disconnectArcsUsedBelowThreshold(double threshold);

	/**
	 * Disconnects in this <code>HeuristicsNet</code> object the activities and
	 * arcs that are not used during the parsing of an event log.
	 * <p>
	 * <b> Note:</b> This method should be used with care since, at the starting
	 * of a parsing, no elements (activities or arcs) have been used!
	 */

	public abstract void disconnectUnusedElements();

	public abstract HNSubSet getStartActivities(Map<Integer, Integer> oldNewIndexMap);

	public abstract HNSubSet getEndActivities(Map<Integer, Integer> oldNewIndexMap);

	public abstract int[] getActivitiesActualFiring(Map<Integer, Integer> oldNewIndexMap, int newSize);

	public abstract DoubleMatrix2D getArcUsage(Map<Integer, Integer> oldNewIndexMap, int newSize);

}