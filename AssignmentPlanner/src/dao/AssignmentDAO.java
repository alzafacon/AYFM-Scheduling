package dao;


import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import entities.Assgn_t;
import entities.Assignment;
import entities.Role;
import util.DAOException;
import util.Suggestion;

/*
 * DAO object that describes a class (AssignmentDAO) that encapsulates a classes (Assignment) ORM.
 * Object Relational Mapping. 
 * */
public interface AssignmentDAO {
	/*
	 * Persists an Assignment object.
	 * throws an error when the object has a non-null id value 
	 * */
	public Assignment create(Connection connection, Assignment assgn) throws SQLException, DAOException;
	
	/**
	 * Updates lesson completion, passed lesson or other changes.
	 * @return Number of modified rows. Expected to be one.
	 * */
	public int update(Connection connection, Assignment assgn);
	
	/* Finds the date of the most recent week of assignments in the DB
	 * */
	public LocalDate lastAssgnDate(Connection connection) throws SQLException, DAOException;
	
	/*
	 * Finds the assignment (of whatever type) that the publisher has last given
	 * (could this be null?) TODO
	 */
	public Assignment retrieveMostRecentParticipationByPublisher(Connection connection, String publisher) throws SQLException, DAOException;

	/*
	 * This is the core of Schedule Planner.
	 * generates a list of suggestions in the order that assignments should be given for a given type and role
	 * without accounting for section because fairness breaks down otherwise
	 */
	public List<Suggestion> retrieveSuggestion(Connection connection, Assgn_t type, Role role) throws SQLException, DAOException;
}
