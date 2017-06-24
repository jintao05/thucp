package model.TCPM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopicSequence {
	public String pID;
	public ArrayList<String> tLabels;
	public double thr;

	public TopicSequence(TopicSequence ts) {	//copy a TopicSequence
		this.pID = ts.pID;
		this.tLabels = new ArrayList<String>();
		this.tLabels.addAll(ts.tLabels);
		this.thr = ts.thr;
	}

	public TopicSequence(PatientTrace pt, double[][] d2tDist, int dayNo, double thr) {
		this.pID = pt.pID;
		this.thr = thr;
		this.tLabels = new ArrayList<String>();
		for (int i = 0; i < pt.dayNum; i++) {
			double[] dist = d2tDist[dayNo + i];
			String tLabel = calcTopicLabel(dist);
			this.tLabels.add(tLabel);
		}
	}

	public String calcTopicLabel(double[] dist) {
		String tLabel = "";
		Map<Integer, Double> idx2topic = new HashMap<Integer, Double>();
		for (int i = 0; i < dist.length; i++) {
			idx2topic.put(i, dist[i]);
		}

		// sort the map by value
		List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(idx2topic.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				// return o1.getValue().compareTo(o2.getValue());
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		tLabel += list.get(0).getKey();
		double max = list.get(0).getValue();
		for (int i = 1; i < list.size(); i++) {
			Map.Entry<Integer, Double> en = list.get(i);
			int key = en.getKey();
			double value = en.getValue();
			if (value / max >= thr) {
				tLabel += "," + key;
			}
		}

		return tLabel;
	}

	@Override
	public String toString() {
		String str = "";
		for (String tLabel : tLabels) {
			str += tLabel + "\t";
		}
		str = str.substring(0, str.length() - 1);

		return str;
	}
}
