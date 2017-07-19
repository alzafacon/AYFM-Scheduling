package util;

import entities.Assignment;
import entities.Student;


/**
 * This is a DTO (Data Transfer Object)
 * perhaps in the future this could be extended to make for specific suggestions
 * 
 * Wraps a Student object together with an Assignment object for convenience
 */
public class Participation {
	
	public Student student;
	
	public Assignment mostRecentAssignment;
		
}
