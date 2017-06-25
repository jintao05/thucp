package org.processmining.models.heuristics.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.heuristics.HeuristicsNet;

/**
 * Manages the marking of a given <code>HeuristicsNet</code> object.
 * 
 * @author Ana Karla Alves de Medeiros
 * 
 */
public class MarkingHeuristicsNet {

	private HashMap<HNSubSet, Integer>[] marking = null;
	private MappingToSubsets auxMapping = null;
	private int startPlace = 0;
	private int endPlace = 0;
	private HeuristicsNet hNet = null;
	private int numberTokens = 0;
	private int size = 0;
	private CombinationTasksToFire bestCombination = null;
	private Random generator = null;

	private HNSubSet possiblyEnabledElements = new HNSubSet();

	private static final int ROOT_TASK_ID = -1; //invalid index used as root.

	/**
	 * Builds an initial marking for a given <code>HeuristicsNet</code> object.
	 * 
	 * @param net
	 *            heuristics net object to which this marking will refer to
	 * @param generator
	 *            random generator used when modifying this marking
	 * @throws NullPointerException
	 *             if any input or output set of the given
	 *             <code>HeuristicsNet</code> object is <code>null</code>
	 */
	public MarkingHeuristicsNet(HeuristicsNet net, Random generator) throws NullPointerException {

		this.generator = generator;
		size = net.size();
		hNet = net;
		//checking if all input and output sets are DIFFERENT from null...
		for (int i = 0; i < size; i++) {
			if ((hNet.getInputSet(i) == null) || (hNet.getOutputSet(i) == null)) {
				throw new NullPointerException("Net has disconnected elements!!");
			}

		}
		createMarking();
	}

	/**
	 * Creates an initial marking.
	 */
	@SuppressWarnings("unchecked")
	private void createMarking() {

		HNSet set = null;

		marking = null;

		marking = new HashMap[size];

		//Building the marking data structure. This structure is an array of hashmaps.
		//For a given hashmap, we have:
		// - Key: Integer = element
		// - Value: HashMap = subsets + the number of tokens associated to them
		//The idea is to keep track of which tasks fired and the number of tokens at their output subsets.
		//Initially, the number of tokens is 0.
		for (int i = 0; i < size; i++) {

			set = hNet.getOutputSet(i);
			HashMap<HNSubSet, Integer> map = new HashMap<HNSubSet, Integer>();
			for (int j = 0; j < set.size(); j++) {
				map.put(set.get(j), new Integer(0));
			}
			marking[i] = map;
		}

		auxMapping = new MappingToSubsets(hNet);
		reset();
	}

	/**
	 * Sets this marking to its initial state
	 */
	public void reset() {

		Iterator<HNSubSet> outElements = null;
		HNSubSet outSubset = null;

		numberTokens = 1; //in the source place
		startPlace = 1;
		endPlace = 0;

		//initially, only the single start tasks is enabled
		possiblyEnabledElements = new HNSubSet();
		for (int i = 0; i < hNet.getStartActivities().size(); i++) {
			possiblyEnabledElements.add(hNet.getStartActivities().get(i));
		}
		for (int i = 0; i < size; i++) {
			outElements = marking[i].keySet().iterator();
			while (outElements.hasNext()) {
				outSubset = outElements.next();
				marking[i].put(outSubset, new Integer(0));
			}
			outElements = null;
		}

	}

	/**
	 * Creates a string representation of this marking
	 * 
	 * @return String representation of the current marking
	 */
	public String printCurrentMarking() {

		StringBuffer sb = new StringBuffer();

		sb.append("\n start place = ").append(startPlace);
		sb.append("\n end place = ").append(endPlace);

		for (int i = 0; i < size; i++) {

			sb.append("\n====>  task = ").append(i).append("\n       Marking: ");
			sb.append("\n       ").append(marking[i].toString());
		}

		return sb.toString();
	}

