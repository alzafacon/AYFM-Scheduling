package services;

import java.sql.SQLException;

import util.DAOException;

public interface StudentPersistenceService {

	public String getGenderById(Long id) throws SQLException, DAOException;
}
