package io.fidelcoria.ayfmap.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.fidelcoria.ayfmap.domain.Assignment;
import io.fidelcoria.ayfmap.domain.Person;
import io.fidelcoria.ayfmap.service.AssignmentImportService;
import io.fidelcoria.ayfmap.service.StudentImportService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
public class ImportTabController {

	@Autowired
	AssignmentImportService assignmentImportService;
	@Autowired
	StudentImportService studentImportService;
	
	
	@FXML
	ChoiceBox<Month> scheduleImportMonthChoiceBox;
	@FXML
	ChoiceBox<Integer> scheduleImportYearChoiceBox;
	@FXML
	Button importScheduleButton;
	@FXML
	ProgressIndicator scheduleImportProgress;
	@FXML
	Label scheduleImportFeedback;
	
	@FXML
	Button importEnrollmentButton;
	@FXML
	ProgressIndicator enrollmentImportProgress;
	@FXML
	Label enrollmentImportFeedback;
	
	@Value("${installation.directory.workspace}")
	private String workspace;
	
	public void initialize() {
		
		int currentYear = LocalDate.now().getYear();
		
		scheduleImportMonthChoiceBox.getItems().addAll(Month.values());
		scheduleImportMonthChoiceBox.getSelectionModel().selectFirst();
		
		List<Integer> years = new ArrayList<>();
		
		for (int offset = -6; offset < 3; offset++) {
			years.add((Integer)(currentYear+offset));
		}
		
		scheduleImportYearChoiceBox.getItems().addAll(years);
		scheduleImportYearChoiceBox.getSelectionModel().selectFirst();
	}
	
	public File openScheduleFilePicker() {
		
		FileChooser chooseSchedule = new FileChooser();
		
		chooseSchedule.setInitialDirectory(new File(workspace));
		chooseSchedule.getExtensionFilters().add(new ExtensionFilter("MS Word", "*.docx"));
		
		File schedule = chooseSchedule.showOpenDialog(null);
		
		return schedule;
	}
	
	public void importSchedule() throws FileNotFoundException, IOException {
		
		scheduleImportFeedback.setVisible(false);
		
		Month month = scheduleImportMonthChoiceBox.getValue();
		Integer year = scheduleImportYearChoiceBox.getValue();
		
		File scheduleToImport = openScheduleFilePicker();
		
		if (scheduleToImport == null) {
			return;
		}
		
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				List<Assignment> assignments;
				
				try {
					assignments = assignmentImportService
						.readAssignmentsFromDocx(scheduleToImport, year, month.getValue());
					assignmentImportService.save(assignments);
					
					updateProgress(1, 1);
				} catch (FileNotFoundException e) {
					updateProgress(0,1);
					updateMessage("File not found");
					
					// TODO logging
					e.printStackTrace();
				} catch (IOException e) {
					updateProgress(0,1);
					updateMessage("Failed");
					
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
		};
		
		scheduleImportProgress.progressProperty().bind(task.progressProperty());
		scheduleImportFeedback.textProperty().bind(task.messageProperty());
		
		new Thread(task).start();
		
		scheduleImportProgress.setVisible(true);
		scheduleImportFeedback.setVisible(true);
	}
	
	public File openEnrollmentFilePicker() {
	
		FileChooser chooseEnrollment = new FileChooser();
		chooseEnrollment.setInitialDirectory(new File(workspace));
		chooseEnrollment.getExtensionFilters().add(new ExtensionFilter("CSV", "*.csv"));
		
		File enrollment = chooseEnrollment.showOpenDialog(null);
		
		return enrollment;
	}
	
	public void importEnrollment() throws Exception {
		
		enrollmentImportFeedback.setVisible(false);
		
		File enrollmentToImport = openEnrollmentFilePicker();
		
		if (enrollmentToImport == null) {
			return;
		}
		
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				List<Person> students;
				
				try {
					students = studentImportService
						.readStudentsWithCsvMapReader(enrollmentToImport.getAbsolutePath());
					studentImportService.saveStudents(students);
					
					updateProgress(1, 1);
				} catch (Exception e) {
					updateProgress(0, 1);
					updateMessage("Import failed");
					
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return null;
			}
		};
		
		enrollmentImportProgress.progressProperty().bind(task.progressProperty());
		enrollmentImportFeedback.textProperty().bind(task.messageProperty());
		
		new Thread(task).start();
		
		enrollmentImportProgress.setVisible(true);
		enrollmentImportFeedback.setVisible(true);
	}
}
