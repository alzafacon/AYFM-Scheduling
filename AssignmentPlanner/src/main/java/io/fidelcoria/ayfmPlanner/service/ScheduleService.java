package io.fidelcoria.ayfmPlanner.service;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fidelcoria.ayfmPlanner.domain.Assignment;
import io.fidelcoria.ayfmPlanner.domain.AssignmentRepository;
import io.fidelcoria.ayfmPlanner.domain.Person;
import io.fidelcoria.ayfmPlanner.domain.PersonRepository;
import io.fidelcoria.ayfmPlanner.util.Assignment_t;
import io.fidelcoria.ayfmPlanner.util.Participation;
import io.fidelcoria.ayfmPlanner.util.Progression;
import io.fidelcoria.ayfmPlanner.util.Role;
import io.fidelcoria.ayfmPlanner.util.Section;

/**
 * 
 * @author FidelCoria
 *
 */
@Service("schedule")
public class ScheduleService {
	
	@Autowired
	PersonRepository personRepository;
	@Autowired
	AssignmentRepository assignmentRepository;
	
	public static final int MAX_NUM_WEEKS = 5;
	public static final int NUM_WEEKLY_ASSIGNMENT_TYPES = 4; // categories each week
	public static final int NUM_SECTIONS = 2;
	
	private Assignment[][][] schedule;
	
	private int year;
	private int month;
	private int effectiveWeeks; // number of weeks actually in month (less than MAX_NUM_WEEKS)
	
	/**
	 * Protected: let Spring handle instantiation and injection
	 */
	protected ScheduleService() {
		
	}
	
	/**
	 * Initialize the autowired Schedule for a desired month to be generated.
	 * @param year 
	 * @param month
	 */
	public void setYearMonth(int year, int month) {
		this.year = year;
		this.month = month;
		
		this.init();
	}

	/**
	 * Create a skeleton schedule with dates initialized using parameters.
	 * @param year Numeric year to build schedule
	 * @param month Numeric month to build schedule
	 */
	private void init() {
		schedule = new Assignment[MAX_NUM_WEEKS][NUM_WEEKLY_ASSIGNMENT_TYPES][NUM_SECTIONS];
		
		// the first week of every month ONLY has one reading assignment
		schedule[0] = new Assignment[1][1];
		
		// all other weeks have all assignment types for both sections
		
		// the number of weeks actually in the schedule for the given month
		this.effectiveWeeks = 0;
		
		LocalDate date = LocalDate.of(this.year, this.month, 1).with(firstInMonth(DayOfWeek.MONDAY));
		
		// initialize all assignments with dates, types, and sections
		for (int week = 0; week < MAX_NUM_WEEKS; week++) {
			for (int atype = 0; atype < schedule[week].length; atype++) {
				for (int section = 0; section < schedule[week][atype].length; section++) {
					
					schedule[week][atype][section] = 
						new Assignment(date, Assignment_t.get(atype+1), Section.get(section+1));
				}
			}
			effectiveWeeks++;
			
			date = date.plusWeeks(1);
			if (date.getMonthValue() != this.month) {
				// the skeleton should not contain any weeks from next month
				break;
			}
		}
	}
	
