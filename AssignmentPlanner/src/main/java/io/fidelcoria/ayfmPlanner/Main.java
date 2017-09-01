package io.fidelcoria.ayfmPlanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
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
 @ComponentScan("io.fidelcoria.ayfmPlanner.controller") // needed for fxml to auto wire correctly
public class Main extends Application {
	 
	 private ConfigurableApplicationContext springContext;
	 private Parent rootNode;
	 
	 public static void main(String[] args) {
		Application.launch(args);
	}

    @Override
    public void init() throws Exception {
    	springContext = SpringApplication.run(Main.class);
    	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
    	fxmlLoader.setControllerFactory(springContext::getBean);
    	rootNode = fxmlLoader.load();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
    	stage.setTitle("AYFM Planner");
    	stage.setScene(new Scene(rootNode));
    	stage.show();
    }
    
    @Override
    public void stop() {
    	springContext.close();
    }
    
}
