package model.TCPM;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class TopicSequences {
	public int sequNum;
	public ArrayList<TopicSequence> sequences;
	public double[][] d2tDist;
	public double thr;
	public Map<String, TopicSequence> pID2sequence;

	public TopicSequences(TopicSequences tss) {	//copy a TopicSequences
		this.sequNum = tss.sequNum;
		this.sequences = new ArrayList<TopicSequence>();
		this.pID2sequence = new LinkedHashMap<>();
		for (TopicSequence ts : tss.sequences) {
			TopicSequence newTs = new TopicSequence(ts);
			this.pID2sequence.put(newTs.pID, newTs);
			this.sequences.add(newTs);
		}
		this.d2tDist = tss.d2tDist;
		this.thr = tss.thr;
	}

	public TopicSequences(PatientTraces pts, double[][] d2tDist, double thr) {
		this.thr = thr;
		this.sequences = new ArrayList<TopicSequence>();
		this.pID2sequence = new LinkedHashMap<>();
		this.d2tDist = d2tDist;
		int dayNo = 0;
		for (PatientTrace pt : pts.traces) {
			TopicSequence ts = new TopicSequence(pt, d2tDist, dayNo, thr);
			addSequence(ts);
			dayNo += pt.dayNum;
		}
	}

	public void addSequence(TopicSequence ts) {
		sequences.add(ts);
		pID2sequence.put(ts.pID, ts);
		sequNum++;
	}
}
