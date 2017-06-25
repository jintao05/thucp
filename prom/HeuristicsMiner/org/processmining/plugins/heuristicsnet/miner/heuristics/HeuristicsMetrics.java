package org.processmining.plugins.heuristicsnet.miner.heuristics;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.processmining.models.heuristics.impl.HNSubSet;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

public class HeuristicsMetrics {

private int eventsNumber, tracesNumber, instancesNumber;
	
	private DoubleMatrix1D eventCount, startCount, endCount;
	private DoubleMatrix2D directSuccessionCount;
	private DoubleMatrix2D succession2Count;
	
	private DoubleMatrix2D longRangeSuccessionCount;
	private DoubleMatrix2D longRangeDependencyMeasures;
	
	private DoubleMatrix1D L1LdependencyMeasuresAll;
	private DoubleMatrix2D L2LdependencyMeasuresAll;
	private DoubleMatrix2D ABdependencyMeasuresAll;

	private DoubleMatrix2D dependencyMeasuresAccepted;

	private DoubleMatrix2D andInMeasuresAll, andOutMeasuresAll;
	
	private DoubleMatrix2D noiseCounters;
	
	
	private boolean[] L1Lrelation;
	private int[] L2Lrelation;
	double[] bestInputMeasure, bestOutputMeasure;
	int[] bestInputEvent, bestOutputEvent;
	private boolean[] alwaysVisited;

	HNSubSet[] inputSet, outputSet;
	
	int bestStart, bestEnd;
	
	
	public HeuristicsMetrics(XLogInfo logInfo){
		
		eventsNumber = logInfo.getEventClasses().size();
		instancesNumber = logInfo.getNumberOfEvents();
		tracesNumber = logInfo.getNumberOfTraces();
		
		longRangeSuccessionCount = DoubleFactory2D.dense.make(eventsNumber, eventsNumber, 0);
		longRangeDependencyMeasures = DoubleFactory2D.dense.make(eventsNumber, eventsNumber, 0);
		L1LdependencyMeasuresAll = DoubleFactory1D.sparse.make(eventsNumber, 0);
		andInMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		andOutMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);

		L2LdependencyMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		ABdependencyMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		dependencyMeasuresAccepted = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		noiseCounters = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);

		eventCount = DoubleFactory1D.sparse.make(eventsNumber, 0);
		startCount = DoubleFactory1D.sparse.make(eventsNumber, 0);
		endCount = DoubleFactory1D.sparse.make(eventsNumber, 0);
		directSuccessionCount = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		succession2Count = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		
		L1Lrelation = new boolean[eventsNumber];
		L2Lrelation = new int[eventsNumber];
		bestInputMeasure = new double[eventsNumber];
		bestOutputMeasure = new double[eventsNumber];
		bestInputEvent = new int[eventsNumber];
		bestOutputEvent = new int[eventsNumber];
		alwaysVisited = new boolean[eventsNumber];

		inputSet = new HNSubSet[eventsNumber];
		outputSet = new HNSubSet[eventsNumber];
		
		bestStart = 0;
		bestEnd = 0;
	}
	