	/**
	 * Generate the monthly schedule using a 'queue' of participations.
	 * 
	 */
	public void generateSchedule() {
		
		/**
		 * 1+ for the reading during the first week
		 * effectiveWeeks-1 so the first week is not counted
		 * *NUM_ASSIGNMENT_TYPES*NUM_SECTIONS for the number of assignments per week
		 * +(effectiveWeeks-1)*(NUM_WEEKLY_ASSIGNMENT_TYPES-1)*NUM_SECTIONS for the
		 *  householder assignments... there must be a better way to count the assignments
		 *  for controlling the algorithm
		 */
		final int MAX_ASSGNS = 1+(effectiveWeeks-1)*NUM_WEEKLY_ASSIGNMENT_TYPES*NUM_SECTIONS
				+(effectiveWeeks-1)*(NUM_WEEKLY_ASSIGNMENT_TYPES-1)*NUM_SECTIONS;
		int assignmentsPlaced = 0;
		
		List<Participation> prioritizedParticipations = createPrioritizedParticipations();

		while (!prioritizedParticipations.isEmpty() && assignmentsPlaced < MAX_ASSGNS) {
			
			Participation part = prioritizedParticipations.remove(0);
			boolean advanceLesson = part.getAssignment() != null;
			Assignment suggestion = generateSuggestion(part, advanceLesson);
			Assignment cycleStart = suggestion;
			
			int week = 0;
			
			int typeAsInt = suggestion.getAssignmentType().toInt() - 1;
			int sectionAsInt = suggestion.getClassroom().toInt() - 1;
			
			// week: 0 only has array index for type: 0 and section: 0
			// if those are not the values then skip to week: 1
			if (!(typeAsInt == 0 && sectionAsInt == 0)) {
				week = 1;
			}
			
			// iteration control flag. loop also terminated by `break`
			boolean placed = false;
			while (!placed) {
				
				if (isCompatible(suggestion, schedule[week][typeAsInt][sectionAsInt])) {
					placed = true;
					assignmentsPlaced++;

					mergeInto(
						schedule[week][typeAsInt][sectionAsInt], suggestion
					);
					part.updateMostRecentAssignment(schedule[week][typeAsInt][sectionAsInt]);
					
					// append to the end of the queue
					prioritizedParticipations.add(part);
				}
				
				week++;
				
				if (week == effectiveWeeks) {
					// all weeks failed, apply suggestion again
					suggestion = generateSuggestion(new Participation(part.getStudent(), suggestion), false);
					
					week = 0;
					typeAsInt = suggestion.getAssignmentType().toInt() - 1;
					sectionAsInt = suggestion.getClassroom().toInt() - 1;
					
					if (!(typeAsInt == 0 && sectionAsInt == 0)) {
						week = 1;
					}
					
					if (suggestion.equals(cycleStart)) {
						// full progression cycle traversed
						// do not append student back to queue
						break;
					}
					
				}
			} // `break;` escapes here
			
		}
		
//		System.out.println("placed = "+assignmentsPlaced);
//		System.out.println("max no = "+MAX_ASSGNS);
	}
	
	/**
	 * Merges a suggestion into the schedule. This is done to preserve the information
	 * already in the schedule[][][]. The only values to be merged are the person,
	 * either as assignee or householder, only one. The type and the section should have
	 * already been synchronized by the algorithm. The date is an attribute to be preserved.
	 * @param assignment reference to the assignment in the schedule
	 * @param suggestion reference to the suggestion to be merged into schedule
	 */
	private void mergeInto(Assignment assignment, Assignment suggestion) {
		Person assignee = suggestion.getAssignee();
		Person householder = suggestion.getHouseholder();
		
		if (assignee != null) {
			assignment.setAssignee(assignee);
		}
		if (householder != null) {
			assignment.setHouseholder(householder);
		}
	}

	/**
	 * Determine whether the suggestion is compatible with scheduleSlot.
	 * The person slot must be null (available) and the gender must match
	 * the assignment partner, if there is a partner.
	 * @param suggestion generated with progression
	 * @param scheduleSlot reference to slot in schedule to be filled
	 * @return true when the conditions above are met, false othewise
	 */
	private boolean isCompatible(Assignment suggestion, Assignment scheduleSlot) {
		
		Role suggestedRole = (suggestion.getAssignee() == null)? Role.HOUSEHOLDER : Role.ASSIGNEE;
		Person personToAssign = suggestion.getPersonInRole(suggestedRole);
		Person scheduleSlotPersonToFill = scheduleSlot.getPersonInRole(suggestedRole);
		
		// if the role is already assigned: not compatible
		if (scheduleSlotPersonToFill != null) {
			return false;
		}

		// The role is available but...

		// if other role is null or same gender: compatible
		Role otherRole = (suggestedRole == Role.ASSIGNEE)? Role.HOUSEHOLDER : Role.ASSIGNEE;
		Person assignmentPartner = scheduleSlot.getPersonInRole(otherRole);
		if (assignmentPartner == null
			|| personToAssign.getGender().equals(assignmentPartner.getGender())) {
			return true;
		}

		// otherwise: not compatible
		return false;
	}

