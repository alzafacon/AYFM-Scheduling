package services.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import dao.StudentDAO;
import dao.impl.StudentDAOimpl;
import services.StudentPersistenceService;
import util.DAOException;

public class StudentPersistenceServiceImpl implements StudentPersistenceService {

	private DataSource dataSource;
	
	public StudentPersistenceServiceImpl(DataSource ds) {
		dataSource = ds;
	}
	
	@Override
	public String getGenderById(Long id) throws SQLException, DAOException {
		
		Connection connection = dataSource.getConnection();
		
		StudentDAO studentDAO = new StudentDAOimpl();
		
		try {
			return studentDAO.getGenderById(connection, id);
		}
		finally {
			if (connection == null || !connection.isClosed()) {
				connection.close();
			}
		}
		
	}

}
