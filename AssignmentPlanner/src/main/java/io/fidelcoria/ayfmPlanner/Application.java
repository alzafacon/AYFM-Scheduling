package io.fidelcoria.ayfmPlanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * Spring Boot Gradle Plugin searches for the 
 * public static void main() method to flag as a runnable class.
 * Solely for testing purposes at this point.
 * @author FidelCoria
 *
 */
 @SpringBootApplication
 @ComponentScan("io.fidelcoria.ayfmPlanner.service") // need for tests to grab context correctly
public class Application implements CommandLineRunner {
	 
	 public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

    @Override
    public void run(String... strings) throws Exception {
    	
    }
    
}
