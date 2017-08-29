package suggestiongenerator.services;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.time.ParseLocalDate;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import suggestiongenerator.entities.Assignment;
import suggestiongenerator.entities.Person;
import suggestiongenerator.repository.AssignmentRepository;
import suggestiongenerator.repository.PersonRepository;
import util.Assignment_t;
import util.Section;

@Service
public class ImportService {
	
	@Autowired
	PersonRepository personRepository;
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	private CellProcessor[] getStudentCsvProcessors() {
		
		String[] activeTrueValues = new String[] {
			"t"
		};
		String[] activeFalseValues = new String[] {
			"f"
		};
		
		final CellProcessor[] processors = new CellProcessor[] {
			// Gender
			new NotNull(),
			
			// Last Name
			new NotNull(),
			
			// First Name
			new NotNull(),
			
			// Active
			new ParseBool(activeTrueValues, activeFalseValues),
			
			// Reading
			new Optional(new ParseBool("1", "")),
			
			// Initial Call
			new Optional(new ParseBool("2", "")),
			
			// Return Visit
			new Optional(new ParseBool("3", "")),
			
			// Bible Study
			new Optional(new ParseBool("4", "")),
			
			// Talk
			new Optional(new ParseBool("5", ""))
		};
		
		return processors;
	}

	private boolean toBoolean(Object thing) {
		
		boolean flag = false;
		
		if (thing != null) {
			flag = (boolean) thing;
		}
		
		return flag;
	}
	
	public List<Person> readStudentsWithCsvMapReader(String csvFileName) throws Exception {
		
		List<Person> students = new LinkedList<>();
		ICsvMapReader mapReader = null;
		
		try {
			// not specifying the file format as UTF-8 because excel does not use it
			mapReader = new CsvMapReader(new FileReader(csvFileName), CsvPreference.EXCEL_PREFERENCE);
			
			final String[] header = mapReader.getHeader(true);
			final CellProcessor[] processors = getStudentCsvProcessors();
			
			// read a single student record at a time
			Map<String, Object> student;
			while ((student = mapReader.read(header, processors)) != null) {
				Person studentToPersist = new Person();
				
				
				studentToPersist.setGender((String)student.get("Gender"));
				studentToPersist.setLastName((String)student.get("Last Name"));
				studentToPersist.setFirstName((String)student.get("First Name"));
				System.out.println(studentToPersist.getFullName());
				studentToPersist.setActive(toBoolean(student.get("Active")));
				studentToPersist.setEligibleReading(toBoolean(student.get("Reading")));
				studentToPersist.setEligibleInitCall(toBoolean(student.get("Initial Call")));
				studentToPersist.setEligibleRetVisit(toBoolean(student.get("Return Visit")));
				studentToPersist.setEligibleBibStudy(toBoolean(student.get("Bible Study")));
				studentToPersist.setEligibleTalk(toBoolean(student.get("Talk")));
				
//				Gender,Last Name,First Name,Active,Reading,Initial Call,Return Visit,Bible Study,Talk	
				students.add(studentToPersist);
			}
		}
		finally {
			if (mapReader != null) {
				mapReader.close();
			}
		}
		
		return students;
	}
	
	public List<Person> saveStudents(List<Person> students) {
		 return personRepository.save(students);
	}
	
	/**
	 * Assignment CSV file column processor
	 * @return CellProcessor[], each column is an element in the array
	 */
 	private CellProcessor[] getAssignmentCsvProcessors() {
    	
    	final CellProcessor[] processors = new CellProcessor[] {
    		// Assignment Date
			 new ParseLocalDate(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    			
			// Assignment Type
			new ParseInt(), 
			
			// Assignee
			new NotNull(),
			
			// Householder
			new Optional(), 
			
			// Lesson
			new Optional(new ParseInt()),
			
			// Classroom
			new ParseInt()
    	};
    	
    	return processors;
    }
    
	/**
	 * Map CSV file records to a list of Assignment objects.
	 * @param csvFileName use full path when possible
	 * @return List of Assignments that were mapped from the records to Assignment objects.
	 * @throws Exception File may not be found or not able to read. // TODO
	 */
    public List<Assignment> readAssignmentsWithCsvMapReader(String csvFileName) throws Exception {
    	
    	List<Assignment> assignments = new LinkedList<>();
    	ICsvMapReader mapReader = null;
    	
    	try {
    		mapReader = new CsvMapReader(
				new InputStreamReader(
					new FileInputStream(csvFileName),
					Charset.forName("UTF-8")),
				CsvPreference.STANDARD_PREFERENCE);
    		
    		final String[] header = mapReader.getHeader(true);
    		final CellProcessor[] processors = getAssignmentCsvProcessors();
    		
    		// read a single assignment record at a time
    		Map<String, Object> assignment;
    		while ((assignment = mapReader.read(header, processors)) != null) {
    			
    			Assignment assignmentToPersist = new Assignment();
    			
    			Person assignee = personRepository.findByFullName((String)assignment.get("Assignee"));
    			Person householder = personRepository.findByFullName((String)assignment.get("Householder"));

    			// TODO: what to do when the person (assignee or householder) is not found?
    			if (assignee == null) {
    				System.out.println("Unable to find "+assignment.get("Assignee"));
    				System.out.println("ignoring person for now but should be asking user for clarification");
    				continue;
    			}
    			
    			// map record values into Java Assignment Object
    			assignmentToPersist.setWeek((LocalDate)assignment.get("Date"));
    			assignmentToPersist.setAssignee(assignee);
    			assignmentToPersist.setHouseholder(householder);
    			assignmentToPersist.setAssignmentType(Assignment_t.get((int)assignment.get("Type")));
    			if (assignment.get("Lesson") != null) {
    				assignmentToPersist.setLesson((int)assignment.get("Lesson"));
    			}
    			assignmentToPersist.setClassroom(Section.get((int)assignment.get("Classroom")));
    			
    			assignments.add(assignmentToPersist);
    		}
    		
    	}
    	finally {
    		if (mapReader != null) {
    			mapReader.close();
    		}
    	}
    	
    	return assignments;
    }
    
    /**
     * Persists the list of assignments
     * @param assignments
     * @return the saved entities
     */
    public List<Assignment> saveAssignments(List<Assignment> assignments) {
    	
    	List<Assignment> persistedAssignments = assignmentRepository.save(assignments);
    	
    	return persistedAssignments;
    }
    
    
}
