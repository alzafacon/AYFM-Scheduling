package util;

import java.time.LocalDate;

import suggestiongenerator.entities.Assignment;
import suggestiongenerator.entities.Person;


/**
 * This is a DTO (Data Transfer Object)
 * perhaps in the future this could be extended to make for specific suggestions
 * 
 * Wraps a Student object together with an Assignment object for convenience
 */
public class Participation {
	
	public Person student;
	
	public Assignment mostRecentAssignment;
	
	public Participation() {
		
	}
	
	public Participation(Person s) {
		this(s, null);
	}
	
	public Participation(Person s, Assignment a) {
		this.student = s;
		this.mostRecentAssignment = a;
	}
	
	public LocalDate getDate() {
		if (mostRecentAssignment == null) {
			return null;
		} else {
			return mostRecentAssignment.getWeek();
		}
	}
}
