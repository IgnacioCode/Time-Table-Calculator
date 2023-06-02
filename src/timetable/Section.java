package timetable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Section implements Serializable{
	
	private static final long serialVersionUID = 1L;
	List<Shift> shifts;
	
	public Section() {
		
	}
	
	public Section(List<Shift> shiftsList) {
		this.shifts = new LinkedList<Shift>();
		this.shifts.addAll(shiftsList);
	}

	public static boolean compatibleSections(Section s1,Section s2) {
		
		
		//If only one shift is equal, then both Section aren't compatible
		for (int i = 0; i < s1.shifts.size(); i++) {
			for (int j = 0; j < s2.shifts.size(); j++) {
				if(s1.shifts.get(i).equals(s2.shifts.get(j)))
					return false;
			}
		}
		
		return true;
	}

	
	public List<Shift> getShifts() {
		return shifts;
	}

	public void setShifts(List<Shift> shifts) {
		this.shifts = shifts;
	}

	@Override
	public String toString() {
		String line = "";
		for (Shift shift : shifts) {
			line.concat(shift.day + " " + shift.time);
		}
		return line;
	}
	
	
	
}