	/**
	 * Removes tokens from the output sets of given activities whenever these
	 * sets contain a given activity to be fired.
	 * 
	 * @param activityToFire
	 *            identifier of the activity to be fired
	 * @param activities
	 *            whose some output subsets contain the
	 *            <code>activityToFire</code>
	 */
	private void removeTokensOutputPlaces(int activityToFire, HNSubSet activities) {

		HNSubSet subset = null;
		HNSet subsets = null;

		Integer tokens = null;
		int taskToFire;
		int task;

		//Checking if element is a start element
		if (hNet.getInputSet(activityToFire).size() == 0) {
			if (startPlace > 0) {
				startPlace--;
				numberTokens--;
			}
		} else {
			taskToFire = activityToFire;
			for (int iTasks = 0; iTasks < activities.size(); iTasks++) {
				task = activities.get(iTasks);
				subsets = auxMapping.getRelatedSubsets(taskToFire, task);
				if (subsets != null) {
					for (int iSubsets = 0; iSubsets < subsets.size(); iSubsets++) {
						subset = subsets.get(iSubsets);
						tokens = getNumberTokens(task, subset);
						if (tokens.intValue() > 0) {
							decreaseNumberTokens(task, subset);
							numberTokens--;
						}
					}
				}
			}
		}
	}

	private int decreaseNumberTokens(int inElement, HNSubSet outElement) {
		int numTokens = 0;

		numTokens = getNumberTokens(inElement, outElement).intValue();
		numTokens--;
		setNumberTokens(inElement, outElement, new Integer(numTokens));

		return numTokens;
	}

	/**
	 * @throws java.lang.NullPointerException
	 *             when inElement is not in the hash. I.e, inElement has OUT=[]
	 * @throws java.lang.NumberFormatException
	 *             when outElement does not belong to the OUT set of inElement.
	 */
	private Integer getNumberTokens(int inElement, HNSubSet outElement) throws NullPointerException,
			NumberFormatException {
		return marking[inElement].get(outElement);
	}

	/**
	 * @throws java.lang.NullPointerException
	 *             when inElement is not in the hash. I.e, inElement has OUT=[]
	 * @throws java.lang.NumberFormatException
	 *             when outElement does not belong to the OUT set of inElement.
	 */
	private void setNumberTokens(int inElement, HNSubSet outElement, Integer numTokens) throws NullPointerException,
			NumberFormatException {
		marking[inElement].put(outElement, numTokens);
	}

	/**
	 * The number of tokens that this marking currently contains
	 * 
	 * @return number of tokens in this marking
	 */
	public int getNumberTokens() {
		return numberTokens;
	}

	/**
	 * Indicates if this marking is in a proper completion state
	 * 
	 * @return <code>true</code> if this marking is a proper completion state,
	 *         <code>false</code> otherwise
	 */
	public boolean properlyCompleted() {
		if ((endPlace == 1) && (numberTokens == 1)) {
			return true;
		}
		return false;
	}

	/**
	 * Indicates if the "end place" of <code>HeuristicsNet</code> object is
	 * marked
	 * 
	 * @return <code>true</code> if the end place of is marked,
	 *         <code>false</code> otherwise
	 */
	public boolean endPlace() {
		return (endPlace > 0);
	}

	/**
	 * Retrieves the number of tokens in the "end place" of the
	 * <code>HeuristicsNet</code> object of this marking
	 * 
	 * @return number of tokens contained in the end place
	 */
	public int getNumTokensEndPlace() {
		return endPlace;
	}

	/**
	 * Adds tokens to the output places of a given element
	 * 
	 * @param element
	 *            element to fire
	 */
	private void addTokensOutputPlaces(int element) {

		/*
		 * Pseudo-code: 1. Retrieve all elements in the OUT set of 'element' 2.
		 * Increase the number of tokens for every output element
		 */

		HNSet set = null;

		//update global counter for number of tokens
		//note that OR-situations count as a single token.

		set = hNet.getOutputSet(element);
		if (set.size() == 0) { //element is connected to the end place
			numberTokens++;
			endPlace++;
		} else {
			numberTokens += set.size();
		}

		//update marking...
		for (int iSet = 0; iSet < set.size(); iSet++) {
			increaseNumberTokens(element, set.get(iSet));

		}

	}

	private int increaseNumberTokens(int inElement, HNSubSet subset) {
		int numTokens = 0;

		numTokens = getNumberTokens(inElement, subset).intValue();
		numTokens++;
		setNumberTokens(inElement, subset, new Integer(numTokens));

		return numTokens;
	}

