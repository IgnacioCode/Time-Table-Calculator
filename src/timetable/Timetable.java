package timetable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Timetable implements Serializable{
	
	private static final long serialVersionUID = 1L;
	List<Section> sectionsList;
	List<String> namesList;
	
	public Timetable() {
		
	}
	
	public Timetable(List<Section> sectionsList, List<String> namesList) {
		this.sectionsList = new LinkedList<Section>();
		this.namesList = new LinkedList<String>();
		this.sectionsList.addAll(sectionsList);
		this.namesList.addAll(namesList);
	}
	
	public static List<Timetable> calculateTimeTables(List<Subject> subjects) {
		
		int[] sectionsCount = new int[subjects.size()];
		
		List<Section> currentSectionList = new LinkedList<Section>();
		List<String> currentNamesList = new LinkedList<String>();
		List<Timetable> resultList = new LinkedList<Timetable>();
		
		for (int i = 0; i < sectionsCount.length; i++) {
			sectionsCount[i] = subjects.get(i).getSections().size();
		}
		
		Ring ring = new Ring(sectionsCount);
		
		int numberOfCombinations = 1;
		
		for (Subject s : subjects) {
			numberOfCombinations*= s.sections.size();
		}
		
		for (int i = 0; i < numberOfCombinations; i++) {
			
			int j = 0;
			System.out.print(Arrays.toString(ring.getValue()));
			for (Subject subject : subjects) {
				
				currentSectionList.add(subject.sections.get(ring.getValue()[j]));
				currentNamesList.add(subject.name);
				j++;
			}
			
			Timetable actualTimeTable = new Timetable(currentSectionList,currentNamesList);
			
			if(actualTimeTable.isCorrect()) {
				System.out.print(" CORRECT COMBINATION");
				resultList.add(actualTimeTable);
			}
			
			currentNamesList.removeAll(currentNamesList);
			currentSectionList.removeAll(currentSectionList);
			System.out.println();
			ring.turn();
		}
		
		return resultList;
	}
	
	
	public boolean isCorrect() {
		
		for (int i = 0; i < sectionsList.size(); i++)
			for(int j=i+1;j<sectionsList.size();j++)
				if(!Section.compatibleSections(sectionsList.get(i),sectionsList.get(j)))
					return false;
		
		return true;
	}
	
	public List<Section> getSectionsList() {
		return sectionsList;
	}

	public void setSectionsList(List<Section> sectionsList) {
		this.sectionsList = sectionsList;
	}

	public List<String> getNamesList() {
		return namesList;
	}

	public void setNamesList(List<String> namesList) {
		this.namesList = namesList;
	}
	
	
	
}
