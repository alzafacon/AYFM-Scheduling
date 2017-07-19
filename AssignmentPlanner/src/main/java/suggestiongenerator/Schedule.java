package suggestiongenerator;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import dao.AssignmentDAO;
import entities.Assignment;
import entities.Student;
import util.Assgn_t;
import util.DAOException;
import util.Participation;
import util.Section;


public class Schedule {
	
	public static final int NUM_WEEKS = 5;
	public static final int NUM_ASSIGNMENT_TYPES = 4;
	public static final int NUM_SECTIONS = 2;
	
	private Assignment[][][] schedule;
	
	private int year;
	private int month;
	
	/**
	 * Create a skeleton schedule with dates initialized using parameters.
	 * @param year Numeric year to build schedule
	 * @param month Numeric month to build schedule.
	 */
	public Schedule(int year, int month) {
		
		schedule = new Assignment[NUM_WEEKS][NUM_ASSIGNMENT_TYPES][NUM_SECTIONS];
		
		// the first week of every month only has one reading assignment
		schedule[0] = new Assignment[1][1];
		
		// all other weeks have all assignment types for both sections
		
		this.year = year;
		this.month = month;
		
		LocalDate date = LocalDate.of(year, month, 1).with(firstInMonth(DayOfWeek.MONDAY));
		
		// initialize all assignments with dates, types, and sections
		for (int week = 0; week < NUM_WEEKS; week++) {
			for (int atype = 0; atype < schedule[week].length; atype++) {
				for (int section = 0; section < schedule[week][atype].length; section++) {
					
					schedule[week][atype][section] = 
							new Assignment(date, Assgn_t.get(atype+1), Section.get(section));
				}
			}
			date = date.plusWeeks(1);
		}
	}

	/**
	 * Generate the monthly schedule by creating a 'queue' of participants called `suggestions`.
	 * 
	 * @throws SQLException
	 * @throws DAOException
	 */
	public void generateSchedule() throws SQLException, DAOException {
		
		AssignmentDAO assgnDao = new AssignmentDAO();
		
		Assignment suggestion;
		
		List<Participation> mostRecentParticipations = assgnDao.retrieveParticipations();
		
		suggestion = generateSuggestion(mostRecentParticipations.get(0));
		
		// find a week to place the suggested assignment in
		
		
	}
	
	/**
	 * Progression: Reading -> Initial Call(assignee) -> Initial Call(hhold)
	 * 						-> Return Visit(assignee) -> Return Visit(hhold)
	 * 						-> Bible Study(assignee) -> Bible Study(hhold)
	 * This progression is filtered based on what the student can work on.
	 * 
	 * @param mostRecent Student placed in assignment type and role as above
	 * @return Suggestion based on most recent participation
	 */
	private Assignment generateSuggestion(Participation participation) {
	
		Student student = participation.student;
		Assignment pastAssignment = participation.mostRecentAssignment;
		
		List<Assgn_t> worksOn;
		
		Assignment suggestion = new Assignment();
		
		
		
		return null;
	}
	
	public void print() {
		
		for (int week = 0; week < NUM_WEEKS; week++) {
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