	/**
	 * Executes an activity linking to a given event in a log trace. The
	 * activity is executed even if it is not enabled in this marking.
	 * Additionally, in case there are multiple activities linking to this event
	 * (i.e., duplicates), the execution of a given activity is set by looking
	 * ahead in the log trace.
	 * 
	 * @param event
	 *            event linking to an activity to be fired.
	 * @param trace
	 *            process instance (or trace) where the element to be fired is
	 * @param eventPositionInTrace
	 *            position of the event in the process instance
	 * @return int number of tokens that needed to be added to this marking to
	 *         fire an activity linking to this event.
	 */
	public int fire(XEventClass event, XTrace trace, int eventPositionInTrace) {

		int addedTokens = 0;
		int elementDuplicates;

		HNSubSet activitiesForThisEvent = hNet.getActivitiesMappingStructures().getReverseActivitiesMapping()
				.get(event);

		if (activitiesForThisEvent.size() == 1) {
			//If the size is one, we can get the first element directly
			elementDuplicates = activitiesForThisEvent.get(0);
		} else {
			//identify which duplicate to fire
			HNSubSet duplicates = activitiesForThisEvent.deepCopy();

			//getting the duplicates that are enabled
			for (int i = 0; i < duplicates.size(); i++) {

				if (!isEnabled(duplicates.get(i))) {
					duplicates.remove(duplicates.get(i));
				}

			}

			if (duplicates.size() > 0) {
				if (duplicates.size() == 1) {
					elementDuplicates = duplicates.get(0);
				} else {
					//getting the output tasks of the duplicates. These output are used to
					//look ahead at the process instance
					Set<XEventClass> unionMappedToATEsCode = getAllOutputElementsOfDuplicates(duplicates);

					//advancing the pointer in the ATEntries till the current element + 1
					XEvent nextOutputEventInTrace = null;
					Iterator<XEvent> iterator = trace.iterator();
					int i = 0;
					int desiredPositionToStop = eventPositionInTrace + 1;
					while (iterator.hasNext() && (i < desiredPositionToStop)) {
						iterator.next();
						i++;
					}
					//iterator is now at the desired position
					do {
						try {
							nextOutputEventInTrace = iterator.next();
							if (unionMappedToATEsCode.contains(nextOutputEventInTrace)) {
								break;
							}
						} catch (IndexOutOfBoundsException ex) {
							break;
						}
					} while (iterator.hasNext());

					elementDuplicates = identifyDuplicateToFire(duplicates, nextOutputEventInTrace);
				}
			} else {
				//because no duplicate acitivity is enabled, a random one is chosen to fire...
				elementDuplicates = activitiesForThisEvent.get(generator.nextInt(activitiesForThisEvent.size()));
			}

		}

		bestCombination = findBestSetTasks(elementDuplicates);
		addedTokens += bestCombination.getNumberMissingTokens();
		removeTokensOutputPlaces(elementDuplicates, bestCombination.getTasks());
		addTokensOutputPlaces(elementDuplicates);
		addToPossiblyEnabledElements(elementDuplicates);

		//registering the firing of element...
		//TODO - Update this line to actually get the number
		//of grouped trace. For now, we simply return 1.
		int numberOfGroupedTraces = 1;
		hNet.increaseElementActualFiring(elementDuplicates, numberOfGroupedTraces);
		hNet.increaseArcUsage(bestCombination.getElementToFire(), bestCombination.getTasks(), numberOfGroupedTraces);

		return addedTokens;
	}

	private int identifyDuplicateToFire(HNSubSet duplicates, XEvent nextOutputElementInTrace) {

		HNSubSet candidateDuplicates = new HNSubSet();
		HNSubSet allElements;

		for (int i = 0; i < duplicates.size(); i++) {
			allElements = hNet.getAllElementsOutputSet(duplicates.get(i));
			for (int j = 0; j < allElements.size(); j++) {
				if (nextOutputElementInTrace == hNet.getActivitiesMappingStructures().getActivitiesMapping()[allElements
						.get(j)]) {
					candidateDuplicates.add(duplicates.get(i));
					break;
				}
			}
		}

		if (candidateDuplicates.size() <= 0) {
			candidateDuplicates = duplicates; //we can choose any of the tasks because none has
			//followers in the process instance...
		}

		return candidateDuplicates.get(generator.nextInt(candidateDuplicates.size()));
	}

