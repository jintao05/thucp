package model.TCPM;

import java.util.ArrayList;

public class PatientDay {
	public String pID;
	public String date;
	public ArrayList<String> activities;

	public PatientDay(String pID, String date, ArrayList<String> activities) {
		this.pID = pID;
		this.date = date;
		this.activities = activities;
	}
}
