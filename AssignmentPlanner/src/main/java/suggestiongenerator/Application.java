package suggestiongenerator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import suggestiongenerator.entities.Assignment;
import suggestiongenerator.repository.AssignmentRepository;
import suggestiongenerator.services.ImportService;
import suggestiongenerator.services.Schedule;


/**
 * Spring Boot Gradle Plugin searches for the 
 * public static void main() method to flag as a runnable class.
 * Solely for testing purposes at this point.
 * @author FidelCoria
 *
 */
 @SpringBootApplication
public class Application implements CommandLineRunner {
	 
	 @Autowired
	 Schedule schedule;
	 
	 @Autowired
	 ImportService importService;
	 
	 @Autowired
	 AssignmentRepository assignmentRepository;
	 
	 public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

    @Override
    public void run(String... strings) throws Exception {
    	
    }
    
}