	private Set<XEventClass> getAllOutputElementsOfDuplicates(HNSubSet duplicates) {
		//Returns the union set of the output tasks of the tasks in "duplicates".
		//The returned union set has already the codes mapped to the ATEs in the log!
		Set<XEventClass> union = new HashSet<XEventClass>();
		HNSubSet allElements;

		for (int i = 0; i < duplicates.size(); i++) {
			allElements = hNet.getAllElementsOutputSet(duplicates.get(i));
			for (int j = 0; j < allElements.size(); j++) {
				union.add(hNet.getActivitiesMappingStructures().getActivitiesMapping()[allElements.get(j)]);
			}
		}

		return union;
	}

	private void addToPossiblyEnabledElements(int element) {
		HNSubSet subset = hNet.getAllElementsOutputSet(element);
		for (int i = 0; i < subset.size(); i++) {
			possiblyEnabledElements.add(subset.get(i));
		}
	}

	private CombinationTasksToFire findBestSetTasks(int element) {
		CombinationTasksToFire bCombination = null;
		CombinationTasksToFire combination = null;
		HNSubSet noTokensFromTasks = null;
		HNSubSet treatedTasks = null;
		HNSet inputSet = null;
		HNSet temp_inputSet = null;

		HNSubSet subset = null;

		int numberMissingTokens = 0;
		int rootTask = ROOT_TASK_ID;

		bCombination = new CombinationTasksToFire();

		inputSet = hNet.getInputSet(element);

		if (inputSet.size() == 0) {
			if (startPlace <= 0) {
				numberMissingTokens++; //one token is missing
			}
		} else {
			//inputSubset is not empty. Search for tasks that "have tokens" to element

			noTokensFromTasks = getTasksWithEmptyOutputPlaces(element);

			//>>>>>>>>>>>>>>>>>> Hint!!! I think that's why I don't run into problems...
			///// Idea -> shrink the subsets without using a temp variable, get
			/// the size before shrinking, shrink them, reorder the set and
			///remove the empty set (do this via a method in the class
			///HNSet, get the new size. This is the number of missing tokens.

			//make a copy to avoid destroying the original net
			inputSet = inputSet.deepCopy();
			temp_inputSet = new HNSet();
			//removing the tasks whose output subsets that contain element are empty
			for (int iInputSubsets = 0; iInputSubsets < inputSet.size(); iInputSubsets++) {
				subset = inputSet.get(iInputSubsets);
				subset.removeAll(noTokensFromTasks);
				if (subset.size() == 0) {
					numberMissingTokens += 1;
				} else {
					temp_inputSet.add(subset);
				}
			}
			inputSet = temp_inputSet;
			//retrieving the best combination of tasks that can fire to enable element

			if (inputSet.size() > 0) {
				combination = new CombinationTasksToFire();
				treatedTasks = new HNSubSet();
				bCombination = findBestCombination(bCombination, inputSet, rootTask, combination, treatedTasks);
			}
		}

		bCombination.setElementToFire(element);
		bCombination.setTokens(bCombination.getNumberMissingTokens() + numberMissingTokens);

		return bCombination;
	}

