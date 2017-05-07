package services.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import dao.AssignmentDAO;
import dao.impl.AssignmentDAOimpl;
import entities.Assgn_t;
import entities.Assignment;
import entities.Role;
import services.AssignmentPersistenceService;
import util.DAOException;
import util.Suggestion;

//TODO transactions should be rolled back in the event of an exception
public class AssignmentPersistenceServiceImpl implements AssignmentPersistenceService {

	private DataSource dataSource;
	
	public AssignmentPersistenceServiceImpl(DataSource ds) {
		this.dataSource = ds;
	}
	
	@Override
	public Assignment create(Assignment assgn) throws SQLException, DAOException {
		
		Connection connection = dataSource.getConnection();
		AssignmentDAO assgnDAO = new AssignmentDAOimpl();
		
		try {
			connection.setAutoCommit(false);
			
			assgn = assgnDAO.create(connection, assgn);
			connection.commit();
			
			return assgn;
		}
		catch (Exception excp) {
			connection.rollback();
			throw excp;
		}
		finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
		
	}


	@Override
	public List<Suggestion> retrieveSuggestions(Assgn_t type, Role role) throws SQLException, DAOException {

		Connection connection = dataSource.getConnection();
		AssignmentDAO assgnDAO = new AssignmentDAOimpl();
		
		try {
			return assgnDAO.retrieveSuggestion(connection, type, role);
		}
		finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	
	@Override
	public LocalDate lastScheduledAssgnDate() throws SQLException, DAOException {
		
		Connection connection = dataSource.getConnection();
		AssignmentDAO assgnDAO = new AssignmentDAOimpl();
		
		try {	
			return assgnDAO.lastAssgnDate(connection); 
		}
		finally {
			
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}

	}

	@Override
	public Assignment retrieveMostRecentParticipationByPublisher(String publisher) throws SQLException, DAOException {

		Connection connection = dataSource.getConnection();
		AssignmentDAO assgnDAO = new AssignmentDAOimpl();
		
		try {
			Assignment mostRecent = assgnDAO.retrieveMostRecentParticipationByPublisher(connection, publisher);
			
			return mostRecent;
		}
		finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}


}
