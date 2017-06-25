package org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings;

import org.deckfour.xes.classification.XEventClassifier;
import org.processmining.plugins.heuristicsnet.miner.heuristics.HeuristicsMinerConstants;

public class HeuristicsMinerSettings {

	protected double relativeToBestThreshold = HeuristicsMinerConstants.RELATIVE_TO_BEST_THRESHOLD;
	protected int positiveObservationThreshold = HeuristicsMinerConstants.POSITIVE_OBSERVATIONS_THRESHOLD;
	protected double dependencyThreshold = HeuristicsMinerConstants.DEPENDENCY_THRESHOLD;
	protected double l1lThreshold = HeuristicsMinerConstants.L1L_THRESHOLD;
	protected double l2lThreshold = HeuristicsMinerConstants.L2L_THRESHOLD;
	protected double longDistanceThreshold = HeuristicsMinerConstants.LONG_DISTANCE_THRESHOLD;
	protected int dependencyDivisor = HeuristicsMinerConstants.DEPENDENCY_DIVISOR;
	protected double andThreshold = HeuristicsMinerConstants.AND_THRESHOLD;

	protected boolean extraInfo = false;
	protected boolean useAllConnectedHeuristics = true;
	protected boolean useLongDistanceDependency = false;
	
	protected XEventClassifier classifier;

	public XEventClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(XEventClassifier classifier) {
		this.classifier = classifier;
	}

	/*
	 * [HV] Added for Ticket #3037.
	 */
	private boolean checkBestAgainstL2L = true;
	
	//------------------

	public double getRelativeToBestThreshold() {
		return relativeToBestThreshold;
	}

	public void setRelativeToBestThreshold(double relativeToBestThreshold) {
		this.relativeToBestThreshold = relativeToBestThreshold;
	}

	public int getPositiveObservationThreshold() {
		return positiveObservationThreshold;
	}

	public void setPositiveObservationThreshold(int positiveObservationThreshold) {
		this.positiveObservationThreshold = positiveObservationThreshold;
	}

	public double getDependencyThreshold() {
		return dependencyThreshold;
	}

	public void setDependencyThreshold(double dependencyThreshold) {
		this.dependencyThreshold = dependencyThreshold;
	}

	public double getL1lThreshold() {
		return l1lThreshold;
	}

	public void setL1lThreshold(double threshold) {
		l1lThreshold = threshold;
	}

	public double getL2lThreshold() {
		return l2lThreshold;
	}

	public void setL2lThreshold(double threshold) {
		l2lThreshold = threshold;
	}

	public double getLongDistanceThreshold() {
		return longDistanceThreshold;
	}

	public void setLongDistanceThreshold(double longDistanceThreshold) {
		this.longDistanceThreshold = longDistanceThreshold;
	}

	public int getDependencyDivisor() {
		return dependencyDivisor;
	}

	public void setDependencyDivisor(int dependencyDivisor) {
		this.dependencyDivisor = dependencyDivisor;
	}

	public double getAndThreshold() {
		return andThreshold;
	}

	public void setAndThreshold(double andThreshold) {
		this.andThreshold = andThreshold;
	}

	public boolean isExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(boolean extraInfo) {
		this.extraInfo = extraInfo;
	}

	public boolean isUseAllConnectedHeuristics() {
		return useAllConnectedHeuristics;
	}

	public void setUseAllConnectedHeuristics(boolean useAllConnectedHeuristics) {
		this.useAllConnectedHeuristics = useAllConnectedHeuristics;
	}

	public boolean isUseLongDistanceDependency() {
		return useLongDistanceDependency;
	}

	public void setUseLongDistanceDependency(boolean useLongDistanceDependency) {
		this.useLongDistanceDependency = useLongDistanceDependency;
	}

	/*
	 * [HV] Updated for Ticket #3037.
	 */
	public String toString() {

		return "Relative to Best Threshold = " + relativeToBestThreshold + "\nPositive Observation Threshold = "
				+ positiveObservationThreshold + "\nDependency Threshold = " + dependencyThreshold
				+ "\nL1L Threshold = " + l1lThreshold + "\nL2L Threshold = " + l2lThreshold
				+ "\nLong Distance Threshold = " + longDistanceThreshold + "\nDependency Divisor = "
				+ dependencyDivisor + "\nAND Threshold = " + andThreshold + "\nCheck Best Against L2L = "
				+ checkBestAgainstL2L + "\n";
	}

	/*
	 * [HV] Added for Ticket #3037.
	 */
	public boolean isCheckBestAgainstL2L() {
		return checkBestAgainstL2L;
	}

	/*
	 * [HV] Added for Ticket #3037.
	 */
	public void setCheckBestAgainstL2L(boolean checkBestAgainstL2L) {
		this.checkBestAgainstL2L = checkBestAgainstL2L;
	}
}