	private CombinationTasksToFire findBestCombination(CombinationTasksToFire bCombination, HNSet inputSet,
			int rootTask, CombinationTasksToFire combination, HNSubSet treatedTasks) {

		int task = -1;
		HNSet alreadyMarkedPlaces = null;
		HNSet temp_inputSet = null;
		HNSubSet noTokensFromTasks = null;
		HNSubSet subset = null;

		if ((bCombination.getTasks().size() == 0)
				|| (bCombination.getNumberMissingTokens() > combination.getNumberMissingTokens())) {
			if (rootTask != ROOT_TASK_ID) {
				alreadyMarkedPlaces = getAlreadyMarkedPlaces(inputSet, rootTask);
				noTokensFromTasks = HNSet.getUnionSet(alreadyMarkedPlaces);
				inputSet.removeAll(alreadyMarkedPlaces);
				combination.getTasks().add(rootTask);
			}

			if (inputSet.size() == 0) {
				bCombination = combination.copy();
			} else {

				//akam: I stoppe here - 10/07/2005
				if (rootTask != ROOT_TASK_ID) {
					temp_inputSet = new HNSet();
					for (int iInputSet = 0; iInputSet < inputSet.size(); iInputSet++) {
						subset = inputSet.get(iInputSet);
						subset.removeAll(noTokensFromTasks);
						subset.removeAll(treatedTasks);
						if (subset.size() == 0) {
							combination.setTokens(combination.getNumberMissingTokens() + 1);
						} else {
							temp_inputSet.add(subset);
						}
					}
					inputSet = temp_inputSet;
				}

				for (int iInputSet = 0; iInputSet < inputSet.size(); iInputSet++) {
					subset = inputSet.get(iInputSet);
					while (subset.size() > 0) {
						task = subset.get(generator.nextInt(subset.size()));
						bCombination = findBestCombination(bCombination, inputSet.deepCopy(), task, combination.copy(),
								treatedTasks.deepCopy());
						treatedTasks.add(task);
						subset.remove(task);

					}
				}
			}
		}

		return bCombination;
	}

	private HNSet getAlreadyMarkedPlaces(HNSet set, int task) {
		HNSet markedPlaces = null;
		HNSubSet subset = null;

		markedPlaces = new HNSet();

		for (int iSet = 0; iSet < set.size(); iSet++) {
			subset = set.get(iSet);
			if (subset.contains(task)) {
				markedPlaces.add(subset);
			}

		}

		return markedPlaces;

	}

	private HNSubSet getTasksWithEmptyOutputPlaces(int task) {

		HNSubSet tasksEmptyOutPlaces;
		HNSubSet inputTasks;

		int inputTask;
		HNSet outputSubsets;

		inputTasks = hNet.getAllElementsInputSet(task);

		tasksEmptyOutPlaces = new HNSubSet();

		for (int iInputTasks = 0; iInputTasks < inputTasks.size(); iInputTasks++) {
			inputTask = inputTasks.get(iInputTasks);
			outputSubsets = auxMapping.getRelatedSubsets(task, inputTask);
			if (outputSubsets != null) {
				if (!allSubsetsAreMarked(inputTask, outputSubsets)) {
					tasksEmptyOutPlaces.add(inputTask);
				}
			} else {
				tasksEmptyOutPlaces.add(inputTask);
			}
		}

		return tasksEmptyOutPlaces;

	}

