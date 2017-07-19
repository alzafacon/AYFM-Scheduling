package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import dao.mapper.ParticipationRowMapper;
import entities.Assignment;
import util.DAOException;
import util.Participation;

/**
 * DAO object that describes a class (AssignmentDAO) that encapsulates a classes (Assignment) ORM.
 * Object Relational Mapping. 
 */
@Repository
public class AssignmentDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	final static String INSERT_ASSGN = "INSERT INTO assignment (`week`, assignee, lesson, `classroom`, assgn_type) "
			+ "SELECT ?, id_person, ?, ?, ? "
			+ "FROM person "
			+ "WHERE full_name = ?; ";
	
	/**
	 * Persists an Assignment object.
	 * throws an error when the object has a non-null id value 
	 */
	public Assignment create(Assignment assgn) throws SQLException, DAOException {

		if (assgn.getId() != null) {
			throw new DAOException("Assignment id should be null for insertion.");
		}
		
		jdbcTemplate.update(INSERT_ASSGN, 
			new Object[] {
				assgn.getDate(), assgn.getLesson(), assgn.getSection(), assgn.getType(),
				assgn.getAssignee()
			});
		// TODO: this object does not have the id value set in the database
		return assgn;
	}

	/**
	 * Updates lesson completion, passed lesson or other changes.
	 * @return Number of modified rows. Expected to be one.
	 */
	public int update(Connection connection, Assignment assgn) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	final static String SUGGESTION = 
			  "SELECT "
				  + "MAX(a.`week`) AS `most_recent`, "
				  + "p.`id` AS pId, "
				  + "p.`full_name` AS pName, "
				  + "p.`gender` AS pGender, "
				  + "a.`assignee` AS aAssigneeId, "
				  + "a.`householder` AS aHouseholderId, "
				  + "a.`lesson` AS aLesson, "
				  + "a.`classroom` AS aClassroom, "
				  + "a.`type` AS aType, "
				  + "a.`completed` AS aCompleted, "
				  + "a.`passed` AS aPassed "
			  +	"FROM "
			  	+ "`ayfm`.`person` AS p "
			  		+ "LEFT JOIN "
		  		+ "`ayfm`.`assignment` AS a " 
		  			+ "ON p.`id` = a.`assignee` "
	  				+ "OR p.`id` = a.`householder` "
		      + "WHERE "
		      	+ "TRUE = p.`isactive` "
		      + "GROUP BY p.`id` "
		      + "ORDER BY `most_recent`;";

	/**
	 * This is the core of Schedule Planner.
	 * generates a list of suggestions in the order that assignments should be given for a given type and role
	 * without accounting for section because fairness breaks down otherwise
	 */
	public List<Participation> retrieveParticipations()
			throws SQLException {
						
		return jdbcTemplate.query(SUGGESTION, new ParticipationRowMapper());
	}
	
}
