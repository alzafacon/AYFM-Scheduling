package services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import entities.Assgn_t;
import entities.Assignment;
import entities.Role;
import util.DAOException;
import util.Suggestion;

public interface AssignmentPersistenceService {
	
	/* Inserts a new Assignment to the DB
	 * the id must be null, an exception is thrown otherwise
	 * when insertion is successful, the id will be populated
	 * */
	Assignment create(Assignment assgn) throws SQLException, DAOException;
	
	/* Filters and Sorts the history to suggest the publisher who
	 * has not participated for a given role and type in the longest time
	 * */
	List<Suggestion> retrieveSuggestions(Assgn_t type, Role role) throws SQLException, DAOException;
	
	
	LocalDate lastScheduledAssgnDate() throws SQLException, DAOException;

	Assignment retrieveMostRecentParticipationByPublisher(String assignee) throws SQLException, DAOException;
}
