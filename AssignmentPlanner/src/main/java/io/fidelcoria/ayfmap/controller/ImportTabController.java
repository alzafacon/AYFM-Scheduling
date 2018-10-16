package io.fidelcoria.ayfmap.controller;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.fidelcoria.ayfmap.domain.Assignment;
import io.fidelcoria.ayfmap.domain.Person;
import io.fidelcoria.ayfmap.domain.PersonRepository;
import io.fidelcoria.ayfmap.fx.control.WeekForm;
import io.fidelcoria.ayfmap.service.AssignmentImportService;
import io.fidelcoria.ayfmap.service.StudentImportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

@Component
public class ImportTabController {

	@Autowired
	AssignmentImportService assignmentImportService;
	@Autowired
	StudentImportService studentImportService;
	@Autowired
	PersonRepository personRepository;
	
	@FXML
	ChoiceBox<Month> scheduleImportMonth;
	@FXML
	ChoiceBox<Integer> scheduleImportYear;
	@FXML
	VBox weekForms;
	@FXML
	Button doImportSchedule;
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
	
	private static final int MAX_WEEKS_PER_MONTH = 5;
	private int numWeeksThisMonth = 5;
	
	public void initialize() {

		scheduleImportMonth.getItems().addAll(Month.values());
		
		int currentYear = LocalDate.now().getYear();
		IntStream.rangeClosed(currentYear, currentYear+2)
			.forEachOrdered(scheduleImportYear.getItems()::add);
		
		ObservableList<Person> ps = FXCollections.observableArrayList(
			personRepository.findAllActiveStudents().stream()
				.map(s -> s.getStudent()).collect(Collectors.toList())
		);
		
		for (int i = 0; i < MAX_WEEKS_PER_MONTH; i++) {
			weekForms.getChildren().add(new WeekForm(ps));
		}
		 
		scheduleImportMonth.getSelectionModel().selectFirst();
		scheduleImportYear .getSelectionModel().selectFirst();
		
		// since updateCalenar depends on both month and year having a value
		// and either one triggers updateCalendar
		// the EventHandlers are registered after setting values...
		scheduleImportMonth.setOnAction(e -> { updateCalendar(e); });
		scheduleImportYear .setOnAction(e -> { updateCalendar(e); });
		// ... and then a dummy event is fired to trigger the call
		scheduleImportMonth.fireEvent(new ActionEvent());		
	}
	
	public void updateCalendar(ActionEvent e) {
		
		LocalDate date = LocalDate
				.of(scheduleImportYear.getValue(), scheduleImportMonth.getValue(), 1)
				.with(firstInMonth(DayOfWeek.MONDAY));
		
		numWeeksThisMonth = 0;
		for (int i = 0; i < MAX_WEEKS_PER_MONTH; i++) {
			if (date.getMonth() == scheduleImportMonth.getValue()) {
				((WeekForm) weekForms.getChildren().get(i))
					.setWeekDate(date.getMonth()+" "+date.getDayOfMonth());
				numWeeksThisMonth++;
			} else {
				weekForms.getChildren().get(i).setVisible(false);
			}
			date = date.plusWeeks(1);
		}
	}
	
	public void importSchedule() throws FileNotFoundException, IOException {
		
		scheduleImportFeedback.setVisible(false);
		
		Month month = scheduleImportMonth.getValue();
		Integer year = scheduleImportYear.getValue();
		
		File scheduleToImport = null;
		
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
	
	public void importEnrollment() throws Exception {
		
		enrollmentImportFeedback.setVisible(false);
		
		File enrollmentToImport = null;
		
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
