package dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import entities.Student;

public class StudentRowMapper implements RowMapper<Student> {

	@Override
	public Student mapRow(ResultSet rs, int count) throws SQLException {
		
		Student student = new Student(
			rs.getLong("id"),
			rs.getString("full_name"),
			rs.getString("gender").charAt(0),
			rs.getBoolean("isactive")
		);
		
		return student;
	}
}