	private boolean allSubsetsAreMarked(int inputTask, HNSet outputSet) {

		HNSubSet subset = null;

		for (int iOutputSet = 0; iOutputSet < outputSet.size(); iOutputSet++) {
			subset = outputSet.get(iOutputSet);
			if (marking[inputTask].get(subset).intValue() <= 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks if an activity is enabled at this marking.
	 * 
	 * @param activity
	 *            identifier of the activity to check.
	 * @return <code>true</code> if the activity is enabled at the this marking,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEnabled(int activity) {
		if (hNet.getInputSet(activity) == null) {
			return false;
		}

		if (hNet.getInputSet(activity).size() == 0) {
			if (startPlace < 1) {
				return false;
			}
		} else {
			bestCombination = findBestSetTasks(activity);
			if (bestCombination.getNumberMissingTokens() > 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Retrieves the current enabled activities for this current marking.
	 * 
	 * @return set of currently enabled activities
	 */

	public HNSubSet getCurrentEnabledElements() {

		int element = -1;
		HNSubSet disabledElements = null;

		disabledElements = new HNSubSet();
		for (int iPossiblyEnabledElements = 0; iPossiblyEnabledElements < possiblyEnabledElements.size(); iPossiblyEnabledElements++) {
			element = possiblyEnabledElements.get(iPossiblyEnabledElements);
			if (!isEnabled(element)) {
				disabledElements.add(element);
			}
		}

		possiblyEnabledElements.removeAll(disabledElements);

		return possiblyEnabledElements;

	}

	/**
	 * Retrieves the number of currently enabled activities at this marking.
	 * 
	 * @return current number of enabled activities
	 */
	public int getCurrentNumEnabledElements() {
		return getCurrentEnabledElements().size();
	}

}

class MappingToSubsets {

	private HNSet[][] mapping = null;

	public MappingToSubsets(HeuristicsNet hn) {
		buildMapping(hn);
	}

	private void buildMapping(HeuristicsNet hn) {

		HNSet outputSubsets;
		HNSubSet outputSubset;
		int taskInSubset;

		mapping = new HNSet[hn.size()][hn.size()];

		for (int task = 0; task < hn.size(); task++) {
			outputSubsets = hn.getOutputSet(task);
			for (int iOutputSubsets = 0; iOutputSubsets < outputSubsets.size(); iOutputSubsets++) {
				//inserting for every element
				outputSubset = outputSubsets.get(iOutputSubsets);
				for (int iSubset = 0; iSubset < outputSubset.size(); iSubset++) {
					taskInSubset = outputSubset.get(iSubset);
					insertInMapping(taskInSubset, task, outputSubset);
				}
			}
		}
	}

	private void insertInMapping(int taskInSubset, int task, HNSubSet subset) {

		HNSet hSet = mapping[taskInSubset][task];

		if (hSet == null) {
			hSet = new HNSet();
		}

		hSet.add(subset);
		mapping[taskInSubset][task] = hSet;

	}

	/**
	 * Returns the output subsets of <i>task</i> that contains
	 * <i>taskInSubset</i>
	 * 
	 * @return a HashSet with the subsets. Returns null if there are no subsets.
	 */
	public HNSet getRelatedSubsets(int taskInSubset, int task) {
		return mapping[taskInSubset][task];
	}
}

class CombinationTasksToFire {

	private HNSubSet tasks = null;
	private int numberMissingTokens = 0;
	private int elementToFire = -1;

	public CombinationTasksToFire() {
		tasks = new HNSubSet();
	}

	/**
	 * This method sets the element that is going to fire for this combination
	 * of tasks. This method is useful when dealing with duplicate tasks because
	 * we precisely know which duplicate requires this combination. Note: This
	 * method is useful when counting arc usage for an individual!
	 */

	public void setElementToFire(int element) {
		elementToFire = element;
	}

	/**
	 * This method returns the element that is going to fire for this
	 * combination of tasks. This method is useful when dealing with duplicate
	 * tasks because we precisely know which duplicate requires this
	 * combination. Note: This method is useful when counting arc usage for an
	 * individual!
	 * 
	 * @return -1 if no element has been set.
	 */
	public int getElementToFire() {
		return elementToFire;
	}

	/**
	 * Returns the stored set of tasks;
	 */
	public HNSubSet getTasks() {
		return tasks;
	}

	/**
	 * Returns the stored number of missing tokens;
	 */
	public int getNumberMissingTokens() {
		return numberMissingTokens;
	}

	/**
	 * Sets stored the set of task.
	 */

	public void setTasks(HNSubSet newSetOftasks) {
		tasks = newSetOftasks;
	}

	/**
	 * Sets the stored number of missing tokens. The number of missing tokens
	 * ranges from 0 to maximum integer. So, if <i>newNumberMissingTokens</i> is
	 * smaller than 0, the number of missing tokens is automatically set to 0.
	 * 
	 * @return the number of missing tokens.
	 */

	public int setTokens(int newNumberMissingTokens) {
		if (newNumberMissingTokens > 0) {
			numberMissingTokens = newNumberMissingTokens;
		} else {
			numberMissingTokens = 0;
		}
		return numberMissingTokens;
	}

	/**
	 * Makes a deep copy of this object.
	 */
	protected Object clone() {

		CombinationTasksToFire clone = new CombinationTasksToFire();

		clone.setTokens(getNumberMissingTokens());
		clone.setTasks(getTasks().deepCopy());

		return clone;
	}

	/**
	 * Returns the casted Object from the method clone().
	 */
	public CombinationTasksToFire copy() {
		return (CombinationTasksToFire) clone();

	}

	/**
	 * Returns the String representation of CombinationTasksToFire. The format
	 * is "[set of tasks to fire] : number_of_missing_tokens"
	 * 
	 * @return
	 */
	public String toString() {
		return tasks.toString() + " : " + numberMissingTokens;
	}
}
