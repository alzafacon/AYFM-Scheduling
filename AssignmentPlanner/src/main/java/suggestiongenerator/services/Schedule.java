package suggestiongenerator.services;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import suggestiongenerator.entities.Assignment;
import suggestiongenerator.entities.Person;
import suggestiongenerator.repository.AssignmentRepository;
import suggestiongenerator.repository.PersonRepository;
import util.Assignment_t;
import util.Participation;
import util.Progression;
import util.Role;
import util.Section;

/**
 * 
 * @author FidelCoria
 *
 */
@Service("schedule")
public class Schedule {
	
	@Autowired
	PersonRepository personRepository;
	@Autowired
	AssignmentRepository assignmentRepository;
	
	public static final int MAX_NUM_WEEKS = 5;
	public static final int NUM_ASSIGNMENT_TYPES = 4;
	public static final int NUM_SECTIONS = 2;
	
	private Assignment[][][] schedule;
	
	private int year;
	private int month;
	private int effectiveWeeks;
	
	protected Schedule() {
		
	}
	
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
		schedule = new Assignment[MAX_NUM_WEEKS][NUM_ASSIGNMENT_TYPES][NUM_SECTIONS];
		
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
		 */
		final int MAX_ASSGNS = 1+(effectiveWeeks-1)*NUM_ASSIGNMENT_TYPES*NUM_SECTIONS;
		int assignmentsPlaced = 0;
		
		List<Participation> prioritizedParticipations = createPrioritizedParticipations();

		while (!prioritizedParticipations.isEmpty() && assignmentsPlaced < MAX_ASSGNS) {
			
			Participation part = prioritizedParticipations.remove(0);
			Assignment suggestion = generateSuggestion(part);
			Assignment cycleStart = suggestion;
			
			int week = 0;
			Assignment scheduleSlot;
			
			int typeAsInt = suggestion.getAssignmentType().toInt();
			int sectionAsInt = suggestion.getClassroom().toInt();
			
			// week: 0 only has array index for type: 0 and section: 0
			// if those are not the values then skip to week: 1
			if (!(typeAsInt == 0 && sectionAsInt == 0)) {
				week = 1;
			}
			
			scheduleSlot = schedule[week][typeAsInt][sectionAsInt];
			
			// iteration control flag. loop also terminated by `break`
			boolean placed = false;
			while (!placed) {
				
				if (isCompatible(suggestion, scheduleSlot)) {
					placed = true;

					mergeInto(
						schedule[week][typeAsInt][sectionAsInt], suggestion
					);
					part.mostRecentAssignment = schedule[week][typeAsInt][sectionAsInt];
					
					// append to the end of the queue
					prioritizedParticipations.add(part);
				}
				
				week++;
				
				if (week == effectiveWeeks) {
					// all weeks failed, apply suggestion again
					suggestion = generateSuggestion(new Participation(part.student, suggestion));
					// TODO : missing set up for typeAsInt etc.
					if (suggestion == cycleStart) { // TODO: WRONG EQUALITY
						// full progression cycle traversed
						// do not append student back to queue
						break;
					}
				}
			} // `break;` escapes here
			
		}
		
		
		System.out.println("placed = "+assignmentsPlaced);
		System.out.println("max no = "+MAX_ASSGNS);
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

	private boolean isCompatible(Assignment suggestion, Assignment scheduleSlot) {
		// TODO
		return false;
	}

