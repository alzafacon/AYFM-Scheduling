package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dao.StudentDAO;
import util.DAOException;

public class StudentDAOimpl implements StudentDAO {

	
	final static String SELECT_GENDER_BY_ID = "SELECT gender FROM person WHERE id_person = ?; ";
	@Override
	public String getGenderById(Connection connection, Long id) throws SQLException, DAOException {
		
		PreparedStatement ps = null;
		
		try {
			ps = connection.prepareStatement(SELECT_GENDER_BY_ID);
			
			ps.setLong(1, id);
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next() == false) {
				throw new DAOException("Unable to find gender.");
			}
			
			return rs.getString("gender");
		}
		finally {
			if (ps != null && !ps.isClosed()) {
				ps.close();
			}
		}
		
	}

}
