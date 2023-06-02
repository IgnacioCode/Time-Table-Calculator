package timetable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Subject implements Serializable{
	
	private static final long serialVersionUID = 1L;
	String name;
	List<Section> sections;
	boolean waiting = true;
	
	public Subject() {
		
	}
	
	public Subject(List<Section> sectionList, String name) {
		this.sections = new LinkedList<Section>();
		this.sections.addAll(sectionList);
		this.name = name;
	}
	
	public Subject(List<Section> sectionList, String name, boolean waiting) {
		this.sections = new LinkedList<Section>();
		this.sections.addAll(sectionList);
		this.name = name;
		this.waiting = waiting;
	}

	@Override
	public String toString() {
		String line = "";
		
		for (Section section : sections) {
			line.concat(section.toString()+" ");
		}
		
		return name /*+" "+ line*/;
	}

	public boolean isWaiting() {
		return waiting;
	}

	public void setWaiting(boolean waiting) {
		this.waiting = waiting;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	
	
	
}