	/**
	 * Create the 'queue' of participations.
	 * @return List of participations sorted with null first and dates in ascending order.
	 */
	private List<Participation> createPrioritizedParticipations() {
		
		List<Participation> activeStudentParticipations = personRepository.findAllActiveStudents();
		
		// attempt to find the most recent assignment of each active person
		//  when found attach to the participation object
		for (Participation part : activeStudentParticipations) {
			
			List<Assignment> lastAssignment = assignmentRepository.findMostRecentAssignment(part.getStudent().getId());
			
			if (lastAssignment.size() == 1) {
				part.updateMostRecentAssignment(lastAssignment.get(0));
			}
		}
		
		// prioritize participations: students at the top of the list
		//  have not participated in the longest so should be prioritized
		//	for placement in a generated schedule
		activeStudentParticipations.sort(
			Comparator.comparing(
				Participation::getDate,
				Comparator.nullsFirst(Comparator.naturalOrder())
			)
		);
		
		return activeStudentParticipations;
	}
	
	/**
	 * Progression: as defined in the method
	 * This progression is filtered based on what the student can work on.
	 * The suggested assignment includes the Person, Type, Section, and Lesson.
	 * The role is determined as the Person (assignee or householder)
	 * which is not null, while the other Person is null.
	 * @param participation used to find current position in progression
	 * 	and then find next position
	 * @return Assignment suggestion based on most recent participation
	 */
	private Assignment generateSuggestion(Participation participation, boolean advanceLesson) {
	
		Person student = participation.getStudent();
		Assignment pastAssignment = participation.getAssignment();
		
		// filter the progression
		List<Assignment_t> eligibility = student.getEligibility();
		
		ArrayList<Progression> filteredProgression = new ArrayList<>();
		
		for (Progression progression : Progression.defaultProgression) {
			if (eligibility.contains(progression.getType())) {
				filteredProgression.add(progression);
			}
		}
		if (filteredProgression.size() == 0) {
//			throw new IndexOutOfBoundsException("No eligibility set up for student "+student.getFullName());
			System.out.println("size of progression was 0");
		}
		
		Assignment suggestion = new Assignment();
		 
		Role lastRole;
		Assignment_t lastType;
		Section lastSection;
		Integer lastLesson;
		
		Person nextAssignee;
		Person nextHouseholder;
		Assignment_t nextType;
		Section nextSection;
		Integer nextLesson;
		
		// find current position in fillteredProgression
		// if no previous assignment just take the first
		if (pastAssignment == null) {
			nextAssignee = student;
			nextHouseholder = null;
			nextType = filteredProgression.get(0).getType();
			nextSection = filteredProgression.get(0).getSection();
			nextLesson = getNextLesson(nextType, 0);
		} else {
			if (student.equals(pastAssignment.getAssignee())) {
				lastRole = Role.ASSIGNEE;
			} else {
				lastRole = Role.HOUSEHOLDER;
			}
			
			lastType = pastAssignment.getAssignmentType();
			lastSection = pastAssignment.getClassroom();
			
			lastLesson = pastAssignment.getLesson();
			
			int currentProgressionIndex = 
				filteredProgression.indexOf(new Progression(lastRole, lastType, lastSection));
			
			int nextProgressionIndex =
				(currentProgressionIndex + 1) % filteredProgression.size();
			
			Progression nextProgression = filteredProgression.get(nextProgressionIndex);
			
			nextType = nextProgression.getType();
			nextSection = nextProgression.getSection();
			
			if (advanceLesson == true) {
				nextLesson = getNextLesson(nextType, lastLesson);
			} else {
				nextLesson = lastLesson;
			}
			
			if (nextProgression.getRole() == Role.ASSIGNEE) {
				nextAssignee = student;
				nextHouseholder = null;
			} else {
				nextAssignee = null;
				nextHouseholder = student;
			}
		}
		
		// save the advanced values to the suggestion
		suggestion.setAssignmentType(nextType);
		suggestion.setClassroom(nextSection);
		suggestion.setLesson(nextLesson);
		suggestion.setAssignee(nextAssignee);
		suggestion.setHouseholder(nextHouseholder);

		return suggestion;
	}
	
	/**
	 * Advance lesson number based on the lessons applicable to each lesson type.
	 * @param nextType
	 * @param lastLesson
	 * @return Lesson number to be suggested next, 0 if none could be suggested.
	 */
	private int getNextLesson(Assignment_t nextType, int lastLesson) {
		
		if (lastLesson == 0) {
			return 0;
		}
		
		switch (nextType) {
		case READING:
			return getNextReadingLesson(lastLesson);
		
		case INITIAL_CALL:
		case RETURN_VISIT:
		case BIBLE_STUDY:
			return getNextDemonstrationLesson(lastLesson);
			
		default:
			return 0;
		}
	}
	
