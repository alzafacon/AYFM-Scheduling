package dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import entities.Assignment;
import entities.Student;
import util.Participation;
import util.Section;

public class ParticipationRowMapper implements RowMapper<Participation> {

	@Override
	public Participation mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		Participation participation = new Participation();
		
		Student person = new Student(
				rs.getLong("pId"),
				rs.getString("pName"),
				rs.getString("pGender").charAt(0),
				true // must be active to be considered for the participation list
			);
		
		Assignment mostRecent = new Assignment();
		
		mostRecent.setDate(			rs.getDate("most_recent").toLocalDate());
		mostRecent.setAssignee(		rs.getLong("aAssigneeId"));
		mostRecent.setHouseholder(	rs.getLong("aHouseholderId"));
		mostRecent.setLesson(		rs.getInt("aLesson"));
		mostRecent.setSection(Section.get(rs.getString("aClassroom")));
		mostRecent.setCompleted(	rs.getBoolean("aCompleted"));
		mostRecent.setPassed(		rs.getBoolean("aPassed"));
		
		participation.mostRecentAssignment = mostRecent;
		participation.student = person;
		
		return participation;
	}

}
