package dao;

import java.sql.Connection;
import java.sql.SQLException;

import util.DAOException;

public interface StudentDAO {

	
	public String getGenderById(Connection connection, Long id) throws SQLException, DAOException;
}
