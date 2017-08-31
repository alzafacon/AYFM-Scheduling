package io.fidelcoria.ayfmPlanner.util;

import java.time.LocalDate;

import io.fidelcoria.ayfmPlanner.domain.Assignment;
import io.fidelcoria.ayfmPlanner.domain.Person;


/**
 * Participation used similar to a DTO (Data Transfer Object).
 * 
 * Wraps a Person object together with an Assignment object for convenience.
 * The Person is either the Assignee or Householder on the Assignment object.
 * Since Participation objects are used to identify the last participation
 * the student reference is needed to clarify which Person the participation
 * applies to.
 * 
 * @author FidelCoria
 * 
 */
public class Participation {
	
	private Person student;
	private Assignment mostRecentAssignment;
	
	public Participation() {
		this(null, null);
	}
	
	public Participation(Person s) {
		this(s, null);
	}
	
	public Participation(Person s, Assignment a) {
		student = s;
		mostRecentAssignment = a;
	}
	
	public Person getStudent() {
		return student;
	}
	
	public void updateMostRecentAssignment(Assignment assignment) {
		mostRecentAssignment = assignment;
	}
	
	public Assignment getAssignment() {
		return mostRecentAssignment;
	}

	/**
	 * Function<> KeyExtractor for use by sort. 
	 * @return date of last participation (null if no participation available)
	 */
	public LocalDate getDate() {
		if (mostRecentAssignment == null) {
			return null;
		} else {
			return mostRecentAssignment.getWeek();
		}
	}
}