	private List<Participation> createPrioritizedParticipations() {
		
		List<Participation> activeStudentParticipations = personRepository.findAllActiveStudents();
		
		// attempt to find the most recent assignment of each active person
		//  when found attach to the participation object
		for (Participation part : activeStudentParticipations) {
			
			List<Assignment> lastAssignment = assignmentRepository.findMostRecentAssignment(part.student.getId());
			
			if (lastAssignment.size() == 1) {
				part.mostRecentAssignment = lastAssignment.get(0);
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
	 * 
	 * @param participation used to find current position in progression
	 * 	and then find next position
	 * @return Assignment suggestion based on most recent participation
	 */
	private Assignment generateSuggestion(Participation participation) {
	
		final Progression[] defaultProgression = {
			new Progression(Role.ASSIGNEE, Assignment_t.READING, Section.A),
			new Progression(Role.ASSIGNEE,    Assignment_t.INITIAL_CALL, Section.A),
			new Progression(Role.HOUSEHOLDER, Assignment_t.INITIAL_CALL, Section.A),				
			new Progression(Role.ASSIGNEE,    Assignment_t.RETURN_VISIT, Section.A),
			new Progression(Role.HOUSEHOLDER, Assignment_t.RETURN_VISIT, Section.A),				
			new Progression(Role.ASSIGNEE,    Assignment_t.BIBLE_STUDY, Section.A),
			new Progression(Role.HOUSEHOLDER, Assignment_t.BIBLE_STUDY, Section.A),
			
			new Progression(Role.ASSIGNEE, Assignment_t.READING, Section.B),
			new Progression(Role.ASSIGNEE,    Assignment_t.INITIAL_CALL, Section.B),
			new Progression(Role.HOUSEHOLDER, Assignment_t.INITIAL_CALL, Section.B),				
			new Progression(Role.ASSIGNEE,    Assignment_t.RETURN_VISIT, Section.B),
			new Progression(Role.HOUSEHOLDER, Assignment_t.RETURN_VISIT, Section.B),
			new Progression(Role.ASSIGNEE,    Assignment_t.BIBLE_STUDY, Section.B),
			new Progression(Role.HOUSEHOLDER, Assignment_t.BIBLE_STUDY, Section.B)
		};
	
		Person student = participation.student;
		Assignment pastAssignment = participation.mostRecentAssignment;
		
		System.out.println("\n"+student.getFullName());
		
		
		List<Assignment_t> eligibility = student.getEligibility();
		
		// filter the progression
		
		ArrayList<Progression> filteredProgression = new ArrayList<>();
		
		for (Progression progression : defaultProgression) {
			if (eligibility.contains(progression.getType())) {
				filteredProgression.add(progression);
			}
		}
		
		
		Assignment suggestion = new Assignment();
		
		// find item in fillteredProgression that matches pastAssignment
		Role lastRole;
		if (student.getId() == pastAssignment.getAssignee().getId()) {
			lastRole = Role.ASSIGNEE;
		} else {
			lastRole = Role.HOUSEHOLDER;
		}
		
		Assignment_t lastType = pastAssignment.getAssignmentType();
		Section lastSection = pastAssignment.getClassroom();
		int progressionIndex = filteredProgression.indexOf(new Progression(lastRole, lastType, lastSection));
		
		int nextProgressionIndex = (progressionIndex+1) % filteredProgression.size();
		
		Progression nextProgression = filteredProgression.get(nextProgressionIndex);
		
		Assignment_t nextType = nextProgression.getType();
		Section nextSection = nextProgression.getSection();
		
		Person nextAssignee = null;
		Person nextHouseholder = null;
		
		if (Role.ASSIGNEE == nextProgression.getRole()) {
			nextAssignee = student;
		} else {
			nextHouseholder = student;
		}
		
		// Attempt to advance progression on lesson
		// TODO: this line is causing nullptr exceptions
		Integer lastLesson = pastAssignment.getLesson();
		Integer nextLesson = null;
		
		if (lastLesson != null) {	
			nextLesson = getNextLesson(nextType, lastLesson);
		}
		// if there was no lesson suggested before, do not suggest one now
		
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
	 * @return next lesson to be assigned, 0 if not able to suggest one
	 */
	private int getNextReadingLesson(int lastLesson) {
		
		if (lastLesson == 0) {
			return 0;
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
	 * @return next lesson to be assigned, 0 if not able to suggest one
	 */
	private int getNextDemonstrationLesson(int lastLesson) {
		
		// the appropriate demonstration lessons fall in the following ranges:
		// [1, 6] U [8, 51]
		
		switch (lastLesson) {
		case 0:
			// invalid lesson
			return 0;
			
		case 6:
			// skip 7
			return 8;
			
		case 51:
			// wrap around
			return 1;

		default:
			// advance to next
			return lastLesson + 1;
		}
		
	}

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
	
}
