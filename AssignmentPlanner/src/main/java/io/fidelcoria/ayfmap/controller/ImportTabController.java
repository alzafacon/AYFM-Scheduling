package io.fidelcoria.ayfmap.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.fidelcoria.ayfmap.domain.Assignment;
import io.fidelcoria.ayfmap.domain.Person;
import io.fidelcoria.ayfmap.service.AssignmentImportService;
import io.fidelcoria.ayfmap.service.StudentImportService;
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
	
	public void initialize() {
		
		int currentYear = LocalDate.now().getYear();
		
		scheduleImportMonthChoiceBox.getItems().addAll(Month.values());
		
		List<Integer> years = new ArrayList<>();
		
		for (int offset = -6; offset < 3; offset++) {
			years.add((Integer)(currentYear+offset));
		}
		
		scheduleImportYearChoiceBox.getItems().addAll(years);
	}
	
	public File openScheduleFilePicker() {
		
		FileChooser chooseSchedule = new FileChooser();
		chooseSchedule.setInitialDirectory(
				new File(System.getProperty("user.home")+"/Documents/AYFM/"));
		chooseSchedule.getExtensionFilters().add(
				new ExtensionFilter("MS Word", "*.docx"));
		
		File schedule = chooseSchedule.showOpenDialog(null);
		
		return schedule;
	}
	
	public void importSchedule() throws FileNotFoundException, IOException {
		
		scheduleImportFeedback.setVisible(false);
		
		Month month = scheduleImportMonthChoiceBox.getValue();
		Integer year = scheduleImportYearChoiceBox.getValue();
		
		File scheduleToImport = openScheduleFilePicker();
		
		scheduleImportProgress.setVisible(true);
		
		List<Assignment> assignments = 
				assignmentImportService.readAssignmentsFromDocx(
						scheduleToImport, year, month.getValue());
		
		assignmentImportService.save(assignments);
		
		scheduleImportProgress.setVisible(false);
		scheduleImportFeedback.setVisible(true);
		
	}
	
	public File openEnrollmentFilePicker() {
	
		FileChooser chooseEnrollment = new FileChooser();
		chooseEnrollment.setInitialDirectory(
				new File(System.getProperty("user.home")+"/Documents/AYFM/"));
		chooseEnrollment.getExtensionFilters().add(
				new ExtensionFilter("CSV", "*.csv"));
		
		File enrollment = chooseEnrollment.showOpenDialog(null);
		
		return enrollment;
	}
	
	public void importEnrollment() throws Exception {
		
		enrollmentImportFeedback.setVisible(false);
		
		File enrollmentToImport = openEnrollmentFilePicker();
		
		enrollmentImportProgress.setVisible(true);
		
		List<Person> students = 
				studentImportService.readStudentsWithCsvMapReader(
						enrollmentToImport.getAbsolutePath());
		
		studentImportService.saveStudents(students);
		
		enrollmentImportProgress.setVisible(false);
		enrollmentImportFeedback.setVisible(true);
	}
}
