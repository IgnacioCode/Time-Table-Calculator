package timetable;

public class WeekRow {
	
	String monday;
	String tuesday;
	String wednesday;
	String thursday;
	String friday;
	String saturday;
	
	String time;
	
	public WeekRow(String t) {
		monday = "";
		tuesday = "";
		wednesday = "";
		thursday = "";
		friday = "";
		saturday = "";
		time = t;
	}
	
	public String getMonday() {
		return monday;
	}


	public WeekRow(String monday, String tuesday, String wednesday, String thursday, String friday, String saturday) {
		this.monday = monday;
		this.tuesday = tuesday;
		this.wednesday = wednesday;
		this.thursday = thursday;
		this.friday = friday;
		this.saturday = saturday;
	}

	public void extractValues(String[] arr) {
		arr[0] = monday;
		arr[1] = tuesday;
		arr[2] = wednesday;
		arr[3] = thursday;
		arr[4] = friday;
		arr[5] = saturday;
	}
	
	
	public void setMonday(String monday) {
		this.monday = monday;
	}


	public String getTuesday() {
		return tuesday;
	}


	public void setTuesday(String tuesday) {
		this.tuesday = tuesday;
	}


	public String getWednesday() {
		return wednesday;
	}


	public void setWednesday(String wednesday) {
		this.wednesday = wednesday;
	}


	public String getThursday() {
		return thursday;
	}


	public void setThursday(String thursday) {
		this.thursday = thursday;
	}


	public String getFriday() {
		return friday;
	}


	public void setFriday(String friday) {
		this.friday = friday;
	}


	public String getSaturday() {
		return saturday;
	}


	public void setSaturday(String saturday) {
		this.saturday = saturday;
	}
	
	
}