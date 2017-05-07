package assignmentplanner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

import entities.Assgn_t;
import entities.Assignment;
import entities.Role;
import services.AssignmentPersistenceService;
import services.StudentPersistenceService;
import services.impl.AssignmentPersistenceServiceImpl;
import services.impl.StudentPersistenceServiceImpl;
import util.DAOException;
import util.DataSourceManager;
import util.Suggestion;

public class Schedule {
	
	ArrayList<Assignment> schedule;
	
	private static final int MIN_LAPSED_DAYS = 7*4; // (7 days / week) * (4 weeks) = 28 days
	
	public Schedule() {
		schedule = new ArrayList<>();
	}
	
	public boolean add(Assignment assgn) {
		//TODO: this method should organize the assignments as they are added.
		/* sort by date
		 *  by type
		 *  by section
		 */
	
		return schedule.add(assgn);
	}
	
	private LocalDate lastParticipationInSchedule(String publisher) {
		
		LocalDate date = null;
		//TODO look for the greatest date not just the last one
		for (Assignment a : schedule) {
			if (a == null) {
				continue;
			}
			if (a.getAssignee().equals(publisher) || a.getHouseholder().equals(publisher)) {
				date = a.getDate();
			}
		}
		
		return date;
	}
	
	public int size() {
		return schedule.size();
	}

	public void generateSchedule() throws SQLException, IOException, DAOException {
		
		DataSource ds = DataSourceManager.getDataSource();
		AssignmentPersistenceService assgnService = new AssignmentPersistenceServiceImpl(ds);
		
		
		LocalDate baseDate = assgnService.lastScheduledAssgnDate();
		LocalDate date = null;
		
		//I am afraid there may be run time errors
		//Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate()

		
		List<Suggestion> readingSug = assgnService.retrieveSuggestions(Assgn_t.READING, Role.ASSIGNEE);
		
		List<Suggestion> initPubSug = assgnService.retrieveSuggestions(Assgn_t.INITIAL_CALL, Role.ASSIGNEE);
		List<Suggestion> initHhdSug = assgnService.retrieveSuggestions(Assgn_t.INITIAL_CALL, Role.HOUSEHOLDER);
		
		List<Suggestion> retvPubSug = assgnService.retrieveSuggestions(Assgn_t.RETURN_VISIT, Role.ASSIGNEE);
		List<Suggestion> retvHhdSug = assgnService.retrieveSuggestions(Assgn_t.RETURN_VISIT, Role.HOUSEHOLDER);
		
		List<Suggestion> studPubSug = assgnService.retrieveSuggestions(Assgn_t.BIBLE_STUDY, Role.ASSIGNEE);
		List<Suggestion> studHhdSug = assgnService.retrieveSuggestions(Assgn_t.BIBLE_STUDY, Role.HOUSEHOLDER);
		
		
		date = baseDate.plusDays(7); // only reading is assigned on the first week
		this.add( generateAssignment(new Assignment(date, Assgn_t.READING, "a"), readingSug, null) );
		
		// the types are done in parallel (rather than finishing all weeks for one type and moving on)
		// so that assignments are given more evenly and fairly
		for (int i = 0; i < 5; i++) {
			date = date.plusDays(7); //increment past the first week (which only has one reading) and onto the next
			
			this.add( generateAssignment(new Assignment(date,  Assgn_t.READING, "a"), readingSug, null) );
			this.add( generateAssignment(new Assignment(date,  Assgn_t.READING, "b"), readingSug, null) );
			
			this.add( generateAssignment(new Assignment(date, Assgn_t.INITIAL_CALL, "a"), initPubSug, initHhdSug) );
			this.add( generateAssignment(new Assignment(date, Assgn_t.INITIAL_CALL, "b"), initPubSug, initHhdSug) );
			
			this.add( generateAssignment(new Assignment(date, Assgn_t.RETURN_VISIT, "a"), retvPubSug, retvHhdSug) );
			this.add( generateAssignment(new Assignment(date, Assgn_t.RETURN_VISIT, "b"), retvPubSug, retvHhdSug) );
			
			this.add( generateAssignment(new Assignment(date, Assgn_t.BIBLE_STUDY, "a"), studPubSug, studHhdSug) );
			this.add( generateAssignment(new Assignment(date, Assgn_t.BIBLE_STUDY, "b"), studPubSug, studHhdSug) );
		}
		
	}
	
