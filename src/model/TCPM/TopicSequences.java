package model.TCPM;

import java.util.ArrayList;

public class TopicSequences {
	public int sequNum;
	public ArrayList<TopicSequence> sequences;
	public double[][] d2tDist;
	public double thr;

	public TopicSequences(PatientTraces pts, double[][] d2tDist, double thr) {
		this.thr = thr;
		sequences = new ArrayList<TopicSequence>();
		this.d2tDist = d2tDist;
		int dayNo = 0;
		for(PatientTrace pt : pts.traces) {
			TopicSequence ts = new TopicSequence(pt, d2tDist, dayNo, thr);
			addSequence(ts);
			dayNo += pt.dayNum;
		}
	}

	public void addSequence(TopicSequence ts) {
		sequences.add(ts);
		sequNum++;
	}
}