public HeuristicsMetrics(XLogInfo logInfo, XEventClassifier classifier){
		
		eventsNumber = logInfo.getEventClasses(classifier).size();
		instancesNumber = logInfo.getNumberOfEvents();
		tracesNumber = logInfo.getNumberOfTraces();
		
		longRangeSuccessionCount = DoubleFactory2D.dense.make(eventsNumber, eventsNumber, 0);
		longRangeDependencyMeasures = DoubleFactory2D.dense.make(eventsNumber, eventsNumber, 0);
		L1LdependencyMeasuresAll = DoubleFactory1D.sparse.make(eventsNumber, 0);
		andInMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		andOutMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);

		L2LdependencyMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		ABdependencyMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		dependencyMeasuresAccepted = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		noiseCounters = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);

		eventCount = DoubleFactory1D.sparse.make(eventsNumber, 0);
		startCount = DoubleFactory1D.sparse.make(eventsNumber, 0);
		endCount = DoubleFactory1D.sparse.make(eventsNumber, 0);
		directSuccessionCount = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		succession2Count = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		
		L1Lrelation = new boolean[eventsNumber];
		L2Lrelation = new int[eventsNumber];
		bestInputMeasure = new double[eventsNumber];
		bestOutputMeasure = new double[eventsNumber];
		bestInputEvent = new int[eventsNumber];
		bestOutputEvent = new int[eventsNumber];
		alwaysVisited = new boolean[eventsNumber];

		inputSet = new HNSubSet[eventsNumber];
		outputSet = new HNSubSet[eventsNumber];
		
		bestStart = 0;
		bestEnd = 0;
	}
	
	public HeuristicsMetrics(int nEventClasses){
		eventsNumber = nEventClasses;
		instancesNumber = 0;
		tracesNumber = 0;
		longRangeSuccessionCount = DoubleFactory2D.dense.make(eventsNumber, eventsNumber, 0);
		longRangeDependencyMeasures = DoubleFactory2D.dense.make(eventsNumber, eventsNumber, 0);
		L1LdependencyMeasuresAll = DoubleFactory1D.sparse.make(eventsNumber, 0);
		andInMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		andOutMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);

		L2LdependencyMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		ABdependencyMeasuresAll = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		dependencyMeasuresAccepted = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		noiseCounters = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);

		eventCount = DoubleFactory1D.sparse.make(eventsNumber, 0);
		startCount = DoubleFactory1D.sparse.make(eventsNumber, 0);
		endCount = DoubleFactory1D.sparse.make(eventsNumber, 0);
		directSuccessionCount = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		succession2Count = DoubleFactory2D.sparse.make(eventsNumber, eventsNumber, 0);
		L1Lrelation = new boolean[eventsNumber];
		L2Lrelation = new int[eventsNumber];
		bestInputMeasure = new double[eventsNumber];
		bestOutputMeasure = new double[eventsNumber];
		bestInputEvent = new int[eventsNumber];
		bestOutputEvent = new int[eventsNumber];
		alwaysVisited = new boolean[eventsNumber];

		inputSet = new HNSubSet[eventsNumber];
		outputSet = new HNSubSet[eventsNumber];
		bestStart = 0;
		bestEnd = 0;
	}

	
	//LongRangeSuccessionCount (1,1,)
	public double getLongRangeSuccessionCount(int x, int y){
		return this.longRangeSuccessionCount.get(x, y);
	}
	
	public void incrementLongRangeSuccessionCount(int x, int y, double value){
		this.longRangeSuccessionCount.set(x, y, (this.longRangeSuccessionCount.get(x, y) + value));
	}
	
	public void setLongRangeSuccessionCount(int x, int y, double value){
		this.longRangeSuccessionCount.set(x, y, value);
	}
	
	//EventCount (1,1,)
	public double getEventCount(int x){
		return this.eventCount.get(x);
	}
	
	public void incrementEventCount(int x, double value){
		this.eventCount.set(x, (this.eventCount.get(x) + value));
	}
	
	public void setEventCount(int x, double value){
		this.eventCount.set(x, value);
	}
	
	//StartCount (1,1,)
	public double getStartCount(int x){
		return this.startCount.get(x);
	}
	
	public DoubleMatrix1D getStartCounter(){ return this.startCount; }
	
	public void incrementStartCount(int x, double value){
		this.startCount.set(x, (this.startCount.get(x) + value));
	}
	
	public void setStartCount(int x, double value){
		this.startCount.set(x, value);
	}
	
	//EndCount (1,1,)
	public double getEndCount(int x){
		return this.endCount.get(x);
	}
	
	public DoubleMatrix1D getEndCounter(){ return this.endCount; }
	
	public void incrementEndCount(int x, double value){
		this.endCount.set(x, (this.endCount.get(x) + value));
	}
	
	public void setEndCount(int x, double value){
		this.endCount.set(x, value);
	}
	
	//DirectSuccessionCount (1,1,)
	public double getDirectSuccessionCount(int x, int y){
		return this.directSuccessionCount.get(x, y);
	}
	
	public void incrementDirectSuccessionCount(int x, int y, double value){
		this.directSuccessionCount.set(x, y, (this.directSuccessionCount.get(x, y) + value));
	}
	
	public void setDirectSuccessionCount(int x, int y, double value){
		this.directSuccessionCount.set(x, y, value);
	}
	
	//L1LdependencyMeasuresAll (1,1,1)
	public double getL1LdependencyMeasuresAll(int x){
		return this.L1LdependencyMeasuresAll.get(x);
	}
	
	public void incrementL1LdependencyMeasuresAll(int x, double value){
		this.L1LdependencyMeasuresAll.set(x, (this.L1LdependencyMeasuresAll.get(x) + value));
	}
	
	public void setL1LdependencyMeasuresAll(int x, double value){
		this.L1LdependencyMeasuresAll.set(x, value);
	}
	
	//L2LdependencyMeasuresAll (,,1)
	public double getL2LdependencyMeasuresAll(int x, int y){
		return this.L2LdependencyMeasuresAll.get(x, y);
	}
	
	public void incrementL2LdependencyMeasuresAll(int x, int y, double value){
		this.L2LdependencyMeasuresAll.set(x, y, (this.L2LdependencyMeasuresAll.get(x, y) + value));
	}
	
	public void setL2LdependencyMeasuresAll(int x, int y, double value){
		this.L2LdependencyMeasuresAll.set(x, y, value);
	}
	
	//LongRangeDependencyMeasures (,,1)
	public double getLongRangeDependencyMeasures(int x, int y){
		return this.longRangeDependencyMeasures.get(x, y);
	}
	
	public void incrementLongRangeDependencyMeasures(int x, int y, double value){
		this.longRangeDependencyMeasures.set(x, y, (this.longRangeDependencyMeasures.get(x, y) + value));
	}
	
	public void setLongRangeDependencyMeasures(int x, int y, double value){
		this.longRangeDependencyMeasures.set(x, y, value);
	}
	
	
	//AndInMeasuresAll (,,1)
	public double getAndInMeasuresAll(int x, int y){
		return this.andInMeasuresAll.get(x, y);
	}
	
	public void incrementAndInMeasuresAll(int x, int y, double value){
		this.andInMeasuresAll.set(x, y, (this.andInMeasuresAll.get(x, y) + value));
	}
	
	public void setAndInMeasuresAll(int x, int y, double value){
		this.andInMeasuresAll.set(x, y, value);
	}
	
	
	//AndOutMeasuresAll (,,1)
	public double getAndOutMeasuresAll(int x, int y){
		return this.andOutMeasuresAll.get(x, y);
	}
	
	public void incrementAndOutMeasuresAll(int x, int y, double value){
		this.andOutMeasuresAll.set(x, y, (this.andOutMeasuresAll.get(x, y) + value));
	}
	
	public void setAndOutMeasuresAll(int x, int y, double value){
		this.andOutMeasuresAll.set(x, y, value);
	}

	
	//Succession2Count (1,1,)
	public double getSuccession2Count(int x, int y){
		return this.succession2Count.get(x, y);
	}
	
	public void incrementSuccession2Count(int x, int y, double value){
		this.succession2Count.set(x, y, (this.succession2Count.get(x, y) + value));
	}
	
	public void setSuccession2Count(int x, int y, double value){
		this.succession2Count.set(x, y, value);
	}

	//DependencyMeasuresAccepted (1,,1)
	public DoubleMatrix2D getDependencyMeasuresAccepted(){
		return this.dependencyMeasuresAccepted;
	}
	
	public double getDependencyMeasuresAccepted(int x, int y){
		return this.dependencyMeasuresAccepted.get(x, y);
	}
	
	public void incrementDependencyMeasuresAccepted(int x, int y, double value){
		this.dependencyMeasuresAccepted.set(x, y, (this.dependencyMeasuresAccepted.get(x, y) + value));
	}
	
	public void setDependencyMeasuresAccepted(int x, int y, double value){
		this.dependencyMeasuresAccepted.set(x, y, value);
	}
	
	//ABdependencyMeasuresAll (,,1)
	public double getABdependencyMeasuresAll(int x, int y){
		return this.ABdependencyMeasuresAll.get(x, y);
	}
	
	public void incrementABdependencyMeasuresAll(int x, int y, double value){
		this.ABdependencyMeasuresAll.set(x, y, (this.ABdependencyMeasuresAll.get(x, y) + value));
	}
	
	public void setABdependencyMeasuresAll(int x, int y, double value){
		this.ABdependencyMeasuresAll.set(x, y, value);
	}
	
	//NoiseCounters (1,,1)
	public double getNoiseCounters(int x, int y){
		return this.noiseCounters.get(x, y);
	}
	
	public void incrementNoiseCounters(int x, int y, double value){
		this.noiseCounters.set(x, y, (this.noiseCounters.get(x, y) + value));
	}
	
	public void setNoiseCounters(int x, int y, double value){
		this.noiseCounters.set(x, y, value);
	}
	
	//--------------------------------------
	
	// (1,1)
	public boolean getL1Lrelation(int x){ return this.L1Lrelation[x]; }
	public void setL1Lrelation(int x, boolean value){ this.L1Lrelation[x] = value; }
	
	// (1,1)
	public int getL2Lrelation(int x){ return this.L2Lrelation[x]; }
	public void setL2Lrelation(int x, int value){ this.L2Lrelation[x] = value; }
	
	// (1,1)
	public double getBestInputMeasure(int x){ return this.bestInputMeasure[x]; }
	public void setBestInputMeasure(int x, double value){ this.bestInputMeasure[x] = value; }
	
	// (1,1)
	public double getBestOutputMeasure(int x){ return this.bestOutputMeasure[x]; }
	public void setBestOutputMeasure(int x, double value){ this.bestOutputMeasure[x] = value; }
	
	// (1,1)
	public int getBestInputEvent(int x){ return this.bestInputEvent[x]; }
	public void setBestInputEvent(int x, int value){ this.bestInputEvent[x] = value; }

	// (1,1)
	public int getBestOutputEvent(int x){ return this.bestOutputEvent[x]; }
	public void setBestOutputEvent(int x, int value){ this.bestOutputEvent[x] = value; }
	
	// (1,1)
	public boolean getAlwaysVisited(int x){ return this.alwaysVisited[x]; }
	public void setAlwaysVisited(int x, boolean value){ this.alwaysVisited[x] = value; }
	
	// (1)
	public void initAdjacencies(int x){
		
		this.L1Lrelation[x] = false;
		this.L2Lrelation[x] = -1;
		this.bestInputMeasure[x] = -10;
		this.bestOutputMeasure[x] = -10;
		this.bestInputEvent[x] = -1;
		this.bestOutputEvent[x] = -1;

		this.inputSet[x] = new HNSubSet();
		this.outputSet[x] = new HNSubSet();
	}
	
	//--------------------------------------

	// (1,1,)
	public int getEventsNumber(){ return this.eventsNumber; }
	public int getTracesNumber(){ return this.tracesNumber; }
	public int getInstancesNumber(){ return this.instancesNumber; }
	
	// (,12,1,2?,12,1)
	public HNSubSet[] getInputSet(){ return this.inputSet; }
	public HNSubSet getInputSet(int x){ return this.inputSet[x]; }
	public void addInputSet(int x, int value){ this.inputSet[x].add(value); }
	public HNSubSet[] getOutputSet(){ return this.outputSet; }
	public HNSubSet getOutputSet(int x){ return this.outputSet[x]; }
	public void addOutputSet(int x, int value){ this.outputSet[x].add(value); }
	
	// (1,1)
	public int getBestStart(){ return this.bestStart; }
	public void setBestStart(int value){ this.bestStart = value; }
	
	// (1,1)
	public int getBestEnd(){ return this.bestEnd; }
	public void setBestEnd(int value){ this.bestEnd = value; }
	
	//--------------------------------------
	
	public void printData(){
		
		System.out.println("Events Number: " + eventsNumber);
		System.out.println("Traces Number: " + tracesNumber);
		System.out.println("Instances Number: " + instancesNumber + "\n");

		System.out.println("Long Range Succession Count\n" + longRangeSuccessionCount + "\n");
		System.out.println("Long Range Dependency Measures\n" + longRangeDependencyMeasures + "\n");
		System.out.println("L1L Dependency Measures All\n" + L1LdependencyMeasuresAll + "\n");
		System.out.println("And In Measures All\n" + andInMeasuresAll + "\n");
		System.out.println("And Out Measures All\n" + andOutMeasuresAll + "\n");
		System.out.println("L2L Dependency Measures All\n" + L2LdependencyMeasuresAll + "\n");
		System.out.println("AB Dependency Measures All\n" + ABdependencyMeasuresAll + "\n");
		System.out.println("Dependency Measures Accepted\n" + dependencyMeasuresAccepted + "\n");
		System.out.println("Noise Counters\n" + noiseCounters + "\n");

		System.out.println("Event Counter\n" + eventCount + "\n");
		System.out.println("Start Counter\n" + startCount + "\n");
		System.out.println("End Counter\n" + endCount + "\n");
		System.out.println("Direct Succession Counter\n" + directSuccessionCount + "\n");
		System.out.println("Succession 2 Counter\n" + succession2Count + "\n");

		System.out.println("L1L Relation");
		for (boolean value : L1Lrelation) {
			System.out.print(value + " ");
		}
		System.out.println();
		System.out.println("\nL2L Relation");
		for (int value : L2Lrelation) {
			System.out.print(value + " ");
		}
		System.out.println();
		System.out.println("\nBest Input Measure");
		for (double value : bestInputMeasure) {
			System.out.print(value + " ");
		}
		System.out.println();
		System.out.println("\nBest Output Measure");
		for (double value : bestOutputMeasure) {
			System.out.print(value + " ");
		}
		System.out.println();
		System.out.println("\nBest Input Event");
		for (int value : bestInputEvent) {
			System.out.print(value + " ");
		}
		System.out.println();
		System.out.println("\nBest Output Event");
		for (int value : bestOutputEvent) {
			System.out.print(value + " ");
		}
		System.out.println();
		System.out.println("\nInput Set");
		for (HNSubSet value : inputSet) {
			System.out.print(value.toString() + " ");
		}
		System.out.println();
		System.out.println("\nOutput Set");
		for (HNSubSet value : outputSet) {
			System.out.print(value.toString() + " ");
		}
		System.out.println();
	}
}