	/**
	 * Advances lesson specifically for Reading type of assignments.
	 * @param lastLesson
	 * @return next lesson to be assigned, start over at 1 if not able to suggest one
	 */
	private int getNextReadingLesson(int lastLesson) {
		
		if (lastLesson < 0) {
			return 1;
		}
		
		final int GREATEST_READING_LESSON = 17;
		
		return ((GREATEST_READING_LESSON-1) % 17) + 1;
	}
	
	/**
	 * Advances lesson for Demonstration type of assignments. These include:
	 * INITIAL_CALL
	 * RETURN_VISIT
	 * BIBLE_STUDY
	 * @param lastLesson
	 * @return next lesson to be assigned, start over at 1 if not able to suggest one
	 */
	private int getNextDemonstrationLesson(int lastLesson) {
		
		// the appropriate demonstration lessons fall in the following ranges:
		// [1, 6] U [8, 51]
		
		if (lastLesson <= 0 || lastLesson >= 51) {
			return 1;
		} else if (lastLesson == 6) {
			return 8;
		} else {
			return lastLesson + 1;
		}
		
	}

	/**
	 * Print to console the assignments
	 * weeks are separated by a line of dashed
	 */
	public void print() {
		
		for (int week = 0; week < MAX_NUM_WEEKS; week++) {
			System.out.println("-----------------------");
			for (int atype = 0; atype < schedule[week].length; atype++) {
				for (int section = 0; section < schedule[week][atype].length; section++) {
					System.out.println(schedule[week][atype][section]);
				}
			}
			System.out.println();
		}
	}
	
	public void saveToDocxSchedule(File outputDocx) throws FileNotFoundException, IOException {
		
		File template = new File("C:\\Program Files\\AYFM\\docx-template\\schedule-S.docx");
		
		XWPFDocument document = new XWPFDocument(new FileInputStream(template));
		XWPFTable table = document.getTables().get(0);
		Iterator<XWPFTableRow> scheduleRowIt = table.getRows().iterator();
		
		XWPFTableRow row; // handle for return from iterator
		
		LocalDate weekDate = LocalDate.of(this.year, this.month, 1).with(firstInMonth(DayOfWeek.MONDAY));
		int week = 0;
		int type = Assignment_t.READING.toInt()-1;
		int section = Section.A.toInt()-1;
		
		scheduleRowIt.next(); // throw away the header row
		
		row = scheduleRowIt.next(); // first week date header
		
		row.getCell(0).setText(weekDate.toString());
		
		row = scheduleRowIt.next(); // first week in month
		
		if (this.schedule[week][type][section] != null
				&& this.schedule[week][type][section].getAssignee() != null) {
			
			String student = this.schedule[week][type][section].getAssignee().getFullName();
			int lesson = this.schedule[week][type][section].getLesson();
			
			row.getCell(1).setText(student);
			if (lesson > 0) {
				row.getCell(2).setText(Integer.toString(lesson));
			}
		}
		
		for (week = 1; week <= 4; week++) {
			
			row = scheduleRowIt.next(); // get week date header
			
			row.getCell(0).setText(weekDate.plusWeeks(week).toString()); // set date
			
			for (type = 0; type <= 3; type++) {
				row = scheduleRowIt.next(); // get assignment row
				
				for (section = 0; section <= 1; section++) {
					if (schedule[week][type][section] != null
							&& schedule[week][type][section].getAssignee() != null) {
						
						String assignee = schedule[week][type][section].getAssignee().getFullName();
						Person householder = schedule[week][type][section].getHouseholder();
						
						int lesson = schedule[week][type][section].getLesson();
						
						row.getCell(1+section*2).setText(assignee);
						
						if (householder != null) {
							XWPFParagraph householderParagraph = row.getCell(1+section*2).addParagraph();
							XWPFRun householderRun =  householderParagraph.createRun();
							householderRun.setText(householder.getFullName());
						}
						
						if (lesson > 0) {
							row.getCell(2+section*2).setText(Integer.toString(lesson));
						}
						
					}
				}
			}
			
		}
		
		document.write(new FileOutputStream(outputDocx));
		
		document.close();
		
	}
}
