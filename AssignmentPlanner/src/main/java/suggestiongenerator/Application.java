package suggestiongenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Spring Boot Gradle Plugin searches for the 
 * public static void main() method to flag as a runnable class.
 * Solely for testing purposes at this point.
 * @author FidelCoria
 *
 */
// @SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"dao"})
public class Application implements CommandLineRunner {

	@Autowired
    JdbcTemplate jdbcTemplate;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);	
	}

    @Override
    public void run(String... strings) throws Exception {

    	System.out.println("Welcome to Assignment Planner.");
		
		int year = 2017;
		int month = 7;		
		Schedule sched = new Schedule(year, month);
		
		
		System.out.println("\nGenerating Schedule...");
		sched.generateSchedule();
		
		sched.print();
    }

}
