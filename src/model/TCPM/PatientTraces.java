package model.TCPM;

import java.util.ArrayList;
import java.util.Arrays;

public class PatientTraces {
	//public String studyName;
	public int traceNum;
	public ArrayList<PatientTrace> traces;

	public PatientTraces(ArrayList<String[]> pditemArray) {
		traces = new ArrayList<PatientTrace>();
		String prePID = "";
		PatientTrace pt = null;
		for(String[] strs : pditemArray) {
			String pID = strs[0];
			String date = strs[1];
			ArrayList<String> activities = new ArrayList<String>(Arrays.asList(strs[2].split(" ")));
			PatientDay pd = new PatientDay(pID, date, activities);
			if(!pID.equals(prePID)) {
				if(pt != null) {
					addTrace(pt);
				}
				pt = new PatientTrace(pID);
				pt.addDay(pd);
				prePID = pID;
			} else {
				pt.addDay(pd);
			}
		}
		System.out.println("traceNum: " + traceNum);
	}

	public void addTrace(PatientTrace pt) {
		traces.add(pt);
		traceNum++;
	}
}
