package io.fidelcoria.ayfmPlanner.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.fidelcoria.ayfmPlanner.domain.Assignment;
import io.fidelcoria.ayfmPlanner.domain.Person;
import io.fidelcoria.ayfmPlanner.service.AssignmentImportService;
import io.fidelcoria.ayfmPlanner.service.StudentImportService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
public class ImportTabController {

	@Autowired
	AssignmentImportService assignmentImportService;
	@Autowired
	StudentImportService studentImportService;
	
	
	@FXML
	Button choseScheduleButton;
	@FXML
	ChoiceBox<Month> scheduleImportMonthChoiceBox;
	@FXML
	ChoiceBox<Integer> scheduleImportYearChoiceBox;
	@FXML
	Label scheduleChosenForImportLabel;
	@FXML
	Button importScheduleButton;
	private File scheduleToImport;
	
	@FXML
	Button chooseEnrollmentButton;
	@FXML
	Label enrollmentChosenLabel;
	@FXML
	Button importEnrollmentButton;
	private File enrollmentToImport;
	
	public void initialize() {
		scheduleImportMonthChoiceBox.getItems().addAll(Month.values());
		
		int currentYear = LocalDate.now().getYear();
		
		List<Integer> years = new ArrayList<>();
		
		for (int offset = -6; offset < 3; offset++) {
			years.add((Integer)(currentYear+offset));
		}
		
		scheduleImportYearChoiceBox.getItems().addAll(years);
	}
	
	public void openScheduleFilePicker() {
		
		FileChooser chooseSchedule = new FileChooser();
		chooseSchedule.setInitialDirectory(new File("C:\\Users\\FidelCoria\\Documents\\AYFM\\"));
		chooseSchedule.getExtensionFilters().add(new ExtensionFilter("MS Word", "*.docx"));
		
		File schedule = chooseSchedule.showOpenDialog(null);
		
		if (schedule != null) {
			scheduleToImport = schedule;
			scheduleChosenForImportLabel.setText(scheduleToImport.getName());
		} else {
			scheduleToImport = null;
			scheduleChosenForImportLabel.setText("no file chosen");
		}
	}
	
	public void importSchedule() throws FileNotFoundException, IOException {
		
		Month month = scheduleImportMonthChoiceBox.getValue();
		Integer year = scheduleImportYearChoiceBox.getValue();
		
		List<Assignment> assignments = assignmentImportService.readAssignmentsFromDocx(scheduleToImport, year, month.getValue());
		
		assignmentImportService.save(assignments);
		
		// the only feed back the use will get... uugh
		scheduleChosenForImportLabel.setText("import successful");
	}
	
	public void openEnrollmentFilePicker() {
	
		FileChooser chooseEnrollment = new FileChooser();
		chooseEnrollment.setInitialDirectory(new File("C:\\Users\\FidelCoria\\Documents\\AYFM\\"));
		chooseEnrollment.getExtensionFilters().add(new ExtensionFilter("CSV", "*.csv"));
		
		File enrollment = chooseEnrollment.showOpenDialog(null);
		
		if (enrollment != null) {
			enrollmentToImport = enrollment;
			enrollmentChosenLabel.setText(enrollment.getName());
		} else {
			enrollmentToImport = null;
			enrollmentChosenLabel.setText("no file chosen");
		}
	}
	
	public void importEnrollment() throws Exception {
		
		List<Person> students = studentImportService.readStudentsWithCsvMapReader(enrollmentToImport.getAbsolutePath());
		
		studentImportService.saveStudents(students);
		
		enrollmentChosenLabel.setText("import successful");
	}
}
