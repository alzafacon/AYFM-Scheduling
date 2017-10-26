package io.fidelcoria.ayfmap.service;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fidelcoria.ayfmap.domain.Assignment;
import io.fidelcoria.ayfmap.domain.AssignmentRepository;
import io.fidelcoria.ayfmap.domain.Person;
import io.fidelcoria.ayfmap.domain.PersonRepository;
import io.fidelcoria.ayfmap.util.Assignment_t;
import io.fidelcoria.ayfmap.util.Section;

/**
 * Import assignment schedule from DOCX file.
 * @author FidelCoria
 *
 */
@Service
public class AssignmentImportService {

	@Autowired
	AssignmentRepository assignmentRepository;

	@Autowired
	PersonRepository personRepository;
	
	
	public AssignmentImportService() {
		
	}
	
	/**
	 * Map assignment schedule into Assignment objects
	 * @param docxAssignments File object to filled out DOCX schedule template 
	 * @param year
	 * @param month
	 * @return Assignments that were successfully extracted from the DOCX file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public List<Assignment> readAssignmentsFromDocx(File docxAssignments, int year, int month) throws FileNotFoundException, IOException {
		
		// assignments extracted from the DOCX file
		List<Assignment> assignmentsToPersist = new ArrayList<>();
    	
		XWPFDocument document = new XWPFDocument(new FileInputStream(docxAssignments));
		
		List<XWPFTable> tables = document.getTables();
		
		if (tables.size() != 1) {
			document.close();
			throw new IndexOutOfBoundsException("There must be exactly one (1) table in the document."); 
	    }
		
		XWPFTable scheduleTable = tables.get(0); // assume the rest of the document is in the correct form
		
		Iterator<XWPFTableRow> scheduleRowsIt = scheduleTable.getRows().iterator();
		XWPFTableRow row; // handle for return from iterator
		
		LocalDate weekDate;
		int weekOffset = 0; // number of weeks offset from first Monday in month
		
		scheduleRowsIt.next(); // throw away the first row, it's the header
		scheduleRowsIt.next(); // throw away week header row
		
		// first week in monthly schedule has only 1 assignment (reading)
		weekDate = getWeekDate(year, month, weekOffset);
		row = scheduleRowsIt.next();
		assignmentsToPersist.addAll(parseAssignmentRow(row, weekDate, Assignment_t.READING));
		
		// there are 5 weeks in every schedule (not all weeks may have assignments)
		for (weekOffset = 1; weekOffset <= 4; weekOffset++) {
			
			weekDate = getWeekDate(year, month, weekOffset);
			scheduleRowsIt.next(); // throw away week header row
			
			// go through each of the four assignment types
			for (int type = 1; type <= 4; type++) {
				row = scheduleRowsIt.next();
				assignmentsToPersist.addAll(parseAssignmentRow(row, weekDate, Assignment_t.get(type)));
			}
		}
		
		document.close();
		
		return assignmentsToPersist;
	}
	
	/**
	 * Generates Assignment objects from a schedule table row. Names of students must match those enrolled in the database.
	 * TODO: If there are any missing ask user to select or create a Student entry. 
	 * @param tablerow
	 * @param weekDate 
	 * @param type
	 * @return empty list if nothing is mapped, up to two objects possible
	 */
	List<Assignment> parseAssignmentRow(XWPFTableRow tablerow, LocalDate weekDate, Assignment_t type) {

		List<Assignment> assignments = new ArrayList<>();

		// constants indicating the column indices for certain information
		final int[][] column = new int[][] {
			// section a
			{1, 2}, // participant column, lesson column
			//section b
			{3, 4} // participant column, lesson column
		};
		// constants for the second index to the array above
		final int PEOPLE = 0;
		final int LESSON = 1;

		for (int section = 0; section <= 1; section++) {

			// word DOCX keeps the text inside of XML nested tags table/tableRow/cell/paragraph/text (there might be runs too)
			List<XWPFParagraph> paragraphs = tablerow.getCell(column[section][PEOPLE]).getParagraphs();
			
			if (paragraphs.size() <= 0) { continue; } // if there are no people then the assignment is skipped
			
			String assigneeName = paragraphs.get(0).getText().trim();

			if ("".equals(assigneeName)) { continue; } // if there are no people then the assignment is skipped
			
			System.out.println("assignee: "+assigneeName);
			
			int lesson;
			try{
				lesson = Integer.parseInt(tablerow.getCell(column[section][LESSON]).getParagraphs().get(0).getText().trim());
			}
			catch (Exception e) {
				lesson = 0;
			}
			
			Person assignee = personRepository.findByFullName(assigneeName.trim());
			

			// TODO  If people are not found ask user to create or select existing
			if (assignee == null) { continue; }
			
			Assignment assignment = new Assignment();

			assignment.setWeek(weekDate);
			assignment.setAssignmentType(type);
			assignment.setLesson(lesson);
			assignment.setClassroom(Section.get(section+1)); // plus one b/c section is 0-indexed but Enum.Section is not

			assignment.setAssignee(assignee);

			if (paragraphs.size() > 1) {
				String householderName = paragraphs.get(1).getText().trim();
				
				Person householder = personRepository.findByFullName(householderName);
				if (householder == null) { continue; }
				
				assignment.setHouseholder(householder);
			}

			assignments.add(assignment);	 			 
		}

		return assignments;
	}
	
	/**
	 * Week date of Monday in month.
	 * @param year
	 * @param month
	 * @param weekOffset from the first Monday in the month.
	 * @return
	 */
	LocalDate getWeekDate(int year, int month, int weekOffset) {
		
		// find date of first Monday in month
		LocalDate date = LocalDate.of(year, month, 1).with(firstInMonth(DayOfWeek.MONDAY));
		
		 return date.plusWeeks(weekOffset);
	 }
	
	/**
	 * Save all given entities
	 * @param assignments list to be persisted
	 * @return saved entities (include id)
	 */
	public List<Assignment> save(List<Assignment> assignments) {
		
		if (assignments == null) {
			return null;
		}
		
		return assignmentRepository.save(assignments);
	}
}
