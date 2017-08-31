package io.fidelcoria.ayfmPlanner.service;

import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import io.fidelcoria.ayfmPlanner.domain.AssignmentRepository;
import io.fidelcoria.ayfmPlanner.domain.Person;
import io.fidelcoria.ayfmPlanner.domain.PersonRepository;

@Service
public class StudentImportService {
	
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
	
}
