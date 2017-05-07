package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * This is a service which maintains a Singleton instance of the MySQL
 * DataSource. Application use the static method getDataSource() to obtain an
 * open connection to the MySQL server. The DBMS connection parameters (url, id,
 * and password) is maintained in a property file 'dbconfig.properties'. The
 * property file must be located on the application's CLASSPATH. See the
 * configuration property file is loaded by the method
 * getPropertiesFromClasspath().
 */
public class DataSourceManager {
	
	private static DataSource dataSource;
	
	// should I include the synchronized key word here? what does it mean?
	public static DataSource getDataSource() throws IOException {
		
		
		
		if (dataSource == null) {
			MysqlDataSource ds = null;
			
			Properties props = getPropertiesFromClasspath();
			
			String url = props.getProperty("url");
			if (url == null || url.isEmpty()) {
				throw new RuntimeException("property 'url' not found in configuration file");
			}
			
			String id = props.getProperty("id");
			if (id == null || id.isEmpty()) {
				throw new RuntimeException("property 'id' not found in configuration file");
			}
			
			String password = props.getProperty("password");
			if (password == null || password.isEmpty()) {
				throw new RuntimeException("property 'password' not found in configuration file");
			}
			
			ds = new MysqlDataSource();
			
			ds.setURL(url);
			ds.setUser(id);
			ds.setPassword(password);
			
			dataSource = ds;
		}
		
		return dataSource;
	}
	
	private final static String propFileName = "dbconfig.properties";
	
	public static Properties getPropertiesFromClasspath() throws IOException {
		InputStream inputStream = DataSourceManager.class.getClassLoader().getResourceAsStream(propFileName);
		
		if (inputStream == null) {
			throw new IOException("Property file '"+propFileName+"' not found in classpath.");
		}
		
		Properties properties = new Properties();
		properties.load(inputStream);
		
		return properties;
	}
}
