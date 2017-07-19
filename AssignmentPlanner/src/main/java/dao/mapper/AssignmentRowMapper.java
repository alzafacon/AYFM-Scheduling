package dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import entities.Assignment;
import util.Assgn_t;
import util.Section;

public class AssignmentRowMapper implements RowMapper<Assignment> {

	@Override
	public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Assignment a = new Assignment();
		
		a.setId(			rs.getLong("id"));
		a.setDate(			rs.getDate("week").toLocalDate());
		a.setAssignee(		rs.getLong("assignee"));
		a.setHouseholder(	rs.getLong("householder"));
		a.setLesson(		rs.getInt("lesson"));
		a.setSection(		Section.get(rs.getString("section")));
		a.setType(			Assgn_t.get(rs.getInt("assgn_type")));
		a.setCompleted(		rs.getBoolean("completed"));
		a.setPassed(		rs.getBoolean("passed"));
		
		return a;
	}

}
