package model.TCPM;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class PatientTrace {
	public String pID;
	public int dayNum;
	public ArrayList<PatientDay> patientDays;

	public PatientTrace(String pID) {
		this.pID = pID;
		this.dayNum = 0;
		this.patientDays = new ArrayList<PatientDay>();
	}

	public void addDay(PatientDay pd) {
		patientDays.add(pd);
		dayNum++;
	}
}
