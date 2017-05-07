package dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import com.mysql.jdbc.Statement;

import dao.AssignmentDAO;
import entities.Assgn_t;
import entities.Assignment;
import entities.Role;
import util.DAOException;
import util.Suggestion;

public class AssignmentDAOimpl implements AssignmentDAO {

	//careful! `section` is a sql keyword!
	final static String insertAssgn = "INSERT INTO assignment (assgn_date, assignee, lesson, `section`, assgn_type) "
			+ "SEECT ?, id_person, ?, '?', ? "
			+ "FROM person "
			+ "WHERE full_name = ?; ";
	
	@Override
	public Assignment create(Connection connection, Assignment assgn) throws SQLException, DAOException {

		if (assgn.getId() != null) {
			throw new DAOException("Assignment id should be null for insertion.");
		}
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(insertAssgn, Statement.RETURN_GENERATED_KEYS);
			
			ps.setDate(1, Date.valueOf( assgn.getDate() ));
			ps.setInt(2, assgn.getLesson());
			ps.setString(3, assgn.getSection());
			ps.setInt(4, assgn.getType().toInt());
			ps.setString(5, assgn.getAssignee());
			
			int rowCount = ps.executeUpdate();
			
			if (rowCount != 1) {
				throw new DAOException("Unexpected number of rows ("+rowCount+") affected.");
			}
			
			ResultSet key = ps.getGeneratedKeys();
			key.next();
			long returned_id = key.getLong(1);
			assgn.setId((Long) returned_id);
		}
		finally {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
		}
		return assgn;
	}

	@Override
	public int update(Connection connection, Assignment assgn) {
		// TODO Auto-generated method stub
		return 0;
	}

	private static final String LAST_ASSGN_DATE = "SELECT MAX(date_assgn) FROM assignment;";
	
	@Override
	public LocalDate lastAssgnDate(Connection connection) throws SQLException, DAOException {
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(LAST_ASSGN_DATE);
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next() == false) {
				throw new DAOException("Unable to find most recent assignment date.");
			}
			
			return rs.getDate(1).toLocalDate();
		}
		finally {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
		}
	}

	final static String LAST_ASSGN_BY_PUBLISHER = 
			  "SELECT id_assignment, date_assgn, assignee, householder, lesson, section, assgn_type, completed, passed "
			+ "FROM 	assignment "
			+ "		NATURAL JOIN "
			+ "			(SELECT assignee, MAX(date_assgn) date_assgn "
			+ "			FROM assignment "
			+ "			GROUP BY assignee "
			+ "			) most_recent_assgn "
			+ "WHERE assignee = (SELECT id_person FROM person where full_name = '?'); ";

	final static String LAST_PARTICIPATION_BY_PUBLISHER =
			  "SELECT id_assignment, date_assgn, assignee, householder, lesson, section, assgn_type, completed, passed "
			+ "FROM assignment "
			+ "WHERE (SELECT id_person FROM person WHERE full_name = ?) IN (assignee, householder) "
			+ "ORDER BY date_assgn DESC "
			+ "LIMIT 1; ";
	
	@Override
	public Assignment retrieveMostRecentParticipationByPublisher(Connection connection, String publisher) throws SQLException, DAOException {
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(LAST_PARTICIPATION_BY_PUBLISHER);
			
			ps.setString(1, publisher);
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next() == false) {
				return null; // there is no history for this user. which is not an exception
				//throw new DAOException("Unable to find most recent assignment by "+publisher+".");
			}
			
			Assignment a = this.RStoAssignment(rs);
			
			if (rs.next() == true) {
				throw new DAOException("More than one assignment received for most recent assignment.");
			}
			
			return a;
		}
		finally {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
		}
	}
	
	final static String SUGGESTION = 
			  "SELECT full_name, MAX(date_assgn), person_id "
			+ "FROM	works_on "
			+ "LEFT JOIN "
			+ "		assignment "
			+ "ON person_id = %s and assignment_id = assgn_type "
			+ "JOIN "
			+ "		person "
			+ "ON person_id = person.id_person "
			+ "WHERE assignment_id = ? "
			+ "AND isactive = TRUE "
			+ "GROUP BY person_id "
			+ "ORDER BY MAX(date_assgn); ";

	@Override
	public List<Suggestion> retrieveSuggestion(Connection connection, Assgn_t type, Role role)
			throws SQLException, DAOException {
		
		PreparedStatement ps = null;
		
		try {
			
			String SUGGESTION_F = String.format(SUGGESTION, role.toString());
						
			ps = connection.prepareStatement(SUGGESTION_F);
			ps.setInt(1, type.toInt());
			
			ResultSet rs = ps.executeQuery();
			
			List<Suggestion> suggestion = new LinkedList<Suggestion>();
			
			while ( rs.next() ) {
				Suggestion a = new Suggestion();
				
				a.setName( rs.getString(1) );
				
				//the date can be null if the student has never been assigned
				Date date = rs.getDate(2);
				
				if (date == null) {
					a.setDate(null);
				} else {
					a.setDate( date.toLocalDate() );
				}
				
				a.setId( rs.getLong(3) );
				
				suggestion.add( a );
			}
			
			return suggestion;
		}
		finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		}
	}

	private Assignment RStoAssignment(ResultSet rs) throws SQLException {
		
		Assignment a = new Assignment();
		
		a.setId(			rs.getLong("id_assignment") );
		a.setDate(			rs.getDate("date_assgn").toLocalDate() );
		a.setAssignee(		rs.getString("assignee") );
		a.setHouseholder(	rs.getString("householder") );
		a.setLesson(		rs.getInt("lesson") );
		a.setSection(		rs.getString("section") );
		a.setType(			Assgn_t.toAssgn_t( rs.getInt("assgn_type") ) );
		a.setCompleted(		rs.getBoolean("completed") );
		a.setPassed(		rs.getBoolean("passed") );
		
		return a;
	}
	
}
