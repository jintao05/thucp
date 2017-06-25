package org.processmining.plugins.heuristicsnet.miner.heuristics.index;

import java.util.HashMap;

public class HeuristicsMinerIndex {

	private final HashMap<Integer, HeuristicsMinerIndex> indices;
	private HashMap<Integer, Double> values;

	public HeuristicsMinerIndex() {

		indices = new HashMap<Integer, HeuristicsMinerIndex>();
		values = null;
	}

	private void set(int matrix, double value) {

		if (values == null) {
			values = new HashMap<Integer, Double>();
		}

		values.put(matrix, value);
	}

	public void set(int matrix, int x, double value) {

		if (indices.containsKey(x)) {

			HeuristicsMinerIndex index = indices.get(x);
			index.set(matrix, value);
		} else {

			HeuristicsMinerIndex index = new HeuristicsMinerIndex();
			index.set(matrix, value);
			indices.put(x, index);
		}
	}

	public void set(int matrix, int x, int y, double value) {

		if (indices.containsKey(x)) {

			HeuristicsMinerIndex index = indices.get(x);
			index.set(matrix, y, value);
		} else {

			HeuristicsMinerIndex index = new HeuristicsMinerIndex();
			index.set(matrix, y, value);
			indices.put(x, index);
		}
	}

	//----------------------------------------------

	private Double get(int matrix) {

		if (values == null) {
			return new Double(0);
		} else {

			if (values.containsKey(matrix)) {
				return values.get(matrix);
			} else {
				return new Double(0);
			}
		}
	}

	public Double get(int matrix, int x) {

		if (indices.containsKey(x)) {

			HeuristicsMinerIndex index = indices.get(x);
			return index.get(matrix);
		} else {
			return new Double(0);
		}
	}

	public Double get(int matrix, int x, int y) {

		if (indices.containsKey(x)) {

			HeuristicsMinerIndex index = indices.get(x);
			return index.get(matrix, y);
		} else {
			return new Double(0);
		}
	}

	//----------------------------------------------

	private void increment(int matrix, double value) {

		if (values == null) {

			values = new HashMap<Integer, Double>();
			values.put(matrix, value);
		} else {

			if (values.containsKey(matrix)) {

				Double oldValue = values.get(matrix);
				oldValue += value;
			} else {
				values.put(matrix, value);
			}
		}

	}

	public void increment(int matrix, int x, double value) {

		if (indices.containsKey(x)) {

			HeuristicsMinerIndex index = indices.get(x);
			index.increment(matrix, value);
		} else {

			HeuristicsMinerIndex index = new HeuristicsMinerIndex();
			index.increment(matrix, value);
			indices.put(x, index);
		}
	}

	public void increment(int matrix, int x, int y, double value) {

		if (indices.containsKey(x)) {

			HeuristicsMinerIndex index = indices.get(x);
			index.increment(matrix, y, value);
		} else {

			HeuristicsMinerIndex index = new HeuristicsMinerIndex();
			index.increment(matrix, y, value);
			indices.put(x, index);
		}
	}
}