	/*
	 * Fills in the name for the assignment by looking through the suggestions and the schedule to determine who should be assigned.
	 * */
	private Assignment generateAssignment(Assignment assgn, List<Suggestion> publisher_sug, List<Suggestion> householder_sug) throws SQLException, IOException, DAOException {
		
		DataSource ds = DataSourceManager.getDataSource();
		AssignmentPersistenceService assgnService = new AssignmentPersistenceServiceImpl(ds);
		
		StudentPersistenceService studentService = new StudentPersistenceServiceImpl(ds);
		
		LocalDate lastScheduled = null;
		
		Iterator<Suggestion> pubSug_it = null;
		Iterator<Suggestion> hholdSug_it = null;
		
		if (publisher_sug != null) {
			pubSug_it = publisher_sug.iterator();
			
			while ((assgn.getAssignee() == null || assgn.getAssignee() == "") && pubSug_it.hasNext()) {
				Suggestion s = pubSug_it.next();
				
				String publisher = s.getName();
				
				Assignment mostRecentPart = assgnService.retrieveMostRecentParticipationByPublisher(publisher);
				lastScheduled = this.lastParticipationInSchedule(publisher);
				
				
				if (lastScheduled == null || Math.abs(ChronoUnit.DAYS.between(lastScheduled, assgn.getDate())) >= MIN_LAPSED_DAYS) {
					
					if (mostRecentPart == null || Math.abs(ChronoUnit.DAYS.between(mostRecentPart.getDate(), assgn.getDate())) >= MIN_LAPSED_DAYS) {
						
						assgn.setAssignee(publisher);
						
						//System.out.format("%s assigned to type %d\n\n", publisher, assgn.getType().toInt());
						
						//update the suggestion (this makes sure that the elements are sorted by suggestion date)
						s.setDate(assgn.getDate());
						pubSug_it.remove();
						publisher_sug.add(s);
						
					} else {
						//System.out.format("%s participated already.\n", publisher);
					}
				} else {
					//System.out.format("%s placed in schedule already.\n", publisher);
				}
			}
		}
		
		if (householder_sug != null) {
			hholdSug_it = householder_sug.iterator();
			
			while ((assgn.getHouseholder() == null || assgn.getHouseholder() == "") && hholdSug_it.hasNext()) {
				Suggestion s = hholdSug_it.next();
				
				String hholder = s.getName();
				
				lastScheduled = this.lastParticipationInSchedule(hholder);
				Assignment mostRecentPart = assgnService.retrieveMostRecentParticipationByPublisher(hholder);
				
				//check that the student has not been been placed in the schedule already (w/in last 3 weeks)
				if ((lastScheduled == null
						|| Math.abs(ChronoUnit.DAYS.between(lastScheduled, assgn.getDate())) >= MIN_LAPSED_DAYS)) {
					
					//check that student has not participated some other way within the last 3 weeks
					if (mostRecentPart == null
							|| Math.abs(ChronoUnit.DAYS.between(mostRecentPart.getDate(), assgn.getDate())) >= MIN_LAPSED_DAYS) {
						
						//since the assgn has not been added to the schedule lastScheduled will not reveal if the hholder was set to be the publisher
						if (!assgn.getAssignee().equals(hholder)) {
							
							String assignee = assgn.getAssignee();
							Long a_id = null;
							
							for (Suggestion sg : publisher_sug) {
								if (sg.getName().equals(assignee)) {
									a_id = sg.getId();
								}
							}
							
							if (studentService.getGenderById(a_id).equals(studentService.getGenderById( s.getId() ))) {
								// use assgn.getAssignee() with the publisher suggestion list to find the id for the assignee
								studentService.getGenderById( s.getId() );
								
								
								//System.out.format("%s assigned to hhold type %d\n\n", hholder, assgn.getType().toInt());
								assgn.setHouseholder(hholder);
								
								//update the suggestion (this makes sure that the elements are sorted by suggestion date)
								s.setDate(assgn.getDate());
								hholdSug_it.remove();
								householder_sug.add(s);
							} else {
								//System.out.println("Gender did not match.");
							}
						} else {
							//System.out.println(hholder+" assigned to be publisher already.");
						}
					} else {
						//System.out.format("%s participated already.\n", hholder);
					}
						
				} else {
					//System.out.println(hholder+" placed in schedule already.");
				}
			}
		}
	
	
		if (assgn.getAssignee() == null || assgn.getAssignee() == "") {
			System.out.println("Unable to make a suggestion.");
			System.out.format("type: %d", assgn.getType().toInt());
			throw new IOException("Unable to make a suggestion");
		}

		return assgn;
	}

	public void print() {
		for (Assignment a : schedule) {
			System.out.println(a.toString());
		}
	}
}
