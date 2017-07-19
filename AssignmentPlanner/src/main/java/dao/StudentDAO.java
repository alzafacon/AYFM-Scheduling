package dao;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import dao.mapper.StudentRowMapper;
import entities.Student;
import util.DAOException;

public class StudentDAO {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	final static String SELECT_GENDER_BY_ID = "SELECT gender FROM person WHERE id_person = ?; ";
	
	public char getGenderById(Long id) throws SQLException, DAOException {
		
		Student student = jdbcTemplate.query(SELECT_GENDER_BY_ID, new Object[]{id}, new StudentRowMapper()).get(0);
		
		return student.getGender(); 
	}

}
